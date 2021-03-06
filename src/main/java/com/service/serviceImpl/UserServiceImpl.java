package com.service.serviceImpl;

import com.common.Const;
import com.common.ServerResponse;
import com.common.TokenCache;
import com.dao.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pojo.User;
import com.service.serviceInterface.IUserService;
import com.sun.org.apache.regexp.internal.RE;
import com.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 1、先检查用户名是否存在
     * 2、比较加密后的密码是否相等
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.countByUsername(username);

        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //todo 密码登录MD5
        //密码是被加密过的，需要比较加密后的密码
        String md5Password = MD5Util.getMD5Str(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户名或者密码错误");
        }
        //密码不显示
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    /**
     * 要检验用户名或email是否存在
     *
     * @param user
     * @return
     */
    public ServerResponse<String> register(User user) {
        //先要校验user是否存在
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名已经存在");
        }
        //校验email
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("emial已经存在");

        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        // MD5加密
        user.setPassword(MD5Util.getMD5Str(user.getPassword()));
        //插入数据库
        int resultCount = userMapper.insertSelective(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccess("注册成功");
    }

    //用于实时校验
    public ServerResponse<String> checkValid(String str, String type) {
        //type不为空则开始校验
        if (org.apache.commons.lang3.StringUtils.isNotBlank(type)) {
            //用户名校验
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.countByUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户已经存在");
                }
            }
            //email校验
            if (Const.EMAIL.equals(type)) {
                int count = userMapper.countByEmail(str);
                if (count > 0) {
                    return ServerResponse.createByErrorMessage("emial已经存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        //用户名与email都不存在的时候isSuccess是true
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //用户名与email都不存在的时候isSuccess是true
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestion(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题为空");
    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int count = userMapper.checkAnswer(username, question, answer);
        if (count > 0) {
            //说明问题及回答正确
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");

    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //校验token
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (!StringUtils.isNotBlank(token)) {
            //校验token是否过期
            return ServerResponse.createByErrorMessage("token无效或者已经过期");
        }

        //比较两个token是否相等
        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.getMD5Str(passwordNew);
            //如果相等则更新密码
            int count = userMapper.updateByUsername(username, md5Password);
            if (count > 0) {
                return ServerResponse.createBySuccessMessage("更新密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取token");
        }

        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPasswrod(String passwordOld, String passwordNew, User user) {
        int count = userMapper.checkPassword(MD5Util.getMD5Str(passwordOld), user.getId());
        if (count == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.getMD5Str(passwordNew));

        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("更新密码成功");
        }
        return ServerResponse.createByErrorMessage("更新密码失败");
    }

    public ServerResponse<User> updateInformation(User user) {
        //username不能被更新
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("email已经存在");
        }

        //只更新下面的字段，用户名不更新
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        //更新了之后在设置updateUser的username,不然controller获取不到username
        updateUser.setUsername(user.getUsername());
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新用户成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新用户失败");
    }

    //获取user的所有信息
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //先要把password置为空，即不返回password
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public boolean isAdmin(User user) {
        if (user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return true;
        }
        return false;
    }

    public ServerResponse getUsers(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<User> userList = userMapper.selectByExample(null);
        PageInfo pageInfo=new PageInfo(userList,5);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
