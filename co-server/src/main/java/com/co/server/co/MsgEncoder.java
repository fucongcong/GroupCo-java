package com.co.server.co;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import com.co.server.util.SocketUtil;

public class MsgEncoder  extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) {

        byte[] resByte = SocketUtil.strToByteArray(msg);
        int dataLength = resByte.length;

        // Write a message.
        out.writeInt(dataLength);  // data length
        out.writeBytes(resByte);      // data
    }
}