package co.server.transport;

import co.server.serialization.ObjectEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;

import java.net.ConnectException;
import java.net.InetSocketAddress;

public class Client {
    protected String host;

    protected Integer port;

    protected Integer timeout = 5000;

    protected Float calltime;

    protected Boolean closed = false;

    protected Bootstrap bootstrap;
    protected EventLoopGroup eventLoopGroup;
    protected volatile Channel channel;

    public Client() throws ConnectException {
        this("0.0.0.0", 8011);
    }

    public Client(String host, Integer port) throws ConnectException {
        this.host = host;
        this.port = port;
        init();
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public void init() throws ConnectException {
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.eventLoopGroup).channel(NioSocketChannel.class);
        this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.getTimeout());
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(
                        new ObjectEncoder(),
                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                        new ObjectClientHandler());
            }
        });
    }

    public void connect() throws ConnectException {
        if (closed) return;

        ChannelFuture future = this.bootstrap.connect(host, port);
        future.awaitUninterruptibly();

        assert future.isDone();
        if (future.isCancelled()) {
            throw new ConnectException("client[" + getConnectAddress().getAddress() + "] is cancelled by user");
        } else if (!future.isSuccess()) {
            future.cause();
            throw new ConnectException("client[" + getConnectAddress().getAddress() + "] connect fail");
        }

        Channel newChan = future.channel();

        Channel oldChan = this.channel;
        if (oldChan != null) {
            try {
                oldChan.close();
            } finally {
                CoChannel.removeChannel(oldChan);
            }
        }

        this.channel = newChan;
    }

    public CoChannel getChannel() {
        if (this.channel == null) return null;

        if (this.channel.isActive()) {
            return CoChannel.getChannel(this.channel);
        }

        return null;
    }

    public void close() {
        closed = true;
        CoChannel.removeChannel(this.channel);
        this.eventLoopGroup.shutdownGracefully();
    }

    public InetSocketAddress getConnectAddress() {
        return new InetSocketAddress(this.host, this.port);
    }
}
