package co.server.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;

import java.io.ObjectInputStream;
import java.util.List;

public class MsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 1) {
            return;
        }

        in.markReaderIndex();

        //检查是否java内部对象调用
        int magicNumber = in.readUnsignedByte();
        int dataLength = 0;
        if (magicNumber != 'F') {
            in.resetReaderIndex();

            dataLength = in.readInt();
            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }
            ByteBuf msg = in.readBytes(dataLength);
            out.add(msg);
        } else {
            if (in.readableBytes() < 4) {
                return;
            }
            dataLength = in.readInt();
            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }
            ByteBuf msg = in.readBytes(dataLength);

            ObjectInputStream ois = new CompactObjectInputStream(new ByteBufInputStream(msg, true), ClassResolvers.cacheDisabled(null));
            try {
                out.add(ois.readObject());
            } finally {
                ois.close();
            }
        }
    }
}