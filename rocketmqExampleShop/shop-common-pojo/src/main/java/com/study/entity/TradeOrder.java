package com.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author studyStar
 * @since 2023-02-15
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("trade_order")
public class TradeOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "order_id", type = IdType.ASSIGN_ID)
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单状态 0未确认 1已确认 2已取消 3无效 4退款
     */
    private Integer orderStatus;

    /**
     * 支付状态 0未支付 1支付中 2已支付
     */
    private Integer payStatus;

    /**
     * 发货状态 0未发货 1已发货 2已收货
     */
    private Integer shippingStatus;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 收货人
     */
    private String consignee;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品数量
     */
    private Integer goodsNumber;

    /**
     * 商品价格
     */
    private BigDecimal goodsPrice;

    /**
     * 商品总价
     */
    private Long goodsAmount;

    /**
     * 运费
     */
    private BigDecimal shippingFee;

    /**
     * 订单价格
     */
    private BigDecimal orderAmount;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券
     */
    private BigDecimal couponPaid;

    /**
     * 已付金额
     */
    private BigDecimal moneyPaid;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 创建时间
     */
    private Date addTime;

    /**
     * 订单确认时间
     */
    private Date confirmTime;

    /**
     * 支付时间
     */
    private Date payTime;
}
