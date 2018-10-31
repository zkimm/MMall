package com.controller.portal;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.serviceInterface.IUserService;
import com.util.CookieUtil;
import com.util.JsonUtil;
import com.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public ServerResponse<User> login(String username, String password, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        HttpSession session=request.getSession();
        if (response.isSuccess()) {
//            session.setAttribute(Const.CURRENT_USER, response.getData());
            CookieUtil.writerLoginToken(httpServletResponse,session.getId());
            String token = CookieUtil.readLoginToken(request);
            System.out.println(token);
            //6026D3E82ED223BA4494462B312C5E1C
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCache.REDIS_SESSION_TIME);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/logout.do", method = RequestMethod.POST)
    public ServerResponse<String> logout(HttpSession session) {
        //将其从session中去除掉
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @ResponseBody
    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    @ResponseBody
    @RequestMapping(value = "/check_valid.do", method = RequestMethod.POST)
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    @ResponseBody
    @RequestMapping(value = "/get_user_info.do", method = RequestMethod.POST)
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取用户的信息");
    }

    /**
     * 获取密码提示问题
     *
     * @param username
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/forget_get_question.do", method = RequestMethod.POST)
    public ServerResponse<String> selectQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    @ResponseBody
    @RequestMapping(value = "/forget_check_answer.do", method = RequestMethod.POST)
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    @ResponseBody
    @RequestMapping(value = "/forget_reset_password.do", method = RequestMethod.POST)
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    @ResponseBody
    @RequestMapping(value = "/reset_password.do", method = RequestMethod.POST)
    public ServerResponse<String> ResetPassword(String passwordOld, String passwordNew, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPasswrod(passwordOld, passwordNew, user);
    }

    @ResponseBody
    @RequestMapping(value = "/update_information.do", method = RequestMethod.POST)
    public ServerResponse<User> updateInformation(User user, HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());

        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            //将更新了的user放到session中
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/get_information.do", method = RequestMethod.POST)
    public ServerResponse<User> get_information(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录，需要强制登录status=10");
        }
        return iUserService.getInformation(user.getId());
    }
}
