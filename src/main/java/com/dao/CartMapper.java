package com.dao;

import com.pojo.Cart;
import com.pojo.CartExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface CartMapper {
    long countByExample(CartExample example);

    int deleteByExample(CartExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    List<Cart> selectByExample(CartExample example);

    Cart selectByExampleWithUserIdProductId(CartExample example);

    Cart selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Cart record, @Param("example") CartExample example);

    int updateByExample(@Param("record") Cart record, @Param("example") CartExample example);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    int deleteCartByUserIdProductIds(@Param("userId") Integer userId,@Param("productIdList") List<String> productIdList);

    int checkedOrUnchecked(@Param("userId") Integer userId,@Param("productId") Integer productId,@Param("checked") Integer checked);

    int getCartProductCount(@Param("userId") Integer userId);
}