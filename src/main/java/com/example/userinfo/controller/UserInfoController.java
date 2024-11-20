package com.example.userinfo.controller;

import com.example.userinfo.entity.UserInfo;
import com.example.userinfo.service.KeyLoaderService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.PublicKey;
import java.util.stream.Collectors;

@RestController
public class UserInfoController {

    private KeyLoaderService keyLoaderService;

    @GetMapping("/userInfo/addUserInfo")
    public void addUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String userToken, @RequestBody UserInfo userInfo) {
        String userId = getUserIdFromToken(userToken);

    }

    private String getUserIdFromToken(String authHeader) {
        String token = authHeader.replace("Bearer", "").trim();

        PublicKey publicKey;
        try {
            String path = UserInfoController.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File publicKeyFile = new File(path, "decoding_key");
            if (!publicKeyFile.exists()) {
                return "aaa";
            }
            BufferedReader reader = new BufferedReader(new FileReader(publicKeyFile));
            String publicKeyContent = reader.lines().collect(Collectors.joining("\n"));
            reader.close();
            publicKey = keyLoaderService.getPublicKey(publicKeyContent);
        } catch (Exception e) {
            return "bbb";
        }

        Claims claims = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();

        String userId = claims.get("user_id", String.class);
        if (userId == null) {
            return "ccc";
        }

        return userId;
    }

}
