package org.crudboy.cloud.mall.cartorder.service;

import org.crudboy.cloud.mall.cartorder.model.vo.CartVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    List<CartVO> add(Integer userId, Integer productId, Integer count);

    List<CartVO> list(Integer userId);

    List<CartVO> update(Integer userId, Integer productId, Integer count);

    void delete(Integer userId, Integer productId);

    List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected);

    List<CartVO> selectAllOrNot(Integer userId, Integer selected);
}
