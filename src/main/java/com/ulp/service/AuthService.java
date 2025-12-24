package com.ulp.service;

import com.ulp.bean.UserModel;
import com.ulp.dao.UserDao;
import com.ulp.dao.impl.UserDaoImpl;

public class AuthService {
    private final UserDao userDao = new UserDaoImpl();

    /**
     * 用户认证
     * @param username 用户名
     * @param password 原始密码
     * @return 验证成功返回用户对象，失败返回null
     */
    public UserModel authenticate(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        
        // 从数据库验证
        return userDao.authenticate(username, password);
    }

    /**
     * 注册新用户
     * @param user 用户对象
     * @throws IllegalArgumentException 如果用户数据无效或用户已存在
     */
    public void registerUser(UserModel user) {
//        System.out.println("准备注册用户："+user.toString());
        // 验证用户数据
        if (!user.validate()) {
            throw new IllegalArgumentException("用户数据无效");
        }
        
        // 检查用户名是否已存在
        if (userDao.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userDao.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        
        // 加密密码
        user.setPassword(user.getPassword());
        
        // 保存到数据库
        if (!userDao.insertUser(user)) {
            throw new IllegalArgumentException("注册失败，请稍后重试");
        }
    }
    
    /**
     * 检查用户名是否可用
     */
    public boolean isUsernameAvailable(String username) {
        return !userDao.existsByUsername(username);
    }
    
    /**
     * 检查邮箱是否可用
     */
    public boolean isEmailAvailable(String email) {
        return !userDao.existsByEmail(email);
    }

//    public static void main(String[] args) {
//        AuthService authService = new AuthService();
//        // 测试用户注册
//        UserModel user = new UserModel("test", "123456", "student", "test@example.com", null);
//        try {
//            authService.registerUser(user);
//            System.out.println("用户注册成功");
//        } catch (IllegalArgumentException e) {
//            System.out.println("用户注册失败：" + e.getMessage());
//        }
//
//        // 测试用户登录
//        UserModel user2 = authService.authenticate("test", "123456");
//        if (user2 != null) {
//            System.out.println("用户login成功");
//        }
//    }
}