package com.marssvn.svnapi;

import com.marssvn.svnapi.enums.ERepositoryType;

/**
 * svn admin interface
 *
 * @author zhangkx
 */
public interface ISvnAdmin {

    /**
     * createRepository a new repository
     *
     * @param rootPath root path, use user.home if null
     * @param repoName repository name
     * @param repoType repository type, fsfs: FSFS(default), bdb: Berkeley DB
     * @return repository path
     */
    String createRepository(String rootPath, String repoName, ERepositoryType repoType);

    /**
     * moveRepository repository
     *
     * @param rootPath    root path
     * @param oldRepoName old repository name
     * @param newRepoName new repository name
     */
    void moveRepository(String rootPath, String oldRepoName, String newRepoName);

    /**
     * deleteRepository repository
     *
     * @param repoPath repository path
     */
    void deleteRepository(String repoPath);
}
