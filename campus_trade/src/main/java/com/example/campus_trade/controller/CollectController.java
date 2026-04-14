package com.example.campus_trade.controller;

import com.example.campus_trade.service.CollectService;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/collect")
public class CollectController {

    private final CollectService collectService;

    public CollectController(CollectService collectService) {
        this.collectService = collectService;
    }

    // 收藏商品
    @PostMapping("/add")
    public String add(@RequestParam Integer uid, @RequestParam Integer pid) {
        try {
            boolean success = collectService.addCollect(uid, pid);
            return success ? "收藏成功" : "收藏失败";
        } catch (Exception e) {
            e.printStackTrace();
            return "收藏失败：" + e.getMessage();
        }
    }

    // 取消收藏
    @PostMapping("/delete")
    public String delete(@RequestParam Integer uid, @RequestParam Integer pid) {
        try {
            boolean success = collectService.deleteCollect(uid, pid);
            return success ? "取消收藏成功" : "取消收藏失败";
        } catch (Exception e) {
            e.printStackTrace();
            return "取消收藏失败：" + e.getMessage();
        }
    }

    // 查询是否已收藏
    @GetMapping("/isCollect")
    public Map<String, Object> isCollect(@RequestParam Integer uid, @RequestParam Integer pid) {
        Map<String, Object> map = new HashMap<>();
        try {
            boolean isCollect = collectService.isCollect(uid, pid);
            map.put("code", 200);
            map.put("isCollect", isCollect);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 500);
            map.put("isCollect", false);
        }
        return map;
    }

    // 获取我的收藏商品ID列表
    @GetMapping("/myIds")
    public Map<String, Object> getMyCollectIds(@RequestParam Integer uid) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("code", 200);
            map.put("ids", collectService.getMyCollectIds(uid));
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 500);
            map.put("ids", null);
        }
        return map;
    }
}
