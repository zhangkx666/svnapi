package com.marssvn.svnapi;

import com.marssvn.svnapi.common.CommandUtils;
import com.marssvn.svnapi.common.StringUtils;
import com.marssvn.svnapi.enums.ERepositoryType;
import com.marssvn.svnapi.exception.SvnApiException;
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
public class SvnAdminForLocale implements ISvnAdmin {

    /**
     * slf4j.Logger
     */
    private Logger logger = LoggerFactory.getLogger(SvnAdminForLocale.class);

    /**
     * default svn root path
     */
    private static String defaultSvnRootPath;

    static {
        defaultSvnRootPath = System.getProperty("user.home") + "/svn";
    }

    /**
     * createRepository a new repository
     *
     * @param rootPath root path, use user.home if null
     * @param repoName repository name
     * @param repoType repository type, fsfs: FSFS(default), bdb: Berkeley DB
     * @return repository path
     */
    @Override
    public String createRepository(String rootPath, String repoName, ERepositoryType repoType) throws IOException {

        // svn root path, here we use use home path
        if (StringUtils.isBlank(rootPath)) {
            rootPath = defaultSvnRootPath;
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

        logger.info("Create repository: " + repoName);
        CommandUtils.execute(command);

        // backup svn repository settings
        backupSettings(repoPath);

        // write svnserve.conf
        writeSvnserveConf(repoPath, repoName);

        return StringUtils.backslash2Slash(repoPath);
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
        if (StringUtils.isBlank(rootPath)) {
            rootPath = defaultSvnRootPath;
        }
        File oldFolder = new File(rootPath + "/" + oldRepoName);
        File newFolder = new File(rootPath + "/" + newRepoName);
        logger.info("Move repository: " + oldFolder + " -> " + newFolder);
        FileUtils.moveDirectory(oldFolder, newFolder);
    }

    /**
     * deleteRepository repository
     *
     * @param rootPath root path
     * @param repoName repository name
     * @throws IOException IOException
     */
    @Override
    public void deleteRepository(String rootPath, String repoName) throws IOException {
        if (StringUtils.isBlank(rootPath)) {
            rootPath = defaultSvnRootPath;
        }
        logger.info("Delete repository: " + repoName);
        FileUtils.deleteDirectory(new File(rootPath + "/" + repoName));
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
    private void writeSvnserveConf(String repoPath, String repoName) throws IOException {

        // write svnserve.conf
        String confPath = repoPath + "/conf/svnserve.conf";
        String confText = "[general]\n";
        confText += "anon-access = none\n";
        confText += "auth-access = write\n";
        confText += "password-db = passwd\n";
        confText += "authz-db = authz\n";
        confText += "realm = " + repoName;
        FileUtils.writeStringToFile(new File(confPath), confText, "UTF-8");

        // write authz
        String authzPath = repoPath + "/conf/authz";
        String authzText = "[/]\nmarssvn = rw";
        FileUtils.writeStringToFile(new File(authzPath), authzText, "UTF-8");

        // write passwd
        String passwdPath = repoPath + "/conf/passwd";
        String passwdText = "[users]\nmarssvn = marssvn";
        FileUtils.writeStringToFile(new File(passwdPath), passwdText, "UTF-8");
    }

    /**
     * restart svnserve
     *
     * @param rootPath root path
     */
    @Override
    public void restartSvnserve(String rootPath) {

        // stop svnserve process
        CommandUtils.stopProcess("svnserve");

        if (StringUtils.isBlank(rootPath)) {
            rootPath = defaultSvnRootPath;
        }
        if (CommandUtils.osIsLinux()) {
            CommandUtils.execute("svnserve -d -r " + rootPath);
        } else if (CommandUtils.osIsWindows()) {
            throw new SvnApiException("EA0001", "Please restart the svnserve service manually on Windows.");
        }
    }
}
