package com.example.campus_trade.service.impl;

import com.example.campus_trade.entity.User;
import com.example.campus_trade.mapper.UserMapper;
import com.example.campus_trade.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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
            if (user == null) {
                log.error("注册失败：user 对象为 null");
                return false;
            }
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                log.error("注册失败：用户名为空");
                return false;
            }
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                log.error("注册失败：密码为空");
                return false;
            }
            if (user.getIdentity() == null || user.getIdentity().isEmpty()) {
                log.error("注册失败：身份为空");
                return false;
            }

            User exist = getUserByUsername(user.getUsername());
            if (exist != null) {
                log.error("注册失败：用户名已存在 - {}", user.getUsername());
                return false;
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setStatus(0);

            int rows = userMapper.addUser(user);
            return rows > 0;

        } catch (Exception e) {
            log.error("注册异常", e);
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getUid() <= 0) {
            return false;
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
