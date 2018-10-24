package com.service.serviceImpl;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.dao.CartMapper;
import com.dao.ProductMapper;
import com.google.common.base.Splitter;
import com.pojo.Cart;
import com.pojo.CartExample;
import com.pojo.Product;
import com.pojo.ProductExample;
import com.service.serviceInterface.ICartService;
import com.util.BigDecimalUtil;
import com.vo.CartProductVo;
import com.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.Caret;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    //增加商品
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //检查productId是否存在
        if (!this.isExistProductId(productId)){
            return ServerResponse.createByErrorMessage("该商品不存在");
        }

        CartExample cartExample = new CartExample();
        CartExample.Criteria criteria = cartExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andProductIdEqualTo(productId);
        Cart cart = cartMapper.selectByExampleWithUserIdProductId(cartExample);

        //如果从数据库中获取不到cart则新增一个cart
        if (cart == null) {
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            //插入数据库
            cartMapper.insertSelective(cartItem);
        } else {
            //该产品已存在，则数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            //更新购物车
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    //更新商品数量
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //检查productId是否存在
        if (!this.isExistProductId(productId)){
            return ServerResponse.createByErrorMessage("该商品不存在");
        }

        CartExample example = new CartExample();
        CartExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andProductIdEqualTo(productId);
        Cart cart = cartMapper.selectByExampleWithUserIdProductId(example);

        if (cart != null) {
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    //删除商品
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        for (String productId:productList){
            //检查productId是否存在
            if (!this.isExistProductId(Integer.parseInt(productId))){
                return ServerResponse.createByErrorMessage(productId+"商品不存在");
            }
        }

        cartMapper.deleteCartByUserIdProductIds(userId, productList);
        return this.list(userId);
    }

    //获取所有商品
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = this.getCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    //全选或全反选，productId为null则表示全选或者全反选
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUnchecked(userId, productId, checked);
        return this.list(userId);
    }

    public ServerResponse getCartProductCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.getCartProductCount(userId));
    }

    /**
     * 购物车的核心方法
     *
     * @param userId
     * @return
     */
    private CartVo getCartVo(Integer userId) {
        CartVo cartVo = new CartVo();
        List<CartProductVo> cartProductVoList = new ArrayList<>();

        CartExample example = new CartExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<Cart> cartList = cartMapper.selectByExample(example);

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (!cartList.isEmpty()) {
            for (Cart cart : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cart.getProductId());

                Product product = productMapper.selectProductByPrimaryKey(cart.getProductId());
                if (product != null) {
                    //如果productId不为空，继续组装productVo
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= cart.getQuantity()) {
                        //库存充足
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        //需要的大于库存
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        //更新到数据库中
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }

                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity().doubleValue()));
                    //勾选
                    cartProductVo.setProductChecked(cart.getChecked());
                }

                if (cart.getChecked() == Const.Cart.CHECKED) {
                    //如果商品是选中状态，则计算价格
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllChecked(userId));
        return cartVo;
    }

    /**
     * 判断是否全选
     * @param userId
     * @return
     */
    private boolean getAllChecked(Integer userId) {
        if (userId == null) {
            return false;
        } else {
            CartExample example = new CartExample();
            CartExample.Criteria criteria = example.createCriteria();
            criteria.andUserIdEqualTo(userId);
            criteria.andCheckedEqualTo(0);//0 :未勾选
            long count = cartMapper.countByExample(example);
            return count == 0;
        }
    }

    /**
     * 先要检查productId是否存在
     * @param productId
     * @return 不存在返回false
     */
    private boolean isExistProductId(Integer productId){
        ProductExample example=new ProductExample();
        example.createCriteria().andIdEqualTo(productId);
        long count = productMapper.countByExample(example);
        if (count==0){
            //如果不存在该商品
            return false;
        }
        return true;
    }
}
