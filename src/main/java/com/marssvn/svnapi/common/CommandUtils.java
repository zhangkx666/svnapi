package com.marssvn.svnapi.common;

import com.marssvn.svnapi.exception.SVNException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

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
    private static Logger logger = Logger.getLogger("CommandUtils");

    /**
     * execute command, windows: cmd,  linux: /bin/sh
     *
     * @param command command
     * @throws IOException IOException
     */
    public static void execute(String command) throws IOException {
        // os
        String os = System.getProperty("os.name").toLowerCase();

        Process process;
        if (os.startsWith("win")) {
            logger.debug("cmd: " + command);
            process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", command});
        } else {
            logger.debug("/bin/sh: " + command);
            process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-ce", command});
        }
        String errorMsg = IOUtils.toString(process.getErrorStream(), System.getProperty("sun.jnu.encoding"));
        if (StringUtils.isNotBlank(errorMsg)) {
            throw new SVNException(errorMsg);
        }
    }
}
