package com.example.campus_trade.service.impl;

import com.example.campus_trade.entity.Product;
import com.example.campus_trade.mapper.ProductMapper;
import com.example.campus_trade.service.ProductService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public List<Product> getAllProducts() {
        return productMapper.findAll();
    }

    @Override
    public Product getProductById(long pid) {
        if (pid <= 0) {
            return null;
        }
        return productMapper.findById(pid);
    }

    @Override
    public List<Product> getProductsByCid(long cid) {
        if (cid <= 0) {
            return null;
        }
        return productMapper.findByCid(cid);
    }

    @Override
    public List<Product> getProductsByUid(long uid) {
        if (uid <= 0) {
            return null;
        }
        return productMapper.findByUid(uid);
    }

    @Override
    public List<Product> getProductsByStatus(long status) {
        // 校验状态合法性（0-待审核 1-在售 2-售出 3-已下架）
        if (status < 0 || status > 3) {
            return null;
        }
        return productMapper.findByStatus(status);
    }

    @Override
    public List<Product> searchProductsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return productMapper.searchByKeyword(keyword);
    }

    @Override
    public boolean addProduct(Product product) {
        if (product == null ||
                product.getTitle() == null || product.getTitle().trim().isEmpty() ||
                product.getPrice() <= 0 ||  // 修复点：去掉==null，校验价格>0
                product.getCid() <= 0 ||
                product.getUid() <= 0) {
            return false;
        }
        return productMapper.addProduct(product) > 0;
    }

    @Override
    public boolean updateProduct(Product product) {
        if (product == null || product.getPid() <= 0) {
            return false;
        }
        return productMapper.updateProduct(product) > 0;
    }

    @Override
    public boolean updateProductStatus(long pid, long status) {
        if (pid <= 0 || status < 0 || status > 3) {
            return false;
        }
        return productMapper.updateProductStatus(pid, status) > 0;
    }

    @Override
    public boolean deleteProduct(long pid) {
        if (pid <= 0) {
            return false;
        }
        return productMapper.deleteProduct(pid) > 0;
    }
}