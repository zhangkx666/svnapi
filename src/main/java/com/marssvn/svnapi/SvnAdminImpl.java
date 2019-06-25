package com.marssvn.svnapi;

import com.marssvn.svnapi.common.CommandUtils;
import com.marssvn.svnapi.common.StringUtils;
import com.marssvn.svnapi.enums.ERepositoryType;
import com.marssvn.svnapi.exception.SvnException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * svn admin
 *
 * @author zhangkx
 */
@Component
public class SvnAdminImpl implements ISvnAdmin {

    /**
     * slf4j.Logger
     */
    private Logger logger = LoggerFactory.getLogger(SvnAdminImpl.class);

    /**
     * createRepository a new repository
     *
     * @param rootPath root path, use user.home if null
     * @param repoName repository name
     * @param repoType repository type, fsfs: FSFS(default), bdb: Berkeley DB
     * @return repository path
     */
    @Override
    public String createRepository(String rootPath, String repoName, ERepositoryType repoType) {
        try {
            // svn root path, here we use use home path
            if (StringUtils.isBlank(rootPath)) {
                rootPath = System.getProperty("user.home") + "/svn";
            }

            // if repository root path not exists, mkdir
            File repoRoot = new File(rootPath);
            if (!repoRoot.exists()) {
                FileUtils.forceMkdir(repoRoot);
            }

            // execute svnadmin create command
            String repoPath = rootPath + "/" + repoName;
            String command = "svnadmin create " + repoPath;

            if (repoType != null) {
                command += " --fs-type " + repoType.getCode();
            }

            CommandUtils.execute(command);

            // backup svn repository settings
            backupSettings(repoPath);

            // write svnserve.conf
            writeSvnserveConf(repoPath, repoName);

            return StringUtils.backslash2Slash(repoPath);
        } catch (IOException e) {
            throw new SvnException(e.getMessage());
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
    public void moveRepository(String rootPath, String oldRepoName, String newRepoName) {
        if (StringUtils.isBlank(rootPath)) {
            rootPath = System.getProperty("user.home") + "/svn";
        }
        try {
            File oldFolder = new File(rootPath + "/" + oldRepoName);
            File newFolder = new File(rootPath + "/" + newRepoName);
            FileUtils.moveDirectory(oldFolder, newFolder);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SvnException(e.getMessage());
        }
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
            e.printStackTrace();
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
        FileUtils.copyFile(new File(confPath + "/svnserve.conf"), new File(confPath + "/backup/svnserve.conf"));
    }

    /**
     * write svnserve.conf
     *
     * @param repoPath repository path
     * @param repoName repository name
     */
    private void writeSvnserveConf(String repoPath, String repoName) {

        // write svnserve.conf
        String confPath = repoPath + "/conf/svnserve.conf";
        CommandUtils.execute("echo [general]> " + confPath);
        CommandUtils.execute("echo anon-access = none>> " + confPath);
        CommandUtils.execute("echo auth-access = write>> " + confPath);
        CommandUtils.execute("echo password-db = passwd>> " + confPath);
        CommandUtils.execute("echo authz-db = authz>> " + confPath);
        CommandUtils.execute("echo realm = " + repoName + ">> " + confPath);

        // write authz
        String authzPath = repoPath + "/conf/authz";
        CommandUtils.execute("echo [/]> " + authzPath);
        CommandUtils.execute("echo marssvn = rw> " + authzPath);

        // write passwd
        String passwdPath = repoPath + "/conf/passwd";
        CommandUtils.execute("echo [users]> " + passwdPath);
        CommandUtils.execute("echo marssvn = 123456> " + passwdPath);
    }
}
