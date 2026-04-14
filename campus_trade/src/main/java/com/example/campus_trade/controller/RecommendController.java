package com.example.campus_trade.controller;

import com.example.campus_trade.entity.Product;
import com.example.campus_trade.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/user/{uid}")
    public List<Product> recommendForUser(@PathVariable Long uid) {
        return recommendService.recommendForUser(uid);
    }
}