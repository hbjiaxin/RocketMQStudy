package com.study.common.constant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseCode {

    private Boolean success;
    private Integer code;
    private String msg;


    @Override
    public String toString() {
        return "ShopCode{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + msg + '\'' +
                '}';
    }
}

