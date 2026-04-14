package com.example.campus_trade.mapper;

import com.example.campus_trade.entity.Orders;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrdersMapper {

    // 查询所有订单
    @Select("SELECT * FROM orders")
    List<Orders> findAll();

    // 根据OID查询订单
    @Select("SELECT * FROM orders WHERE OID = #{oid}")
    Orders findById(@Param("oid") long oid);

    // 根据买家ID(buyer_id)查询订单
    @Select("SELECT * FROM orders WHERE buyer_id = #{buyerId}")
    List<Orders> findByBuyerId(@Param("buyerId") long buyerId);

    // 根据卖家ID(seller_id)查询订单
    @Select("SELECT * FROM orders WHERE seller_id = #{sellerId}")
    List<Orders> findBySellerId(@Param("sellerId") long sellerId);

    // 根据商品ID(PID)查询订单
    @Select("SELECT * FROM orders WHERE PID = #{pid}")
    List<Orders> findByPid(@Param("pid") long pid);

    // 根据状态查询订单（筛选待交易/已完成/已取消）
    @Select("SELECT * FROM orders WHERE `status` = #{status}")
    List<Orders> findByStatus(@Param("status") long status);

    // 新增订单
    @Insert("INSERT INTO orders(PID, buyer_id, seller_id, trade_time, trade_place, `status`, create_time) " +
            "VALUES (#{pid}, #{buyerId}, #{sellerId}, #{tradeTime}, #{tradePlace}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "oid", keyColumn = "OID")
    int addOrders(Orders orders);

    // 修改订单信息（仅修改交易时间/地点）
    @Update("UPDATE orders SET trade_time = #{tradeTime}, trade_place = #{tradePlace} WHERE OID = #{oid}")
    int updateOrders(Orders orders);

    // 修改订单状态（完成/取消交易）
    @Update("UPDATE orders SET `status` = #{status} WHERE OID = #{oid}")
    int updateOrderStatus(@Param("oid") long oid, @Param("status") long status);

    // 删除订单
    @Delete("DELETE FROM orders WHERE OID = #{oid}")
    int deleteOrders(@Param("oid") long oid);

    @Select("SELECT * FROM orders WHERE buyer_id = #{uid} OR seller_id = #{uid}")
    List<Orders> getUserOrders(@Param("uid") Long uid);
}