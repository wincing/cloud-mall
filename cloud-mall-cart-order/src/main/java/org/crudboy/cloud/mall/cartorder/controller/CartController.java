package org.crudboy.cloud.mall.cartorder.controller;

import io.swagger.annotations.ApiOperation;
import org.crudboy.cloud.mall.cartorder.feign.UserFeignClient;
import org.crudboy.cloud.mall.cartorder.model.vo.CartVO;
import org.crudboy.cloud.mall.cartorder.service.CartService;
import org.crudboy.cloud.mall.common.common.ApiRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.crudboy.cloud.mall.common.common.Constant.MALL_TOKEN;

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
    public ApiRestResponse list(HttpServletRequest request,
                                Integer productId, Integer count) {
        String token = request.getHeader(MALL_TOKEN);
        Integer userId = userFeignClient.get(token);
        List<CartVO> cartVOList = cartService.list(userId);
        return ApiRestResponse.success(cartVOList);
    }

    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public ApiRestResponse add(HttpServletRequest request,
                               Integer productId, Integer count) {
        String token = request.getHeader(MALL_TOKEN);
        Integer userId = userFeignClient.get(token);
        List<CartVO> cartVOList = cartService.add(userId, productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @ApiOperation("更新购物车")
    @PostMapping("/update")
    public ApiRestResponse update(HttpServletRequest request,
                                  Integer productId, Integer count) {
        String token = request.getHeader(MALL_TOKEN);
        Integer userId = userFeignClient.get(token);
        List<CartVO> cartVOList = cartService.update(userId, productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @ApiOperation("删除购物车")
    @PostMapping("/delete")
    public ApiRestResponse delete(HttpServletRequest request, Integer productId) {
        String token = request.getHeader(MALL_TOKEN);
        Integer userId = userFeignClient.get(token);
        cartService.delete(userId, productId);
        return ApiRestResponse.success();
    }


    @ApiOperation("选中购物车商品")
    @PostMapping("/select")
    public ApiRestResponse select(HttpServletRequest request,
                                  Integer productId, Integer selected) {
        String token = request.getHeader(MALL_TOKEN);
        Integer userId = userFeignClient.get(token);
        List<CartVO> cartVOS = cartService.selectOrNot(userId, productId, selected);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("全选购物车商品")
    @PostMapping("/selectAll")
    public ApiRestResponse select(HttpServletRequest request, Integer selected) {
        String token = request.getHeader(MALL_TOKEN);
        Integer userId = userFeignClient.get(token);
        List<CartVO> cartVOS = cartService.selectAllOrNot(userId, selected);
        return ApiRestResponse.success(cartVOS);
    }
}
