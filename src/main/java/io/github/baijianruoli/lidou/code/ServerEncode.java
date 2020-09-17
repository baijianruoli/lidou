package io.github.baijianruoli.lidou.code;

import io.github.baijianruoli.lidou.util.BaseResponse;
import io.github.baijianruoli.lidou.util.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ServerEncode extends MessageToByteEncoder<BaseResponse> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BaseResponse baseResponse, ByteBuf byteBuf) throws Exception {

        byteBuf.writeBytes(ProtostuffUtils.serialize(baseResponse));
    }
}
