package com.marssvn.svnapi.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SVNLock extends BaseBean {

    /**
     * SVNLock token
     */
    private String token;

    /**
     * SVNLock owner
     */
    private String owner;

    /**
     * SVNLock comment
     */
    private String comment;

    /**
     * SVNLock created at
     */
    private Date createdAt;
}
