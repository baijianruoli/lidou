package io.github.baijianruoli.lidou.config;

import io.github.baijianruoli.lidou.annotation.LidouService;
import io.github.baijianruoli.lidou.annotation.Reference;
import io.github.baijianruoli.lidou.code.ClientDecode;
import io.github.baijianruoli.lidou.code.ClientEncode;
import io.github.baijianruoli.lidou.handler.ClientHandler;
import io.github.baijianruoli.lidou.handler.ServerHandler;
import io.github.baijianruoli.lidou.code.ServerDecode;
import io.github.baijianruoli.lidou.code.ServerEncode;
import io.github.baijianruoli.lidou.service.LoadBalanceService;
import io.github.baijianruoli.lidou.util.BaseRequest;
import io.github.baijianruoli.lidou.util.PathUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class InitRpcConfig implements CommandLineRunner {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    @Autowired
    private ApplicationContext applicationContext;
    public static Map<String, Object> rpcServiceMap = new HashMap<>();
    @Value("${lidou.port}")
    private Integer port;
    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private ZkClient zkClient;
    @Autowired
    private InitRpcConfig initRpcConfig;
    @Autowired
    private LoadBalanceService loadBalanceService;

    public Object getBean(final Class<?> serviceClass, final Object o, String mode) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceClass}, (proxy, method, args) -> {
            BaseRequest baseRequest = new BaseRequest((String) o, method.getName(), args, method.getParameterTypes());
            //负载均衡
            //获得zookeeper路径
            String url;
            String port;
            String path = PathUtils.addZkPath(serviceClass.getName());
            List<String> children = zkClient.getChildren(path);
            //负载均衡
            String tmp = loadBalanceService.loadBalance(path, children, mode);
            String[] split = tmp.split(":");
            url = split[0];
            port = split[1];
            NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
            ClientHandler clientHandler = new ClientHandler();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new ClientEncode());
                    pipeline.addLast(new ClientDecode());
                    pipeline.addLast(new IdleStateHandler(80L, 80L, 80L, TimeUnit.SECONDS));
                    pipeline.addLast(clientHandler);
                }
            });
            ChannelFuture future1 = bootstrap.connect(url, Integer.valueOf(port)).sync();
            clientHandler.setPars(baseRequest);
            Object result = executor.submit(clientHandler).get();
            future1.channel().closeFuture();
            bossGroup.shutdownGracefully();
            return result;
        });
    }

    @Override
    public void run(String... args) throws Exception {
        //获取@LidouService注解标记的类
        init();
        //使@Reference获得代理对象
        Di();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup groupGroup = new NioEventLoopGroup(4);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, groupGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new ServerDecode());
                        pipeline.addLast(new ServerEncode());
                        pipeline.addLast(new IdleStateHandler(100, 100, 100, TimeUnit.SECONDS));
                        pipeline.addLast(new ServerHandler());
                    }
                });
        InetAddress address = InetAddress.getLocalHost();
        String hostAddress = address.getHostAddress();
        try {
            ChannelFuture future = serverBootstrap.bind(hostAddress, port).sync();
            log.info("服务端启动");
            future.channel().closeFuture();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(hostAddress);
            System.out.println(port);
        }


    }

    public void Di() throws IllegalAccessException {
        Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(Controller.class);
        for (Object bean : beansWithAnnotation.values()) {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                if (f.isAnnotationPresent(Reference.class)) {
                    Class<?> type = f.getType();
                    Reference annotation = f.getAnnotation(Reference.class);
                    //获得代理对象
                    Object bean1 = this.initRpcConfig.getBean(type, type.getName(), annotation.loadBalance());
                    //注入代理对象
                    f.set(bean, bean1);
                }
            }
        }
    }

    public void init() throws UnknownHostException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(LidouService.class);
        for (Object bean : beansWithAnnotation.values()) {
            Class<?> clazz = bean.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> inter : interfaces) {
                rpcServiceMap.put(inter.getName(), bean);
                log.info("已经加载的服务" + inter.getName());
                InetAddress address = InetAddress.getLocalHost();
                String hostAddress = address.getHostAddress();
                String next = hostAddress + ":" + port;
                zkClient.createPersistent(PathUtils.addZkPath(inter.getName()), true);
                zkClient.createEphemeral(PathUtils.addZkPath(inter.getName()) + "/lidou" + next);
                zkClient.writeData(PathUtils.addZkPath(inter.getName()) + "/lidou" + next, next);
            }
        }
    }

}
