package com.example.campus_trade.mapper;

import com.example.campus_trade.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {

    // 查询所有用户
    @Select("SELECT * FROM user")
    List<User> findAll();

    // 根据UID查询用户
    @Select("SELECT * FROM user WHERE UID = #{uid}")
    User findById(@Param("uid") long uid);

    // 根据用户名查询用户（登录/注册校验用）
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    // 根据手机号查询用户（手机号唯一索引，用于登录/注册校验）
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(@Param("phone") String phone);

    // 新增用户（主键自增，插入后回填UID）
    @Insert("INSERT INTO user(username, password, phone, email, identity, avatar, status) " +
            "VALUES (#{username}, #{password}, #{phone}, #{email}, #{identity}, #{avatar}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "uid", keyColumn = "UID")
    int addUser(User user);

    // 修改用户信息
    @Update("UPDATE user SET username = #{username}, password = #{password}, phone = #{phone}, " +
            "email = #{email}, avatar = #{avatar} WHERE UID = #{uid}")
    int updateUser(User user);

    // 修改用户状态（禁用/启用）
    @Update("UPDATE user SET status = #{status} WHERE UID = #{uid}")
    int updateUserStatus(@Param("uid") long uid, @Param("status") long status);

    // 删除用户
    @Delete("DELETE FROM user WHERE UID = #{uid}")
    int deleteUser(@Param("uid") long uid);

    @Update("UPDATE user SET password=#{password}, phone=#{phone}, email=#{email} WHERE username=#{username}")
    int updateByUsername(User user);
}