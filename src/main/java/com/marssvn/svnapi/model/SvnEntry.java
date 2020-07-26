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
     * file fullPath(full path)
     */
    private String fullPath;

    /**
     * headRevision (head headRevision)
     */
    private long headRevision;

    /**
     * last changed revision
     */
    private long commitRevision;

    /**
     * commit commitAuthor
     */
    private String commitAuthor;

    /**
     * updated date (committed at)
     */
    private Date commitDate;

    /**
     * commit message
     */
    private String commitMessage;

    /**
     * file size (byte)
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
     * lockOwner
     */
    private SvnLock lock;

//    /**
//     * get extension
//     *
//     * @return extension. eg. xls, pdf, jpg
//     */
//    public String getExtension() {
//        if (StringUtils.isBlank(this.name)) {
//            return "";
//        }
//        return this.name.substring(this.name.lastIndexOf(".") + 1);
//    }
}
