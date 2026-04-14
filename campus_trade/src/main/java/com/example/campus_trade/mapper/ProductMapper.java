package com.example.campus_trade.mapper;

import com.example.campus_trade.entity.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductMapper {

    // 查询所有商品
    @Select("SELECT * FROM product")
    List<Product> findAll();

    // 根据PID查询商品
    @Select("SELECT * FROM product WHERE PID = #{pid}")
    Product findById(@Param("pid") long pid);

    // 根据分类ID(CID)查询商品
    @Select("SELECT * FROM product WHERE CID = #{cid}")
    List<Product> findByCid(@Param("cid") long cid);

    // 根据发布者ID(UID)查询商品
    @Select("SELECT * FROM product WHERE UID = #{uid}")
    List<Product> findByUid(@Param("uid") long uid);

    // 根据状态查询商品
    @Select("SELECT * FROM product WHERE status = #{status}")
    List<Product> findByStatus(@Param("status") long status);

    // 根据关键词模糊搜索商品
    @Select("SELECT * FROM product WHERE keywords LIKE CONCAT('%', #{keyword}, '%')")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    // 新增商品
    @Insert("INSERT INTO product(title, description, price, conditions, keywords, image_path, CID, UID, status, release_time) " +
            "VALUES (#{title}, #{description}, #{price}, #{condition}, #{keywords}, #{imagePath}, #{cid}, #{uid}, #{status}, #{releaseTime})")
    @Options(useGeneratedKeys = true, keyProperty = "pid", keyColumn = "PID")
    int addProduct(Product product);

    // 修改商品信息
    @Update("UPDATE product SET title = #{title}, description = #{description}, price = #{price}, " +
            "conditions = #{condition}, keywords = #{keywords}, image_path = #{imagePath}, CID = #{cid}, status = #{status} WHERE PID = #{pid}")
    int updateProduct(Product product);

    // 修改商品状态（上架/下架/售出等）
    @Update("UPDATE product SET status = #{status} WHERE PID = #{pid}")
    int updateProductStatus(@Param("pid") long pid, @Param("status") long status);

    // 删除商品
    @Update("UPDATE product SET status = 3 WHERE pid = #{pid}")
    int deleteProduct(@Param("pid") long pid);

    // 根据分类查询，排除自己发布的
    @Select("SELECT * FROM product WHERE cid = #{cid} AND uid != #{uid}")
    List<Product> findByCidAndNotSelf(@Param("cid") Integer cid, @Param("uid") Long uid);

    // 热门商品推荐
    @Select("SELECT * FROM product WHERE status != 3 ORDER BY pid DESC LIMIT 8")
    List<Product> findHotProducts();

    // 根据商品ID查询
    @Select("SELECT * FROM product WHERE pid = #{pid}")
    Product getProductById(Long pid);
}