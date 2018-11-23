package com.marssvn.svnapi;

import java.io.IOException;

/**
 * svn admin interface
 *
 * @author zhangkx
 */
public interface ISVNAdmin {

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
     * @throws IOException IOException
     */
    void moveRepository(String rootPath, String oldRepoName, String newRepoName) throws IOException;

    /**
     * deleteRepository repository
     *
     * @param repoPath repository path
     */
    void deleteRepository(String repoPath);
}
