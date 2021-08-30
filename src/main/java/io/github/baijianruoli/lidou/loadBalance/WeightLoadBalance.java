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
import java.util.Collections;
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
    // 前缀加二分
    @Override
    public ZkEntry localBalance(String path) throws Exception{

        List<String> children = zkClient.getChildren(path);
        ArrayList<ZkEntry> list = new ArrayList<>();
        int ans=0;
        for(String res:children){
            String prefix=path;
            prefix+= "/" + res;
            ZkEntry zkEntry = pathUtils.readData(prefix);
            ans+=zkEntry.getWeight();
            zkEntry.setWeight(ans);
            list.add(zkEntry);
        }
        return list.get(binarySearch(new Random().nextInt(ans),list));
    }
    // java没有lower_bound 手写的二分
    private int binarySearch(int x,ArrayList<ZkEntry> pre) {
        int low = 0, high = pre.size() - 1;
        while (low < high) {
            int mid = (high - low) / 2 + low;
            if (pre.get(mid).getWeight() < x) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }


}
