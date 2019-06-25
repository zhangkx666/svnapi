package com.marssvn.svnapi.model;

import com.marssvn.svnapi.enums.ESvnNodeKind;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SVNNodeItem extends BaseBean {

    /**
     * file name
     */
    private String name;

    /**
     * file path
     */
    private String path;

    /**
     * file url
     */
    private String fullPath;

    /**
     * revision
     */
    private long revision;

    /**
     * node kind: DIR, FILE, NONE
     */
    private ESvnNodeKind nodeKind;

    /**
     * last changed
     */
    private SVNCommit commit;

    /**
     * file extension
     */
    private String extension;

    /**
     * mime type
     */
    private String mimeType;

    /**
     * parent path
     */
    private String parentPath;

    /**
     * file size
     */
    private long size;

    /**
     * lockOwner
     */
    private SVNLock lock;

    /**
     * children (dir only)
     */
    private List<SVNNodeItem> children;
}
