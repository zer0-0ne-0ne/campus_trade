package com.example.campus_trade.mapper;

import com.example.campus_trade.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CategoryMapper {

    // 查询所有分类
    @Select("SELECT * FROM category")
    List<Category> findAll();

    // 根据CID查询分类
    @Select("SELECT * FROM category WHERE CID = #{cid}")
    Category findById(@Param("cid") long cid);

    // 新增分类
    @Insert("INSERT INTO category(category_name, category_description) " +
            "VALUES (#{categoryName}, #{categoryDescription})")
    @Options(useGeneratedKeys = true, keyProperty = "cid", keyColumn = "CID")
    int addCategory(Category category);

    // 修改分类
    @Update("UPDATE category SET category_name = #{categoryName}, " +
            "category_description = #{categoryDescription} WHERE CID = #{cid}")
    int updateCategory(Category category);

    // 删除分类
    @Delete("DELETE FROM category WHERE CID = #{cid}")
    int deleteCategory(@Param("cid") long cid);
}