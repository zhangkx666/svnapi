package com.marssvn.svnapi.common;

import com.marssvn.svnapi.exception.SvnApiException;
import org.apache.commons.io.IOUtils;
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
        execute(command, false);
    }

    /**
     * execute command, windows: cmd,  linux: /bin/sh
     *
     * @param command     command
     * @param ignoreError ignore error, true: ignore,  false: throw SvnApiException
     */
    public static void execute(String command, boolean ignoreError) {
        Process process = null;
        try {
            if (osIsWindows()) {
                command = "cmd /c " + command;
            } else if (osIsLinux()) {
                command = "/bin/sh -ce " + command;
            } else {
                throw new SvnApiException("ECM0001", "Support Windows and Linux only!");
            }

            logger.debug(command);
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            if (!ignoreError) {
                String errorMsg = IOUtils.toString(process.getErrorStream(), Charset.defaultCharset());
                if (StringUtils.isNotBlank(errorMsg)) {
                    throw new SvnApiException(errorMsg);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new SvnApiException(e.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
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
