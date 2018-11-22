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
public class SVNRepository {

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

    public SVNRepository(String rootPath) {
        this.rootPath = rootPath;
    }
}
