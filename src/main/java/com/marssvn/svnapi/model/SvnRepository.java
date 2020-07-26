package com.marssvn.svnapi.model;

import com.marssvn.svnapi.common.StringUtils;
import com.marssvn.svnapi.enums.ERepositoryType;
import com.marssvn.svnapi.enums.ESvnProtocol;
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
     * repository name
     */
    private String name;

    /**
     * repository UUID
     */
    private String uuid;

    /**
     * repository type
     */
    private ERepositoryType repositoryType;

    /**
     * svn protocol
     */
    private ESvnProtocol svnProtocol;

    /**
     * repository rootPathLocal
     */
    private String rootPathLocal;

    /**
     * head headRevision
     */
    private long headRevision;

    /**
     * admin account
     */
    private SvnUser adminUser;

    /**
     * Get Root Path, if root path is blank, return System.getProperty("user.home") + "/svn"
     *
     * @return Root Path
     */
    public String getRootPathLocal() {
        if (StringUtils.isBlank(rootPathLocal)) {
            rootPathLocal = System.getProperty("user.home") + "/svn";
        }
        return rootPathLocal;
    }

    /**
     * get local full path
     *
     * @return full path (local)
     */
    public String getFullPathLocal() {
        return StringUtils.fixFileSeparatorChar(this.getRootPathLocal() + "/" + name);
    }
}
