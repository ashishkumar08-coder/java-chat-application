package com.college.chat.service;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class EncryptionService {
    private static final String KEY = "CollegeProjectKey_KeepItSecure!!"; // 32 chars
    private static final String ALGO = "AES";

    public String encrypt(String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.substring(0,16).getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) { return "Encryption Error"; }
    }
}