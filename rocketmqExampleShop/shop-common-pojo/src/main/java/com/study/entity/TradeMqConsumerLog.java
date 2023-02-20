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
 * @since 2023-02-17
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("trade_mq_consumer_log")
public class TradeMqConsumerLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String msgId;

    @TableField(value = "group_name")
    private String groupName;

    @TableField(value = "msg_tag")
    private String msgTag;

    @TableField(value = "msg_key")
    private String msgKey;

    private String msgBody;

    private Integer consumerStatus;

    private Integer consumerTimes;

    private Date consumerTimestamp;

    private String remark;
}
