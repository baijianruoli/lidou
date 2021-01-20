package io.github.baijianruoli.lidou.loadBalance;



import io.github.baijianruoli.lidou.util.ZkEntry;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConSistentHashingWithoutVirtualNode {
    //待添加入Hash环的服务器列表


    //真实结点列表,考虑到服务器上线、下线的场景，即添加、删除的场景会比较频繁，这里使用LinkedList会更好
    private static ConcurrentLinkedQueue<ZkEntry> realNodes = new ConcurrentLinkedQueue<ZkEntry>();

    //虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
    private static SortedMap<Integer, ZkEntry> virtualNodes = new TreeMap<Integer,ZkEntry>();

    //虚拟节点的数目，这里写死，为了演示需要，一个真实结点对应5个虚拟节点
    private static final int VIRTUAL_NODES = 5;

    public static void addNode(ZkEntry zkEntry)
    {
        //先把原始的服务器添加到真实结点列表中
        realNodes.add(zkEntry);
        //再添加虚拟节点，遍历LinkedList使用foreach循环效率会比较高
        for(int i=0; i<VIRTUAL_NODES; i++){
            String virtualNodeName = zkEntry.getHost()+zkEntry.getPort()+ "&&VN" + String.valueOf(i);
            int hash = getHash(virtualNodeName);
            virtualNodes.put(hash, zkEntry);
        }
    }
    public static void removeAll()
    {
        realNodes.clear();
        virtualNodes.clear();
    }


    //使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
    private static int getHash(String str){
        final int p = 16777619;
        int hash = (int)2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    //得到应当路由到的结点
    public static ZkEntry getServer(String key){
        //得到该key的hash值
        int hash = getHash(key);
        // 得到大于该Hash值的所有Map
        SortedMap<Integer, ZkEntry> subMap = virtualNodes.tailMap(hash);
        ZkEntry virtualNode;
        if(subMap.isEmpty()){
            //如果没有比该key的hash值大的，则从第一个node开始
            Integer i = virtualNodes.firstKey();
            //返回对应的服务器
            virtualNode = virtualNodes.get(i);
        }else{
            //第一个Key就是顺时针过去离node最近的那个结点
            Integer i = subMap.firstKey();
            //返回对应的服务器
            virtualNode = subMap.get(i);
        }
        //virtualNode虚拟节点名称要截取一下
        if(virtualNode!=null){
            return virtualNode;
        }
        return null;
    }
}
