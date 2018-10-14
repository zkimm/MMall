package com.service.serviceInterface;


import com.common.ServiceResponse;
import com.pojo.User;

public interface IUserService {

    /**
     * 登录，并将user放到session中
     *
     * @param username
     * @param password
     * @return
     */
    ServiceResponse<User> login(String username, String password);


    ServiceResponse<String> register(User user);

    /**
     * 检查用户名或者Email是否存在
     *
     * @param str
     * @param type
     * @return
     */
    ServiceResponse<String> checkValid(String str, String type);

    /**
     * 获取设置的问题
     *
     * @param username
     * @return
     */
    ServiceResponse selectQuestion(String username);

    /**
     * 回答问题正确之后，就会把forgetToken放到cache中，
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServiceResponse<String> checkAnswer(String username, String question, String answer);

    ServiceResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    /**
     * 重置密码
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    ServiceResponse<String> resetPasswrod(String passwordOld, String passwordNew, User user);

    ServiceResponse<User> updateInformation(User user);


    ServiceResponse<User>  getInformation(Integer userId);

}
