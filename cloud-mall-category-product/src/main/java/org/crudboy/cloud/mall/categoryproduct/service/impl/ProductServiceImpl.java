package org.crudboy.cloud.mall.categoryproduct.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.crudboy.cloud.mall.categoryproduct.model.dao.ProductMapper;
import org.crudboy.cloud.mall.categoryproduct.model.file.FdfsFile;
import org.crudboy.cloud.mall.categoryproduct.model.pojo.Product;
import org.crudboy.cloud.mall.categoryproduct.model.query.ProductListQuery;
import org.crudboy.cloud.mall.categoryproduct.model.request.AddProductReq;
import org.crudboy.cloud.mall.categoryproduct.model.request.ListProductReq;
import org.crudboy.cloud.mall.categoryproduct.model.vo.CategoryVO;
import org.crudboy.cloud.mall.categoryproduct.service.CategoryService;
import org.crudboy.cloud.mall.categoryproduct.service.ProductService;
import org.crudboy.cloud.mall.categoryproduct.util.FdfsUtil;
import org.crudboy.cloud.mall.common.common.Constant;
import org.crudboy.cloud.mall.common.exception.MallException;
import org.crudboy.cloud.mall.common.exception.MallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryService categoryService;


    @Override
    public void add(AddProductReq addProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);

        Product productOld = productMapper.selectByName(product.getName());
        if (productOld != null) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }

        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new MallException(MallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public URI upload(MultipartFile multipartFile) {
        // ???????????????
        String fileName = multipartFile.getOriginalFilename();
        // ??????????????????
        byte[] content = null;
        try {
            content = multipartFile.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ?????????????????????
        String ext = "";
        if (fileName != null && !"".equals(fileName)) {
            ext = fileName.substring(fileName.lastIndexOf("."));
        }
        // ???????????????????????????
        FdfsFile fastDFSFile = new FdfsFile(fileName, content, ext);
        // ??????
        String[] uploadResults = FdfsUtil.upload(fastDFSFile);

        try {
            // ????????????????????????????????????????????????, uploadResults[0]?????????, uploadResults[1]????????????????????????
            String path = FdfsUtil.getTrackerUrl() + uploadResults[0] + "/" + uploadResults[1];
            return new URI(path);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Product product) {
        Product productOld = productMapper.selectByName(product.getName());
        if (productOld != null && !productOld.getId().equals(product.getId())) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }

        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count == 0) {
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        if (productOld == null) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public void batchUpdateStatus(Integer[] ids, Integer status) {
        productMapper.batchUpdateStatusById(ids, status);
    }

    @Override
    public PageInfo selectForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    @Override
    public PageInfo selectForCustomer(ListProductReq listProductReq) {
        // ???????????????????????????
        ProductListQuery productListQuery = new ProductListQuery();
        if (!StringUtils.isEmpty(listProductReq.getKeyword())) {
            String keyword =
                    new StringBuilder().append("%").append(listProductReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }

        // ?????????????????????
        Integer rootId = listProductReq.getCategoryId();
        if(rootId != null) {
            List<CategoryVO> categoryVOList = categoryService.selectForCustomer(rootId);
            ArrayList<Integer> categoryIdList = new ArrayList<>();
            // ?????????????????????????????????
            categoryIdList.add(rootId);

            dfsCategory(categoryVOList, categoryIdList);
            productListQuery.setCategoryIds(categoryIdList);
        }

        String orderBy = listProductReq.getOrderBy();
        if (Constant.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(listProductReq.getPageNum(), listProductReq.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(listProductReq.getPageNum(), listProductReq.getPageSize());
        }

        List<Product> productList = productMapper.selectList(productListQuery);
        return new PageInfo(productList);
    }

    /**
     * ??????????????????????????????list???
     * @param categoryVOList
     * @param categoryIdList
     */
    private void dfsCategory(List<CategoryVO> categoryVOList, List categoryIdList) {
        for (CategoryVO categoryVO : categoryVOList) {
            if (categoryVO != null) {
                categoryIdList.add(categoryVO.getId());
                dfsCategory(categoryVO.getChildCategory(), categoryIdList);
            }
        }
    }

    @Override
    public void updateStock(Integer productId, Integer stock) {
        Product product = new Product();
        product.setId(productId);
        product.setStock(stock);
        productMapper.updateByPrimaryKeySelective(product);
    }
}
