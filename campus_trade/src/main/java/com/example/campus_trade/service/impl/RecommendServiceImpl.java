package com.example.campus_trade.service.impl;

import com.example.campus_trade.entity.Orders;
import com.example.campus_trade.entity.Product;
import com.example.campus_trade.mapper.OrdersMapper;
import com.example.campus_trade.mapper.ProductMapper;
import com.example.campus_trade.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> recommendForUser(Long uid) {
        // 获取用户所有订单
        List<Orders> userOrders = ordersMapper.getUserOrders(uid);

        // 如果用户没有订单 → 直接推荐热门商品
        if (userOrders == null || userOrders.isEmpty()) {
            return productMapper.findHotProducts();
        }

        // 提取用户【感兴趣的分类】 + 【已经买过的商品ID】
        Set<Integer> userInterestCategories = new HashSet<>();
        Set<Long> userBoughtProductIds = new HashSet<>();

        for (Orders order : userOrders) {
            Product product = productMapper.getProductById(order.getPid());
            if (product != null) {
                userInterestCategories.add((int) product.getCid());
                userBoughtProductIds.add(product.getPid());
            }
        }

        // 推荐同类别的商品
        List<Product> recommendList = new ArrayList<>();
        for (Integer cid : userInterestCategories) {
            // 查询该分类下，不是当前用户发布的商品
            List<Product> products = productMapper.findByCidAndNotSelf(cid, uid);
            for (Product p : products) {
                // 过滤：没买过 + 没下架
                if (!userBoughtProductIds.contains(p.getPid()) && p.getStatus() != 3) {
                    recommendList.add(p);
                }
            }
        }

        // 去重 + 最多返回8条
        return recommendList.stream()
                .distinct()
                .limit(8)
                .collect(Collectors.toList());
    }
}