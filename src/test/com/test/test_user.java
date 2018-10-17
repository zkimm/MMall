package com.test;

import com.controller.portal.UserController;
import com.dao.UserMapper;
import com.pojo.User;
import com.pojo.UserExample;
import com.util.PropertiesUtil;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

public class test_user {
    ApplicationContext ioc=new ClassPathXmlApplicationContext("applicationContext.xml");
    UserMapper userMapper=ioc.getBean(com.dao.UserMapper.class);
//    UserController userController=ioc.getBean(UserController.class);

    @Test
   public void test_countByUsername(){
//        int count=userMapper.countByUsername("admin");
        UserExample userExample=new UserExample();
        userExample.createCriteria().andUsernameEqualTo("admin");
        long count=userMapper.countByExample(userExample);
        System.out.print(count);
    }

    @Test
    public void test_register(){
        User user=new User();
        user.setId(2);
        user.setUsername("zkimm");
        user.setPassword("104615");
        user.setQuestion("问题");
        user.setAnswer("答案");
        user.setPhone("123456");
        user.setRole(1);
        UserController userController=new UserController();
        userController.register(user);
    }

    @Test
   public void test_test(){
        com.test.Test.show();
        com.test.Test.setId(2);
        com.test.Test test=new com.test.Test();
        test.show();
//       System.out.println(PropertiesUtil.getProperty("ftp.user"));
    }

    @Test
    public void test_file(){
        File file=new File("F:\\water.png");
        System.out.println(file.getName());
    }

    @Test
    public void test_root(){
        String path="/home/ftpuser/images/item/1/123.jpg";
        String root=PropertiesUtil.getProperty("ftp.server.http.root");
        System.out.println(root+" "+root.length());
        String mm=path.substring(root.length());
        System.out.println(mm);
    }
}
