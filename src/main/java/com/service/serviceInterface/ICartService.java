package com.service.serviceInterface;

import com.common.ServerResponse;
import com.vo.CartVo;

public interface ICartService {

    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);


    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    /**
     * 删除商品
     * @param userId
     * @param productIds
     * @return
     */
    ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);

    /**
     * 获取购物车中的商品
     * @param userId
     * @return
     */
    ServerResponse<CartVo> list(Integer userId);

    /**
     * 全选，全返选，单选，单独反选
     * @param userId
     * @param productId 有productId则只选中一个或者反选一个
     * @param checked
     * @return
     */
    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    /**
     * 获取购物车中商品的总数
     * @param userId
     * @return
     */
    ServerResponse getCartProductCount(Integer userId);

}
