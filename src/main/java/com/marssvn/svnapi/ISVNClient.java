package com.marssvn.svnapi;

import com.marssvn.svnapi.model.SVNLock;
import com.marssvn.svnapi.model.SVNNodeItem;

import java.util.List;

/**
 * SVN client interface
 *
 * @author zhangkx
 */
public interface ISVNClient {

    /**
     * make directory, will make parent directories also
     *
     * @param dirPath directory path
     * @param message message
     */
    void mkdir(String dirPath, String message);

    /**
     * get head revision of path
     *
     * @param path path
     * @return head revision
     */
    long headRevision(String path);

    /**
     * get file mime type
     *
     * @param filePath file path
     * @return mime type
     */
    String getMimeType(String filePath);

    /**
     * svn info
     *
     * @param path     file or directory path
     * @param revision revision, default is HEAD
     * @return info
     */
    String info(String path, long revision);

    /**
     * @param filePath file path
     * @param revision revision, default is HEAD
     * @return blame string
     */
    String blame(String filePath, long revision);

    /**
     * list directories and files of path
     *
     * @param path directory path
     * @return list
     */
    List<SVNNodeItem> list(String path);

    /**
     * lock the file of path, if path is a directory, lock all of it's children file
     *
     * @param path    directory or file path
     * @param comment lock comment
     * @param force   force to steal the lock from another user or working copy
     * @return
     */
    boolean lock(String path, String comment, boolean force);

    /**
     * unlock the file of path, if path is a directory, unlock all of it's children file
     *
     * @param path  directory or file path
     * @param force force to break the lock
     * @return
     */
    boolean unLock(String path, boolean force);

    /**
     * get lock info of path
     *
     * @param filePath file path
     * @return SVNLock
     */
    SVNLock getLock(String filePath);

    /**
     * get file content of text file
     *
     * @param filePath file path
     * @param revision revision, default is HEAD
     * @return file content
     */
    String getFileContent(String filePath, long revision);


    /**
     * moveRepository source path to dest path
     *
     * @param sourcePath source path
     * @param destPath   dest path
     * @param message    log message
     * @return
     */
    boolean move(String sourcePath, String destPath, String message);

    /**
     * export exports a clean directory tree from the repository
     *
     * @param path     path
     * @param revision revision, default is HEAD
     * @return
     */
    String export(String path, long revision);

    /**
     * Display local changes or differences between two revisions or paths
     *
     * @param filePath     file path
     * @param olderVersion old revision
     * @param newerVision  new revision
     * @return differences
     */
    String diff(String filePath, long olderVersion, long newerVision);


}
