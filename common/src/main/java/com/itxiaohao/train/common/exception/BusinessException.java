package com.itxiaohao.train.common.exception;

/**
 * @Author: itxiaohao
 * @date: 2023-08-30 21:32
 * @Description: 自定义异常类
 */
public class BusinessException extends RuntimeException{
    private BusinessExceptionEnum e;

    public BusinessException(BusinessExceptionEnum e) {
        this.e = e;
    }

    public BusinessExceptionEnum getE() {
        return e;
    }

    public void setE(BusinessExceptionEnum e) {
        this.e = e;
    }
    /*
    重写fillInStackTrace，自定义的异常不必打印所有的错误堆栈信息
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
