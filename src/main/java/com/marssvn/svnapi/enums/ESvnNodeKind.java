package com.marssvn.svnapi.enums;

/**
 * svn node kind
 *
 * @author zhangkx
 */
public enum ESvnNodeKind {

    /**
     * directory
     */
    DIR("dir"),

    /**
     * file
     */
    FILE("file");

    private String value;

    ESvnNodeKind(String value) {
        this.value = value;
    }

    public boolean equalsValue(String kind) {
        return this.value.equals(kind);
    }
}
