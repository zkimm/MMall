package com.controller.portal;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.serviceInterface.ICartService;
import com.service.serviceInterface.IUserService;
import com.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private ICartService iCartService;

    @Autowired
    private IUserService iUserService;

    @ResponseBody
    @RequestMapping("/list.do")
    public ServerResponse<CartVo> list(HttpServletRequest request, Integer count, Integer productId) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        //添加逻辑
        return iCartService.list(user.getId());
    }

    @ResponseBody
    @RequestMapping("/add.do")
    public ServerResponse<CartVo> add(HttpServletRequest request, Integer count, Integer productId) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        //添加逻辑
        return iCartService.add(user.getId(), productId, count);
    }

    @ResponseBody
    @RequestMapping("/update.do")
    public ServerResponse<CartVo> update(HttpServletRequest request, Integer count, Integer productId) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        //添加逻辑
        return iCartService.update(user.getId(), productId, count);
    }

    @ResponseBody
    @RequestMapping("/delete_product.do")
    public ServerResponse<CartVo> deleteProduct(HttpServletRequest request, String productIds) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        //添加逻辑
        return iCartService.deleteProduct(user.getId(), productIds);
    }


    //全选
    @ResponseBody
    @RequestMapping("/select_all.do")
    public ServerResponse<CartVo> selectAll(HttpServletRequest request) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        //添加逻辑
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.CHECKED);
    }

    //全反选
    @ResponseBody
    @RequestMapping("/un_select_all.do")
    public ServerResponse<CartVo> unselectAll(HttpServletRequest request) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        //添加逻辑
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.UN_CHECKED);
    }

    //单独选
    @ResponseBody
    @RequestMapping("/select.do")
    public ServerResponse<CartVo> select(HttpServletRequest request, Integer productId) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        //添加逻辑
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }

    //单独反选
    @ResponseBody
    @RequestMapping("/un_select.do")
    public ServerResponse<CartVo> unselect(HttpServletRequest request, Integer productId) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        //添加逻辑
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    //查询购物车里面的产品数量，如果一个产品有十个就显示10
    @ResponseBody
    @RequestMapping("/get_cart_product_count.do")
    public ServerResponse getCartProductCount(HttpServletRequest request, Integer productId) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        //添加逻辑
        return iCartService.getCartProductCount(user.getId());
    }

}
