package co.server.transport;

import co.server.echo.ObjectEchoClientHandler;
import co.server.pack.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ObjectClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(ObjectEchoClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Response response = (Response) msg;
        CoFuture.receive(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
