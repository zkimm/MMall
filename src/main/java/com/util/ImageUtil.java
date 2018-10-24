package com.util;

import com.pojo.Product;
import com.vo.ProductListVo;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImageUtil {

    private static String imgPath = PropertiesUtil.getProperty("ftp.imgPath");
    private static String serverIp = PropertiesUtil.getProperty("ftp.server.http.serverIp");

    /**
     * 1、主要用于将product转换成ProductListVo，返回前台所需要的字段
     * 2、拼接图片的路径
     *
     * @param product
     * @return
     */
    public static ProductListVo assembleProductListVo(Product product) {
//        String root = PropertiesUtil.getProperty("ftp.server.http.root");//图片的根目录

        ProductListVo productListVo = new ProductListVo();
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setId(product.getId());
//        这里获取图片在服务器中的真实路径
        productListVo.setImageHost(null);
        String img = product.getMainImage();
        productListVo.setMainImage(serverIp + img);
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());
        return productListVo;
    }

    public static List<ProductListVo> assembleProductListVo(List<Product> productList) {
        List<ProductListVo> productListVos=new ArrayList<>();
        for (Product product:productList){
            productListVos.add(assembleProductListVo(product));
        }
        return productListVos;
    }

    public static String setImgPathById(Integer id) {
        String path = FtpFileUploadUtil.getImgBasePath() + id + "/";
        return path;
    }

    //设置图片的路径<路径包含文件的名字>
    public static String getImgPath(CommonsMultipartFile multipartFile, Integer productId) {
        String path = FtpFileUploadUtil.getImgBasePath() + productId + "/";
        String oldName = multipartFile.getOriginalFilename();
        //设置图片的名字
        String fileName = UUID.randomUUID().toString();
        //获取拓展名
        String extetion = oldName.substring(oldName.lastIndexOf("."));
        //文件的最终名字为
        String newName = fileName + extetion;
        return path + newName;
    }

    //设置product的属性
    public static void setProductImg(String path, Product product) {
        product.setMainImage(path);
    }
}
