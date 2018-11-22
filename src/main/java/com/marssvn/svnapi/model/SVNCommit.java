package com.marssvn.svnapi.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SVNCommit extends BaseBean {

    /**
     * commit revision
     */
    private long revision;

    /**
     * commit author
     */
    private String author;

    /**
     * committed at
     */
    private Date date;

    /**
     * commit message
     */
    private String message;
}
