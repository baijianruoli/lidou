package io.github.baijianruoli.lidou.config;

import com.alibaba.fastjson.JSON;
import io.github.baijianruoli.lidou.annotation.LidouService;
import io.github.baijianruoli.lidou.annotation.Reference;
import io.github.baijianruoli.lidou.code.ClientDecode;
import io.github.baijianruoli.lidou.code.ClientEncode;
import io.github.baijianruoli.lidou.code.ServerDecode;
import io.github.baijianruoli.lidou.code.ServerEncode;
import io.github.baijianruoli.lidou.exception.LidouException;
import io.github.baijianruoli.lidou.handler.ClientHandler;
import io.github.baijianruoli.lidou.handler.ServerHandler;
import io.github.baijianruoli.lidou.service.LoadBalanceService;
import io.github.baijianruoli.lidou.util.BaseRequest;
import io.github.baijianruoli.lidou.util.GlobalReferenceMap;
import io.github.baijianruoli.lidou.util.PathUtils;
import io.github.baijianruoli.lidou.util.ZkEntry;
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
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class InitRpcConfig implements CommandLineRunner {

    private ExecutorService executor = Executors.newFixedThreadPool(8);
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

    public Object getBean(Object bean, final Class<?> serviceClass, final Object o, String mode, String fallbackMethod) {
        // jdk动态代理
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceClass}, (proxy, method, args) -> {
            BaseRequest baseRequest = new BaseRequest((String) o, method.getName(), args, method.getParameterTypes());


            //负载均衡
            //获得zookeeper路径
            String url;
            int port;
            String path = PathUtils.addZkPath(serviceClass.getName());
            //TODO 限流

            //TODO 熔断
            //选择负载均衡算法,获得信息
            try {
                ZkEntry tmp = loadBalanceService.selectLoadBalance(path, mode);
                url = tmp.getHost();
                port = tmp.getPort();
                ClientHandler clientHandler;
                if (GlobalReferenceMap.CHANNELMAP.containsKey(url + port) && GlobalReferenceMap.CHANNELMAP.get(url + port).getSemaphore().availablePermits() == 1) {
                    clientHandler = GlobalReferenceMap.CHANNELMAP.get(url + port);
                } else {
                    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
                    clientHandler = new ClientHandler();
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(bossGroup).channel(NioSocketChannel.class)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    ChannelPipeline pipeline = socketChannel.pipeline();
                                    pipeline.addLast(new ClientEncode());
                                    pipeline.addLast(new ClientDecode());
                                    pipeline.addLast(clientHandler);
                                }
                            });
                    ChannelFuture future1 = bootstrap.connect(url, port).sync();
                    GlobalReferenceMap.CHANNELMAP.put(url + port, clientHandler);
                    clientHandler.setAddress(url + port);
                    future1.channel().closeFuture();
                }
                //设置参数
                clientHandler.setPars(baseRequest);
                return executor.submit(clientHandler).get();
            } catch (Exception e) {
                //TODO 降级
                if (!StringUtils.isEmpty(fallbackMethod)) {
                    try {
                        Method declaredMethod = bean.getClass().getDeclaredMethod(fallbackMethod, method.getParameterTypes());
                        return declaredMethod.invoke(bean, args);
                    } catch (NoSuchMethodException es) {
                        es.printStackTrace();
                    }
                }
                throw new LidouException("未找到有效节点");
            }
        });
    }

    @Override
    public void run(String... args) throws Exception {
        //获取@LidouService注解标记的类
        init();
        //使@Reference获得代理对象
        Di();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup groupGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, groupGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new ServerDecode());
                        pipeline.addLast(new ServerEncode());
                        pipeline.addLast(new IdleStateHandler(30, 30, 30, TimeUnit.SECONDS));
                        pipeline.addLast(new ServerHandler());
                    }
                });
        InetAddress address = InetAddress.getLocalHost();
        String hostAddress = address.getHostAddress();
        try {
            ChannelFuture future = serverBootstrap.bind(hostAddress, port).sync();
            future.channel().closeFuture();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void Di() {
        List<Class<? extends Annotation>> classes = Arrays.asList(Controller.class, Service.class, Component.class, Repository.class);
        classes.forEach(res -> {
            Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(res);
            for (Object bean : beansWithAnnotation.values()) {
                Field[] fields = bean.getClass().getDeclaredFields();
                for (Field f : fields) {
                    f.setAccessible(true);
                    if (f.isAnnotationPresent(Reference.class)) {
                        Class<?> type = f.getType();
                        Reference annotation = f.getAnnotation(Reference.class);
                        // 限流
                        int token = annotation.tokenLimit();
                        //获得代理对象
                        Object bean1 = this.initRpcConfig.getBean(bean, type, type.getName(), annotation.loadBalance(), annotation.fallback());
                        //注入代理对象
                        try {
                            f.set(bean, bean1);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

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
                //获取LidouService的权重
                LidouService annotation = clazz.getAnnotation(LidouService.class);
                //分装为对象保存到zookeeper
                ZkEntry zkEntry = new ZkEntry(hostAddress, port, annotation.weight());
                String prefix = PathUtils.addZkPath(inter.getName());
                zkClient.createPersistent(prefix, true);
                try {
                    zkClient.createEphemeral(prefix + "/lidou" + next);
                    zkClient.writeData(prefix + "/lidou" + next, JSON.toJSON(zkEntry));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }
    }

}
