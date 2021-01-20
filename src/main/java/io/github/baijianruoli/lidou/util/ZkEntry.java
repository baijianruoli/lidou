package io.github.baijianruoli.lidou.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZkEntry {
       private String host;
       private int port;
       private int weight;
}
