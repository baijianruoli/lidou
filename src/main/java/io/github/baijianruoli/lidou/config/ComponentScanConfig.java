package io.github.baijianruoli.lidou.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Configuration
@EnableAsync
@ComponentScan({"io.github.baijianruoli.lidou.config"
        ,"io.github.baijianruoli.lidou.loadBalance"
        ,"io.github.baijianruoli.lidou.service",
        "io.github.baijianruoli.lidou.util"
})
@EnableConfigurationProperties({
        LidouProperties.class
})
public class ComponentScanConfig {

}
