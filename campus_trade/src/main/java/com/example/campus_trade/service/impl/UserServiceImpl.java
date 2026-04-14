package com.example.campus_trade.service.impl;

import com.example.campus_trade.entity.User;
import com.example.campus_trade.mapper.UserMapper;
import com.example.campus_trade.service.UserService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    // 构造器注入
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @Override
    public User getUserById(long uid) {
        if (uid <= 0) {
            return null;
        }
        return userMapper.findById(uid);
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userMapper.findByUsername(username);
    }

    @Override
    public User getUserByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        return userMapper.findByPhone(phone);
    }

    @Override
    public boolean addUser(User user) {
        try {
            System.out.println("==================== 注册开始 ====================");

            // 基础校验
            if (user == null) {
                System.out.println("【错误】user 对象为 null");
                return false;
            }
            System.out.println("用户名：" + user.getUsername());
            System.out.println("密码：" + user.getPassword());
            System.out.println("身份：" + user.getIdentity());

            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                System.out.println("【错误】用户名为空");
                return false;
            }
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                System.out.println("【错误】密码为空");
                return false;
            }
            if (user.getIdentity() == null || user.getIdentity().isEmpty()) {
                System.out.println("【错误】身份为空");
                return false;
            }

            // 检查用户名是否重复
            User exist = getUserByUsername(user.getUsername());
            if (exist != null) {
                System.out.println("【错误】用户名已存在：" + user.getUsername());
                return false;
            }
            System.out.println("【检查】用户名可用");

            // 补全状态
            user.setStatus(0);
            System.out.println("【状态】设置 status=0");

            // 执行插入
            System.out.println("【执行】开始插入数据库...");
            int rows = userMapper.addUser(user);
            System.out.println("【结果】插入行数：" + rows);

            return rows > 0;

        } catch (Exception e) {
            System.out.println("==================== 注册异常 ====================");
            e.printStackTrace(); // 打印完整错误
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getUid() <= 0) {
            return false;
        }
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public boolean updateUserStatus(long uid, long status) {
        if (uid <= 0 || (status != 0 && status != 1)) {
            return false;
        }
        return userMapper.updateUserStatus(uid, status) > 0;
    }

    @Override
    public boolean deleteUser(long uid) {
        if (uid <= 0) {
            return false;
        }
        return userMapper.deleteUser(uid) > 0;
    }

    // 登录
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        // 比对密码
        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}