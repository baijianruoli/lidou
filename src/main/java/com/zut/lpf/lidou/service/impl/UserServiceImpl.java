package com.zut.lpf.lidou.service.impl;

import com.zut.lpf.lidou.config.LidouService;
import com.zut.lpf.lidou.service.UserService;
import com.zut.lpf.lidou.util.BaseResponse;
import com.zut.lpf.lidou.util.StatusCode;
import org.springframework.stereotype.Service;

@LidouService
public class UserServiceImpl implements UserService {


    @Override
    public BaseResponse add(String msg, int id) {
        return new BaseResponse<String>(StatusCode.Success,id+msg);
    }

    @Override
    public BaseResponse delete(String msg) {
        return new BaseResponse<String>(StatusCode.Success,msg);
    }
}
