package org.crudboy.cloud.mall.cartorder.service.impl;

import org.crudboy.cloud.mall.cartorder.feign.ProductFeignClient;
import org.crudboy.cloud.mall.cartorder.model.dao.CartMapper;
import org.crudboy.cloud.mall.cartorder.model.pojo.Cart;
import org.crudboy.cloud.mall.cartorder.model.vo.CartVO;
import org.crudboy.cloud.mall.cartorder.service.CartService;
import org.crudboy.cloud.mall.categoryproduct.model.pojo.Product;
import org.crudboy.cloud.mall.common.common.Constant;
import org.crudboy.cloud.mall.common.exception.MallException;
import org.crudboy.cloud.mall.common.exception.MallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    CartMapper cartMapper;

    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            // 物品数目合法性校验
            validProduct(productId, count);
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.CartSelectedStatus.SELECTED);
            cartMapper.insertSelective(cart);
        } else {
            count += cart.getQuantity();
            validProduct(productId, count);

            Cart cartNew = new Cart();
            BeanUtils.copyProperties(cart, cartNew);
            cartNew.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return list(userId);
    }

    /**
     * 添加行为的合法性校验
     * @param productId 商品id
     * @param count 添加数量
     */
    private void validProduct(Integer productId, Integer count) {
        Product product = productFeignClient.detailForFeign(productId);
        if (product == null || !product.getStatus().equals(Constant.ProductStatus.SALE)) {
           throw new MallException(MallExceptionEnum.PRODUCT_DISABLE);
        }

        if (count > product.getStock()) {
            throw new MallException(MallExceptionEnum.NOT_ENOUGH);
        }
    }

    /**
     * 返回当前用户购物车列表
     * @param userId 用户id
     */
    @Override
    public List<CartVO> list(Integer userId) {
        List<CartVO> cartVOList = cartMapper.selectList(userId);
        for (CartVO cartVO : cartVOList) {
            cartVO.setTotalPrice(cartVO.getQuantity() * cartVO.getPrice());
        }
        return cartVOList;
    }

    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);

        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        } else {
            Cart cartNew = new Cart();
            BeanUtils.copyProperties(cart, cartNew);
            cartNew.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cartNew);
            if (count == 0) {
                delete(userId, productId);
            }
        }
        return list(userId);
    }

    @Override
    public void delete(Integer userId, Integer productId) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        } else {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    /**
     * 选中或取消购物车
     * @param userId 用户id
     * @param productId 商品id
     * @param selected 勾选状态： 0代表未勾选，1代表已勾选
     */
    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        } else {
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return list(userId);
    }

    /**
     * 选中或取消所有购物车
     */
    @Override
    public List<CartVO> selectAllOrNot(Integer userId, Integer selected) {
        cartMapper.selectOrNot(userId, null, selected);
        return list(userId);
    }
}
