package com.example.campus_trade.service;

import com.example.campus_trade.entity.Category;
import java.util.List;

public interface CategoryService {
    // 查询所有分类
    List<Category> getAllCategories();

    // 根据ID查询分类
    Category getCategoryById(long cid);

    // 新增分类（返回是否成功）
    boolean addCategory(Category category);

    // 修改分类（返回是否成功）
    boolean updateCategory(Category category);

    // 删除分类（返回是否成功）
    boolean deleteCategory(long cid);
}