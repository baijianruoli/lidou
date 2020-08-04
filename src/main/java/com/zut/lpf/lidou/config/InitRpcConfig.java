package com.zut.lpf.lidou.config;

import com.zut.lpf.lidou.code.ServerDecode;
import com.zut.lpf.lidou.code.ServerEncode;
import com.zut.lpf.lidou.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InitRpcConfig implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;
    public static Map<String,Object> rpcServiceMap=new HashMap<>();
    @Override
    public void run(String... args) throws Exception {

        Map<String,Object> beansWithAnnoatation=applicationContext.getBeansWithAnnotation(Service.class);
        for(Object bean: beansWithAnnoatation.values())
        {
            Class<?> clazz=bean.getClass();
            Class<?>[] interfaces=clazz.getInterfaces();
            for(Class<?> inter:interfaces)
            {
                rpcServiceMap.put(inter.getName(),bean);
                log.info("已经加载的服务"+inter.getName());

            }
        }
        NioEventLoopGroup bossGroup=new NioEventLoopGroup(1);
        NioEventLoopGroup groupGroup=new NioEventLoopGroup();
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(bossGroup,groupGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new ServerDecode());
                        pipeline.addLast(new ServerEncode());
                        pipeline.addLast(new ServerHandler());
                    }
                });
        ChannelFuture future= serverBootstrap.bind("localhost", 9091).sync();
        System.out.println("服务端启动");
        future.channel().closeFuture().sync();
    }

}
