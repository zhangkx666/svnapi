package com.marssvn.svnapi;

import com.marssvn.svnapi.model.SvnRepository;

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
     * @param svnRepository SvnRepository
     * @return repository path
     * @throws IOException IOException
     */
    SvnRepository createRepository(SvnRepository svnRepository);

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
     * restart svnserve service
     *
     * @param rootPath root path
     * @throws IOException IOException
     */
    void restartSvnService(String rootPath) throws IOException;
}
