package com.service.serviceImpl;

import com.service.serviceInterface.IFileUploadService;
import com.util.FtpFileUploadUtil;
import com.util.HttpServerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class FileUploadServiceImpl implements IFileUploadService {

    private Logger logger= LoggerFactory.getLogger(FileUploadServiceImpl.class);

    @Override
    public boolean fileUpload(String path, File file) {
       return FtpFileUploadUtil.fileUpload(path,file);
    }

    @Override
    public boolean fileUpload(String path, List<File> fileList) {
        return FtpFileUploadUtil.fileUpload(path,fileList);
    }

    @Override
    public boolean fileUpload(String path,  InputStream inputStream) {
        return FtpFileUploadUtil.fileUpload(path, inputStream);
    }

    public boolean fileUpload(CommonsMultipartFile multipartFile,String path){
        try {
           return FtpFileUploadUtil.fileUpload(path,multipartFile.getInputStream());
        } catch (IOException e) {
            logger.error("上传文件出现异常");
        }
        return false;
    }

    @Override
    public boolean deleteImg(String path) {
       int count=FtpFileUploadUtil.deleteImg(path);
       if (count>0){
           return true;
       }
       return false;
    }
}
