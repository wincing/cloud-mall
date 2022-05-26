package org.crudboy.cloud.mall.categoryproduct.model.query;

import java.util.List;

/**
 * 商品查询的条件
 */
public class ProductListQuery {

    private String keyword;

    // 某个目录下的子目录
    private List<Integer> categoryIds;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
