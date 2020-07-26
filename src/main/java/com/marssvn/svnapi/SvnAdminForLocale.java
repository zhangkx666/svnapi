package com.marssvn.svnapi;

import com.marssvn.svnapi.common.CommandUtils;
import com.marssvn.svnapi.common.StringUtils;
import com.marssvn.svnapi.enums.ESvnProtocol;
import com.marssvn.svnapi.exception.SvnApiException;
import com.marssvn.svnapi.model.SvnRepository;
import com.marssvn.svnapi.model.SvnUser;
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
     * @param svnRepository SvnRepository
     * @return repository path
     */
    @Override
    public SvnRepository createRepository(SvnRepository svnRepository) {
        try {

            // if repository root path not exists, mkdir
            File repoRoot = new File(svnRepository.getRootPathLocal());
            if (!repoRoot.exists()) {
                FileUtils.forceMkdir(repoRoot);
            }

            // execute svnadmin create command
            String repoName = svnRepository.getName();
            String repoPathLocal = svnRepository.getFullPathLocal();
            String command = "svnadmin create \"" + StringUtils.fixFileSeparatorChar(repoPathLocal) + "\"";

            if (svnRepository.getRepositoryType() != null) {
                command += " --fs-type " + svnRepository.getRepositoryType().getCode();
            }

            logger.info("Create repository: " + repoName);
            CommandUtils.executeAsync(command);

            // backup svn repository settings
            backupSettings(repoPathLocal);

            // svn admin
            SvnUser svnAdmin = new SvnUser("marssvn", StringUtils.createRandomPassword(16));
            svnRepository.setAdminUser(svnAdmin);
            logger.debug("password: " + svnAdmin.getPassword());

            // write svnserve.conf
            writeSvnserveConf(svnRepository);

            svnRepository.setSvnProtocol(ESvnProtocol.SVN);
            return svnRepository;

        } catch (IOException e) {
            throw new SvnApiException("EA0001", e.getMessage());
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
            rootPath = defaultSvnRootPath;
        }
        File oldFolder = new File(rootPath + "/" + oldRepoName);
        File newFolder = new File(rootPath + "/" + newRepoName);
        try {
            logger.info("Move repository: " + oldFolder + " -> " + newFolder);
            FileUtils.moveDirectory(oldFolder, newFolder);
        } catch (IOException e) {
            throw new SvnApiException("EA0002", e.getMessage());
        }
    }

    /**
     * deleteRepository repository
     *
     * @param rootPath root path
     * @param repoName repository name
     */
    @Override
    public void deleteRepository(String rootPath, String repoName) {
        if (StringUtils.isBlank(rootPath)) {
            rootPath = defaultSvnRootPath;
        }
        try {
            logger.info("Delete repository: " + repoName);
            FileUtils.deleteDirectory(new File(StringUtils.fixFileSeparatorChar(rootPath + "/" + repoName)));
        } catch (IOException e) {
            throw new SvnApiException("EA0003", e.getMessage());
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
     * @param svnRepository SvnRepository
     */
    private void writeSvnserveConf(SvnRepository svnRepository) throws IOException {

        // write svnserve.conf
        String repoPath = svnRepository.getFullPathLocal();
        String confPath = repoPath + "/conf/svnserve.conf";
        String confText = "[general]\n";
        confText += "anon-access = none\n";
        confText += "auth-access = write\n";
        confText += "password-db = passwd\n";
        confText += "authz-db = authz\n";
        confText += "realm = " + svnRepository.getName();
        FileUtils.writeStringToFile(new File(confPath), confText, "UTF-8");

        // write authz
        String authzPath = repoPath + "/conf/authz";
        String authzText = "[/]\nmarssvn = rw";
        FileUtils.writeStringToFile(new File(authzPath), authzText, "UTF-8");

        // write passwd
        String passwdPath = repoPath + "/conf/passwd";
        String passwdText = "[users]\nmarssvn = " + svnRepository.getAdminUser().getPassword();
        FileUtils.writeStringToFile(new File(passwdPath), passwdText, "UTF-8");
    }

    /**
     * restart svnserve service
     *
     * @param rootPath root path
     */
    @Override
    public void restartSvnService(String rootPath) {

        if (StringUtils.isBlank(rootPath)) {
            rootPath = defaultSvnRootPath;
        }
        if (CommandUtils.osIsLinux()) {
            CommandUtils.execute("pkill svnserve", 5000);
            CommandUtils.execute("svnserve -d -r \"" + rootPath + "\"");
        } else if (CommandUtils.osIsWindows()) {
            throw new SvnApiException("EA0004", "Please restart the svnserve service manually on Windows.");
        }
    }
}
