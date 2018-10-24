package com.service.serviceImpl;

import com.common.ResponseCode;
import com.common.ServerResponse;
import com.dao.ProductImgMapper;
import com.dao.ProductMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.pojo.Product;
import com.pojo.ProductExample;
import com.pojo.ProductImg;
import com.pojo.ProductImgExample;
import com.service.serviceInterface.IProductService;
import com.util.FtpFileUploadUtil;
import com.util.ImageUtil;
import com.util.PropertiesUtil;
import com.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImgMapper productImgMapper;

    //新增与保存
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product != null) {
//           //todo 设置mainimage

            if (product.getId() != null) {
                //更新，id不为空
                int count = productMapper.updateByPrimaryKey(product);
                if (count > 0) {
                    return ServerResponse.createBySuccessMessage("更新产品成功");
                }
            } else {
                //id为空，保存
                int saveCount = productMapper.insertSelective(null);
                if (saveCount > 0) {
                    return ServerResponse.createBySuccessMessage("保存产品成功");
                }
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    //更新产品状态
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int count = productMapper.updateByPrimaryKey(product);
        if (count > 0) {
            return ServerResponse.createBySuccessMessage("修改产品状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品状态失败");

    }

    @Transactional//事务
    public ServerResponse<Product> manageProductDetail(Integer productId) {
        ProductImgExample productImgExample = new ProductImgExample();
        productImgExample.createCriteria().andProductIdEqualTo(productId);
        List<ProductImg> productImgList = productImgMapper.selectByExample(productImgExample);

        Product product = productMapper.selectProductByPrimaryKey(productId);
        product.setProductImgList(productImgList);

        return ServerResponse.createBySuccess(product);
    }

    public ServerResponse getProductList(Integer pageNum, Integer pageSize) {
        //pageNum当前第几页，pagesize每页显示的数量
        PageHelper.startPage(pageNum, pageSize);
        //获取productList
        ProductExample productExample = new ProductExample();
        productExample.setOrderByClause("id asc");
        List<Product> productList = productMapper.selectByExample(productExample);

        //返回前台所需的字段
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = ImageUtil.assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }


    public ServerResponse getProductById(Integer productId) {
        return ServerResponse.createBySuccess(productMapper.selectProductByPrimaryKey(productId));
    }

    public ServerResponse getProductByProductName(String productName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        ProductExample example = new ProductExample();
        example.createCriteria().andNameLike("%" + productName + "%");
        List<Product> productList = productMapper.selectByExample(example);
        List<ProductListVo> productListVoList = ImageUtil.assembleProductListVo(productList);
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse getAllProduct() {
        List<Product> productList = productMapper.selectByExample(null);
        return ServerResponse.createBySuccess(productList);
    }

    public ServerResponse uploadMainProductImg(HttpServletRequest request, Integer productId) {

        return null;
    }

    public int updateProduct(Product product) {
        int count = productMapper.updateProductSelective(product);
        return count;
    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId,
                   int pageNum, int pageSize, String orderBy){
        PageHelper.startPage(pageNum,pageSize);
        ProductExample example=new ProductExample();
        ProductExample.Criteria criteria=example.createCriteria();
        criteria.andNameLike("%"+keyword+"%");
        criteria.andCategoryIdEqualTo(categoryId);
        example.setOrderByClause(orderBy);

        List<Product> productList=productMapper.selectByExample(example);
        PageInfo pageInfo=new PageInfo(productList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
