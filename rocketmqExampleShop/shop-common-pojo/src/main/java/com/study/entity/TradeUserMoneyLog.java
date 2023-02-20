package com.study.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2023-02-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("trade_user_money_log")
public class TradeUserMoneyLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    private Long userId;

    @TableField("order_id")
    private Long orderId;

    @TableField("money_log_type")
    private Integer moneyLogType;

    private BigDecimal useMoney;

    private Date createTime;
}
