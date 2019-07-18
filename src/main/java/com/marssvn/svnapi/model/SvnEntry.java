package com.marssvn.svnapi.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhangkx
 */
@Getter
@Setter
public class SvnEntry extends BaseBean {

    /**
     * node kind: file, dir
     */
    private String kind;

    /**
     * file or directory name
     */
    private String name;

    /**
     * file path(relative)
     */
    private String path;

    /**
     * parent path
     */
    private String parentPath;

    /**
     * file url(full path)
     */
    private String url;

    /**
     * revision (head revision)
     */
    private long revision;

    /**
     * last changed revision
     */
    private long lastChangedRevision;

    /**
     * commit author
     */
    private String author;

    /**
     * updated date (committed at)
     */
    private Date updatedAt;

    /**
     * file size
     */
    private long size;

    /**
     * file extension
     */
    private String extension;

    /**
     * mime type
     */
    private String mimeType;

    /**
     * last changed
     */
    private SvnCommit commit;

    /**
     * lockOwner
     */
    private SvnLock lock;
}
