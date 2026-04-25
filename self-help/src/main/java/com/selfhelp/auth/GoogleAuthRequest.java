package com.selfhelp.auth;

import lombok.Data;

@Data
public class GoogleAuthRequest {
    private String IdToken; // Contains Google access token from Expooo
    private String email;
    private String name;
    private String imageUrl;
}
