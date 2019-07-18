package com.marssvn.svnapi.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SvnLock extends BaseBean {

    /**
     * SvnLock token
     */
    private String token;

    /**
     * SvnLock owner
     */
    private String owner;

    /**
     * SvnLock comment
     */
    private String comment;

    /**
     * SvnLock created at
     */
    private Date createdAt;
}
