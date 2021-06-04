package io.github.baijianruoli.lidou.loadBalance;

import com.alibaba.fastjson.JSON;
import io.github.baijianruoli.lidou.util.GlobalReferenceMap;
import io.github.baijianruoli.lidou.util.PathUtils;
import io.github.baijianruoli.lidou.util.ZkEntry;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@Slf4j
public class WeightLoadBalance implements  LoadBalance {
    @Autowired
    private ZkClient zkClient;
    @Autowired
    private PathUtils pathUtils;
    @Override
    public ZkEntry localBalance(String path) throws Exception{

            List<String> children = zkClient.getChildren(path);
            ArrayList<ZkEntry> list = new ArrayList<>();
            children.forEach(res->{
                String prefix=path;
                prefix+= "/" + res;
                ZkEntry zkEntry = pathUtils.readData(prefix);
                IntStream.rangeClosed(1,zkEntry.getWeight()).forEach(ans->{
                    list.add(zkEntry);
                });
            });
                ZkEntry zkEntry= list.get(new Random().nextInt(list.size()));
                return zkEntry;
        }
}
