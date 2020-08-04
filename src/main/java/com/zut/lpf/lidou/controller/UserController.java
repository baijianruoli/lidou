package com.zut.lpf.lidou.controller;

import com.zut.lpf.lidou.config.InitRpcConfig;
import com.zut.lpf.lidou.service.UserService;
import com.zut.lpf.lidou.util.BaseResponse;
import com.zut.lpf.lidou.util.StatusCode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RestController
public class UserController {
    @RequestMapping("/user")
    public BaseResponse user(String msg) throws InvocationTargetException, IllegalAccessException {
        Object o = InitRpcConfig.rpcServiceMap.get("com.zut.lpf.lidou.service.UserService");
        System.out.println(o.getClass());
        Method[] methods = o.getClass().getMethods();
        for(Method p: methods)
        {
            System.out.println(p.getParameterTypes());

            System.out.println(p.getName());
            System.out.println(p.getParameters());
            if(p.getName().equals("add"))
            {
                Object invoke = p.invoke(o, msg);
                return new BaseResponse(StatusCode.Success,invoke);
            }
        }


        return new BaseResponse(StatusCode.Success);



    }
}
