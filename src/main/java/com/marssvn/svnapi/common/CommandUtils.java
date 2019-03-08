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
     * execute command, windows: cmd,  linux: /bin/sh
     *
     * @param command command
     */
    public static void execute(String command) {
        try {
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
                throw new SvnException(errorMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new SvnException(e.getMessage());
        }
    }
}
