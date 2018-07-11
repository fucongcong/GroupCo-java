package co.server.transport;

import co.server.pack.Request;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CoChannel {
    private static final Map<Channel, CoChannel> channelMap = new ConcurrentHashMap();

    private final Channel channel;

    public CoChannel(Channel channel) {
        this.channel = channel;
    }

    public void sendMsg(Request request) {
        this.channel.writeAndFlush(request);
    }

    public  static void removeChannel(Channel ch) {
        channelMap.remove(ch);
    }

    public static CoChannel getChannel(Channel ch) {
        if (channelMap.containsKey(ch)) {
            return channelMap.get(ch);
        }

        CoChannel cch = new CoChannel(ch);
        channelMap.put(ch, cch);

        return cch;
    }
}
