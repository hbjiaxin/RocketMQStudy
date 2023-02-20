package com.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2023-02-20
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("trade_mq_producer_temp")
public class TradeMqProducerTemp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private String groupName;

    private String msgTopic;

    private String msgTag;

    private String msgKey;

    private String msgBody;

    private Integer msgStatus;

    private Date createTime;
}
