package com.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderVo {
    private BigDecimal payment;//价格
    private Long orderNo;//订单编号
    private Integer paymentType;//支付方式
    private String paymentTypeDesc;//支付描述
    private Integer postage;//邮费
    private Integer status;//状态
    private String statusDesc;//状态描述，不能返回数字
    private String paymentTime;
    private String sendTime;//发货时间
    private String endTime;
    private String closeTime;
    private String createTime;
    //订单明细
    private List<OrderItemVo> orderItemVoList;
    //图片
    private String imageHost;
    //地址
    private Integer shippingId;
    private String receiverName;
    private ShippingVo shippingVo;

}
