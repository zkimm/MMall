package com.controller.backend;

import com.common.Const;
import com.common.ServiceResponse;
import com.pojo.User;
import com.service.serviceInterface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    @ResponseBody
    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    public ServiceResponse<User> login(HttpSession session,String username,String  password){
        ServiceResponse<User> response=iUserService.login(username,password);
        if (response.isSuccess()){
            User user=response.getData();
            if (user.getRole()== Const.Role.ROLE_ADMIN){
                //登录的是管理源
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            }else {
                return ServiceResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }
        return response;
    }


}
