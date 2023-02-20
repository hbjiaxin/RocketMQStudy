package com.study.common.constant;

import lombok.Data;

@Data
public class R<T> {

    private BaseCode baseCode;
    private T data;

    private R() {

    }

    public static R success() {
        R result = new R();
        result.setBaseCode(ShopCode.SHOP_SUCCESS);
        return result;
    }

    public static R success(BaseCode BaseCode) {
        R result = new R();
        result.setBaseCode(BaseCode);
        return result;
    }

    public static R success(Object o) {
        R result = success();
        result.setData(o);
        return result;
    }

    public static R success(BaseCode BaseCode, Object o) {
        R result = new R();
        result.setData(o);
        result.setBaseCode(BaseCode);
        return result;
    }

    public static R fail() {
        R result = new R();
        result.setBaseCode(ShopCode.SHOP_FAIL);
        return result;
    }

    public static R fail(BaseCode BaseCode) {
        R result = new R();
        result.setBaseCode(BaseCode);
        return result;
    }

    public static R fail(Object o) {
        R result = fail();
        result.setData(o);
        return result;
    }

    public static R fail(BaseCode BaseCode, Object o) {
        R result = new R();
        result.setData(o);
        result.setBaseCode(BaseCode);
        return result;
    }
}