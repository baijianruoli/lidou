package io.github.baijianruoli.lidou.util;

import java.util.concurrent.ConcurrentHashMap;

public class GlobalReferenceMap {
    public static ConcurrentHashMap<String, Integer> REFERENCEMAP = new ConcurrentHashMap<>();
}
