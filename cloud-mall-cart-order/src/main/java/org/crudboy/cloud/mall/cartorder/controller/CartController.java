package org.crudboy.cloud.mall.cartorder.controller;

import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.bcel.Const;
import org.crudboy.cloud.mall.cartorder.feign.UserFeignClient;
import org.crudboy.cloud.mall.cartorder.model.vo.CartVO;
import org.crudboy.cloud.mall.cartorder.service.CartService;
import org.crudboy.cloud.mall.common.common.ApiRestResponse;
import org.crudboy.cloud.mall.common.common.Constant;
import org.crudboy.cloud.mall.user.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 购物车模块路由
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    UserFeignClient userFeignClient;

    @ApiOperation("购物车列表")
    @GetMapping("/list")
    public ApiRestResponse list(Integer productId, Integer count) {
        User currentUser = userFeignClient.getCurrentUser();
        List<CartVO> cartVOList = cartService.list(currentUser.getId());
        return ApiRestResponse.success(cartVOList);
    }

    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public ApiRestResponse add(Integer productId, Integer count) {
        User currentUser = userFeignClient.getCurrentUser();
        List<CartVO> cartVOList = cartService.add(currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @ApiOperation("更新购物车")
    @PostMapping("/update")
    public ApiRestResponse update(Integer productId, Integer count) {
        User currentUser = userFeignClient.getCurrentUser();
        List<CartVO> cartVOList = cartService.update(currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @ApiOperation("删除购物车")
    @PostMapping("/delete")
    public ApiRestResponse delete(Integer productId) {
        User currentUser = userFeignClient.getCurrentUser();
        cartService.delete(currentUser.getId(), productId);
        return ApiRestResponse.success();
    }


    @ApiOperation("选中购物车商品")
    @PostMapping("/select")
    public ApiRestResponse select(Integer productId, Integer selected) {
        User currentUser = userFeignClient.getCurrentUser();
        List<CartVO> cartVOS = cartService.selectOrNot(currentUser.getId(), productId, selected);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("全选购物车商品")
    @PostMapping("/selectAll")
    public ApiRestResponse select(Integer selected) {
        User currentUser = userFeignClient.getCurrentUser();
        List<CartVO> cartVOS = cartService.selectAllOrNot(currentUser.getId(), selected);
        return ApiRestResponse.success(cartVOS);
    }
}
