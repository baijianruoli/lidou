package io.github.baijianruoli.lidou.config;

import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

public class MyZkSerializer implements ZkSerializer {

    @Override
    public byte[] serialize(Object o) throws ZkMarshallingError {
        return JSON.toJSONBytes(o);
    }
    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        return JSON.parse(bytes);
    }
}
