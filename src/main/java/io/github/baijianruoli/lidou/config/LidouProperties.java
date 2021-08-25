package io.github.baijianruoli.lidou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(LidouProperties.PREFIX)
public class LidouProperties {
    public static final String PREFIX = "lidou";

    private Integer port = 8080;
    private String servicePackage = "xx";
    private String zookeeperUrl = "ee";
}
