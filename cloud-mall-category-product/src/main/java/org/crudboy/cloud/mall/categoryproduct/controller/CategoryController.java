package org.crudboy.cloud.mall.categoryproduct.controller;


import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.crudboy.cloud.mall.categoryproduct.model.pojo.Category;
import org.crudboy.cloud.mall.categoryproduct.model.request.AddCategoryReq;
import org.crudboy.cloud.mall.categoryproduct.model.request.UpdateCategoryReq;
import org.crudboy.cloud.mall.categoryproduct.model.vo.CategoryVO;
import org.crudboy.cloud.mall.categoryproduct.service.CategoryService;
import org.crudboy.cloud.mall.common.common.ApiRestResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 目录模块路由
 */
@Controller
@ResponseBody
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @ApiOperation("后台添加目录")
    @PostMapping("/admin/category/add")
    public ApiRestResponse addCategory(HttpSession session, @RequestBody @Valid AddCategoryReq addCategory) {
        categoryService.add(addCategory);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台更新目录")
    @PostMapping("/admin/category/update")
    public ApiRestResponse updateCategory(HttpSession session, @RequestBody @Valid UpdateCategoryReq updateCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryReq, category);
        categoryService.update(category);

        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除目录")
    @PostMapping("/admin/category/delete")
    public ApiRestResponse delete(Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台列举目录")
    @GetMapping("/admin/category/list")
    public ApiRestResponse selectForAdmin(Integer pageNum, Integer pageSize) {
        PageInfo pageInfo = categoryService.selectForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台列举目录")
    @GetMapping("/category/list")
    public ApiRestResponse selectForCustomer() {
        List<CategoryVO> categoryVOList = categoryService.selectForCustomer(0);
        return ApiRestResponse.success(categoryVOList);
    }

}
