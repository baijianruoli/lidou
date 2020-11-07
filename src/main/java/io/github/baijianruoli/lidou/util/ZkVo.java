package io.github.baijianruoli.lidou.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZkVo {

    private String host;
    private Integer port;
    private String ApplicationName;
}
