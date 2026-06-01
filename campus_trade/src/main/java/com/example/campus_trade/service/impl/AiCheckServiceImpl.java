package com.example.campus_trade.service.impl;

import com.example.campus_trade.service.AiCheckService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiCheckServiceImpl implements AiCheckService {

    @Value("${ai.check.url}")
    private String aiCheckUrl;

    @Override
    public boolean checkViolation(String content) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", content);

            Map response = restTemplate.postForObject(aiCheckUrl, requestBody, Map.class);

            if (response == null || !response.get("code").equals(200)) {
                System.out.println("=== AI服务返回异常，默认放行 ===");
                return false;
            }

            return (boolean) response.get("is_violation");
        } catch (Exception e) {
            // AI服务没启动/网络异常，不拦截用户发布
            System.out.println("=== AI服务连接失败，跳过检测 ===");
            return false;
        }
    }
}