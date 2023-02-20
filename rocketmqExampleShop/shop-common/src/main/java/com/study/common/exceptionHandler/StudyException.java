package com.study.common.exceptionHandler;

import com.study.common.constant.BaseCode;
import com.study.common.constant.ShopCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyException extends RuntimeException {

    private BaseCode msg;

}

