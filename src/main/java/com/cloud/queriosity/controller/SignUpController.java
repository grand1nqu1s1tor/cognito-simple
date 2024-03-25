package com.cloud.queriosity.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SignUpController {

    @Value("${spring.security.oauth2.client.registration.cognito.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.cognito.client-secret}")
    private String secretKey;

    private final CognitoIdentityProviderClient identityProviderClient;



    public SignUpController() {
        this.identityProviderClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_2)
                .build();
    }

    @PostMapping("/server-signup")
    public ResponseEntity<String> signUp(
            String password, String email, String userName) {
        AttributeType userAttrs = AttributeType.builder()
                .name("email")
                .value(email)
                .build();

        List<AttributeType> userAttrsList = new ArrayList<>();
        userAttrsList.add(userAttrs);
        try {
            String secretVal = calculateSecretHash(clientId, secretKey, userName);
            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .userAttributes(userAttrsList)
                    .username(email)
                    .clientId(clientId)
                    .secretHash(secretVal)
                    .password(password)
                    .build();

            SignUpResponse signUpResponse = identityProviderClient.signUp(signUpRequest);
            return ResponseEntity.ok("User has been signed up: " + signUpResponse.userSub());

        } catch (CognitoIdentityProviderException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.awsErrorDetails().errorMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            // Log the exception; for production, use a logger instead of printStackTrace
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred.");
        }
    }

    private String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName)
            throws NoSuchAlgorithmException, InvalidKeyException {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);

        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(signingKey);
        mac.update(userName.getBytes(StandardCharsets.UTF_8));
        byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
        return java.util.Base64.getEncoder().encodeToString(rawHmac);
    }
}


//Not using this since I later discovered that the Hosted Cognito already provides an
//option to self register.