package com.study.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.api.CouponApi;
import com.study.api.GoodsApi;
import com.study.api.UserApi;
import com.study.common.constant.R;
import com.study.common.constant.ShopCode;
import com.study.common.exceptionHandler.StudyException;
import com.study.entity.*;
import com.study.order.mapper.TradeOrderMapper;
import com.study.order.service.TradeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author studyStar
 * @since 2023-02-15
 */
@Service
public class TradeOrderServiceImpl extends ServiceImpl<TradeOrderMapper, TradeOrder> implements TradeOrderService {

}
