package com.controller.backend;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.pojo.Product;
import com.pojo.User;
import com.service.serviceInterface.IFileUploadService;
import com.service.serviceInterface.IProductService;
import com.service.serviceInterface.IUserService;
import com.util.FtpFileUploadUtil;
import com.util.HttpServerUtil;
import com.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileUploadService iFileUploadService;

    @RequestMapping("/save.do")
    @ResponseBody
    public ServerResponse productSave(HttpServletRequest request, Product product) {
        HttpSession session = request.getSession();
        //判断user是否存在
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请以管理员身份登录");
        }
        //判断是否是管理员
        if (iUserService.isAdmin(user)) {
            //保存或更新产品的业务逻辑
//            //1、检查是否有图片上传
//            CommonsMultipartFile multipartFile=HttpServerUtil.getMultipartFileFromRequest(request,"upload_file");
//            //2、获取图片的路径
//            String path=ImageUtil.getImgPath(multipartFile,product.getId());
//            //3、上传图片
//           boolean flag= iFileUploadService.fileUpload(multipartFile,path);
//            //4、设置属性
//            if (flag){
//                //文件上传成功
//                ImageUtil.setProductImg(path,product);
//            }
            //5、保存到数据库中
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("/set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest request, Integer productId, Integer status) {
        //判断user是否存在
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请以管理员身份登录");
        }
        //判断是否是管理员
        if (iUserService.isAdmin(user)) {
            //保存或更新产品的业务逻辑
            return iProductService.setSaleStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @ResponseBody
    @RequestMapping("/list.do")
    public ServerResponse list(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        //判断user是否存在
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请以管理员身份登录");
        }
        //判断是否是管理员
        if (iUserService.isAdmin(user)) {
            //保存或更新产品的业务逻辑
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpServletRequest request, Integer productId) {
        //判断user是否存在
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请以管理员身份登录");
        }
        //判断是否是管理员
        if (iUserService.isAdmin(user)) {
            //获取product的所有信息
            return iProductService.manageProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @ResponseBody
    @RequestMapping("/search.do")
    public ServerResponse searchProduct(HttpServletRequest request, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        //判断user是否存在
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请以管理员身份登录");
        }
        //判断是否是管理员
        if (iUserService.isAdmin(user)) {
            //查询product，有id则用id查询，没有id则用productname查询，两个都没有则获取所有列表
            if (productId != null) {
                return iProductService.getProductById(productId);
            } else {
                if (productName != null) {
                    return iProductService.getProductByProductName(productName, pageNum, pageSize);
                } else {
                    //两个都为空则返回所有列表
                    return iProductService.getAllProduct();
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ServerResponse uploadMainProductImg(HttpServletRequest request, Integer productId) {
        //重request中获取上传的文件对象
        CommonsMultipartFile multipartFile = HttpServerUtil.getMultipartFileFromRequest(request, "upload_file");
        if (multipartFile == null) {
            return ServerResponse.createByErrorMessage("上传图片为空，请重新上传");
        }
        //设置图片的路径=根路径/productId/<name>
        String path = ImageUtil.getImgPath(multipartFile, productId);
        //文件上传
        boolean flag = iFileUploadService.fileUpload(multipartFile, path);
        if (flag) {
            //图片上传成功,设置product的属性，并更新数据库
            Product product = (Product) iProductService.getProductById(productId).getData();
            ImageUtil.setProductImg(path, product);

            int count = iProductService.updateProduct(product);
            if (count <= 0) {
                return ServerResponse.createByErrorMessage("上传图片时，更新product的信息失败");
            }
            return ServerResponse.createBySuccess("图片上传成功", path);
        }
        return ServerResponse.createByErrorMessage("图片上传失败");
    }
}
