package co.server.transport;

import co.server.transport.ResponseCallback;

public interface ResponseFuture {
    Object get() throws Exception;

    Object get(int timeout) throws Exception;

    Object get(int timeout, boolean monitor) throws Exception;

    void setCallback(ResponseCallback resCallback);

    boolean isDone();
}
