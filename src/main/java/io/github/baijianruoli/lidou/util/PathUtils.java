package io.github.baijianruoli.lidou.util;

import com.alibaba.fastjson.JSON;
import io.github.baijianruoli.lidou.loadBalance.ConSistentHashingWithoutVirtualNode;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PathUtils {

    @Autowired
    private ZkClient zkClient;

    //添加前缀
    public static String addZkPath(String path) {
        return "/lidou/" + path + "/providers";
    }

    //读取数据并序列化
    public ZkEntry readData(String path) {
        Object o = zkClient.readData(path);
        ZkEntry zkEntry = JSON.parseObject(o.toString(), ZkEntry.class);
        return zkEntry;
    }

    //添加缓存
    public void addListAndWatch(String path, List<String> list) {
        ArrayList<ZkEntry> zkEntries = new ArrayList<>();
        list.forEach(res -> {
            ZkEntry zkEntry = readData(path + "/" + res);
            zkEntries.add(zkEntry);
            ConSistentHashingWithoutVirtualNode.addNode(zkEntry);
        });

        GlobalReferenceMap.ZKLISTENMAP.put(path, zkEntries);

        zkClient.subscribeChildChanges(path, (cur, child) -> {
            zkClient.unsubscribeChildChanges(path, (c, v) -> {
            });
            //删除缓存
            GlobalReferenceMap.ZKLISTENMAP.remove(path);
            //删除一致性hash槽
            ConSistentHashingWithoutVirtualNode.removeAll();

        });
    }


}
