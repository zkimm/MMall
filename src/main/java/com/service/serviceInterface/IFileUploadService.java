package com.service.serviceInterface;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface IFileUploadService {

    boolean fileUpload(String path, File file);

    boolean fileUpload(String path, List<File> fileList);

    boolean fileUpload(String path, InputStream inputStream);

    //上传一个文件
    boolean fileUpload(CommonsMultipartFile multipartFile, String path);

    boolean deleteImg(String path);
}
