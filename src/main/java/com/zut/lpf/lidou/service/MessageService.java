package com.zut.lpf.lidou.service;

import com.zut.lpf.lidou.util.BaseResponse;

public interface MessageService {
    public BaseResponse send(String msg);
    public BaseResponse get(String msg);
}
