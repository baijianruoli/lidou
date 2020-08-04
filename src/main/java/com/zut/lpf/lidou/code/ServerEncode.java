package com.zut.lpf.lidou.code;

import com.zut.lpf.lidou.util.BaseRequest;
import com.zut.lpf.lidou.util.BaseResponse;
import com.zut.lpf.lidou.util.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ServerEncode extends MessageToByteEncoder<BaseResponse> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BaseResponse baseResponse, ByteBuf byteBuf) throws Exception {
        System.out.println("回复信息："+baseResponse);
        byteBuf.writeBytes(ProtostuffUtils.serialize(baseResponse));
    }
}
