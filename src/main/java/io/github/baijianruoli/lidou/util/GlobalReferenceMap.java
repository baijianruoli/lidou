package io.github.baijianruoli.lidou.util;


import io.github.baijianruoli.lidou.handler.ClientHandler;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalReferenceMap {
    public static ConcurrentHashMap<String, Integer> REFERENCEMAP = new ConcurrentHashMap<>();


    public static ConcurrentHashMap<String, List<ZkEntry>>  ZKLISTENMAP=new ConcurrentHashMap<>();

    //keep-alive  管道长连接
    public static ConcurrentHashMap<String, ClientHandler>  CHANNELMAP=new ConcurrentHashMap<>();
}
