package io.github.baijianruoli.lidou.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalReferenceMap {
    public static ConcurrentHashMap<String, Integer> REFERENCEMAP = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, List<ZkEntry>>  ZKLISTENMAP=new ConcurrentHashMap<>();
}
