package io.github.baijianruoli.lidou.service.impl;

import io.github.baijianruoli.lidou.service.LoadBalanceService;
import io.github.baijianruoli.lidou.util.GlobalReferenceMap;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class LoadBalanceServiceImpl implements LoadBalanceService {

    @Autowired
    private ZkClient zkClient;

    @Override
    public String loadBalance(String path, List<String> children,String mode) {
        if("random".equals(mode))
        {
            path+="/"+children.get(new Random().nextInt(children.size()));
        }else if("roundrobin".equals(mode))
        {
            Integer index;
            if(!GlobalReferenceMap.REFERENCEMAP.containsKey(path))
            {
                GlobalReferenceMap.REFERENCEMAP.put(path,0);
                index=1%children.size();
            }
            else
            {
                index= GlobalReferenceMap.REFERENCEMAP.get(path);
                GlobalReferenceMap.REFERENCEMAP.put(path,(index+1)%children.size());
            }
            path+="/"+children.get(index);
        }else{
            path+="/"+children.get(new Random().nextInt(children.size()));
        }
        String temp = (String)zkClient.readData(path);
        return temp;
    }
}
