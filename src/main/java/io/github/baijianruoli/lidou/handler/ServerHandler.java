package io.github.baijianruoli.lidou.handler;

import io.github.baijianruoli.lidou.config.InitRpcConfig;
import io.github.baijianruoli.lidou.util.BaseRequest;
import io.github.baijianruoli.lidou.util.BaseResponse;
import io.github.baijianruoli.lidou.util.StatusCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent i = (IdleStateEvent) evt;
            switch (i.state()) {
                case READER_IDLE:
//                    log.warn("{}读空闲...{}", ctx.channel().remoteAddress());
                        ctx.channel().close();
                    break;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        log.info("发生了异常{}", cause.getMessage());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        BaseRequest request = (BaseRequest) msg;
        if ("heart".equals(request.getClassName()))
            return;
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Class<?>[] parameTypes = request.getParameTypes();
        Object[] parameters = request.getParameters();
        Object o = InitRpcConfig.rpcServiceMap.get(className);
        Object message = null;
        try {
            Method declaredMethod = o.getClass().getDeclaredMethod(methodName, parameTypes);
            Object invoke = declaredMethod.invoke(o, parameters);
            message = invoke;
        } catch (NoSuchMethodException e) {
            log.info("bean实例化未找到");
            ctx.writeAndFlush(new BaseResponse<String>(StatusCode.Fail, e.getMessage()));
        }
        ctx.writeAndFlush(new BaseResponse(StatusCode.Success, message));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        log.info("{}离开",ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//       log.info("{}加入",ctx.channel().remoteAddress());
    }


}
