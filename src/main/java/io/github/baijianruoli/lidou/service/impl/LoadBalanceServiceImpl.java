package io.github.baijianruoli.lidou.service.impl;

import io.github.baijianruoli.lidou.loadBalance.IpHashLoadBalance;
import io.github.baijianruoli.lidou.loadBalance.RandomLoadBalance;
import io.github.baijianruoli.lidou.loadBalance.RoundRobinLoadBalance;
import io.github.baijianruoli.lidou.loadBalance.WeightLoadBalance;
import io.github.baijianruoli.lidou.service.LoadBalanceService;
import io.github.baijianruoli.lidou.util.GlobalReferenceMap;
import io.github.baijianruoli.lidou.util.ZkEntry;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class LoadBalanceServiceImpl implements LoadBalanceService {


    @Autowired
    private IpHashLoadBalance ipHashLoadBalance;
    @Autowired
    private RandomLoadBalance randomLoadBalance;
    @Autowired
    private RoundRobinLoadBalance roundRobinLoadBalance;
    @Autowired
    private WeightLoadBalance weightLoadBalance;

    @Override
    public ZkEntry selectLoadBalance(String path, String mode) throws  Exception{
        switch (mode)
        {
            case "random":
              return randomLoadBalance.localBalance(path);
            case "roundRobin":
                return roundRobinLoadBalance.localBalance(path);
            case "consistentHash":
                return ipHashLoadBalance.localBalance(path);
            case "weight":
                return weightLoadBalance.localBalance(path);
                default:
                    log.warn("{}策略不存在,默认跳转为随机",mode);
                    return randomLoadBalance.localBalance(path);
        }

    }
}
