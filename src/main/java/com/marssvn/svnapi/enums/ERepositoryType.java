package com.marssvn.svnapi.enums;

/**
 * svn repository type
 *
 * @author zhangkx
 */
public enum ERepositoryType {
    /**
     * FSFS
     */
    FSFS("fsfs", "FSFS"),

    /**
     * Berkeley DB
     */
    BDB("bdb", "Berkeley DB");

    private String code;

    /**
     * description
     */
    private String description;

    ERepositoryType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }
}
