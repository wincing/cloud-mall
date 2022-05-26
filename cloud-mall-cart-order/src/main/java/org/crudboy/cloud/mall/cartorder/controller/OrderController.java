package org.crudboy.cloud.mall.cartorder.controller;


import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.crudboy.cloud.mall.cartorder.feign.ProductFeignClient;
import org.crudboy.cloud.mall.cartorder.feign.UserFeignClient;
import org.crudboy.cloud.mall.cartorder.model.request.CreateOrderReq;
import org.crudboy.cloud.mall.cartorder.model.vo.OrderVO;
import org.crudboy.cloud.mall.cartorder.service.OrderService;
import org.crudboy.cloud.mall.common.common.ApiRestResponse;
import org.crudboy.cloud.mall.common.common.Constant;
import org.crudboy.cloud.mall.user.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 订单模块路由
 */
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserFeignClient userFeignClient;


    @ApiOperation("创建订单")
    @PostMapping("/order/create")
    public ApiRestResponse create(@RequestBody CreateOrderReq createOrderReq) {
        User currentUser = userFeignClient.getCurrentUser();
        String orderNo = orderService.create(currentUser.getId(), createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @ApiOperation("前台订单详情")
    @GetMapping("/order/detail")
    public ApiRestResponse detail(String orderNo) {
        User currentUser = userFeignClient.getCurrentUser();
        OrderVO orderVO = orderService.detail(currentUser.getId(), orderNo);
        return ApiRestResponse.success(orderVO);
    }

    @ApiOperation("前台订单列表")
    @GetMapping("/order/list")
    public ApiRestResponse list(Integer pageNum, Integer pageSize) {
        User currentUser = userFeignClient.getCurrentUser();
        PageInfo pageInfo = orderService.selectForCustomer(currentUser.getId(), pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台取消订单列表")
    @PostMapping("/order/cancel")
    public ApiRestResponse cancel(String orderNo) {
        User currentUser = userFeignClient.getCurrentUser();
        orderService.cancel(currentUser.getId(), orderNo);
        return ApiRestResponse.success();
    }

//    @ApiOperation("生成支付二维码")
//    @PostMapping("/order/qrcode")
//    public ApiRestResponse qrCode(String orderNo) {
//        String codeAddress = orderService.qrCode(orderNo);
//        return ApiRestResponse.success(codeAddress);
//    }

    @ApiOperation("前台订单列表")
    @GetMapping("/admin/order/list")
    public ApiRestResponse listForAdmin(Integer pageNum, Integer pageSize) {
        PageInfo pageInfo = orderService.selectForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("支付接口")
    @PostMapping("/order/pay")
    public ApiRestResponse pay (String orderNo) {
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }

    @ApiOperation("订单发货")
    @PostMapping("/admin/order/delivered")
    public ApiRestResponse delivered (String orderNo) {
        orderService.deliver(orderNo);
        return ApiRestResponse.success();
    }

    @ApiOperation("完结订单")
    @PostMapping("/order/finish")
    public ApiRestResponse finish (String orderNo) {
        User currentUser = userFeignClient.getCurrentUser();
        orderService.finish(currentUser, orderNo);
        return ApiRestResponse.success();
    }
}
