package com.marssvn.svnapi.common;

import com.marssvn.svnapi.exception.SvnException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
     * execute command, windows: cmd,  linux: /bin/sh
     *
     * @param command command
     */
    public static void execute(String command) {
        Process process = null;
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.startsWith(WIN)) {
                command = "cmd /c " + command;

            } else if (os.startsWith(LINUX)) {
                command = "/bin/sh -ce " + command;
            } else {
                throw new SvnException("Support Windows and Linux only!");
            }

            logger.debug(command);
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            String errorMsg = IOUtils.toString(process.getErrorStream(), "UTF-8");
            if (StringUtils.isNotBlank(errorMsg)) {
                throw new SvnException(errorMsg);
            }
        } catch (IOException | InterruptedException e) {
            throw new SvnException(e.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
