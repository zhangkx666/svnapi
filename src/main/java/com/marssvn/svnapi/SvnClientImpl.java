package com.marssvn.svnapi;

import com.marssvn.svnapi.common.CommandUtils;
import com.marssvn.svnapi.common.DateUtils;
import com.marssvn.svnapi.common.StringUtils;
import com.marssvn.svnapi.enums.ESvnNodeKind;
import com.marssvn.svnapi.exception.SvnApiException;
import com.marssvn.svnapi.model.*;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * SVN client
 *
 * @author zhangkx
 */
@Component
public class SvnClientImpl implements ISvnClient {

    /**
     * slf4j.Logger
     */
    private Logger logger = LoggerFactory.getLogger(SvnClientImpl.class);

    private SvnRepository repository;

    /**
     * root path
     */
    private String rootPath;

    /**
     * svn user info
     */
    private SvnUser svnUser;

    /**
     * set root path
     *
     * @param rootPath svn root path
     */
    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * set svn user
     *
     * @param svnUser svn user
     */
    @Override
    public void setSvnUser(SvnUser svnUser) {
        this.svnUser = svnUser;
    }

    /**
     * make directory, will make parent directories also
     *
     * @param dirPath directory path
     * @param message commit message
     */
    @Override
    public void mkdir(String dirPath, String message) {
        if (this.svnUser == null) {
            throw new SvnApiException("EC0001","SVN user is required");
        }
        if (StringUtils.isBlank(this.rootPath)) {
            throw new SvnApiException("EC0002","SVN root path is required");
        }
        if (StringUtils.isBlank(dirPath)) {
            throw new SvnApiException("EC0003","Directory path is required");
        }

        // execute svn mkdir command
        String path = this.rootPath + "/" + dirPath.trim();
        String command = "svn mkdir " + path + " -q -m \"" + message + "\" --parents" + this.svnUser.getAuthString();
        CommandUtils.execute(command);
    }

    /**
     * get head revision by path
     *
     * @param path path
     * @return head revision
     */
    @Override
    public long headRevision(String path) {
        try {
            path = this.rootPath + "/" + (path == null ? "" : path.trim());
            String command = "svn info " + path + " --show-item revision --no-newline" + this.svnUser.getAuthString();

            // print debug log
            logger.debug("execute command: " + command);

            // execute svn info command
            Process process = Runtime.getRuntime().exec(command);

            // check error
            String errorMsg = IOUtils.toString(process.getErrorStream(), "UTF-8");
            if (StringUtils.isNotBlank(errorMsg)) {
                String[] error = errorMsg.split(": ");
                throw new SvnApiException(error[1], errorMsg);
            }

            return Long.valueOf(IOUtils.toString(process.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            throw new SvnApiException(e.getMessage());
        }
    }

    /**
     * get file list
     * @param path
     * @param revision
     * @return
     */
    public SVNNodeItem getList(String path, long revision) {

        // file revision
        String rev = revision == -1 ? "HEAD" : String.valueOf(revision);

        // file full path
        String fullPath = this.repository.getRootPath() + "/" + path;

        // command
        String command = "svn list " + fullPath + " --xml -r " + rev + this.svnUser.getAuthString();
        try {
            Process process = Runtime.getRuntime().exec(command);

            SAXReader xmlReader = new SAXReader();
            Document document = xmlReader.read(process.getInputStream());
            Element rootElement = document.getRootElement().element("list");

            SVNNodeItem directory = new SVNNodeItem();

            // path
            directory.setPath(path);

            // parent path
            // TODO
            directory.setParentPath("");

            // <list><entry></entry></list>
            List<SVNNodeItem> children = new ArrayList<>();
            rootElement.elements().forEach(entry -> {
                SVNNodeItem item = new SVNNodeItem();
                String nodeKind = entry.attributeValue("kind");

                // node kind: directory -> DIR, file -> FILE, else -> NONE
                if ("file".equals(nodeKind)) {
                    item.setNodeKind(ESvnNodeKind.FILE);
                } else if ("dir".equals(nodeKind)) {
                    item.setNodeKind(ESvnNodeKind.DIR);
                } else {
                    item.setNodeKind(ESvnNodeKind.NONE);
                }

                item.setName(entry.elementText("name"));
                item.setPath(path + "/" + item.getName());
                // TODO
                item.setExtension("");
                item.setMimeType("");
                item.setSize(Long.valueOf(entry.elementText("size")));

                // svn commit info
                Element commitElement = entry.element("commit");
                if (commitElement.hasContent()) {
                    SVNCommit svnCommit = new SVNCommit();
                    svnCommit.setRevision(Long.valueOf(commitElement.attributeValue("revision")));
                    svnCommit.setAuthor(commitElement.elementText("author"));
                    svnCommit.setDate(DateUtils.parseDate(commitElement.elementText("date")));
                    item.setCommit(svnCommit);
                }

                // svn lock
                Element lockElement = entry.element("lock");
                if (lockElement.hasContent()) {
                    SVNLock svnLock = new SVNLock();
                    svnLock.setToken(lockElement.elementText("token"));
                    svnLock.setOwner(lockElement.elementText("owner"));
                    svnLock.setComment(lockElement.elementText("comment"));
                    svnLock.setCreatedAt(DateUtils.parseDate(lockElement.elementText("created")));
                    item.setLock(svnLock);
                }

                children.add(item);
            });

            directory.setChildren(children);

            return null;
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * get file by path
     *
     * @param path     file path
     * @param revision file revision
     * @return SVNNodeItem
     */
    public SVNNodeItem getFile(String path, long revision) {
        try {

            // file revision
            String rev = revision == -1 ? "HEAD" : String.valueOf(revision);

            // file full path
            String fullPath = this.repository.getRootPath() + "/" + path;

            // command
            String command = "svn info " + fullPath + " --xml -r " + rev + this.svnUser.getAuthString();
            Process process = Runtime.getRuntime().exec(command);

            // serialize xml to SVNNodeItem
//            Serializer xmlSerializer = new Persister();

            String infoXml = IOUtils.toString(process.getInputStream(), "UTF-8");
            System.out.println(infoXml);

//            List<SvnInfoEntry> entryList = xmlSerializer.read(SvnInfo.class, infoXml).getEntryList();
//            if (entryList.isEmpty()) {
//                throw new SvnApiException("svn info entry is empty");
//            }
//
//            SvnInfoEntry infoEntry = entryList.get(0);
//
            SVNNodeItem nodeItem = new SVNNodeItem();
//
//            // FileIcon.vue
//            String nodeItemName = infoEntry.getPath();
//            nodeItem.setName(nodeItemName);
//
//            // source/marssvn/src/demo/components/FileIcon.vue
//            nodeItem.setPath(infoEntry.getRelativeUrl().replace("^/", ""));
//
//            // svn://127.0.0.1/marssvn/source/marssvn/src/demo/components/FileIcon.vue
//            nodeItem.setFullPath(fullPath);
//
//            // parent path
//            nodeItem.setParentPath(nodeItem.getPath().replace("/" + nodeItemName, ""));
//
//            // revision
//            if (revision == -1) {
//
//                // HEAD revision
//                nodeItem.setRevision(infoEntry.getRevision());
//            } else {
//
//                // parameter revision
//                nodeItem.setRevision(revision);
//            }
//
//            // e.g. vue
//            String extension = nodeItemName.substring(nodeItemName.lastIndexOf(".") + 1);
//            nodeItem.setExtension(extension);
//
//            // mime-type
//            nodeItem.setMimeType(getMimeType(fullPath));
//
//            // DIR: directory   FILE: file,  NONE: nothing
//            if ("file".equals(infoEntry.getKind())) {
//                nodeItem.setNodeKind(ESvnNodeKind.FILE);
//            } else if ("directory".equals(infoEntry.getKind())) {
//                nodeItem.setNodeKind(ESvnNodeKind.DIR);
//            } else {
//                nodeItem.setNodeKind(ESvnNodeKind.NONE);
//            }
//
//            // lock
//            SvnInfoLock infoLock = infoEntry.getLock();
//            if (infoLock != null) {
//
//                // token, owner, comment
//                SVNLock svnLock = infoLock.convertTo(SVNLock.class);
//
//                // lock createdAt
//                svnLock.setCreatedAt(DateUtils.parseDate(infoLock.getCreated()));
//                nodeItem.setLock(svnLock);
//            }
//
//            // commit
//            SvnInfoCommit infoCommit = infoEntry.getCommit();
//            if (infoCommit != null) {
//                long commitRevision = infoCommit.getRevision();
//                SVNCommit commit = new SVNCommit();
//
//                // last changed revision
//                commit.setRevision(commitRevision);
//
//                // last changed author
//                commit.setAuthor(infoCommit.getAuthor());
//
//                // last changed date
//                commit.setDate(DateUtils.parseDate(infoCommit.getDate()));
//
//                // commit message
//                commit.setMessage(getCommitMesssage(fullPath, commitRevision));
//
//                nodeItem.setCommit(commit);
//            }

            return nodeItem;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SvnApiException(e.getMessage());
        }
    }

    @Override
    public String getMimeType(String path) {

        return "demo application/json";
    }

    /**
     * svn info
     *
     * @param path     file or directory path
     * @param revision revision, default is HEAD
     * @return info
     */
    @Override
    public String info(String path, long revision) {
        return null;
    }

    /**
     * @param filePath file path
     * @param revision revision, default is HEAD
     * @return blame string
     */
    @Override
    public String blame(String filePath, long revision) {
        return null;
    }

    /**
     * list directories and files of path
     *
     * @param path directory path
     * @return list
     */
    @Override
    public List<SVNNodeItem> list(String path) {
        return null;
    }

    /**
     * lock the file of path, if path is a directory, lock all of it's children file
     *
     * @param path    directory or file path
     * @param comment lock comment
     * @param force   force to steal the lock from another user or working copy
     * @return boolean
     */
    @Override
    public boolean lock(String path, String comment, boolean force) {
        return false;
    }

    /**
     * unlock the file of path, if path is a directory, unlock all of it's children file
     *
     * @param path  directory or file path
     * @param force force to break the lock
     * @return boolean
     */
    @Override
    public boolean unLock(String path, boolean force) {
        return false;
    }

    /**
     * get lock info of path
     *
     * @param filePath file path
     * @return SVNLock
     */
    @Override
    public SVNLock getLock(String filePath) {
        return null;
    }

    /**
     * get file content of text file
     *
     * @param filePath file path
     * @param revision revision, default is HEAD
     * @return file content
     */
    @Override
    public String getFileContent(String filePath, long revision) {
        return null;
    }

    /**
     * moveRepository source path to dest path
     *
     * @param sourcePath source path
     * @param destPath   dest path
     * @param message    log message
     * @return boolean
     */
    @Override
    public boolean move(String sourcePath, String destPath, String message) {
        return false;
    }

    /**
     * export exports a clean directory tree from the repository
     *
     * @param path     path
     * @param revision revision, default is HEAD
     * @return string
     */
    @Override
    public String export(String path, long revision) {
        return null;
    }

    /**
     * Display local changes or differences between two revisions or paths
     *
     * @param filePath     file path
     * @param olderVersion old revision
     * @param newerVision  new revision
     * @return differences
     */
    @Override
    public String diff(String filePath, long olderVersion, long newerVision) {
        return null;
    }

    public String getCommitMesssage(String path, long revision) {


        return "demo message";
    }
}
