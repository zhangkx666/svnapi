package com.marssvn.svnapi.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhangkx
 */
@Getter
@Setter
public class LogEntry {

    /**
     * commit revision
     */
    private long revision;

    /**
     * commit commitAuthor
     */
    private String author;

    /**
     * commit date
     */
    private Date date;

    /**
     * commit message
     */
    private String message;
}
