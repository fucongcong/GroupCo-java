package co.server.transport;

public interface ResponseCallback {
    public void success(Object res);

    public void fail(Throwable e);
}
