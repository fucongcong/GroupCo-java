package co.server.co;

import co.server.registry.RegistryProcesser;
import io.netty.channel.EventLoopGroup;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

class ShutdownHook implements Runnable
{
    private static final Logger logger = LogManager.getLogger(ShutdownHook.class);

    protected EventLoopGroup workerGroup;

    protected EventLoopGroup bossGroup;

    protected RegistryProcesser processer;

    public ShutdownHook(EventLoopGroup workerGroup, EventLoopGroup bossGroup, RegistryProcesser processer) {
        this.workerGroup = workerGroup;
        this.bossGroup = bossGroup;
        this.processer = processer;
    }

    @Override
    public void run() {
        processer.unRegister();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        logger.info("service shutdown...");
    }
}
