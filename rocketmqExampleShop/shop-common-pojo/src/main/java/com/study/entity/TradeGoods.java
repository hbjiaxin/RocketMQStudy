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
@TableName("trade_goods")
public class TradeGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "goods_id", type = IdType.ASSIGN_ID)
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品库存
     */
    private Integer goodsNumber;

    /**
     * 商品价格
     */
    private BigDecimal goodsPrice;

    /**
     * 商品描述
     */
    private String goodsDesc;

    /**
     * 添加时间
     */
    private Date addTime;
}
