package com.marssvn.svnapi.model;

import lombok.Getter;
import lombok.Setter;

/**
 * svn repository
 *
 * @author zhangkx
 */
@Getter
@Setter
public class SvnRepository {

    /**
     * repository rootPath
     */
    private String rootPath;

    /**
     * repository UUID
     */
    private String uuid;

    /**
     * head revision
     */
    private long headRevision;

    /**
     * admin account
     */
    private String adminAccount;

    /**
     * admin password
     */
    private String adminPassword;
}
