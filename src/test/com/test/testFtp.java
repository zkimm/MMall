package com.test;

import com.util.FtpFileUploadUtil;

import java.io.File;
import org.junit.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class testFtp {

    @Test
    public void test_file() throws IOException{
        File file=new File("f:\\water.png");
        InputStream inputStream=new FileInputStream(file);

//        FtpFileUploadUtil.fileUpload("MMall/item/111/6.jpg",inputStream);
        FtpFileUploadUtil.fileUpload("MMall/images/item/27/56eac145-ac80-45c0-a5e4-4577cd2a5c3f.png",inputStream);
    }

    @Test
    public void test_deleteImg(){
      int count= FtpFileUploadUtil.deleteImg("/home/ftpuser/images/item/2.jpg");
      System.out.println(count);
    }

    @Test
    public void test_redis(){

    }


//    @Test
//    public void testFtp1() throws Exception{
//        //创建客户端对象
//        FTPClient ftp = new FTPClient();
//        InputStream local=null;
//        try {
//            //连接ftp服务器
//            ftp.connect("192.168.25.138", 21);
//            //登录
//            ftp.login("ftpuser", "104615");
//            //设置上传路径
//            String path="/home/ftpuser/images";
//            //检查上传路径是否存在 如果不存在返回false
//            boolean flag = ftp.changeWorkingDirectory(path);
//            if(!flag){
//                //创建上传的路径  该方法只能创建一级目录，在这里如果/home/ftpuser存在则可创建image
//                ftp.makeDirectory(path);
//            }
//            //指定上传路径
//            ftp.changeWorkingDirectory(path);
//            //指定上传文件的类型  二进制文件
//            ftp.setFileType(FTP.BINARY_FILE_TYPE);
//            //读取本地文件
//            File file = new File("f:\\water.png");
//            local = new FileInputStream(file);
//            //第一个参数是文件名
//            ftp.storeFile(file.getName(), local);
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                //关闭文件流
//                local.close();
//                //退出
//                ftp.logout();
//                //断开连接
//                ftp.disconnect();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
