package com.mall.common;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Random;

/**
 * 密码工具类 - SHA256加密
 */
public class PasswordUtil {

    public static String generateSalt() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }
        return sb.toString();
    }

    public static String encryptPassword(String password, String salt) {
        return DigestUtils.sha256Hex(password + salt);
    }

    public static boolean verifyPassword(String inputPassword, String storedPassword, String salt) {
        String encryptedInput = encryptPassword(inputPassword, salt);
        return encryptedInput.equals(storedPassword);
    }
}
