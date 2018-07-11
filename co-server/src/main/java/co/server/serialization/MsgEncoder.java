package co.server.serialization;

import co.server.pack.Response;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import co.server.common.util.SocketUtil;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class MsgEncoder extends MessageToByteEncoder<Response> {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    @Override
    protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) throws IOException {
        if (!msg.getType().equals("object")) {
            byte[] resByte = SocketUtil.strToByteArray(JSON.toJSONString(msg));
            int dataLength = resByte.length;

            out.writeInt(dataLength);
            out.writeBytes(resByte);
        } else {
            int startIdx = out.writerIndex();

            ByteBufOutputStream bout = new ByteBufOutputStream(out);
            ObjectOutputStream oout = null;
            try {
                bout.write(LENGTH_PLACEHOLDER);
                oout = new CompactObjectOutputStream(bout);
                oout.writeObject(msg);
                oout.flush();
            } finally {
                if (oout != null) {
                    oout.close();
                } else {
                    bout.close();
                }
            }

            int endIdx = out.writerIndex();

            out.setInt(startIdx, endIdx - startIdx - 4);
        }
    }
}