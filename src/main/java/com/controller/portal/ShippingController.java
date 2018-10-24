package com.controller.portal;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.pojo.Shipping;
import com.pojo.User;
import com.service.serviceInterface.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @ResponseBody
    @RequestMapping("/add.do")
    public ServerResponse add(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("请先登录");
        }
        //保存shipping
        return iShippingService.addShipping(user.getId(), shipping);
    }

    @ResponseBody
    @RequestMapping("/del.do")
    public ServerResponse delete(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("请先登录");
        }
        //保存shipping
        return iShippingService.delShipping(user.getId(), shippingId);
    }

    @ResponseBody
    @RequestMapping("/update.do")
    public ServerResponse update(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("请先登录");
        }
        //保存shipping
        return iShippingService.updateShipping(user.getId(), shipping);
    }

    @ResponseBody
    @RequestMapping("/select.do")
    public ServerResponse select(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("请先登录");
        }
        //保存shipping
        return iShippingService.selectShipping(user.getId(), shippingId);
    }

    @ResponseBody
    @RequestMapping("/list.do")
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10")  Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("请先登录");
        }
        //保存shipping
        return iShippingService.list(user.getId(), pageNum,pageSize);
    }


}
