package com.zhangguoyu.app;

/**
 * Created by zhangguoyu on 13-5-23.
 */
public class CBlockNotFoundException extends RuntimeException {

    public CBlockNotFoundException() {
        super();
    }

    public CBlockNotFoundException(String msg) {
        super(msg);
    }

    public CBlockNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
