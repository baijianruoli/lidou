package com.zut.lpf.lidou.service.impl;

import com.zut.lpf.lidou.service.MessageService;
import com.zut.lpf.lidou.util.BaseResponse;
import com.zut.lpf.lidou.util.StatusCode;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    @Override
    public BaseResponse<String> send(String msg) {
        return new BaseResponse<String>(StatusCode.Success,msg);
    }

    @Override
    public BaseResponse<String> get(String msg) {
        return new BaseResponse<String>(StatusCode.Success,msg);
    }
}
