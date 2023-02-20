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
@TableName("trade_coupon")
public class TradeCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券ID
     */
    @TableId(value = "coupon_id", type = IdType.ASSIGN_ID)
    private Long couponId;

    /**
     * 优惠券金额
     */
    private BigDecimal couponPrice;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 是否使用 0未使用 1已使用
     */
    private Integer isUsed;

    /**
     * 使用时间
     */
    private Date usedTime;
}
