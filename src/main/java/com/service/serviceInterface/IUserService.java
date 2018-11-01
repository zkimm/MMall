package com.service.serviceInterface;


import com.common.ServerResponse;
import com.pojo.User;

import javax.servlet.http.HttpServletRequest;

public interface IUserService {

    /**
     * 登录，并将user放到session中
     *
     * @param username
     * @param password
     * @return
     */
    ServerResponse<User> login(String username, String password);


    ServerResponse<String> register(User user);

    /**
     * 检查用户名或者Email是否存在
     *
     * @param str
     * @param type
     * @return
     */
    ServerResponse<String> checkValid(String str, String type);

    /**
     * 获取设置的问题
     *
     * @param username
     * @return
     */
    ServerResponse selectQuestion(String username);

    /**
     * 回答问题正确之后，就会把forgetToken放到cache中，
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    /**
     * 重置密码
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    ServerResponse<String> resetPasswrod(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInformation(User user);


    ServerResponse<User> getInformation(Integer userId);

     boolean isAdmin(User user);

     ServerResponse getUsers(Integer pageNum,Integer pageSize);

    User getUserformRedis(HttpServletRequest request);

}
