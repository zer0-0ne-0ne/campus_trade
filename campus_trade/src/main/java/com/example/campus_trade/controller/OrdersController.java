package com.example.campus_trade.controller;

import com.example.campus_trade.entity.Orders;
import com.example.campus_trade.service.OrdersService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping("/list")
    public List<Orders> getAll() {
        return ordersService.getAllOrders();
    }

    @GetMapping("/get/{oid}")
    public Orders getById(@PathVariable Long oid) {
        return ordersService.getOrderById(oid);
    }

    @GetMapping("/buyer/{buyerId}")
    public List<Orders> getByBuyer(@PathVariable Long buyerId) {
        return ordersService.getOrdersByBuyerId(buyerId);
    }

    @GetMapping("/seller/{sellerId}")
    public List<Orders> getBySeller(@PathVariable Long sellerId) {
        return ordersService.getOrdersBySellerId(sellerId);
    }

    @GetMapping("/product/{pid}")
    public List<Orders> getByProduct(@PathVariable Long pid) {
        return ordersService.getOrdersByPid(pid);
    }

    @GetMapping("/status/{status}")
    public List<Orders> getByStatus(@PathVariable Integer status) {
        return ordersService.getOrdersByStatus(status);
    }

    @PostMapping("/add")
    public String add(@RequestBody Orders orders) {
        return ordersService.addOrder(orders) ? "创建订单成功" : "创建失败";
    }

    @PutMapping("/update")
    public String update(@RequestBody Orders orders) {
        return ordersService.updateOrder(orders) ? "修改成功" : "修改失败";
    }

    @PutMapping("/status/{oid}/{status}")
    public String updateStatus(@PathVariable Long oid, @PathVariable Integer status) {
        return ordersService.updateOrderStatus(oid, status) ? "状态修改成功" : "修改失败";
    }

    @PostMapping("/cancel/{oid}")
    public String cancel(@PathVariable Long oid) {
        ordersService.updateOrderStatus(oid, 2);
        return "success";
    }

    @PostMapping("/buyerConfirm/{oid}")
    public String buyerConfirm(@PathVariable Long oid) {
        ordersService.updateOrderStatus(oid, 1);
        return "success";
    }

    @PostMapping("/sellerConfirm/{oid}")
    public String sellerConfirm(@PathVariable Long oid) {
        ordersService.updateOrderStatus(oid, 1);
        return "success";
    }

    @PostMapping("/finish/{oid}")
    public String finish(@PathVariable Long oid) {
        ordersService.updateOrderStatus(oid, 1);
        return "success";
    }

    @DeleteMapping("/delete/{oid}")
    public String delete(@PathVariable Long oid) {
        return ordersService.deleteOrder(oid) ? "删除成功" : "删除失败";
    }
}