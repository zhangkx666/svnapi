package com.marssvn.svnapi.exception;

public class SVNException  extends RuntimeException {
    /**
     * errorCode
     */
    private String errorCode;

    /**
     * get errorCode
     * @return string
     */
    public String getErrorCode() {
        return this.errorCode;
    }

    /**
     * New com.marssvn.svnapi.exception.SVNException by message string
     * @param message message
     */
    public SVNException(String message) {
        super(message);
    }

    /**
     * New com.marssvn.svnapi.exception.SVNException by errorCode and message
     * @param message message
     */
    public SVNException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
