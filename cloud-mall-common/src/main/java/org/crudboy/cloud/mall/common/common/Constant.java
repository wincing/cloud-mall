package org.crudboy.cloud.mall.common.common;

import com.google.common.collect.Sets;

import org.crudboy.cloud.mall.common.exception.MallException;
import org.crudboy.cloud.mall.common.exception.MallExceptionEnum;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Constant {

    // 标识用户会话的key
    public static final String MALL_USER = "mall_user";

    // 支持商品排序的方式
    public static Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");

    /**
     * 商品状态
     */
    public interface ProductStatus {
        int NOT_SALE = 0;
        int SALE = 1;
    }

    /**
     * 购物车商品选择状态
     */
    public interface CartSelectedStatus {
        int NOT_SELECT = 0;
        int SELECTED = 1;
    }

    /**
     * 订单状态
     */
    public enum OrderStatusEnum {
        CANCELED(0, "用户已取消"),
        NOT_PAID(10, "未付款"),
        PAID(20, "已付款"),
        DELIVERED(30, "已发货"),
        FINISHED(40, "交易完成");

        private final String value;
        private final int code;

        OrderStatusEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        /**
         * 根据订单状态码返回状态枚举类
         * @param code 订单状态码
         * @return 订单状态枚举类
         */
        public static OrderStatusEnum codeOf (int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.code == code) {
                    return orderStatusEnum;
                }
            }
            throw new MallException(MallExceptionEnum.NOT_ENUM);
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

    }
}
