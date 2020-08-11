package com.zut.lpf.lidou.handler;

import com.zut.lpf.lidou.config.InitRpcConfig;
import com.zut.lpf.lidou.util.BaseRequest;
import com.zut.lpf.lidou.util.BaseResponse;
import com.zut.lpf.lidou.util.StatusCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter  {

    private int lossCount=0;
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent)
        {
            IdleStateEvent i = (IdleStateEvent) evt;
            String eventType=null;
            switch (i.state())
            {
                case READER_IDLE:
                    eventType="读空闲";
                    lossCount++;
                    log.warn("{}读空闲...{}",ctx.channel().remoteAddress());
                    if(lossCount>2)
                    {
                        log.warn("{}通道关闭",ctx.channel().remoteAddress());
                        ctx.close();
                    }
                    break;
                case WRITER_IDLE:
                    eventType="写空闲";
                    break;
                case ALL_IDLE:
                    eventType="读写空闲";
                    break;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       ctx.close();
       log.info("发生了异常{}",cause.getMessage());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        BaseRequest request = (BaseRequest) msg;
        if(request.getClassName().equals("heart"))
            return ;
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Class<?>[] parameTypes = request.getParameTypes();
        Object[] parameters = request.getParameters();
        Object o = InitRpcConfig.rpcServiceMap.get(className);
        Object message=null;
        try{
            Method declaredMethod = o.getClass().getDeclaredMethod(methodName, parameTypes);
            Object invoke = declaredMethod.invoke(o, parameters);
            Class<?> returnType = declaredMethod.getReturnType();
            message=invoke;

        }catch (NoSuchMethodException e)
        {
            log.info("bean实例化未找到");
            ctx.writeAndFlush(new BaseResponse<String>(StatusCode.Fail,e.getMessage()));
        }
        ctx.writeAndFlush(new BaseResponse(StatusCode.Success,message));


    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       log.info("{}加入注册中心",ctx.channel().remoteAddress());


    }


}
