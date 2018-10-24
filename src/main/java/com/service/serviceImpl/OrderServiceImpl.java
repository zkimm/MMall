package com.service.serviceImpl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.Car;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.common.Const;
import com.common.ServerResponse;
import com.dao.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Ordering;
import com.pojo.*;
import com.service.serviceInterface.IOrderService;
import com.sun.org.apache.regexp.internal.RE;
import com.util.BigDecimalUtil;
import com.util.DateTimeUtil;
import com.util.FtpFileUploadUtil;
import com.util.PropertiesUtil;
import com.vo.OrderItemVo;
import com.vo.OrderProductVo;
import com.vo.OrderVo;
import com.vo.ShippingVo;
import org.apache.commons.lang.StringUtils;
import org.aspectj.weaver.ast.Or;
import org.joda.time.DateTimeUtils;
import org.omg.PortableInterceptor.INACTIVE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements IOrderService {

    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * @param orderNum 订单号
     * @param userId
     * @param path     传到哪里的路径
     * @return 返回订单号以及二维码的url返回给前端
     */
    public ServerResponse pay(Integer userId, Long orderNo, String path) {
        Map<String, String> resultMap = new HashMap<>();
        Order order = orderMapper.selectByUsreIdOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));
        resultMap.put("qrUrl", aplipay(order, path));
        return ServerResponse.createBySuccess(resultMap);
    }


    public ServerResponse aliCallback(Map<String, String> params) {
        long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByUsreIdOrderNo(null, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("非MMall的订单");
        }

        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccessMessage("支付宝重复调用");
        }
        //交易成功
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);

        payInfoMapper.insert(payInfo);


        return ServerResponse.createBySuccess();
    }

    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUsreIdOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("没有该订单");
        }

        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess();
        }

        return ServerResponse.createByError();

    }

    public ServerResponse getOrderList(Integer userId, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        OrderExample example = new OrderExample();
        example.setOrderByClause("create_time desc");
        OrderExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);

        List<Order> orderList = orderMapper.selectByExample(example);
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);

    }

    public ServerResponse getOrderDetail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUsreIdOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("没有找到该订单");
        }
        OrderItemExample example = new OrderItemExample();
        OrderItemExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andOrderNoEqualTo(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(example);

        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);

    }

    public ServerResponse getOrderCartProduct(Integer userId) {
        OrderProductVo orderProductVo = new OrderProductVo();
        //获取购物车中被选中的item
        CartExample example = new CartExample();
        CartExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andCheckedEqualTo(1);
        List<Cart> cartList = cartMapper.selectByExample(example);
        ServerResponse serverResponse = this.getOrderItemByCart(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        //计算总价
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal totalPrice = this.getOrderTotalPrice(orderItemList);
        orderProductVo.setProductTotalPrice(totalPrice);
        orderProductVo.setOrderItemList(orderItemList);
        return ServerResponse.createBySuccess(orderProductVo);

    }

    public ServerResponse cancel(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUsreIdOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该订单不存在");
        }
        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            //该订单不是未付款
            return ServerResponse.createByErrorMessage("该订单已经付款，无法取消");
        }
        //取消订单
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int count = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (count > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        //查找被勾选的cart
        CartExample cartExample = new CartExample();
        CartExample.Criteria criteria = cartExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andCheckedEqualTo(1);
        //获取购物车中被选中的商品
        List<Cart> cartList = cartMapper.selectByExample(cartExample);
        //拼接成orderItem
        ServerResponse serverResponse = this.getOrderItemByCart(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        //计算总价
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);

        //生成订单
        Order order = this.assembleOrder(userId, shippingId, payment);
        if (order == null) {
            return ServerResponse.createByErrorMessage("生成订单失败");
        }
        if (orderItemList.isEmpty()) {
            //如果orderItemList为空
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //给orderItem设置orderNo
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        //批量插入
        orderItemMapper.batchInsert(orderItemList);
        //减少库存
        this.reduceProductStock(orderItemList);
        //清空购物车
        this.clearCart(cartList);

        //返回给前端
        OrderVo orderVo = assembleOrderVo(order, orderItemList);

        return ServerResponse.createBySuccess(orderVo);
    }


    public ServerResponse manageList(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        OrderExample example = new OrderExample();
        example.setOrderByClause("create_time desc");
        List<Order> orderList = orderMapper.selectByExample(example);
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, null);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);

    }

    public ServerResponse manageDetail(Long orderNo) {
        Order order = orderMapper.selectByUsreIdOrderNo(null, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOrderNoEqualTo(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(example);
        return ServerResponse.createBySuccess(assembleOrderVo(order, orderItemList));
    }

    /**
     * fixme 先精确匹配
     *
     * @param orderNo
     * @param pageNo
     * @param pagesize
     * @return
     */
    public ServerResponse manageSearch(Long orderNo, Integer pageNo, Integer pagesize) {
        PageHelper.startPage(pageNo, pagesize);
        Order order = orderMapper.selectByUsreIdOrderNo(null, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOrderNoEqualTo(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(example);
        PageInfo pageInfo = new PageInfo(orderItemList);
        return ServerResponse.createBySuccess(pageInfo);

    }

    public ServerResponse manageSendGoods(Long orderNo) {
        Order order = orderMapper.selectByUsreIdOrderNo(null, orderNo);
        if (order != null) {
            if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {
                //如果是已经付款,更新order的状态
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccessMessage("发货成功");
            }
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    /**
     * 组装一个orderVo里面包括订单的详细信息，还包括收货地址的相关信息
     *
     * @param order
     * @param orderItemList
     * @return
     */
    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        //运费
        orderVo.setPostage(order.getPostage());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            orderItemVoList.add(assembleOrderItem(orderItem));
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;

    }

    private OrderItemVo assembleOrderItem(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;
    }


    private void clearCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectProductByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateProductSelective(product);
        }
    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        order.setPayment(payment);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        //生成订单号
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        //默认不打折
        order.setPostage(0);
        //初始状态是 为未支付
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());

        //插入数据库
        int count = orderMapper.insertSelective(order);
        if (count > 0) {
            return order;
        }
        return null;

    }

    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    private List<OrderItem> getByorderNoUsreId(long orderNo, Integer userId) {
        OrderItemExample example = new OrderItemExample();
        OrderItemExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andOrderNoEqualTo(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(example);
        return orderItemList;
    }

    /**
     * 计算总价
     *
     * @param orderItemList
     * @return
     */
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }


    /**
     * 获取一组OrderItem
     *
     * @param userId
     * @param cartList
     * @return
     */
    private ServerResponse getOrderItemByCart(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        if (cartList.isEmpty()) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectProductByPrimaryKey(cart.getProductId());
            //如果商品没有下架
            if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
                return ServerResponse.createByErrorMessage("该商品已经下架");
            }
            //检查库存是否充足
            if (cart.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足");
            }
            //正式拼接orderitem
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
            //价格由目前商品的价格决定
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId) {
        List<OrderVo> orderVoList = new ArrayList<>();
        for (Order order : orderList) {
            OrderItemExample example = new OrderItemExample();
            OrderItemExample.Criteria criteria = example.createCriteria();
            criteria.andOrderNoEqualTo(order.getOrderNo());
            if (userId != null) {
                //fixme 管理员查询的时候不需要userId
                criteria.andUserIdEqualTo(userId);
            }
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(example);
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }

        return orderVoList;
    }

    /**
     * 返回一个二维码
     *
     * @param order
     * @param path
     * @return
     */
    private String aplipay(Order order, String path) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("MMall扫码支付，订单号为:") + order.getOrderNo().toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "5m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        //todo 订单
        List<OrderItem> orderItemList = getByorderNoUsreId(order.getOrderNo(), order.getUserId());
        for (OrderItem item : orderItemList) {
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods = GoodsDetail.newInstance(item.getProductId().toString(), item.getProductName(),
                    BigDecimalUtil.mul(item.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(), item.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipy.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        //支付宝返回的结果
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                //下单成功，创建二维码，并上传到服务器上
                logger.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                //简单打印应答
                dumpResponse(response);

                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String filePath = String.format(path + "/qr_%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr_%s.png", response.getOutTradeNo());

                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

                File targetFile = new File(path, qrFileName);
                //上传
                String qr_path = "MMall/qr_code/" + targetFile.getName();
                FtpFileUploadUtil.fileUpload(qr_path, targetFile);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.serverIp") + targetFile.getName();

                logger.info("上传二维码二次:" + qrUrl);
                //                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                return qrUrl;

            case FAILED:
                return "支付宝预下单失败!!!";
            case UNKNOWN:
                return "系统异常，预下单状态未知!!!";
            default:
                return "不支持的交易状态，交易返回异常!!!";
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {

        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }


}


