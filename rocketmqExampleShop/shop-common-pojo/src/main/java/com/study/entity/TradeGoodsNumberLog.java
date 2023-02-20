package com.study.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
@TableName("trade_goods_number_log")
public class TradeGoodsNumberLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("goods_id")
    private Long goodsId;

    @TableField("order_id")
    private Long orderId;

    private Integer goodsNumber;

    private Date logTime;
}
