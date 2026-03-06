package com.v1no.LJL.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.v1no.LJL.auth_service.config.FeignConfig;
import com.v1no.LJL.auth_service.model.dto.response.GoogleTokenResponse;
import com.v1no.LJL.auth_service.model.dto.response.GoogleUserInfo;

@FeignClient(
    name = "google-oauth-client",
    url = "https://oauth2.googleapis.com",
    configuration = FeignConfig.class
)
public interface GoogleOAuthClient {
    /**
     * Exchange authorization code for access token
     * 
     * @param code Authorization code từ Google
     * @param clientId Google OAuth Client ID
     * @param clientSecret Google OAuth Client Secret
     * @param redirectUri Redirect URI đã config trong Google Console
     * @param grantType Luôn là "authorization_code"
     * @return GoogleTokenResponse chứa access_token, refresh_token, etc.
     */
    @PostMapping("/token")
    GoogleTokenResponse exchangeCodeForToken(
        @RequestParam("code") String code,
        @RequestParam("client_id") String clientId,
        @RequestParam("client_secret") String clientSecret,
        @RequestParam("redirect_uri") String redirectUri,
        @RequestParam("grant_type") String grantType
    );

    /**
     * Get user info from Google
     * 
     * @param authorization Bearer token (access_token từ Google)
     * @return GoogleUserInfo chứa thông tin user
     */
    @GetMapping("/oauth2/v2/userinfo")
    GoogleUserInfo getUserInfo(
        @RequestHeader("Authorization") String authorization
    );
}
