package io.github.baijianruoli.lidou.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZkClientConfig {

    @Value("${lidou.zookeeper.url}")
    private String ZkUrl;
    @Bean
    public ZkClient zkcline()
    {
        ZkClient zkClient = new ZkClient(ZkUrl , 3000);
        zkClient.setZkSerializer( new MyZkSerializer());
        return zkClient;
    }
}
