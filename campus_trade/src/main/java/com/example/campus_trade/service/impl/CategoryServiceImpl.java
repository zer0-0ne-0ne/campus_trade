package com.example.campus_trade.service.impl;

import com.example.campus_trade.entity.Category;
import com.example.campus_trade.mapper.CategoryMapper;
import com.example.campus_trade.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    // 声明为 final，保证不可变
    private final CategoryMapper categoryMapper;

    // 构造器注入（推荐方式）
    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.findAll();
    }

    @Override
    public Category getCategoryById(long cid) {
        if (cid <= 0) {
            return null;
        }
        return categoryMapper.findById(cid);
    }

    @Override
    public boolean addCategory(Category category) {
        if (category == null || category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            return false;
        }
        return categoryMapper.addCategory(category) > 0;
    }

    @Override
    public boolean updateCategory(Category category) {
        if (category == null || category.getCid() <= 0) {
            return false;
        }
        return categoryMapper.updateCategory(category) > 0;
    }

    @Override
    public boolean deleteCategory(long cid) {
        if (cid <= 0) {
            return false;
        }
        return categoryMapper.deleteCategory(cid) > 0;
    }
}