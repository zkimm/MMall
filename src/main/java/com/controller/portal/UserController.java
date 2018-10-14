package com.controller.portal;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServiceResponse;
import com.pojo.User;
import com.service.serviceInterface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.plugin2.os.windows.SECURITY_ATTRIBUTES;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     *
     * @param userName
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String userName, String password, HttpSession session) {
        ServiceResponse<User> response = iUserService.login(userName, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/logout.do", method = RequestMethod.POST)
    public ServiceResponse<String> logout(HttpSession session) {
        //将其从session中去除掉
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }

    @ResponseBody
    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    public ServiceResponse<String> register(User user) {
        return iUserService.register(user);
    }

    @ResponseBody
    @RequestMapping(value = "/check_valid.do", method = RequestMethod.POST)
    public ServiceResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    @ResponseBody
    @RequestMapping(value = "/check_valid.do", method = RequestMethod.POST)
    public ServiceResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServiceResponse.createBySuccess(user);
        }
        return ServiceResponse.createByErrorMessage("用户未登录，无法获取用户的信息");
    }

    /**
     * 获取密码提示问题
     *
     * @param username
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/forget_get_question.do", method = RequestMethod.POST)
    public ServiceResponse<String> selectQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    @ResponseBody
    @RequestMapping(value = "/forget_check_answer.do", method = RequestMethod.POST)
    public ServiceResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    @ResponseBody
    @RequestMapping(value = "/forget_reset_password.do", method = RequestMethod.POST)
    public ServiceResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    @ResponseBody
    @RequestMapping(value = "/reset_password.do", method = RequestMethod.POST)
    public ServiceResponse<String> ResetPassword(String passwordOld, String passwordNew, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPasswrod(passwordOld, passwordNew, user);
    }

    @ResponseBody
    @RequestMapping(value = "/reset_password.do", method = RequestMethod.POST)
    public ServiceResponse<User> updateInformation(User user, HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorMessage("用户未登录");
        }

        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());

        ServiceResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            //将更新了的user放到session中
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/get_information.do", method = RequestMethod.POST)
    public ServiceResponse<User> get_information(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录，需要强制登录status=10");
        }
        return iUserService.getInformation(user.getId());
    }
}
