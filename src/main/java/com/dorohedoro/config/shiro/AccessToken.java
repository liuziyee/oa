package com.dorohedoro.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class AccessToken implements AuthenticationToken {

    private String accessToken;

    public AccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    @Override
    public Object getPrincipal() {
        return accessToken;
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }
}
