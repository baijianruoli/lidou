package io.github.baijianruoli.lidou.loadBalance;

import io.github.baijianruoli.lidou.util.ZkEntry;

import java.util.List;

public interface LoadBalance {
    public ZkEntry localBalance(String path) throws Exception;
}
