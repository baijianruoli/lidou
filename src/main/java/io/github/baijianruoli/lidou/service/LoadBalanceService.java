package io.github.baijianruoli.lidou.service;

import io.github.baijianruoli.lidou.util.ZkEntry;

import java.util.List;

public interface LoadBalanceService {
    //负载均衡
    public ZkEntry selectLoadBalance(String path, String mode);
}
