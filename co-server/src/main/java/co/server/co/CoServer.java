package co.server.co;


import co.server.registry.RedisRegistryProcesser;
import co.server.registry.RegistryProcesser;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sun.misc.Signal;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class CoServer {
    private static final Logger logger = LogManager.getLogger(CoServer.class);

    private String serviceName;

    private String registry;

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public CoServer() {
        this(8080);
    }

    public CoServer(int port) {
        this.port = port;
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MsgDecoder(), new MsgEncoder(), new CoServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();

            InetAddress addr = InetAddress.getLocalHost();
            String localIp = addr.getHostAddress();
            RegistryProcesser p = new RedisRegistryProcesser(serviceName, localIp+":"+port);
            p.register();

            logger.info(serviceName + " service(" + localIp + ":" + port + ") started...");

            //f.channel().closeFuture().sync();
            registerSignal();

            registerShutdownHook(workerGroup, bossGroup, p);

        } catch (InterruptedException | UnknownHostException e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void registerSignal() {
        Signal sig = new Signal("USR2");
        Signal.handle(sig, new ShutdownHandler());
    }

    private void registerShutdownHook(EventLoopGroup workerGroup, EventLoopGroup bossGroup, RegistryProcesser processer) {
        Thread t = new Thread(new ShutdownHook(workerGroup, bossGroup, processer), "ShutdownHook-Thread");
        Runtime.getRuntime().addShutdownHook(t);
    }
}

