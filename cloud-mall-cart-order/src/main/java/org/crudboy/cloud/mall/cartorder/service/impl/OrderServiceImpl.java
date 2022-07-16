package org.crudboy.cloud.mall.cartorder.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import org.crudboy.cloud.mall.cartorder.feign.ProductFeignClient;
import org.crudboy.cloud.mall.cartorder.feign.UserFeignClient;
import org.crudboy.cloud.mall.cartorder.model.dao.CartMapper;
import org.crudboy.cloud.mall.cartorder.model.dao.OrderItemMapper;
import org.crudboy.cloud.mall.cartorder.model.dao.OrderMapper;
import org.crudboy.cloud.mall.cartorder.model.pojo.Order;
import org.crudboy.cloud.mall.cartorder.model.pojo.OrderItem;
import org.crudboy.cloud.mall.cartorder.model.request.CreateOrderReq;
import org.crudboy.cloud.mall.cartorder.model.vo.CartVO;
import org.crudboy.cloud.mall.cartorder.model.vo.OrderItemVO;
import org.crudboy.cloud.mall.cartorder.model.vo.OrderVO;
import org.crudboy.cloud.mall.cartorder.service.CartService;
import org.crudboy.cloud.mall.cartorder.service.OrderService;
import org.crudboy.cloud.mall.cartorder.util.OrderCodeFactory;
import org.crudboy.cloud.mall.categoryproduct.model.pojo.Product;
import org.crudboy.cloud.mall.common.common.Constant;
import org.crudboy.cloud.mall.common.exception.MallException;
import org.crudboy.cloud.mall.common.exception.MallExceptionEnum;
import org.crudboy.cloud.mall.common.util.QRCodeGenerator;
import org.crudboy.cloud.mall.user.model.pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(Integer userId, CreateOrderReq createOrderReq) {
        // 获取购物车中选中的商品
        List<CartVO> cartVOList = cartService.list(userId);
        List<CartVO> cartVOSListTemp = new ArrayList();
        for (CartVO cartVO : cartVOList) {
            if (cartVO.getSelected().equals(Constant.CartSelectedStatus.SELECTED)) {
                cartVOSListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOSListTemp;

        if (cartVOList.isEmpty()) {
            throw new MallException(MallExceptionEnum.CART_EMPTY);
        }

        // 检测商品合法性
        validOrder(cartVOList);

        // 转化为订单项
        List<OrderItem> orderItemList = convertToOrderItem(cartVOList);

        for (OrderItem orderItem : orderItemList) {
            Product product = productFeignClient.detailForFeign(orderItem.getProductId());
            // 计算商品扣减后的库存
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new MallException(MallExceptionEnum.NOT_ENOUGH);
            }
            productFeignClient.updateStock(product.getId(), stock);
        }

        // 清空购物车列表中已经完成下单的订单项
        cleanCart(cartVOList);

        Order order = new Order();
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));

        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);

        orderMapper.insertSelective(order);

        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(orderNo);
            orderItemMapper.insertSelective(orderItem);
        }
        return orderNo;
    }

    /**
     * 检测订单每项商品的合法性
     * @param cartVOList 订单中的购物车列表
     */
    private void validOrder(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            Product product = productFeignClient.detailForFeign(cartVO.getProductId());
            if (product == null || !product.getStatus().equals(Constant.ProductStatus.SALE)) {
                throw new MallException(MallExceptionEnum.PRODUCT_DISABLE);
            }

            if (cartVO.getQuantity() > product.getStock()) {
                throw new MallException(MallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    /**
     * 将cartVO转化为OrderItem
     * @param cartVOList 购物车视图模型列表
     */
    private List<OrderItem> convertToOrderItem(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList = new ArrayList<>();

        for (CartVO cartVO : cartVOList) {
            OrderItem orderItem = new OrderItem();
            BeanUtils.copyProperties(cartVO, orderItem);
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    /**
     * 清空购物车列表中已经完成下单的订单项
     */
    private void cleanCart(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    /**
     * 计算订单总价
     * @param orderItemList 订单项列表
     * @return 总价格
     */
    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer total = 0;
        for (OrderItem orderItem : orderItemList) {
            total += orderItem.getTotalPrice();
        }
        return total;
    }

    @Override
    public OrderVO detail(Integer userId, String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NOT_ORDER);
        }
        if (!userId.equals(order.getUserId())) {
            throw new MallException(MallExceptionEnum.ORDER_MISMATCH);
        }
        return convertToOrderVo(order);
    }

    private OrderVO convertToOrderVo(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());

        List<OrderItemVO> orderItemVOList =  new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        String orderStatus = Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue();
        orderVO.setOrderStatusName(orderStatus);

        return orderVO;
    }

    private List<OrderVO> convertToOrderVoList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();

        for (Order order : orderList) {
            OrderVO orderVO = convertToOrderVo(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    /**
     * 查找某个用户的所有订单
     * @param userId 用户id
     * @param pageNum 请求的第几页
     * @param pageSize 每页的数据条数
     */
    @Override
    public PageInfo selectForCustomer(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList = convertToOrderVoList(orderList);
        return new PageInfo(orderVOList);
    }

    /**
     * 取消订单
     * @param userId 用户id
     * @param orderNo 订单号
     */
    @Override
    public void cancel(Integer userId, String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NOT_ORDER);
        }
        if (!order.getUserId().equals(userId)) {
            throw new MallException(MallExceptionEnum.ORDER_MISMATCH);
        }

        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    /**
     * 获取所有订单
     * @param pageNum 请求的第几页
     * @param pageSize 每页的数据条数
     */
    @Override
    public PageInfo selectForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAll();
        List<OrderVO> orderVOList = convertToOrderVoList(orderList);
        return new PageInfo(orderVOList);
    }

    /**
     * 将订单状态修改为已支付，未对接实际支付接口，为模拟支付
     * @param orderNo
     */
    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NOT_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }


    /**
     * 将订单状态修改为已发货
     * @param orderNo
     */
    @Override
    public void deliver(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NOT_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    /**
     * 将订单状态修改为完结
     * @param orderNo
     */
    @Override
    public void finish(User user, String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NOT_ORDER);
        }
        if (!userFeignClient.checkAdminRole(user) &&
                !user.getId().equals(order.getUserId())) {
            throw new MallException(MallExceptionEnum.ORDER_MISMATCH);
        }

        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.DELIVERED.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }
}
