package com.ulp.dao;

import com.ulp.bean.UserModel;

public interface UserDao {
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    UserModel findByUsername(String username);
    
    /**
     * 插入新用户
     * @param user 用户对象
     * @return 是否插入成功
     */
    boolean insertUser(UserModel user);
    
    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 密码（已加密）
     * @return 用户对象，如果验证失败返回null
     */
    UserModel authenticate(String username, String password);
    
    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 存在返回true，不存在返回false
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否已存在
     * @param email 邮箱
     * @return 存在返回true，不存在返回false
     */
    public boolean existsByEmail(String email);

    /**
     * 更新数据库中的用户资料
     * @param user 用户模型
     */
    public boolean updateProfileInDatabase(UserModel user);

    /**
     * 根据用户ID查找用户
     * @param userId 用户ID
     * @return 用户对象，如果用户不存在返回null
     */
    public UserModel findUserById(int userId);
    public boolean deleteUserById(int userId);
}
