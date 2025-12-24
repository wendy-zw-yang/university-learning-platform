package com.ulp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 * 使用SHA-256加密算法
 */
public class PasswordUtil {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 使用SHA-256加密密码
     * @param password 原始密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            String saltedPassword = password + salt;
            byte[] hashedBytes = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 简化版：直接使用MD5加密（不使用盐值）
     * 适用于简单应用，推荐使用上面的hashPassword方法
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String hashPasswordSimple(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 验证密码是否匹配
     * @param password 输入的原始密码
     * @param salt 盐值
     * @param hashedPassword 存储的加密密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
        String newHash = hashPassword(password, salt);
        return newHash.equals(hashedPassword);
    }
    
    /**
     * 简化版验证（不使用盐值）
     */
    public static boolean verifyPasswordSimple(String password, String hashedPassword) {
        String newHash = hashPasswordSimple(password);
        return newHash.equals(hashedPassword);
    }
}
