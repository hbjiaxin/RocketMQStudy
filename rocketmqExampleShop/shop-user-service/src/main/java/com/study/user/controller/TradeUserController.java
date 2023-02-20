package com.study.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.api.UserApi;
import com.study.common.constant.R;
import com.study.common.constant.ShopCode;
import com.study.entity.TradeUser;
import com.study.entity.TradeUserMoneyLog;
import com.study.user.service.TradeUserMoneyLogService;
import com.study.user.service.TradeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author studyStar
 * @since 2023-02-15
 */

@RestController
@RequestMapping("/user")
public class TradeUserController implements UserApi {

    @Autowired
    private TradeUserService userService;

    @Autowired
    private TradeUserMoneyLogService userMoneyLogService;

    @Override
    @GetMapping("{userId}")
    public R findOne(@PathVariable("userId") Long userId) {
        if (userId == null) {
            return R.fail(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        TradeUser user = userService.getById(userId);
        if (user != null) {
            return R.success(user);
        }
        return R.fail(ShopCode.SHOP_USER_NO_EXIST);
    }

    @Override
    @PutMapping("pay")
    public R updateMoneyPaid(@RequestBody TradeUserMoneyLog userMoneyLog) {
        // 1.校验参数是否合法
        if (userMoneyLog == null ||
                userMoneyLog.getUserId() == null ||
                userMoneyLog.getOrderId() == null ||
                userMoneyLog.getMoneyLogType() == null ||
                userMoneyLog.getUseMoney() == null ||
                userMoneyLog.getUseMoney().compareTo(BigDecimal.ZERO) == -1) {
            return R.fail(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }

        // 2.查询订单余额使用日志（后序扣减余额和回退余额操作）
        LambdaQueryWrapper<TradeUserMoneyLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(TradeUserMoneyLog::getOrderId, userMoneyLog.getOrderId())
                .eq(TradeUserMoneyLog::getUserId, userMoneyLog.getUserId());
        long count = userMoneyLogService.count(logWrapper);

        // 3.根据参数确定是扣减余额还是回退余额
        TradeUser user = userService.getById(userMoneyLog.getUserId());
        // 3.1 扣减余额
        if (userMoneyLog.getMoneyLogType().equals(ShopCode.SHOP_USER_MONEY_PAID.getCode())) {
            if (count > 0) { // 说明订单已扣减
                return R.fail(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
            } else { // 订单未扣减，扣减余额
                // 需要判断user，新写SQL来更新数值而不是直接修改值传入数据库？
                user.setUserMoney((new BigDecimal(user.getUserMoney()).subtract(userMoneyLog.getUseMoney())).longValue());
                // 更新数据，扣减余额
                boolean success = userService.updateById(user);
                if (!success) {
                    return R.fail(ShopCode.SHOP_USER_MONEY_REDUCE_FAIL);
                }
            }
        }
        // 3.2 回退余额
        if (userMoneyLog.getMoneyLogType().equals(ShopCode.SHOP_USER_MONEY_REFUND.getCode())) {
            if (count == 0) { // 说明订单未付款，不需要回退余额
                return R.fail(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY);
            } else { // 回退余额，防止多次退款
                logWrapper.eq(TradeUserMoneyLog::getMoneyLogType, ShopCode.SHOP_USER_MONEY_REFUND.getCode());
                count = userMoneyLogService.count(logWrapper);
                if (count == 0) {
                    // 退款
                    user.setUserMoney((new BigDecimal(user.getUserMoney()).add(userMoneyLog.getUseMoney())).longValue());
                    userService.updateById(user);
                } else { // 已退过款
                    return R.fail(ShopCode.SHOP_USER_MONEY_REFUND_ALREADY);
                }
            }
        }
        // 4.保存余额使用日志
        userMoneyLog.setCreateTime(new Date());
        userMoneyLogService.save(userMoneyLog); // userid,orderid,type：复合组件
        return R.success();
    }
}
