package com.test;

import com.dao.CartMapper;
import com.pojo.Cart;
import com.pojo.CartExample;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class testCart {
    ApplicationContext ioc=new ClassPathXmlApplicationContext("applicationContext.xml");
    CartMapper cartMapper=ioc.getBean(CartMapper.class);

    @Test
    public void test_cart(){
        CartExample cartExample=new CartExample();
        CartExample.Criteria criteria=cartExample.createCriteria();
        criteria.andUserIdEqualTo(21);
        criteria.andProductIdEqualTo(26);
        Cart cart=cartMapper.selectByExampleWithUserIdProductId(cartExample);

        System.out.println(cart);
    }
}
