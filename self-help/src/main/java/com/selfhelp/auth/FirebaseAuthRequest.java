package com.selfhelp.auth;

import lombok.Data;

@Data
public class FirebaseAuthRequest {
    private String firebaseIdToken;
    private String email;
    private String name;
    private String imageUrl;
}