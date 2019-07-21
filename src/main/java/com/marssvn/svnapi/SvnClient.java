package com.marssvn.svnapi;

import com.marssvn.svnapi.common.CommandUtils;
import com.marssvn.svnapi.common.DateUtils;
import com.marssvn.svnapi.common.StringUtils;
import com.marssvn.svnapi.enums.ESvnNodeKind;
import com.marssvn.svnapi.exception.SvnApiException;
import com.marssvn.svnapi.model.SvnEntry;
import com.marssvn.svnapi.model.SvnLock;
import com.marssvn.svnapi.model.SvnUser;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
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
public class SvnClient implements ISvnClient {

    /**
     * slf4j.Logger
     */
    private Logger logger = LoggerFactory.getLogger(SvnClient.class);

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
     * @param path    directory path
     * @param message commit message
     */
    @Override
    public void mkdir(String path, String message) {
        // execute svn mkdir command
        String fullPath = getFullPath(path);
        String command = "svn mkdir " + fullPath + " -q -m \"" + message + "\" --parents";
        CommandUtils.execute(svnUser, command);
        logger.info("mkdir: " + fullPath);
    }

    /**
     * do base check
     * throw SvnApiException when svnUser or rootPath is null
     */
    private void doBaseCheck(String path) {
        if (this.svnUser == null) {
            throw new SvnApiException("EC0001", "SVN user is required");
        }
        if (StringUtils.isBlank(this.rootPath)) {
            throw new SvnApiException("EC0002", "SVN root path is required");
        }
        if (StringUtils.isBlank(path)) {
            throw new SvnApiException("EC0003", "Path is required");
        }
    }

    /**
     * get full path
     *
     * @param path relative path
     * @return full path
     */
    private String getFullPath(String path) {
        doBaseCheck(path);
        return this.rootPath + "/" + path;
    }

    /**
     * get root path
     *
     * @return root path
     */
    private String getRootPath() {
        doBaseCheck(this.rootPath);
        return this.rootPath;
    }

    /**
     * get head revision
     *
     * @return head revision
     */
    @Override
    public long headRevision() {
        try {
            String command = "svn info " + getRootPath() + " --show-item revision --no-newline";
            return CommandUtils.executeForLong(svnUser, command);
        } catch (IOException e) {
            throw new SvnApiException(e.getMessage());
        }
    }

    /**
     * get last changed revision
     *
     * @param path path
     * @return last changed revision
     */
    @Override
    public long lastChangedRevision(String path) {
        try {
            String command = "svn info " + getFullPath(path) + " --show-item last-changed-revision --no-newline";
            return CommandUtils.executeForLong(svnUser, command);
        } catch (IOException e) {
            throw new SvnApiException(e.getMessage());
        }
    }

    /**
     * get the document list of path
     * svn command: svn list
     *
     * @param path     relative path
     * @param revision revision
     * @return entry list
     */
    @Override
    public List<SvnEntry> list(String path, long revision) {
        try {
            // get head revision
            long headRevision = headRevision();

            // file revision
            String rev = revision == -1 ? "HEAD" : String.valueOf(revision);

            // full path
            String parentFullPath = StringUtils.isBlank(path) ? this.rootPath : getFullPath(path);

            // command
            String command = "svn list " + parentFullPath + " --xml -r " + rev;

            Document document = CommandUtils.executeForXmlDocument(svnUser, command);
            Element rootElement = document.getRootElement().element("list");

            // <list><entry></entry></list>
            List<SvnEntry> list = new ArrayList<>();
            rootElement.elements().forEach(entry -> {
                SvnEntry svnEntry = new SvnEntry();
                svnEntry.setKind(entry.attributeValue("kind"));
                String entryName = entry.elementText("name");
                svnEntry.setName(entryName);
                svnEntry.setParentPath(parentFullPath);
                if (StringUtils.isBlank(path)) {
                    svnEntry.setPath(entryName);
                } else {
                    svnEntry.setPath(path + "/" + entryName);
                }
                svnEntry.setFullPath(parentFullPath + "/" + entryName);
                Element commitElement = entry.element("commit");
                svnEntry.setRevision(headRevision);
                svnEntry.setCommitRevision(Long.valueOf(commitElement.attributeValue("revision")));
                svnEntry.setCommitAuthor(commitElement.elementText("commitAuthor"));
                svnEntry.setCommitDate(DateUtils.parseDate(commitElement.elementText("date")));

                if (ESvnNodeKind.FILE.equalsValue(svnEntry.getKind())) {
                    svnEntry.setSize(Long.valueOf(entry.elementText("size")));
                    svnEntry.setExtension(entryName.substring(entryName.lastIndexOf(".") + 1));
                }

                // svn lock
                Element lockElement = entry.element("lock");
                if (lockElement != null && lockElement.hasContent()) {
                    SvnLock svnLock = new SvnLock();
                    svnLock.setToken(lockElement.elementText("token"));
                    svnLock.setOwner(lockElement.elementText("owner"));
                    svnLock.setComment(lockElement.elementText("comment"));
                    svnLock.setCreatedAt(DateUtils.parseDate(lockElement.elementText("created")));
                    svnEntry.setLock(svnLock);
                }
                list.add(svnEntry);
            });
            return list;
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * get file by path
     *
     * @param path     file path
     * @param revision file revision
     * @return SvnEntry
     */
    public SvnEntry getFile(String path, long revision) {
        try {

            // file revision
            String rev = revision == -1 ? "HEAD" : String.valueOf(revision);

            // file full path
            String fullPath = getFullPath(path);

            // command
            String command = "svn info " + fullPath + " --xml -r " + rev + this.svnUser.getAuthString();
            Process process = Runtime.getRuntime().exec(command);

            // serialize xml to SvnEntry
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
            SvnEntry nodeItem = new SvnEntry();
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
//                SvnLock svnLock = infoLock.convertTo(SvnLock.class);
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
//                SvnCommit commit = new SvnCommit();
//
//                // last changed revision
//                commit.setRevision(commitRevision);
//
//                // last changed commitAuthor
//                commit.setCommitAuthor(infoCommit.getCommitAuthor());
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
     * @return SvnLock
     */
    @Override
    public SvnLock getLock(String filePath) {
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
