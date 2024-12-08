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
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class UserInfoController {

    private KeyLoaderService keyLoaderService;

    private UserInfoService userInfoService;

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"message\" : \"Username already exists\"}"))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"message\" : \"Username can't be empty\"}")))
            }
    )
    @PostMapping("/addUserInfo")
    public ResponseEntity<?> addUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String userToken, @RequestBody UserInfoDTO userInfo) {
        String userId = getUserIdFromToken(userToken);
        Map<String, String> response = new HashMap<>();
        if(userInfoService.getUserInfoByUsername(userInfo.getUsername()) != null) {
            response.put("message", "Username already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        UserInfo newUser = new UserInfo();
        newUser.setId(userId);
        newUser.setUsername(userInfo.getUsername());
        newUser.setName(userInfo.getName());
        newUser.setSurname(userInfo.getSurname());
        if(!validateInput(newUser.getUsername())) {
            response.put("message", "Username can't be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        userInfoService.saveUserInfo(newUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfo.class))),
                    @ApiResponse(responseCode = "404",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"message\" : \"User does not exist\"}")))
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

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfo.class))),
                    @ApiResponse(responseCode = "404",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"message\" : \"No user info found\"}")))
            }
    )
    @GetMapping("/findAllUserInfo")
    public ResponseEntity<?> findAllUserInfo() {
        List<UserInfo> allUserInfo = userInfoService.getAllUserInfo();
        if (allUserInfo == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No user info found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(allUserInfo);
    }

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfo.class))),
                    @ApiResponse(responseCode = "404",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"message\" : \"No user info found\"}")))
            }
    )
    @PostMapping("/findUserInfoByUsername")
    public ResponseEntity<?> findUserInfoByUsername(@RequestBody UserInfoDTO userInfo) {
        UserInfo foundUserInfo = userInfoService.getUserInfoByUsername(userInfo.getUsername());
        if (foundUserInfo == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No user info found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(foundUserInfo);
    }

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfo.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"message\" : \"Inputs can't be empty\"}"))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"message\" : \"Username already exists\"}"))),
                    @ApiResponse(responseCode = "404",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"message\" : \"User does not exist\"}")))
            }
    )
    @PutMapping("/editUserInfo")
    public  ResponseEntity<?> editUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String userToken, @RequestBody UserInfoDTO userInfoDTO) {
        String userId = getUserIdFromToken(userToken);
        Map<String, String> response = new HashMap<>();
        UserInfo userInfo = new UserInfo(userInfoDTO, userId);
        if(!validateInput(userInfo.getUsername()) || !validateInput(userInfo.getName()) || !validateInput(userInfo.getSurname())) {
            response.put("message", "Inputs can't be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        UserInfo sameUsernameInfo = userInfoService.getUserInfoByUsername(userInfo.getUsername());
        if(sameUsernameInfo.getUsername() != null && !Objects.equals(sameUsernameInfo.getId(), userInfo.getId())) {
            response.put("message", "Username already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        UserInfo updatedInfo = userInfoService.updateUserInfo(userInfo);
        if(updatedInfo == null) {
            response.put("message", "User does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(updatedInfo);
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

    private boolean validateInput(String string){
        return !string.isBlank() && !string.isEmpty();
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
