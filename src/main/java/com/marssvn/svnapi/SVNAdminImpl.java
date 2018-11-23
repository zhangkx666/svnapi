package com.marssvn.svnapi;

import com.marssvn.svnapi.common.CommandUtils;
import com.marssvn.svnapi.common.StringUtils;
import com.marssvn.svnapi.exception.SVNException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * svn admin
 *
 * @author zhangkx
 */
@Component
public class SVNAdminImpl implements ISVNAdmin {

    /**
     * log4j.Logger
     */
    private Logger logger = Logger.getLogger("SVNAdminImpl");

    /**
     * createRepository a new repository
     *
     * @param rootPath       root path, use user.home if null
     * @param repositoryName repository name
     * @return repository path
     */
    @Override
    public String createRepository(String rootPath, String repositoryName) {
        try {
            // svn root path, here we use use home path
            if (StringUtils.isBlank(rootPath)) {
                rootPath = System.getProperty("user.home") + "/svn_reps";
                if (!(new File(rootPath)).exists()) {
                    FileUtils.forceMkdir(new File(rootPath));
                }
            }
            String path = rootPath + "/" + repositoryName;
            String command = "svnadmin create " + path;

            // execute svnadmin createRepository command
            CommandUtils.execute(command);

            // backup svn repository settings
            backupSettings(path);

            // write svnserve.conf
            writeSvnserveConf(path, repositoryName);

            return path;
        } catch (IOException e) {
            e.printStackTrace();
            throw new SVNException(e.getMessage());
        }
    }

    /**
     * moveRepository repository
     *
     * @param rootPath    root path
     * @param oldRepoName old repository name
     * @param newRepoName new repository name
     */
    @Override
    public void moveRepository(String rootPath, String oldRepoName, String newRepoName) throws IOException {
        File oldFolder = new File(rootPath + "/" + oldRepoName);
        File newFolder = new File(rootPath + "/" + newRepoName);
        FileUtils.moveDirectory(oldFolder, newFolder);
    }

    /**
     * deleteRepository repository
     *
     * @param repoPath repository path
     */
    @Override
    public void deleteRepository(String repoPath) {
        try {
            FileUtils.deleteDirectory(new File(repoPath));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * backup svn repository settings
     *
     * @param repoPath repo path
     * @throws IOException IOException
     */
    private void backupSettings(String repoPath) throws IOException {
        String confPath = repoPath + "/conf";

        logger.debug("backup authz -> backup/authz");
        FileUtils.copyFile(new File(confPath + "/authz"), new File(confPath + "/backup/authz"));

        logger.debug("backup passwd -> backup/passwd");
        FileUtils.copyFile(new File(confPath + "/passwd"), new File(confPath + "/backup/passwd"));

        logger.debug("backup svnserve.conf -> backup/svnserve.conf");
        FileUtils.copyFile(new File(confPath + "/svnserve.conf"),
                new File(confPath + "/backup/svnserve.conf"));
    }

    /**
     * write svnserve.conf
     *
     * @param repoPath repository path
     * @param repoName repository name
     * @throws IOException IOException
     */
    private void writeSvnserveConf(String repoPath, String repoName) throws IOException {
        String confPath = repoPath + "/conf/svnserve.conf";

        // write svnserve.conf
        CommandUtils.execute("echo [general]> " + confPath);
        CommandUtils.execute("echo anon-access = none>> " + confPath);
        CommandUtils.execute("echo auth-access = write>> " + confPath);
        CommandUtils.execute("echo password-db = passwd>> " + confPath);
        CommandUtils.execute("echo authz-db = authz>> " + confPath);
        CommandUtils.execute("echo realm = " + repoName + ">> " + confPath);
    }
}
