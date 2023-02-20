package com.study.common.exceptionHandler;

import com.study.common.constant.R;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(StudyException.class)
    @ResponseBody
    public R error(StudyException e) {
        e.printStackTrace();
        log.error(e.getMsg().toString());
        return R.fail(e.getMsg());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return R.fail();
    }


}

