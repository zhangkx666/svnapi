package com.marssvn.svnapi.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
public class SVNUser {

    /**
     * user name
     */
    private String username;

    /**
     * password
     */
    private String password;

    public SVNUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getAuthString() {
        return " --username " + this.getUsername() + " --password " + this.getPassword() +
                " --no-auth-cache --non-interactive";
    }
}
