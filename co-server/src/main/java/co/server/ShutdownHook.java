package co.server;

import co.server.context.ApplicationContextUtil;
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
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();

        ((Services) ApplicationContextUtil.getBean("services")).close();

        processer.unRegister();
        processer.unSubscribe();

        logger.info("service shutdown...");
    }
}
