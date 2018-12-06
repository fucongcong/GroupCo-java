package co.sms.api;

import co.server.annotation.Param;
import co.sms.service.SmsEntity;

public interface SmsService {
    public SmsEntity sendSms(@Param("mobile") String mobile);
}
