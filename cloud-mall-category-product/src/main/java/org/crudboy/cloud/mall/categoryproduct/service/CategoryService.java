package org.crudboy.cloud.mall.categoryproduct.service;


import com.github.pagehelper.PageInfo;
import org.crudboy.cloud.mall.categoryproduct.model.pojo.Category;
import org.crudboy.cloud.mall.categoryproduct.model.request.AddCategoryReq;
import org.crudboy.cloud.mall.categoryproduct.model.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    void add(AddCategoryReq addCategoryReq);

    void update(Category category);

    void delete(Integer id);

    PageInfo selectForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> selectForCustomer(Integer parentId);
}
