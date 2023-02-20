package com.study.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MQEntity {
    // 订单Id 优惠券ID 用户ID 余额 商品ID 商品数量
    private Long orderId;
    private Long couponId;
    private Long userId;
    private BigDecimal userMoney;
    private Long goodsId;
    private Integer goodsNum;
}

