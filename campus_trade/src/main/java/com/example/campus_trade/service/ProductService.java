package com.example.campus_trade.service;

import com.example.campus_trade.entity.Product;
import java.util.List;

public interface ProductService {
    // 查询所有商品
    List<Product> getAllProducts();

    // 根据ID查询商品
    Product getProductById(long pid);

    // 根据分类ID查询商品
    List<Product> getProductsByCid(long cid);

    // 根据用户ID查询发布的商品
    List<Product> getProductsByUid(long uid);

    // 根据状态查询商品
    List<Product> getProductsByStatus(long status);

    // 关键词搜索商品
    List<Product> searchProductsByKeyword(String keyword);

    // 新增商品
    boolean addProduct(Product product);

    // 修改商品信息
    boolean updateProduct(Product product);

    // 修改商品状态（上架/下架/售出）
    boolean updateProductStatus(long pid, long status);

    // 删除商品
    boolean deleteProduct(long pid);
}