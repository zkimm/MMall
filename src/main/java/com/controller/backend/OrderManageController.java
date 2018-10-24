package com.controller.backend;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.serviceInterface.IOrderService;
import com.service.serviceInterface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse orderList(HttpSession session,@RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                                    @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请先登录");
        }
        if (!iUserService.isAdmin(user)){
            return ServerResponse.createByErrorMessage("无权限操作");
        }
        return iOrderService.manageList(pageNo, pageSize);
    }

    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse orderDetail(HttpSession session,Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }
        if (!iUserService.isAdmin(user)) {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
        return iOrderService.manageDetail(orderNo);
    }

    @RequestMapping("/search.do")
    @ResponseBody
    public ServerResponse orderSearch(HttpSession session, Long orderNo,@RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                                      @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }
        if (!iUserService.isAdmin(user)) {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
        return iOrderService.manageSearch(orderNo,pageNo,pageSize);
    }

    @RequestMapping("/send_goods.do")
    @ResponseBody
    public ServerResponse orderSend(HttpSession session,Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }
        if (!iUserService.isAdmin(user)) {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
        return iOrderService.manageSendGoods(orderNo);
    }
}
