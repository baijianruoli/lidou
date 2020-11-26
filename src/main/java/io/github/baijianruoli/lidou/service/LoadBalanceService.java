package io.github.baijianruoli.lidou.service;

import java.util.List;

public interface LoadBalanceService {
    //负载均衡
    public String loadBalance(String path, List<String> children, String mode);
}
