package org.crudboy.cloud.mall.categoryproduct.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.crudboy.cloud.mall.categoryproduct.model.dao.ProductMapper;
import org.crudboy.cloud.mall.categoryproduct.model.pojo.Product;
import org.crudboy.cloud.mall.categoryproduct.model.query.ProductListQuery;
import org.crudboy.cloud.mall.categoryproduct.model.request.AddProductReq;
import org.crudboy.cloud.mall.categoryproduct.model.request.ListProductReq;
import org.crudboy.cloud.mall.categoryproduct.model.vo.CategoryVO;
import org.crudboy.cloud.mall.categoryproduct.service.CategoryService;
import org.crudboy.cloud.mall.categoryproduct.service.ProductService;
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

    // 文件上传地址
    public static String FILE_UPLOAD_ADDRESS;

    // zuul网关所在地址
    @Value("${file.upload.ip}")
    public String ip;

    @Value("${file.upload.port}")
    public Integer port;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryService categoryService;

    @Value("${file.upload.address}")
    private void setFileUploadAddress(String address) {
        FILE_UPLOAD_ADDRESS = address;
    }

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
    public URI upload(HttpServletRequest request, MultipartFile file) {
        // 生成新文件名
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String randomName = uuid.toString() + suffix;

        // 创建文件
        File fileDirectory = new File(FILE_UPLOAD_ADDRESS);
        File destFile = new File(FILE_UPLOAD_ADDRESS + randomName);
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {
                throw new MallException(MallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 返回创建后文件路径
        try {
            URI uri = new URI(request.getRequestURL().toString());
            uri = getHost(uri);
            return new URI(uri + "/category-product/image/" + randomName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 选择url中需要的部分
     * @param uri
     * @return
     */
    private URI getHost(URI uri) {
        URI effectiveURI = null;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), ip,
                    port, null, null, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return effectiveURI;
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
        // 处理关键字模糊匹配
        ProductListQuery productListQuery = new ProductListQuery();
        if (!StringUtils.isEmpty(listProductReq.getKeyword())) {
            String keyword =
                    new StringBuilder().append("%").append(listProductReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }

        // 查询所有子目录
        Integer rootId = listProductReq.getCategoryId();
        if(rootId != null) {
            List<CategoryVO> categoryVOList = categoryService.selectForCustomer(rootId);
            ArrayList<Integer> categoryIdList = new ArrayList<>();

            dfsCategory(categoryVOList, categoryIdList);
            productListQuery.setCategoryIds(categoryIdList);
        }

        String orderBy = listProductReq.getOrderBy();
        if (Constant.ProductLIstOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(listProductReq.getPageNum(), listProductReq.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(listProductReq.getPageNum(), listProductReq.getPageSize());
        }

        List<Product> productList = productMapper.selectList(productListQuery);
        return new PageInfo(productList);
    }

    /**
     * 深搜遍历子目录并加入list中
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
