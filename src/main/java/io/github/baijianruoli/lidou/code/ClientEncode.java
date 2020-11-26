package io.github.baijianruoli.lidou.code;

import io.github.baijianruoli.lidou.util.BaseRequest;
import io.github.baijianruoli.lidou.util.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ClientEncode extends MessageToByteEncoder<BaseRequest> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BaseRequest baseRequest, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(ProtostuffUtils.serialize(baseRequest));
    }
}
