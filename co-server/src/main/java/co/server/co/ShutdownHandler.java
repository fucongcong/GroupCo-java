package co.server.co;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class ShutdownHandler implements SignalHandler {
    /**
     * 处理信号
     *
     * @param signal 信号
     */
    public void handle(Signal signal) {
        Runtime.getRuntime().exit(0);
    }
}
