package com.study.api.fallback;

import com.study.api.UserApi;
import com.study.common.constant.R;
import com.study.entity.TradeUserMoneyLog;
import org.springframework.stereotype.Component;

@Component
public class UserApiFallback implements UserApi {
    @Override
    public R findOne(Long userId) {
        return R.fail();
    }

    @Override
    public R updateMoneyPaid(TradeUserMoneyLog userMoneyLog) {
        return R.fail();
    }
}

