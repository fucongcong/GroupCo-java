package co.server.echo;

import co.server.pack.Request;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler implementation for the object echo client.  It initiates the
 * ping-pong traffic between the object echo client and server by sending the
 * first message to the server.
 */
public class ObjectEchoClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(ObjectEchoClientHandler.class);

    private final List<Integer> firstMessage;

    /**
     * Creates a client-side handler.
     */
    public ObjectEchoClientHandler() {
        firstMessage = new ArrayList<Integer>(ObjectEchoClient.SIZE);
        for (int i = 0; i < ObjectEchoClient.SIZE; i ++) {
            firstMessage.add(Integer.valueOf(i));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send the first message if this handler is a client-side handler.
        Request data = new Request();
        data.setCmd("Shop\\Shop::getShop");
        Map param = new HashMap();
        param.put("name", "aaa");
        data.setData(JSON.toJSONString(param));

        ctx.writeAndFlush(data);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Echo back the received object to the server.
        //ctx.write(msg);
        logger.info("type:" + msg.getClass().getName());
        //ShopEntity shopEntity = (ShopEntity) ((Response) msg).getData();
//        ShopService shopService = (ShopService) ((Response) msg).getData();
//        ShopEntity shopEntity = shopService.getShop("aaa");
       //System.out.println(shopEntity.getName());
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
