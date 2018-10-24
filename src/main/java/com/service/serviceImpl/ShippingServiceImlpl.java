package com.service.serviceImpl;

import com.common.ServerResponse;
import com.dao.ShippingMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pojo.Shipping;
import com.pojo.ShippingExample;
import com.service.serviceInterface.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShippingServiceImlpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse addShipping(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int count = shippingMapper.insert(shipping);
        if (count > 0) {
            //添加成功
            Map map = new HashMap();
            map.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("新键地址成功", map);

        }
        return ServerResponse.createBySuccess("新键地址失败");
    }

    public ServerResponse delShipping(Integer userId, Integer shippingId) {
        //这里要重新设置shipping的userid
        ShippingExample shippingExample = new ShippingExample();
        ShippingExample.Criteria criteria = shippingExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andIdEqualTo(shippingId);

        //删除
        int cont = shippingMapper.deleteByExample(shippingExample);
        if (cont > 0) {
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createBySuccessMessage("删除地址失败");
    }

    public ServerResponse updateShipping(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int count = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (count > 0) {
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createBySuccessMessage("更新地址失败");
    }

    public ServerResponse selectShipping(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByUserIdShippingId(userId, shippingId);
        if (shipping!=null){
            return ServerResponse.createBySuccess(shipping);
        }else if (shipping==null){
            //userId存在，但是shipping不存在
            return ServerResponse.createByErrorMessage("");
        }
        return ServerResponse.createByErrorMessage("请登录后查询");
    }

    public ServerResponse list(Integer userId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        ShippingExample example=new ShippingExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<Shipping> shippingList = shippingMapper.selectByExample(example);
        PageInfo pageInfo=new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
