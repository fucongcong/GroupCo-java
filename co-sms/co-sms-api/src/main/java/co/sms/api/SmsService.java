package co.sms.api;

import co.server.annotation.Param;

public interface SmsService {
    public Boolean sendSms(@Param("mobile") String mobile);

    public int isActiveCode(@Param("code") int code, @Param("mobile") String mobile);
}
