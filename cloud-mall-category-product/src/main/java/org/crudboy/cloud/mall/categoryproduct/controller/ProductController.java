package org.crudboy.cloud.mall.categoryproduct.controller;


import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.crudboy.cloud.mall.categoryproduct.model.pojo.Product;
import org.crudboy.cloud.mall.categoryproduct.model.request.AddProductReq;
import org.crudboy.cloud.mall.categoryproduct.model.request.ListProductReq;
import org.crudboy.cloud.mall.categoryproduct.model.request.UpdateProductReq;
import org.crudboy.cloud.mall.categoryproduct.service.ProductService;
import org.crudboy.cloud.mall.common.common.ApiRestResponse;
import org.crudboy.cloud.mall.common.exception.MallException;
import org.crudboy.cloud.mall.common.exception.MallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;


/**
 * 商品模块路由
 */
@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/file/upload")
    public ApiRestResponse uploadFile(@RequestParam("file") MultipartFile file) {
        URI uri = productService.upload(file);
        if (uri == null) {
            throw new MallException(MallExceptionEnum.UPLOAD_FILE_FAILED);
        }
        return ApiRestResponse.success(uri);
    }

    @ApiOperation("后台更新商品")
    @PostMapping("/admin/product/update")
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除商品")
    @PostMapping("/admin/product/delete")
    public ApiRestResponse deleteProduct(Integer id) {
        productService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量更新上架状态")
    @PostMapping("/admin/product/batchUpdateStatus")
    public ApiRestResponse batchUpdateProductStatus(Integer[] ids, Integer status) {
        productService.batchUpdateStatus(ids, status);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台显示商品列表")
    @GetMapping("/admin/product/list")
    public ApiRestResponse selectProductsForAdmin(Integer pageNum, Integer pageSize) {
        PageInfo pageInfo = productService.selectForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前端显示单个商品详情")
    @GetMapping("/product/detail")
    public ApiRestResponse productDetail(Integer id) {
        Product product = productService.detail(id);
        return ApiRestResponse.success(product);
    }

    @ApiOperation("前端显示商品列表")
    @GetMapping("/product/list")
    public ApiRestResponse selectProductsForCustomer(ListProductReq listProductReq) {
        PageInfo pageInfo = productService.selectForCustomer(listProductReq);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("显示单个商品详情(其它模块")
    @GetMapping("/product/detailForFeign")
    public Product detailForFeign(@RequestParam Integer id) {
        return productService.detail(id);
    }

    @ApiOperation("更新库存")
    @PostMapping("/product/updateStock")
    public void updateStock(@RequestParam Integer productId, @RequestParam Integer stock) {
        productService.updateStock(productId, stock);
    }

}
