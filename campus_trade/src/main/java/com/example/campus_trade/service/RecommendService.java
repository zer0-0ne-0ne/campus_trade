package com.example.campus_trade.service;

import com.example.campus_trade.entity.Product;
import java.util.List;

public interface RecommendService {
    List<Product> recommendForUser(Long uid);
}
