package io.github.baijianruoli.lidou.handler;

import io.github.baijianruoli.lidou.util.BaseRequest;
import io.github.baijianruoli.lidou.util.BaseResponse;
import io.github.baijianruoli.lidou.util.GlobalReferenceMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

@Slf4j
@Data
public class ClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;
    private Object result;
    private BaseRequest pars;
    //起始信号量
    private Semaphore semaphore = new Semaphore(0);
    //结果信号量
    private Semaphore resultLock = new Semaphore(0);

    private String address;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        log.error("{}异常", cause.getMessage());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.result = ((BaseResponse) msg).getData();
        resultLock.release();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
        semaphore.release();
    }

    public Object call() throws Exception {
        semaphore.acquire();
        this.context.channel().writeAndFlush(this.pars);
        resultLock.acquire();
        semaphore.release();
        return this.result;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //管道断开
        GlobalReferenceMap.CHANNELMAP.remove(address);
//       log.info("离开{}",ctx.channel().localAddress());
    }
}
