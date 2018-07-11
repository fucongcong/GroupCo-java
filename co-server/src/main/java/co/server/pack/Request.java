package co.server.pack;

import java.io.Serializable;
import java.util.concurrent.atomic.LongAdder;

public class Request implements Serializable {

    private String cmd;

    private String data;

    private  static LongAdder longAdder = new LongAdder();

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public static Long getAtomicId() {
        longAdder.increment();
        return longAdder.longValue();
    }
}
