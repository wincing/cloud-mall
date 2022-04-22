package org.crudboy.cloud.mall.categoryproduct.model.dao;

import org.apache.ibatis.annotations.Param;
import org.crudboy.cloud.mall.categoryproduct.model.pojo.Product;
import org.crudboy.cloud.mall.categoryproduct.model.query.ProductListQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product selectByName(String name);

    int batchUpdateStatusById(@Param("ids") Integer[] ids, @Param("status") Integer status);

    List<Product> selectListForAdmin();

    List<Product> selectList(@Param("query") ProductListQuery productListQuery);
}