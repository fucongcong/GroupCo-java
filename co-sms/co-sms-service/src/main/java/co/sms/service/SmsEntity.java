package co.sms.service;

import java.io.Serializable;

public class SmsEntity implements Serializable {
    public Integer code;

    public SmsEntity(Integer code) {
        this.code = code;
    }
}
