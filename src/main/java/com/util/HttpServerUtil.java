package com.util;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

//处理http中的参数
public class HttpServerUtil {

    //从request中获取上传的文件文件
    public static CommonsMultipartFile getMultipartFileFromRequest(HttpServletRequest request,String key){
        CommonsMultipartFile multipartFile=null;
        MultipartHttpServletRequest multipartHttpServletRequest=null;
        //从request的上下文中获取
        CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(request.getSession().getServletContext());
        try {
            //如果有文件上传
            if (multipartResolver.isMultipart(request)){
                multipartHttpServletRequest= (MultipartHttpServletRequest) request;
                multipartFile= (CommonsMultipartFile) multipartHttpServletRequest.getFile(key);
                if (multipartFile==null||multipartFile.getSize()==0){
                    return null;
                }
            }else {
                //没有文件上传直接返回null
                return null;
            }

            return multipartFile;
        }catch (Exception e){
            throw new RuntimeException("上传文件失败");
        }
    }

    public static void deleteFile(HttpServletRequest request , long tradeNo){
        //fixme 回调完成之后删除二维码  ftp上的以及upload中的
        String path = request.getSession().getServletContext().getRealPath("upload");
        File file=new File(path+"/"+tradeNo+".png");
        if (file.exists()){
            file.delete();
        }
    }
}
