package com.marssvn.svnapi.common;

import com.marssvn.svnapi.exception.SvnApiException;
import com.marssvn.svnapi.model.SvnUser;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * command utils
 *
 * @author zhangkx
 */
public class CommandUtils {

    /**
     * log4j.Logger
     */
    private static Logger logger = LoggerFactory.getLogger(CommandUtils.class);

    /**
     * windows
     */
    private final static String WIN = "win";

    /**
     * linux
     */
    private final static String LINUX = "linux";

    /**
     * OS name. window / linux
     */
    private static String os;

    static {
        os = System.getProperty("os.name").toLowerCase();
    }

    /**
     * Os is windows
     *
     * @return boolean
     */
    public static boolean osIsWindows() {
        return os.startsWith(WIN);
    }

    /**
     * Os is linux
     *
     * @return boolean
     */
    public static boolean osIsLinux() {
        return os.startsWith(LINUX);
    }

    /**
     * execute command, windows: cmd,  linux: /bin/sh
     *
     * @param command command
     */
    public static void execute(String command) {
        execute(null, command, false);
    }

    /**
     * execute command, windows: cmd,  linux: /bin/sh
     *
     * @param command command
     */
    public static void execute(String command, boolean ignoreError) {
        execute(null, command, ignoreError);
    }

    /**
     * execute command, windows: cmd,  linux: /bin/sh
     *
     * @param command command
     */
    public static void execute(SvnUser svnUser, String command) {
        execute(svnUser, command, false);
    }

    /**
     * execute command, windows: cmd,  linux: /bin/sh
     *
     * @param command     command
     * @param ignoreError ignore error, true: ignore,  false: throw SvnApiException
     */
    public static void execute(SvnUser svnUser, String command, boolean ignoreError) {
//        if (osIsWindows()) {
//            command = "cmd /c " + command;
//        } else if (osIsLinux()) {
//            command = "/bin/sh -ce " + command;
//        } else {
//            throw new SvnApiException("ECM0001", "Support Windows and Linux only!");
//        }
        Process process = executeForProcess(svnUser, command, ignoreError);
        process.destroy();
    }

    private static Process executeForProcess(SvnUser svnUser, String command, boolean ignoreError) {
        Process process = null;
        try {
            // print debug log
            logger.debug("execute command: " + command);

            // add auth string to command if svnUser is not null
            if (svnUser != null) {
                command += svnUser.getAuthString();
            }

            // execute command
            process = Runtime.getRuntime().exec(command);
            process.waitFor();

            // check error
            if (!ignoreError) {
                String errorMsg = IOUtils.toString(process.getErrorStream(), Charset.defaultCharset());
                if (StringUtils.isNotBlank(errorMsg)) {
                    throw new SvnApiException(errorMsg);
                }
            }
            return process;
        } catch (IOException | InterruptedException e) {
            if (process != null) {
                process.destroy();
            }
            throw new SvnApiException(e.getMessage());
        }
    }

    /**
     * execute for Long
     *
     * @param svnUser SvnUser
     * @param command command
     * @return Long
     */
    public static long executeForLong(SvnUser svnUser, String command) throws IOException {
        return Long.valueOf(executeForString(svnUser, command, false));
    }

    /**
     * execute command, windows: cmd,  linux: /bin/sh
     *
     * @param svnUser SvnUser
     * @param command command
     * @return String
     */
    public static String executeForString(SvnUser svnUser, String command) throws IOException {
        return executeForString(svnUser, command, false);
    }

    /**
     * execute command, windows: cmd,  linux: /bin/sh
     *
     * @param svnUser     SvnUser
     * @param command     command
     * @param ignoreError ignore error, true: ignore,  false: throw SvnApiException
     * @return String
     */
    public static String executeForString(SvnUser svnUser, String command, boolean ignoreError) throws IOException {
        Process process = executeForProcess(svnUser, command, ignoreError);
        String result = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());
        process.destroy();
        return result;
    }

    public static Document executeForXmlDocument(SvnUser svnUser, String command) throws IOException,
            DocumentException {
        return executeForXmlDocument(svnUser, command, false);
    }

    public static Document executeForXmlDocument(SvnUser svnUser, String command, boolean ignoreError) throws IOException,
            DocumentException {
        Process process = executeForProcess(svnUser, command, ignoreError);
        SAXReader xmlReader = new SAXReader();
        Document document = xmlReader.read(process.getInputStream());
        process.destroy();
        return document;
    }

    /**
     * kill process by process name
     *
     * @param processName process name
     */
    public static void stopProcess(String processName) {
        if (osIsWindows()) {
            execute("taskkill /F /IM " + processName, true);
        } else if (osIsLinux()) {
            execute("pkill " + processName, true);
        } else {
            throw new SvnApiException("ECM0001", "Support Windows and Linux only!");
        }
    }
}
