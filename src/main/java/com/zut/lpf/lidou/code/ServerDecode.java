package com.zut.lpf.lidou.code;

import com.zut.lpf.lidou.util.BaseRequest;
import com.zut.lpf.lidou.util.BaseResponse;
import com.zut.lpf.lidou.util.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteBuffer;
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
