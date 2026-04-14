package com.example.campus_trade.service;

import java.util.List;

public interface CollectService {
    boolean addCollect(Integer uid, Integer pid);
    boolean deleteCollect(Integer uid, Integer pid);
    boolean isCollect(Integer uid, Integer pid);
    List<Integer> getMyCollectIds(Integer uid);
}
