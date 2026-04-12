package com.v1no.LJL.api_gateway.config;

public class EndPoints {
    public static String[] ADMIN_POST_API = new String[] {
        "/actuator/**",
        "/api/v1/decks",
        "/api/v1/decks/**",
        "/api/v1/cards",
        "/api/v1/cards/**",
        "/api/v1/lessons/**",
        "/api/v1/categories/**",
        "/api/v1/sentencess/**",
        "/api/v1/articles/**",
        "/api/v1/article-categories/**"
    };

    public static String[] ADMIN_PUT_API = new String[] {
        "/api/v1/lessons/**",
        "/api/v1/decks",
        "/api/v1/decks/**",
        "/api/v1/cards",
        "/api/v1/cards/**",
        "/api/v1/categories/**",
        "/api/v1/sentencess/**",
        "/api/v1/articles/**",
        "/api/v1/article-categories/**"
    };

    public static String[] ADMIN_DELETE_API = new String[] {
        "/api/v1/decks",
        "/api/v1/decks/**",
        "/api/v1/cards",
        "/api/v1/cards/**",
        "/api/v1/lessons/**",
        "/api/v1/categories/**",
        "/api/v1/sentencess/**",
        "/api/v1/articles/**",
        "/api/v1/article-categories/**"
    };

    public static String[] PUBLIC_GET_API = new String[] {
        "/api/v1/decks",
        "/api/v1/decks/**",
        "/api/v1/cards",
        "/api/v1/cards/**",
        "/api/v1/lessons/**",
        "/api/v1/categories/**",
        "/api/v1/sentencess/**",
        "/api/v1/articles/**",
        "/api/v1/article-categories/**",
        "/api/v1/plans/**",
        "/api/v1/plans",
        "/api/v1/payment/vnpay-return"
    };
}