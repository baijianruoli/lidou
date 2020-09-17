package io.github.baijianruoli.lidou.code;

import io.github.baijianruoli.lidou.util.BaseRequest;
import io.github.baijianruoli.lidou.util.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ServerDecode  extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        BaseRequest deserialize = ProtostuffUtils.deserialize(bytes, BaseRequest.class);

        list.add(deserialize);
    }
}
