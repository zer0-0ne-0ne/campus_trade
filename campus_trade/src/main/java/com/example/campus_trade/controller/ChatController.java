package com.example.campus_trade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.example.campus_trade.service.EmbeddingService;
import com.example.campus_trade.service.RagService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/chat")
@CrossOrigin
public class ChatController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private RagService ragService;

    @Value("${ai.deepseek.api-key}")
    private String deepseekApiKey;

    @Value("${ai.siliconflow.api-key}")
    private String siliconflowApiKey;

    @Value("${ai.deepseek.base-url}")
    private String deepseekBaseUrl;

    @Value("${ai.siliconflow.base-url}")
    private String siliconflowBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // 存储每个用户的对话历史，使用ConcurrentHashMap保证线程安全
    // key: 用户ID, value: 对话消息列表
    private static final Map<String, List<Map<String, String>>> USER_HISTORY = new ConcurrentHashMap<>();
    private static final int MAX_HISTORY_SIZE = 10;

    private record ActionRule(List<String> keywords, String path, String label) {}

    private record Action(String type, String path, String label) {}

    private static final Pattern PID_PATTERN = Pattern.compile("ID[:：]?\\s*(\\d+)");

    private static final List<ActionRule> ACTION_RULES = List.of(
            new ActionRule(List.of("发布", "卖", "上架", "出售", "卖东西", "闲置"), "/publish", "去发布商品"),
            new ActionRule(List.of("收藏", "我收藏的", "我的收藏", "点赞"), "/my-collect", "查看我的收藏"),
            new ActionRule(List.of("我发布的", "我的商品", "我卖的", "我发布的商品"), "/my-publish", "查看我发布的"),
            new ActionRule(List.of("首页", "看看", "推荐", "有什么", "逛逛", "浏览", "买", "想要"), "/", "去看看商品"),
            new ActionRule(List.of("登录", "注册", "登陆", "账号"), "/login", "去登录"),
            new ActionRule(List.of("个人信息", "修改资料", "个人资料", "我的资料", "资料"), "/profile", "修改资料"),
            new ActionRule(List.of("设置", "修改密码", "密码"), "/settings", "去设置"),
        new ActionRule(List.of("订单", "我买的", "我卖出的", "交易"), "/category", "查看订单")
    );

    @PostMapping
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        // 获取用户ID
        String uid = body.get("userId");

        // 验证用户是否登录
        if (uid == null || uid.trim().isEmpty()) {
            return ResponseEntity.ok(Map.of("reply", (Object) "请先登录"));
        }

        try {
            String reply;

            // 判断是否使用RAG
            if (siliconflowApiKey != null && !siliconflowApiKey.isEmpty()) {
                try {
                    reply = chatWithRag(question, uid);
                } catch (Exception e) {
                    System.err.println("RAG调用失败，使用普通模式: " + e.getMessage());
                    reply = chatWithKeywordSearch(question, uid);
                }
            } else {
                reply = chatWithKeywordSearch(question, uid);
            }

            // 导航：关键词匹配优先，其次从回复中提取商品ID
            Action action = matchAction(question);
            if (action == null) {
                action = extractProductAction(reply);
            }

            // 保存对话历史
            saveToHistory(uid, "user", question);
            saveToHistory(uid, "assistant", reply);

            // 构建响应
            Map<String, Object> result = new HashMap<>();
            result.put("reply", reply);
            if (action != null) {
                result.put("action", Map.of("type", action.type, "path", action.path, "label", action.label));
            }
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("reply", (Object) "服务异常"));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, String>>> getHistory(@RequestParam String userId) {
        List<Map<String, String>> history = USER_HISTORY.getOrDefault(userId, new ArrayList<>());
        return ResponseEntity.ok(history);
    }

    // 保存对话历史，限制最多保存10条
    private void saveToHistory(String uid, String role, String content) {
        List<Map<String, String>> history = USER_HISTORY.computeIfAbsent(uid, k -> new ArrayList<>());
        Map<String, String> msg = new HashMap<>();
        msg.put("role", role);
        msg.put("content", content);
        history.add(msg);

        // 保持最近10条
        while (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }

    // 查询用户发布和收藏的商品，供AI准确回答"我发布了什么""我收藏了什么"
    private String buildUserContext(String uid) {
        try {
            List<Map<String, Object>> published = jdbcTemplate.queryForList(
                    "SELECT pid, title FROM product WHERE uid = ? AND status != 3", uid
            );
            List<Map<String, Object>> collected = jdbcTemplate.queryForList(
                    "SELECT p.pid, p.title FROM product p JOIN collect c ON p.pid = c.pid WHERE c.uid = ?", uid
            );
            List<Map<String, Object>> bought = jdbcTemplate.queryForList(
                    "SELECT o.OID, o.PID, p.title, o.status FROM orders o JOIN product p ON o.PID = p.PID WHERE o.buyer_id = ?", uid
            );
            List<Map<String, Object>> sold = jdbcTemplate.queryForList(
                    "SELECT o.OID, o.PID, p.title, o.status FROM orders o JOIN product p ON o.PID = p.PID WHERE o.seller_id = ?", uid
            );

            StringBuilder sb = new StringBuilder();
            sb.append("\n\n当前用户个人数据：\n");

            sb.append("- 已发布 ").append(published.size()).append(" 件: ");
            if (published.isEmpty()) {
                sb.append("无");
            } else {
                for (Map<String, Object> p : published) {
                    sb.append("ID").append(p.get("pid")).append(" ").append(p.get("title")).append(" | ");
                }
                sb.setLength(sb.length() - 3);
            }

            sb.append("\n- 已收藏 ").append(collected.size()).append(" 件: ");
            if (collected.isEmpty()) {
                sb.append("无");
            } else {
                for (Map<String, Object> c : collected) {
                    sb.append("ID").append(c.get("pid")).append(" ").append(c.get("title")).append(" | ");
                }
                sb.setLength(sb.length() - 3);
            }

            sb.append("\n- 已买到的订单 ").append(bought.size()).append(" 笔: ");
            if (bought.isEmpty()) {
                sb.append("无");
            } else {
                String[] statusText = {"待交易", "已完成", "已取消"};
                for (Map<String, Object> o : bought) {
                    long st = ((Number) o.get("status")).longValue();
                    String stStr = st < statusText.length ? statusText[(int) st] : "状态" + st;
                    sb.append("订单").append(o.get("OID")).append(" 商品ID").append(o.get("PID"))
                            .append(" ").append(o.get("title")).append(" ").append(stStr).append(" | ");
                }
                sb.setLength(sb.length() - 3);
            }

            sb.append("\n- 已卖出的订单 ").append(sold.size()).append(" 笔: ");
            if (sold.isEmpty()) {
                sb.append("无");
            } else {
                String[] statusText = {"待交易", "已完成", "已取消"};
                for (Map<String, Object> o : sold) {
                    long st = ((Number) o.get("status")).longValue();
                    String stStr = st < statusText.length ? statusText[(int) st] : "状态" + st;
                    sb.append("订单").append(o.get("OID")).append(" 商品ID").append(o.get("PID"))
                            .append(" ").append(o.get("title")).append(" ").append(stStr).append(" | ");
                }
                sb.setLength(sb.length() - 3);
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // 获取最近5条历史用于AI上下文
    private List<Map<String, String>> getRecentHistory(String uid) {
        List<Map<String, String>> history = USER_HISTORY.getOrDefault(uid, new ArrayList<>());
        int start = Math.max(0, history.size() - 5);
        return new ArrayList<>(history.subList(start, history.size()));
    }

    // 从AI回复中提取商品ID，生成跳转action
    private Action extractProductAction(String reply) {
        if (reply == null) return null;
        Matcher m = PID_PATTERN.matcher(reply);
        if (m.find()) {
            return new Action("navigate", "/product/" + m.group(1), "查看商品详情");
        }
        return null;
    }

    // 根据用户问题匹配导航意图
    private Action matchAction(String question) {
        if (question == null) return null;
        for (ActionRule rule : ACTION_RULES) {
            for (String keyword : rule.keywords) {
                if (question.contains(keyword)) {
                    return new Action("navigate", rule.path, rule.label);
                }
            }
        }
        return null;
    }

    // RAG方式回答（带上下文）
    private String chatWithRag(String question, String uid) {
        List<Map<String, Object>> products = ragService.getActiveProductsForRag();

        if (products.isEmpty()) {
            return "暂无商品，请稍后再来～";
        }

        List<Double> questionEmbedding = embeddingService.getEmbedding(question);

        List<ProductScore> scores = new ArrayList<>();
        for (Map<String, Object> p : products) {
            Integer pid = (Integer) p.get("pid");
            String title = (String) p.get("title");
            String description = (String) p.get("description");
            Double price = ((Number) p.get("price")).doubleValue();

            List<Double> productVector = ragService.getEmbeddingFromDB(pid);
            if (productVector == null) {
                ragService.saveEmbeddingToDB(pid, title, description);
                productVector = ragService.getEmbeddingFromDB(pid);
            }

            double score = cosineSimilarity(questionEmbedding, productVector);
            scores.add(new ProductScore(pid, title, description, price, score));
        }

        scores.sort((a, b) -> Double.compare(b.score, a.score));
        int topK = Math.min(3, scores.size());
        List<ProductScore> topProducts = scores.subList(0, topK);

        return generateAnswerWithContext(question, topProducts, uid);
    }

    // 传统关键词检索方式（带上下文）
    private String chatWithKeywordSearch(String question, String uid) {
        List<Map<String, Object>> products = jdbcTemplate.queryForList(
                "SELECT pid, title, description, price FROM product WHERE status=1"
        );

        StringBuilder productInfo = new StringBuilder();
        for (Map<String, Object> p : products) {
            Object pidObj = p.get("pid");
            String title = String.valueOf(p.get("title"));
            String desc = String.valueOf(p.get("description"));
            String price = String.valueOf(p.get("price"));
            productInfo.append("【ID:").append(pidObj).append("】")
                    .append(title).append(" ").append(desc)
                    .append("，价格").append(price).append("元\n");
        }

        // 获取历史上下文和用户个人数据
        List<Map<String, String>> history = getRecentHistory(uid);
        StringBuilder historyStr = new StringBuilder();
        if (!history.isEmpty()) {
            historyStr.append("\n对话历史：\n");
            for (Map<String, String> msg : history) {
                String role = msg.get("role").equals("user") ? "用户" : "客服";
                historyStr.append(role).append(": ").append(msg.get("content")).append("\n");
            }
        }
        String userCtx = buildUserContext(uid);

        String prompt = """
                你是校园二手平台智能客服。
                下面是平台在售商品：
                %s
                %s%s
                用户问题：%s
                规则：
                1. 如果用户问商品，从上面列表里回答，不要编造。
                2. 用户没说明要买什么、只打招呼、说"随便""推荐""有什么" → 自动推荐2款合适的商品。
                3. 如果用户问的和商品无关，正常聊天回答，不必聊价格。
                4. 回答简洁、自然、像真人客服。
                5. 结合对话历史理解用户意图。
                6. 用户问'我发布了什么''我的收藏'等问题时，以"当前用户个人数据"为准，准确报出数量和清单。
                """.formatted(productInfo.toString(), historyStr.toString(), userCtx, question);

        return callDeepSeekChat(prompt, question);
    }

    // 调用AI生成RAG回答（带上下文）
    private String generateAnswerWithContext(String question, List<ProductScore> products, String uid) {
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < products.size(); i++) {
            ProductScore p = products.get(i);
            context.append(i+1).append(". 【ID:").append(p.pid).append(" ").append(p.title).append("】")
                    .append(p.description)
                    .append("，价格：").append(String.format("%.2f", p.price)).append("元\n");
        }

        // 获取历史上下文
        List<Map<String, String>> history = getRecentHistory(uid);
        StringBuilder historyStr = new StringBuilder();
        if (!history.isEmpty()) {
            historyStr.append("\n对话历史：\n");
            for (Map<String, String> msg : history) {
                String role = msg.get("role").equals("user") ? "用户" : "客服";
                historyStr.append(role).append(": ").append(msg.get("content")).append("\n");
            }
        }

        String userCtx = buildUserContext(uid);

        String systemPrompt = "你是校园二手平台的智能客服。只基于下面的商品信息回答，不要编造。回答简洁自然。结合对话历史理解用户意图。" +
                "用户问'我发布了什么''我的收藏'时，以用户Prompt中提供的个人数据为准。";

        String userPrompt = "用户问题：" + question + "\n\n相关商品：\n" + context.toString()
                + historyStr.toString() + userCtx;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + siliconflowApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", systemPrompt);
        messages.add(sysMsg);

        // 添加历史对话到消息列表
        for (Map<String, String> msg : history) {
            Map<String, String> historyMsg = new HashMap<>();
            historyMsg.put("role", msg.get("role"));
            historyMsg.put("content", msg.get("content"));
            messages.add(historyMsg);
        }

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);
        messages.add(userMsg);

        Map<String, Object> req = new HashMap<>();
        req.put("model", "deepseek-ai/DeepSeek-V3");
        req.put("messages", messages);
        req.put("temperature", 0.7);
        req.put("max_tokens", 500);

        HttpEntity<Object> entity = new HttpEntity<>(req, headers);
        ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                siliconflowBaseUrl,
                HttpMethod.POST, entity, new ParameterizedTypeReference<>() {}
        );

        if (resp.getStatusCode().value() != 200 || resp.getBody() == null) {
            return callDeepSeekChat("", question);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) resp.getBody().get("choices");
        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String reply = (String) message.get("content");
        return reply.replace("**", "");
    }

    // 调用DeepSeek Chat API
    private String callDeepSeekChat(String systemPrompt, String userQuestion) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + deepseekApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", systemPrompt);
        messages.add(sysMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userQuestion);
        messages.add(userMsg);

        Map<String, Object> req = new HashMap<>();
        req.put("model", "deepseek-chat");
        req.put("temperature", 0.7f);
        req.put("stream", false);
        req.put("messages", messages);

        HttpEntity<Object> entity = new HttpEntity<>(req, headers);
        ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                deepseekBaseUrl,
                HttpMethod.POST, entity, new ParameterizedTypeReference<>() {}
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) resp.getBody().get("choices");
        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String reply = (String) message.get("content");
        return reply.replace("**", "");
    }

    // 余弦相似度计算
    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += v1.get(i) * v1.get(i);
            norm2 += v2.get(i) * v2.get(i);
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // 内部类：商品评分
    static class ProductScore {
        int pid;
        String title;
        String description;
        double price;
        double score;

        ProductScore(int pid, String title, String description, double price, double score) {
            this.pid = pid;
            this.title = title;
            this.description = description;
            this.price = price;
            this.score = score;
        }
    }
}