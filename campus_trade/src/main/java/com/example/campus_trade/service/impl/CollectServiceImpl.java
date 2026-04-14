package com.example.campus_trade.service.impl;

import com.example.campus_trade.mapper.CollectMapper;
import com.example.campus_trade.service.CollectService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CollectServiceImpl implements CollectService {

    private final CollectMapper collectMapper;

    // 构造器注入
    public CollectServiceImpl(CollectMapper collectMapper) {
        this.collectMapper = collectMapper;
    }

    @Override
    public boolean addCollect(Integer uid, Integer pid) {
        if (uid == null || pid == null || uid <= 0 || pid <= 0) {
            return false;
        }
        return collectMapper.addCollect(uid, pid) > 0;
    }

    @Override
    public boolean deleteCollect(Integer uid, Integer pid) {
        if (uid == null || pid == null || uid <= 0 || pid <= 0) {
            return false;
        }
        return collectMapper.deleteCollect(uid, pid) > 0;
    }

    @Override
    public boolean isCollect(Integer uid, Integer pid) {
        if (uid == null || pid == null || uid <= 0 || pid <= 0) {
            return false;
        }
        return collectMapper.isCollect(uid, pid) > 0;
    }

    @Override
    public List<Integer> getMyCollectIds(Integer uid) {
        if (uid == null || uid <= 0) {
            return null;
        }
        return collectMapper.getMyCollectIds(uid);
    }
}