package com.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

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
@TableName("trade_pay")
public class TradePay implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付编号
     */
    @TableId(value = "pay_id", type = IdType.ASSIGN_ID)
    private Long payId;

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 是否已支付 1否 2是
     */
    private Integer isPaid;
}
