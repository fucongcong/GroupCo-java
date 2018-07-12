package co.server.transport;

import co.server.pack.Request;
import co.server.pack.Response;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentHashMap;

public class CoFuture implements ResponseFuture {

    private ResponseCallback responseCallback;

    private volatile Response response;

    private Integer timeout = 5;

    private Boolean monitor = false;

    private ReentrantLock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private static final Map<Long, CoChannel> channelMap = new ConcurrentHashMap();
    private static final Map<Long, CoFuture> coFutureMap = new ConcurrentHashMap();

    public CoFuture(CoChannel channel, Request request) {
        channelMap.put(request.getId(), channel);
        coFutureMap.put(request.getId(), this);
    }

    @Override
    public Object get() throws Exception {
        return get(this.timeout);
    }

    @Override
    public Object get(int timeout) throws Exception {
        return get(this.timeout, this.monitor);
    }

    @Override
    public Object get(int timeout, boolean monitor) throws Exception {
        if (isDone()) {
            if (this.response.getCode() == 200) {
                return this.response.getData();
            } else if (this.response.getCode() == 500) {
                throw new RuntimeException("client response error");
            } else {
                throw new TimeoutException("client response timeout");
            }
        }

        long start = System.currentTimeMillis();
        lock.lock();
        try {
            while (!this.isDone()) {
                //做个超时的判断
                condition.await((long)timeout, TimeUnit.SECONDS);
                if (this.isDone() || System.currentTimeMillis() - start > ((long)timeout * 1000)) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        if (!this.isDone()) {
            throw new TimeoutException("client response timeout");
        }

        return this.response.getData();
    }

    @Override
    public void setCallback(ResponseCallback resCallback) {
        if (isDone()) {
            doCallback();
        }

        this.responseCallback = resCallback;
    }

    private void doCallback() {
        if (this.response.getCode() == 200) {
            this.responseCallback.success(this.response.getData());
        } else if (this.response.getCode() == 500) {
            this.responseCallback.fail(new RuntimeException("client response error"));
        } else {
            this.responseCallback.fail(new TimeoutException("client response timeout"));
        }
    }

    @Override
    public boolean isDone() {
        return this.response != null;
    }

    public CoFuture getFuture(Long id) {
        return coFutureMap.get(id);
    }

    public static void receive(Response response) {
        CoFuture coFuture = coFutureMap.remove(response.getId());
        coFuture.doReceive(response);
    }

    public void doReceive(Response response) {
        this.lock.lock();
        try {
            this.response = response;
            if (this.condition != null) {
                this.condition.signal();
            }
        } finally {
            this.lock.unlock();
            channelMap.remove(response.getId());
        }

        if (this.responseCallback != null) {
            doCallback();
        }
    }
}
