package com.marssvn.svnapi.exception;

/**
 * SvnException
 *
 * @author zhangkx
 */
public class SvnException extends RuntimeException {
    /**
     * errorCode
     */
    private String errorCode;

    /**
     * get errorCode
     *
     * @return string
     */
    public String getErrorCode() {
        return this.errorCode;
    }

    /**
     * New com.marssvn.svnapi.exception.SvnException by message string
     *
     * @param message message
     */
    public SvnException(String message) {
        super(message);
    }

    /**
     * New com.marssvn.svnapi.exception.SvnException by errorCode and message
     *
     * @param errorCode error code
     * @param message   message
     */
    public SvnException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
