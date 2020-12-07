# lidou
基于spring boot,netty和zookeeper的自制rpc框架

# Apache Maven

```
<dependency>
  <groupId>io.github.baijianruoli</groupId>
  <artifactId>lidou</artifactId>
  <version>1.2.0</version>
</dependency>
```

# 使用方法

1.在配置文件上配置lidou.port,设置服务器netty端口

2.在配置中心配置lidou.servicePackage,是Service的扫描路径

3.在配置文件加上lidou.zookeeper.url，为zookeeper的注册地址

4.在Service上标记@LidouService注解，使其作为远程服务的Service

5.使用@Reference标记要注入的代理对象

# 实现需求
- [x] jdk动态代理，获得调用方法的类全路径，方法参数，类型，和名称

- [x] protobuf序列化传输数据

- [x] netty进行服务间的通信

- [x] 多线程

- [x] 反射

- [x] 心跳检测

- [x] 自定义注解（现在客户端只要在Service上加上@Reference，自动注入代理对象,调用本地方法一样调用远程方法）

- [x] 加入zookeeper注册中心，提供简单的负载均衡(1.随机算法 2.轮询算法)

- [x] 加入maven中央仓库，方便使用

# 缺少的功能
- [ ]  提供更丰富的负载均衡策略
- [ ]  提高并发


# 压测
2w的压力测试下，tcp响应时间为一百多毫秒，多线程可以继续改进
