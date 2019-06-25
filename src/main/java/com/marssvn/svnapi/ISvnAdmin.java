package com.marssvn.svnapi;

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
     * @return repository path
     */
    String createRepository(String rootPath, String repoName);

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