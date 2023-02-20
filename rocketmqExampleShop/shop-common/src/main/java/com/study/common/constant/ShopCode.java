package com.study.common.constant;

public interface ShopCode {
    //正确
    BaseCode SHOP_SUCCESS = MSG(true, 1, "正确");

    //错误
    BaseCode SHOP_FAIL = MSG(false, 0, "错误");

    //付款
    BaseCode SHOP_USER_MONEY_PAID = MSG(true, 1, "付款");

    //退款
    BaseCode SHOP_USER_MONEY_REFUND = MSG(true, 2, "退款");

    //订单未确认
    BaseCode SHOP_ORDER_NO_CONFIRM = MSG(false, 0, "订单未确认");

    //订单已确认
    BaseCode SHOP_ORDER_CONFIRM = MSG(true, 1, "订单已经确认");

    //订单已取消
    BaseCode SHOP_ORDER_CANCEL = MSG(false, 2, "订单已取消");

    //订单已取消
    BaseCode SHOP_ORDER_INVALID = MSG(false, 3, "订单无效");

    //订单已取消
    BaseCode SHOP_ORDER_RETURNED = MSG(false, 4, "订单已退货");

    //订单已付款
    BaseCode SHOP_ORDER_PAY_STATUS_NO_PAY = MSG(true, 0, "订单未付款");

    //订单已付款
    BaseCode SHOP_ORDER_PAY_STATUS_PAYING = MSG(true, 1, "订单正在付款");

    //订单已付款
    BaseCode SHOP_ORDER_PAY_STATUS_IS_PAY = MSG(true, 2, "订单已付款");

    //消息正在处理
    BaseCode SHOP_MQ_MESSAGE_STATUS_PROCESSING = MSG(true, 0, "消息正在处理");

    //消息处理成功
    BaseCode SHOP_MQ_MESSAGE_STATUS_SUCCESS = MSG(true, 1, "消息处理成功");

    //消息处理失败
    BaseCode SHOP_MQ_MESSAGE_STATUS_FAIL = MSG(false, 2, "消息处理失败");

    //请求参数有误
    BaseCode SHOP_REQUEST_PARAMETER_VALID = MSG(false, -1, "请求参数有误");

    //优惠券已经使用
    BaseCode SHOP_COUPON_ISUSED = MSG(true, 1, "优惠券已经使用");

    //优惠券未使用
    BaseCode SHOP_COUPON_UNUSED = MSG(false, 0, "优惠券未使用");

    //快递运费不正确
    BaseCode SHOP_ORDER_STATUS_UPDATE_FAIL = MSG(false, 10001, "订单状态修改失败");

    //快递运费不正确
    BaseCode SHOP_ORDER_SHIPPINGFEE_INVALID = MSG(false, 10002, "订单运费不正确");

    //订单总价格不合法
    BaseCode SHOP_ORDERAMOUNT_INVALID = MSG(false, 10003, "订单总价格不正确");

    //订单保存失败
    BaseCode SHOP_ORDER_SAVE_ERROR = MSG(false, 10004, "订单保存失败");

    //订单确认失败
    BaseCode SHOP_ORDER_CONFIRM_FAIL = MSG(false, 10005, "订单确认失败");

    //商品不存在
    BaseCode SHOP_GOODS_NO_EXIST = MSG(false, 20001, "商品不存在");

    //订单价格非法
    BaseCode SHOP_GOODS_PRICE_INVALID = MSG(false, 20002, "商品价格非法");

    //商品库存不足
    BaseCode SHOP_GOODS_NUM_NOT_ENOUGH = MSG(false, 20003, "商品库存不足");

    //扣减库存失败
    BaseCode SHOP_REDUCE_GOODS_NUM_FAIL = MSG(false, 20004, "扣减库存失败");

    //库存记录为空
    BaseCode SHOP_REDUCE_GOODS_NUM_EMPTY = MSG(false, 20005, "扣减库存失败");

    //用户账号不能为空
    BaseCode SHOP_USER_IS_NULL = MSG(false, 30001, "用户账号不能为空");

    //用户信息不存在
    BaseCode SHOP_USER_NO_EXIST = MSG(false, 30002, "用户不存在");

    //余额扣减失败
    BaseCode SHOP_USER_MONEY_REDUCE_FAIL = MSG(false, 30003, "余额扣减失败");

    //已经退款
    BaseCode SHOP_USER_MONEY_REFUND_ALREADY = MSG(true, 30004, "订单已经退过款");

    //优惠券不不存在
    BaseCode SHOP_COUPON_NO_EXIST = MSG(false, 40001, "优惠券不存在");

    //优惠券不合法
    BaseCode SHOP_COUPON_INVALIED = MSG(false, 40002, "优惠券不合法");

    //优惠券使用失败
    BaseCode SHOP_COUPON_USE_FAIL = MSG(false, 40003, "优惠券使用失败");

    //余额不能小于0
    BaseCode SHOP_MONEY_PAID_LESS_ZERO = MSG(false, 50001, "余额不能小于0");

    //余额非法
    BaseCode SHOP_MONEY_PAID_INVALID = MSG(false, 50002, "余额非法");

    //Topic不能为空
    BaseCode SHOP_MQ_TOPIC_IS_EMPTY = MSG(false, 60001, "Topic不能为空");

    //消息体不能为空
    BaseCode SHOP_MQ_MESSAGE_BODY_IS_EMPTY = MSG(false, 60002, "消息体不能为空");

    //消息发送失败
    BaseCode SHOP_MQ_SEND_MESSAGE_FAIL = MSG(false, 60003, "消息发送失败");

    //支付订单未找到
    BaseCode SHOP_PAYMENT_NOT_FOUND = MSG(false, 70001, "支付订单未找到");

    //支付订单已支付
    BaseCode SHOP_PAYMENT_IS_PAID = MSG(false, 70002, "支付订单已支付");

    //订单付款失败
    BaseCode SHOP_PAYMENT_PAY_ERROR = MSG(false, 70002, "订单支付失败");

    // 持久化消息已处理
    BaseCode SHOP_DATA_MESSAGE_SEND = MSG(true, 1, "消息还未发送成功");

    // 持久化消息未处理
    BaseCode SHOP_DATA_MESSAGE_NOT_SEND = MSG(false, 0, "消息发送成功");

    static BaseCode MSG(Boolean success, Integer code, String message) {
        return new BaseCode(success, code, message);
    }

}
