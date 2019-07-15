package com.marssvn.svnapi.model;

import lombok.Getter;
import lombok.Setter;

/**
 * svn user
 *
 * @author zhangkx
 */
@Getter
@Setter
public class SvnUser {

    /**
     * user name
     */
    private String username;

    /**
     * password
     */
    private String password;

    public SvnUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getAuthString() {
        return " --username " + this.getUsername() + " --password " + this.getPassword() + " --no-auth-cache --non-interactive";
    }
}
