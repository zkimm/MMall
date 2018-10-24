package com.service.serviceInterface;

import com.common.ServerResponse;
import com.pojo.Shipping;

public interface IShippingService {

    ServerResponse addShipping(Integer userId, Shipping shipping);

    ServerResponse delShipping(Integer userId, Integer shippingId);

    ServerResponse updateShipping(Integer userId, Shipping shipping);

    ServerResponse selectShipping(Integer userId, Integer shippingId);

    ServerResponse list(Integer userId,Integer pageNum,Integer pageSize);


}
