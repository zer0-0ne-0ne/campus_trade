package com.example.campus_trade.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class EmbeddingService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.siliconflow.embedding-url}")
    private String embeddingUrl;

    @Value("${ai.siliconflow.api-key}")
    private String apiKey;

    private static final String MODEL_NAME = "BAAI/bge-m3";

    /**
     * 获取文本的向量
     */
    public List<Double> getEmbedding(String text) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("请设置环境变量 SILICONFLOW_API_KEY");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL_NAME);
        body.put("input", text);
        body.put("encoding_format", "float");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(embeddingUrl, entity, Map.class);

        if (response.getStatusCode().value() != 200 || response.getBody() == null) {
            throw new RuntimeException("Embedding API调用失败: " + response.getStatusCode());
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");
        return (List<Double>) data.get(0).get("embedding");
    }

    /**
     * 向量转JSON字符串
     */
    public String vectorToJson(List<Double> vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(vector.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * JSON字符串转向量
     */
    public List<Double> jsonToVector(String json) {
        if (json == null || json.length() <= 2) return new ArrayList<>();
        String content = json.substring(1, json.length() - 1);
        if (content.isEmpty()) return new ArrayList<>();
        String[] parts = content.split(",");
        List<Double> vector = new ArrayList<>();
        for (String part : parts) {
            vector.add(Double.parseDouble(part.trim()));
        }
        return vector;
    }
}