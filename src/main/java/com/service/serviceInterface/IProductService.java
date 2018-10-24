package com.service.serviceInterface;

import com.common.ServerResponse;
import com.github.pagehelper.PageInfo;
import com.pojo.Product;

import javax.servlet.http.HttpServletRequest;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer productId, Integer status);

    ServerResponse getProductList(Integer pageNum, Integer pageSize);

    ServerResponse getProductById(Integer productId);

    ServerResponse getProductByProductName(String productName,Integer pageNum,Integer pageSize);

    ServerResponse getAllProduct();

    ServerResponse uploadMainProductImg(HttpServletRequest request,Integer productId);

    int updateProduct(Product product);

    ServerResponse<Product> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);

}
