package com.example.campus_trade.controller;

import com.example.campus_trade.entity.User;
import com.example.campus_trade.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            return loginUser;
        } else {
            return null;
        }
    }

    @GetMapping("/get/{uid}")
    public User getById(@PathVariable Long uid) {
        return userService.getUserById(uid);
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.addUser(user) ? "注册成功" : "注册失败（用户名已存在）";
    }

    @PutMapping("/status/{uid}/{status}")
    public String updateStatus(@PathVariable Long uid, @PathVariable Integer status) {
        return userService.updateUserStatus(uid, status) ? "状态修改成功" : "修改失败";
    }

    @PutMapping("/update")
    public String update(@RequestBody User user) {
        boolean result = userService.updateUser(user);
        return result ? "ok" : "err";
    }

    @DeleteMapping("/delete/{uid}")
    public String delete(@PathVariable Long uid) {
        return userService.deleteUser(uid) ? "删除成功" : "删除失败";
    }
}