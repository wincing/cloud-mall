package org.crudboy.cloud.mall.cartorder.service;

import com.github.pagehelper.PageInfo;
import org.crudboy.cloud.mall.cartorder.model.request.CreateOrderReq;
import org.crudboy.cloud.mall.cartorder.model.vo.OrderVO;
import org.crudboy.cloud.mall.user.model.pojo.User;

public interface OrderService {
    String create(Integer userId, CreateOrderReq createOrderReq);

    OrderVO detail(Integer userId, String orderNo);

    PageInfo selectForCustomer(Integer userId, Integer pageNum, Integer pageSize);

    void cancel(Integer userId, String orderNo);

    // String qrCode(String orderNo);

    PageInfo selectForAdmin(Integer pageNum, Integer pageSize);

    void pay(String orderNo);

    void deliver(String orderNo);

    void finish(User user, String orderNo);
}
