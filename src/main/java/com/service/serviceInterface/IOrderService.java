package com.service.serviceInterface;

import com.common.ServerResponse;

import java.util.Map;

public interface IOrderService {

    ServerResponse pay(Integer userId, Long orderNo, String path);

    ServerResponse aliCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse cancel(Integer userId, Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse getOrderDetail(Integer userId,Long orderNo);

    ServerResponse getOrderList(Integer userId, Integer pageNo, Integer pageSize);

    ServerResponse manageList(Integer pageNo,Integer pageSize);

    ServerResponse manageDetail(Long orderNo);

    ServerResponse manageSearch(Long orderNo, Integer pageNo,Integer pagesize);

    ServerResponse manageSendGoods(Long orderNo);

}
