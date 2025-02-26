package com.example.controller;

import com.example.dto.OauthRequest;
import com.example.exception.OAuth2Exception;
import com.example.model.User;
import com.example.service.OAuth2Service;
import com.example.service.UserService;
import com.example.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {
    private final OAuth2Service googleOAuth2Service;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public OAuth2Controller(
            OAuth2Service googleOAuth2Service,
            UserService userService,
            JwtUtil jwtUtil
    ) {
        this.googleOAuth2Service = googleOAuth2Service;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    @GetMapping("callback/google")
    public ResponseEntity<String> callback(){
        return ResponseEntity.ok().body("respond");
    }
    @PostMapping("callback/google")
    public ResponseEntity<?> handleGoogleCallback(@RequestBody OauthRequest oauthRequest) {
        try {
            String code = oauthRequest.getCode();

            // 1. 用 code 換取 access token
            String accessToken = googleOAuth2Service.exchangeCodeForToken(code);

            // 2. 獲取用戶信息
            Map<String, Object> userInfo = googleOAuth2Service.getUserProfile(accessToken);

            // 3. 查找或創建用戶
            User user = userService.findOrCreateGoogleUser(userInfo);

            // 4. 生成 JWT
            String token = jwtUtil.generateToken(user);

            // 5. 設定 Cookie
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofHours(24))
                    .sameSite("Strict")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of(
                            "message", "Google login successful",
                            "email", user.getEmail()
                    ));
        } catch (OAuth2Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}