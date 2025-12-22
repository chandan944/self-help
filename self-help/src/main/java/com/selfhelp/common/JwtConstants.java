package com.selfhelp.common;

public class JwtConstants {

    public static final String SECRET_KEY =
            "super-secret-key-change-this-in-production-please";

    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 hours
}
