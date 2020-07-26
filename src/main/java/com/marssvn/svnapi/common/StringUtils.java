package com.marssvn.svnapi.common;

import java.io.File;

/**
 * String utils
 *
 * @author zhangkx
 */
public class StringUtils {

    public final static String SLASH = "/";

    public static final char SLASH_CHAR = '/';
    public static final char BACKSLASH_CHAR = '\\';

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
     * Fixes the file separator char for the target platform
     * using the following replacement.
     *
     * <ul>
     * <li>'/' &#x2192; File.separatorChar</li>
     * <li>'\\' &#x2192; File.separatorChar</li>
     * </ul>
     *
     * @param arg the argument to fix
     * @return the transformed argument
     */
    public static String fixFileSeparatorChar(final String arg) {
        return arg.replace(SLASH_CHAR, File.separatorChar).replace(BACKSLASH_CHAR, File.separatorChar);
    }

    /**
     * Get random password
     * @param length password length
     * @return random password
     */
    public static String createRandomPassword(int length) {
        StringBuilder password = null;
        String baseStr = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+{}|<>?";
        boolean flag = false;
        while (!flag) {
            password = new StringBuilder();
            int a = 0, b = 0, c = 0, d = 0;
            for (int i = 0; i < length; i++) {
                int rand = (int) (Math.random() * baseStr.length());
                password.append(baseStr.charAt(rand));
                if (rand < 10) {
                    a++;
                }
                if (10 <= rand && rand < 36) {
                    b++;
                }
                if (36 <= rand && rand < 62) {
                    c++;
                }
                if (62 <= rand) {
                    d++;
                }
            }
            flag = (a * b * c * d != 0);
        }
        return password.toString();
    }
}
