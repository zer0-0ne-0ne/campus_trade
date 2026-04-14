package com.example.campus_trade.service;

import com.example.campus_trade.entity.User;
import java.util.List;

public interface UserService {
    // 查询所有用户
    List<User> getAllUsers();

    // 根据ID查询用户
    User getUserById(long uid);

    // 根据用户名查询用户（登录用）
    User getUserByUsername(String username);

    // 根据手机号查询用户（注册校验）
    User getUserByPhone(String phone);

    // 新增用户（注册）
    boolean addUser(User user);

    // 修改用户信息
    boolean updateUser(User user);

    // 修改用户状态（禁用/启用）
    boolean updateUserStatus(long uid, long status);

    // 删除用户
    boolean deleteUser(long uid);

    public User login(String username, String password);
}