package com.marssvn.svnapi;

import com.marssvn.svnapi.enums.ERepositoryType;

import java.io.IOException;

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
     * @throws IOException IOException
     */
    String createRepository(String rootPath, String repoName, ERepositoryType repoType) throws IOException;

    /**
     * createRepository a new repository
     *
     * @param repoName repository name
     * @return repository path
     * @throws IOException IOException
     */
    default String createRepository(String repoName) throws IOException {
        return createRepository(null, repoName, null);
    }

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
     * moveRepository repository
     *
     * @param oldRepoName old repository name
     * @param newRepoName new repository name
     * @throws IOException IOException
     */
    default void moveRepository(String oldRepoName, String newRepoName) throws IOException {
        moveRepository(null, oldRepoName, newRepoName);
    }

    /**
     * deleteRepository repository
     *
     * @param rootPath root path
     * @param repoName repository name
     * @throws IOException IOException
     */
    void deleteRepository(String rootPath, String repoName) throws IOException;

    /**
     * deleteRepository repository
     *
     * @param repoName repository name
     * @throws IOException IOException
     */
    default void deleteRepository(String repoName) throws IOException {
        deleteRepository(null, repoName);
    }

    /**
     * restart svnserve
     * @param rootPath root path
     * @throws IOException IOException
     */
    void restartSvnserve(String rootPath) throws IOException;
}
