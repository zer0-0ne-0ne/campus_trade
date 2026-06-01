package com.example.campus_trade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RagService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EmbeddingService embeddingService;

    /**
     * 从数据库读取商品向量
     */
    public List<Double> getEmbeddingFromDB(Integer pid) {
        try {
            String sql = "SELECT embedding_text FROM product_embedding WHERE pid = ?";
            String json = jdbcTemplate.queryForObject(sql, String.class, pid);
            return embeddingService.jsonToVector(json);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存商品向量到数据库
     */
    public void saveEmbeddingToDB(Integer pid, String title, String description) {
        String text = title + " " + description;
        List<Double> vector = embeddingService.getEmbedding(text);
        String json = embeddingService.vectorToJson(vector);

        String sql = "INSERT INTO product_embedding (pid, embedding_text) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE embedding_text = ?";
        jdbcTemplate.update(sql, pid, json, json);
    }

    /**
     * 获取所有在售商品（仅用于RAG检索）
     */
    public List<Map<String, Object>> getActiveProductsForRag() {
        return jdbcTemplate.queryForList(
                "SELECT pid, title, description, price FROM product WHERE status = 1"
        );
    }
}