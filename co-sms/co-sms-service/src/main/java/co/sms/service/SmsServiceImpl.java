package co.sms.service;

import co.server.annotation.Param;
import org.springframework.stereotype.Service;

@Service("smsService")
public class SmsServiceImpl {
    public SmsEntity sendSms(@Param("mobile") String mobile)
    {
        return new SmsEntity(200);
    }
}
