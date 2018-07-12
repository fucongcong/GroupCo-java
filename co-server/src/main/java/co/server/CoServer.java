package co.server;


import co.server.context.ApplicationContextUtil;
import co.server.serialization.MsgDecoder;
import co.server.serialization.MsgEncoder;
import co.server.registry.RedisRegistryProcesser;
import co.server.registry.RegistryProcesser;
import co.server.transport.CoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sun.misc.Signal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class CoServer {
    private static final Logger logger = LogManager.getLogger(CoServer.class);

    private String serviceName;

    private String registry;
    
    private Map<String, Map<String, String>> references;
    private Map services;

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
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MsgDecoder(), new MsgEncoder(), new CoServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            InetAddress addr = InetAddress.getLocalHost();
            String localIp = addr.getHostAddress();
            RegistryProcesser p = new RedisRegistryProcesser(serviceName, getRefServiceNames(), localIp + ":" + port);
            p.register();

            b.bind(port).sync();

            registerServiceList(p);

            registerSignal();

            registerShutdownHook(workerGroup, bossGroup, p);

            logger.info(serviceName + " service(" + localIp + ":" + port + ") started...");

        } catch (InterruptedException | UnknownHostException e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void registerServiceList(RegistryProcesser p) {
        ((Services) ApplicationContextUtil.getBean("services")).setReferences(references).init(p);
    }

    private void registerSignal() {
        Signal sig = new Signal("USR2");
        Signal.handle(sig, new ShutdownHandler());
    }

    private void registerShutdownHook(EventLoopGroup workerGroup, EventLoopGroup bossGroup, RegistryProcesser processer) {
        Thread t = new Thread(new ShutdownHook(workerGroup, bossGroup, processer), "ShutdownHook-Thread");
        Runtime.getRuntime().addShutdownHook(t);
    }

    private Set<String> getRefServiceNames() {
        if (this.references == null) return null;

        Set<String> ukeys = this.references.keySet();
        Set<String> refServiceNames = new HashSet<>();
        for (String key : ukeys) {
            Map<String, String> reference = this.references.get(key);
            refServiceNames.add(reference.get("serverName"));
        }

        return refServiceNames;
    }

    public void setReferences(Map references) {
        this.references = references;
    }

    public void setServices(Map services) {
        this.services = services;
    }

    public Map getServices() {
        return services;
    }
}

