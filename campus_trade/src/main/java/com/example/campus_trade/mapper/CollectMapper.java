package com.example.campus_trade.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CollectMapper {

    // 收藏商品
    @Insert("INSERT INTO collect(uid, pid) VALUES(#{uid}, #{pid})")
    int addCollect(Integer uid, Integer pid);

    // 取消收藏
    @Delete("DELETE FROM collect WHERE uid=#{uid} AND pid=#{pid}")
    int deleteCollect(Integer uid, Integer pid);

    // 是否已经收藏
    @Select("SELECT COUNT(*) FROM collect WHERE uid=#{uid} AND pid=#{pid}")
    int isCollect(Integer uid, Integer pid);

    // 查询我的收藏列表（返回商品ID列表）
    @Select("SELECT pid FROM collect WHERE uid=#{uid}")
    List<Integer> getMyCollectIds(Integer uid);
}
