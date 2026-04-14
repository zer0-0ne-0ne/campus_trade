package com.example.campus_trade.service;

import com.example.campus_trade.entity.Orders;
import java.util.List;

public interface OrdersService {
    // 查询所有订单
    List<Orders> getAllOrders();

    // 根据ID查询订单
    Orders getOrderById(long oid);

    // 根据买家ID查询订单
    List<Orders> getOrdersByBuyerId(long buyerId);

    // 根据卖家ID查询订单
    List<Orders> getOrdersBySellerId(long sellerId);

    // 根据商品ID查询订单
    List<Orders> getOrdersByPid(long pid);

    // 根据状态查询订单
    List<Orders> getOrdersByStatus(long status);

    // 新增订单
    boolean addOrder(Orders orders);

    // 修改订单信息（交易时间/地点）
    boolean updateOrder(Orders orders);

    // 修改订单状态（完成/取消）
    boolean updateOrderStatus(long oid, long status);

    // 删除订单
    boolean deleteOrder(long oid);

    void buyerConfirmOrder(long oid);
    void sellerConfirmOrder(long oid);
}