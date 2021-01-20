package io.github.baijianruoli.lidou.loadBalance;

import com.alibaba.fastjson.JSON;
import io.github.baijianruoli.lidou.util.GlobalReferenceMap;
import io.github.baijianruoli.lidou.util.PathUtils;
import io.github.baijianruoli.lidou.util.ZkEntry;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoundRobinLoadBalance implements  LoadBalance{
    @Autowired
    private ZkClient zkClient;
    @Autowired
    private PathUtils pathUtils;
    @Override
    public ZkEntry localBalance(String path) {
        Integer index=0;
        if (GlobalReferenceMap.REFERENCEMAP.containsKey(path)) {
            index = GlobalReferenceMap.REFERENCEMAP.get(path);
        }
        GlobalReferenceMap.REFERENCEMAP.put(path, (index + 1) % GlobalReferenceMap.ZKLISTENMAP.size());
        if(!GlobalReferenceMap.ZKLISTENMAP.containsKey(path))
        {
            List<String> children = zkClient.getChildren(path);
            pathUtils.addListAndWatch(path,children);
        }
        return GlobalReferenceMap.ZKLISTENMAP.get(path).get(index);

    }
}
