package com.example.campus_trade.controller;

import com.example.campus_trade.entity.Category;
import com.example.campus_trade.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/get/{cid}")
    public Category getById(@PathVariable Long cid) {
        return categoryService.getCategoryById(cid);
    }

    @PostMapping("/add")
    public String add(@RequestBody Category category) {
        return categoryService.addCategory(category) ? "添加成功" : "添加失败";
    }

    @PutMapping("/update")
    public String update(@RequestBody Category category) {
        return categoryService.updateCategory(category) ? "修改成功" : "修改失败";
    }

    @DeleteMapping("/delete/{cid}")
    public String delete(@PathVariable Long cid) {
        return categoryService.deleteCategory(cid) ? "删除成功" : "删除失败";
    }
}