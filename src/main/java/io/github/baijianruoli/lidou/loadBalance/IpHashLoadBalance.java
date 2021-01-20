package io.github.baijianruoli.lidou.loadBalance;

import io.github.baijianruoli.lidou.util.GlobalReferenceMap;
import io.github.baijianruoli.lidou.util.PathUtils;
import io.github.baijianruoli.lidou.util.ZkEntry;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class IpHashLoadBalance implements  LoadBalance {
    @Autowired
    private ZkClient zkClient;
    @Autowired
    private PathUtils pathUtils;
    @Override
    public ZkEntry localBalance(String path) {
        if(!GlobalReferenceMap.ZKLISTENMAP.containsKey(path))
        {
            List<String> children = zkClient.getChildren(path);
            pathUtils.addListAndWatch(path,children);
        }
        ZkEntry server =ConSistentHashingWithoutVirtualNode.getServer(path);
        return server;
    }
}
