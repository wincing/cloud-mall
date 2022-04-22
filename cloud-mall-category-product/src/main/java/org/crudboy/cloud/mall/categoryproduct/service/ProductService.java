package org.crudboy.cloud.mall.categoryproduct.service;

import com.github.pagehelper.PageInfo;
import org.crudboy.cloud.mall.categoryproduct.model.pojo.Product;
import org.crudboy.cloud.mall.categoryproduct.model.request.AddProductReq;
import org.crudboy.cloud.mall.categoryproduct.model.request.ListProductReq;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * 商品服务接口
 */
public interface ProductService {

    void add(AddProductReq addProductReq);

    URI upload(HttpServletRequest request, MultipartFile file);

    void update(Product product);

    void delete(Integer id);

    void batchUpdateStatus(Integer[] ids, Integer status);

    PageInfo selectForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo selectForCustomer(ListProductReq listProductReq);

    void updateStock(Integer productId, Integer stock);
}
