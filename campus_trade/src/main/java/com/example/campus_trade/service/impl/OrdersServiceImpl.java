package com.example.campus_trade.service.impl;

import com.example.campus_trade.entity.Orders;
import com.example.campus_trade.mapper.OrdersMapper;
import com.example.campus_trade.service.OrdersService;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    private final OrdersMapper ordersMapper;

    // 构造器注入
    public OrdersServiceImpl(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }

    @Override
    public List<Orders> getAllOrders() {
        return ordersMapper.findAll();
    }

    @Override
    public Orders getOrderById(long oid) {
        if (oid <= 0) {
            return null;
        }
        return ordersMapper.findById(oid);
    }

    @Override
    public List<Orders> getOrdersByBuyerId(long buyerId) {
        if (buyerId <= 0) {
            return null;
        }
        return ordersMapper.findByBuyerId(buyerId);
    }

    @Override
    public List<Orders> getOrdersBySellerId(long sellerId) {
        if (sellerId <= 0) {
            return null;
        }
        return ordersMapper.findBySellerId(sellerId);
    }

    @Override
    public List<Orders> getOrdersByPid(long pid) {
        if (pid <= 0) {
            return null;
        }
        return ordersMapper.findByPid(pid);
    }

    @Override
    public List<Orders> getOrdersByStatus(long status) {
        if (status < 0 || status > 2) {
            return null;
        }
        return ordersMapper.findByStatus(status);
    }

    @Override
    public boolean addOrder(Orders orders) {
        if (orders == null || orders.getPid() <= 0 || orders.getBuyerId() <= 0 || orders.getSellerId() <= 0) {
            return false;
        }
        orders.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return ordersMapper.addOrders(orders) > 0;
    }

    @Override
    public boolean updateOrder(Orders orders) {
        if (orders == null || orders.getOid() <= 0) {
            return false;
        }
        return ordersMapper.updateOrders(orders) > 0;
    }

    @Override
    public boolean updateOrderStatus(long oid, long status) {
        if (oid <= 0 || status < 0 || status > 2) {
            return false;
        }
        return ordersMapper.updateOrderStatus(oid, status) > 0;
    }

    @Override
    public void buyerConfirmOrder(long oid) {
        Orders order = getOrderById(oid);
        if (order == null) return;

        if (order.getStatus() == 0) {
            ordersMapper.updateOrderStatus(oid, 3);
        } else if (order.getStatus() == 4) {
            ordersMapper.updateOrderStatus(oid, 1);
        }
    }

    @Override
    public void sellerConfirmOrder(long oid) {
        Orders order = getOrderById(oid);
        if (order == null) return;

        if (order.getStatus() == 0) {
            ordersMapper.updateOrderStatus(oid, 4);
        } else if (order.getStatus() == 3) {
            ordersMapper.updateOrderStatus(oid, 1);
        }
    }

    @Override
    public boolean deleteOrder(long oid) {
        if (oid <= 0) {
            return false;
        }
        return ordersMapper.deleteOrders(oid) > 0;
    }
}