package com.marssvn.svnapi.common;

/**
 * String utils
 * @author zhangkx
 */
public class StringUtils {

    public final static String SLASH = "/";

   /**
     * check if the str is empty
     *
     * @param str string
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * check if the str is not empty
     *
     * @param str string
     * @return boolean
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * check if the str is blank
     *
     * @param str string
     * @return boolean
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * check if the str is not blank
     *
     * @param str string
     * @return boolean
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * convert backslash to Slash
     * eg. C:\windows\log to /C:/windows/log
     *
     * @param path path
     * @return linux path
     */
    public static String backslash2Slash(String path) {
        if (path == null) {
            return null;
        }
        if (!path.startsWith(SLASH)) {
            path = SLASH + path;
        }
        return path.replace("\\", SLASH);
    }
}
