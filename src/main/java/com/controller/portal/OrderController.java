package com.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.google.common.collect.Maps;
import com.pojo.User;
import com.service.serviceInterface.IOrderService;
import com.util.HttpServerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    /**
     * @param session
     * @param orderNo 订单号
     * @param request 用于拿到upload文件夹
     * @return
     */
    @RequestMapping("/pay.do")
    @ResponseBody
    public ServerResponse pay(HttpServletRequest request, Long orderNo) {
        User user = (User) request.getSession().getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(), orderNo, path);
    }

    @RequestMapping("/alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String,String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator();iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){

                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }
        LOGGER.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.

        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

            if(!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求,验证不通过,再恶意请求我就报警找网警了");
            }
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝验证回调异常",e);
        }

        //todo 验证各种数据

        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            //fixme 删除二维码 upload中的
            long orderNo = Long.parseLong(params.get("out_trade_no"));
            HttpServerUtil.deleteFile(request,orderNo);
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping("/query_order_pay_status.do")
    @ResponseBody
    public ServerResponse queryOrderPayStatus(HttpServletRequest request, Long orderNo) {
        User user = (User) request.getSession().getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createByError(false);
    }

    @RequestMapping("/create.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest request, Integer shippingId) {
        User user = (User) request.getSession().getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping("/cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpServletRequest request, Long orderNo) {
        User user = (User) request.getSession().getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.cancel(user.getId(),orderNo);
    }


    @RequestMapping("/get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse detail(HttpServletRequest request, Long orderNo) {
        User user = (User) request.getSession().getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest request, @RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                               @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        User user = (User) request.getSession().getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderList(user.getId(),pageNo,pageSize);
    }


}
