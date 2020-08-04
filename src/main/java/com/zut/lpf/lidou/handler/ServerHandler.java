package com.zut.lpf.lidou.handler;

import com.zut.lpf.lidou.config.InitRpcConfig;
import com.zut.lpf.lidou.util.BaseRequest;
import com.zut.lpf.lidou.util.BaseResponse;
import com.zut.lpf.lidou.util.StatusCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter  {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       ctx.close();
       log.info("发生了异常{}",cause.getMessage());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BaseRequest request = (BaseRequest) msg;
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Class<?>[] parameTypes = request.getParameTypes();
        Object[] parameters = request.getParameters();
        Object o = InitRpcConfig.rpcServiceMap.get(className);
        BaseResponse message=null;
        try{
            Method declaredMethod = o.getClass().getDeclaredMethod(methodName, parameTypes);
            Object invoke = declaredMethod.invoke(o, parameters);
            message=(BaseResponse)invoke;

        }catch (NoSuchMethodException e)
        {
            log.info("bean实例化未找到");
            ctx.writeAndFlush(new BaseResponse<String>(StatusCode.Success,e.getMessage()));
        }
        ctx.writeAndFlush(message);


    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       log.info("{}加入注册中心",ctx.channel().remoteAddress());


    }


}
