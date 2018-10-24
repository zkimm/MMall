package com.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

//上传的时候再拼接/home/ftpuser/,获取的时候去掉根目录部分

public class FtpFileUploadUtil {

    private static Logger logger = LoggerFactory.getLogger(FtpFileUploadUtil.class);

    private static FTPClient ftpClient;

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPassword = PropertiesUtil.getProperty("ftp.password");
    private static String ftpPort = PropertiesUtil.getProperty("ftp.port");

    private static String ftpRoot = PropertiesUtil.getProperty("ftp.server.http.root");
    private static String imgPath = PropertiesUtil.getProperty("ftp.imgPath");

    //默认图片的根路径=/home/ftpuser/+images/item
    private static String imgBasePath = ftpRoot + imgPath;


    public static FTPClient getConnection() {
        ftpClient = new FTPClient();
        try {
            //连接ftp服务器
            ftpClient.connect(ftpIp, Integer.parseInt(ftpPort));
            //登录ftp服务器
            ftpClient.login(ftpUser, ftpPassword);
        } catch (Exception e) {
            logger.error("连接ftp服务器出现异常");
        }
        return ftpClient;
    }

    public static String getImgBasePath() {
        ftpClient = getConnection();
        boolean flag = false;//检查目标路径是否存在
        try {
            flag = ftpClient.changeWorkingDirectory(imgBasePath);
            if (!flag) {
                //目标路径不存在则创建对应的文件夹  <创建文件的时候要带上根目录>
                ftpClient.makeDirectory(imgBasePath);
            }
        } catch (IOException e) {
            logger.error("获取图片路径失败");
        }
        return imgPath.trim();
    }

    //需要带上根目录
    public static int deleteImg(String path) {
        int count = 0;
        ftpClient = getConnection();
        try {
            boolean flag = ftpClient.changeWorkingDirectory(ftpRoot + path);
            if (flag) {
                //删除文件夹
                ftpClient.deleteFile(ftpRoot + path);
            }
            //删除文件
            count = ftpClient.dele(ftpRoot + path);
        } catch (Exception e) {
            logger.error("删除图片失败");
        } finally {
            closeConnection(null, ftpClient);
        }
        return count;
    }

    /**
     * path为一个文件夹
     *
     * @param path
     * @param fileList
     */
    public static boolean fileUpload(String path, List<File> fileList) {
        boolean flag = false;
        for (File file : fileList) {
            flag = fileUpload(path, file);
        }
        return flag;
    }

    public static boolean fileUpload(String path, File file) {
        boolean flag = false;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            flag = fileUpload(path, inputStream);
        } catch (FileNotFoundException e) {
            logger.error("上传文件出现异常");
        } finally {
            closeConnection(inputStream, ftpClient);
        }
        return flag;
    }


    /**
     * 文件上传的主要方法
     *
     * @param path
     * @param inputStream
     * @return
     */
    public static boolean fileUpload(String path, InputStream inputStream) {

        boolean flag = false;
        String realPath = ftpRoot + path;
        String rootPath=null;
        try {
            ftpClient = getConnection();
            //指定上传的路径
            //文件是否存在最后一个/之前的文件夹是否存在（用于检查路径是否存在）
            boolean directory = ftpClient.changeWorkingDirectory(realPath);
            if (!directory) {
                rootPath=mkdirByPath(realPath, ftpClient);
            }
            //指定上传的文件类型  二进制文件
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //文件名字
            String fileName = getFileName(realPath, ftpClient);
            //指定上传的路径
            directory = ftpClient.changeWorkingDirectory(rootPath);
            //上传文件
            if (directory) {
                flag = ftpClient.storeFile(fileName, inputStream);
            }

        } catch (IOException e) {
            logger.error("上传文件失败");
        } finally {
            closeConnection(inputStream, ftpClient);
        }
        return flag;
    }

    /**
     * 通过文件的路径创建相应的文件
     *
     * @param path
     */
    public static String mkdirByPath(String path, FTPClient ftpClient) {
        String filePath = null;
        //最后一个 /  之前的路径是否存在
        try {
            int index = path.lastIndexOf("/");
            filePath = path.substring(0, index + 1);
            //切换到指定目录，无法切换怎没有该目录
            boolean flag = ftpClient.changeWorkingDirectory(filePath);
            if (!flag) {
                ftpClient.makeDirectory(filePath);
            }
        } catch (Exception e) {
            logger.error("创建文件目录失败", e);
        }
        return filePath;
    }

    /**
     * 通过文件的路径获取图片的名字
     *
     * @param path
     * @return
     */
    public static String getFileName(String path, FTPClient ftpClient) {
        //最后一个 /  之前的路径是否存在
        String fileName = "";
        try {
            int index = path.lastIndexOf("/");
            fileName = path.substring(index + 1);

        } catch (Exception e) {
            logger.error("获取文件名字失败", e);
        }
        return fileName;
    }

    /**
     * 断开连接
     *
     * @param inputStream
     * @param ftpClient
     */
    public static void closeConnection(InputStream inputStream, FTPClient ftpClient) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (ftpClient != null) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (Exception e) {
            logger.error("断开ftp连接出现异常");
        }

    }

}
