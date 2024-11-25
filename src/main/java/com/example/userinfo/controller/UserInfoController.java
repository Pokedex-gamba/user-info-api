package com.example.userinfo.controller;

import com.example.userinfo.dto.UserInfoDTO;
import com.example.userinfo.entity.UserInfo;
import com.example.userinfo.service.KeyLoaderService;
import com.example.userinfo.service.UserInfoService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserInfoController {

    private KeyLoaderService keyLoaderService;

    private UserInfoService userInfoService;

    @GetMapping("/addUserInfo")
    public ResponseEntity<?> addUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String userToken, @RequestBody UserInfoDTO userInfo) {
        String userId = getUserIdFromToken(userToken);
        Map<String, String> response = new HashMap<>();
        if(userInfoService.getUserInfoByEmail(userInfo.getEmail()) != null || userInfoService.getUserInfoByUsername(userInfo.getUsername()) != null){
            response.put("message", "Username or email already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        UserInfo newUser = new UserInfo();
        newUser.setId(userId);
        newUser.setUsername(userInfo.getUsername());
        newUser.setName(userInfo.getName());
        newUser.setSurname(userInfo.getSurname());
        newUser.setEmail(userInfo.getEmail());

        userInfoService.saveUserInfo(newUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfo.class)))
            }
    )
    @GetMapping("/findUserInfo")
    public ResponseEntity<?> findUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String userToken) {
        String userId = getUserIdFromToken(userToken);
        UserInfo userInfo = userInfoService.getUserInfo(userId);
        if(userInfo == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            return ResponseEntity.ok(userInfo);
        }
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

    @Autowired
    public void setKeyLoaderService(KeyLoaderService keyLoaderService) {
        this.keyLoaderService = keyLoaderService;
    }

    @Autowired
    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }
}
