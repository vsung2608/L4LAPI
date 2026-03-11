package com.v1no.LJL.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.v1no.LJL.auth_service.config.FeignConfig;
import com.v1no.LJL.auth_service.model.dto.response.GoogleUserInfo;

@FeignClient(
    name = "google-userinfo-client",
    url = "https://www.googleapis.com",
    configuration = FeignConfig.class
)
public interface GoogleUserInfoClient {

    @GetMapping("/oauth2/v2/userinfo")
    GoogleUserInfo getUserInfo(
        @RequestHeader("Authorization") String authorization
    );
}
