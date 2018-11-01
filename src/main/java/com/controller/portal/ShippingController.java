package com.controller.portal;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.pojo.Shipping;
import com.pojo.User;
import com.service.serviceInterface.IShippingService;
import com.service.serviceInterface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @Autowired
    private IUserService iUserService;

    @ResponseBody
    @RequestMapping("/add.do")
    public ServerResponse add(HttpServletRequest request, Shipping shipping) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorMessage("请先登录");
        }
        //保存shipping
        return iShippingService.addShipping(user.getId(), shipping);
    }

    @ResponseBody
    @RequestMapping("/del.do")
    public ServerResponse delete(HttpServletRequest request, Integer shippingId) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorMessage("请先登录");
        }
        //保存shipping
        return iShippingService.delShipping(user.getId(), shippingId);
    }

    @ResponseBody
    @RequestMapping("/update.do")
    public ServerResponse update(HttpServletRequest request, Shipping shipping) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorMessage("请先登录");
        }
        //保存shipping
        return iShippingService.updateShipping(user.getId(), shipping);
    }

    @ResponseBody
    @RequestMapping("/select.do")
    public ServerResponse select(HttpServletRequest request, Integer shippingId) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorMessage("请先登录");
        }
        //保存shipping
        return iShippingService.selectShipping(user.getId(), shippingId);
    }

    @ResponseBody
    @RequestMapping("/list.do")
    public ServerResponse list(HttpServletRequest request, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10")  Integer pageSize) {
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorMessage("请先登录");
        }
        //保存shipping
        return iShippingService.list(user.getId(), pageNum,pageSize);
    }


}
