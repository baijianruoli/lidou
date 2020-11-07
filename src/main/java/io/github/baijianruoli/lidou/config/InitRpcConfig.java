package io.github.baijianruoli.lidou.config;

import io.github.baijianruoli.lidou.handler.ServerHandler;
import io.github.baijianruoli.lidou.code.ServerDecode;
import io.github.baijianruoli.lidou.code.ServerEncode;
import io.github.baijianruoli.lidou.util.ZkVo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class InitRpcConfig implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    public static Map<String,Object> rpcServiceMap=new HashMap<>();
    @Value("${lidou.port}")
    private Integer port;
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private ZkClient zkClient;

    @Override
    public void run(String... args) throws Exception {
        //获取@LidouService注解标记的类
     init();
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
                        pipeline.addLast(new IdleStateHandler(100,100,100, TimeUnit.SECONDS));
                        pipeline.addLast(new ServerHandler());
                    }
                });
        InetAddress address = InetAddress.getLocalHost();
        String hostAddress = address.getHostAddress();
        ChannelFuture future= serverBootstrap.bind(hostAddress, port).sync();
        log.info("服务端启动");
        future.channel().closeFuture();
    }
    public void init() throws UnknownHostException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(LidouService.class);
        for(Object bean: beansWithAnnotation.values())
        {
            Class<?> clazz=bean.getClass();
            Class<?>[] interfaces=clazz.getInterfaces();
            for(Class<?> inter:interfaces)
            {
                rpcServiceMap.put(inter.getName(),bean);
                log.info("已经加载的服务"+inter.getName());
                InetAddress address = InetAddress.getLocalHost();
                String hostAddress = address.getHostAddress();
                zkClient.createPersistent("/lidou/"+inter.getName()+"/providers/lidou"+hostAddress+":"+port,true);
                zkClient.writeData("/lidou/"+inter.getName()+"/providers/lidou"+hostAddress+":"+port,hostAddress+":"+port);


            }
        }





    }

}
