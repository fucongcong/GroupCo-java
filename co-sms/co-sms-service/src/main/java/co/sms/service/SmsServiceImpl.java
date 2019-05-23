package co.sms.service;

import co.server.annotation.Param;
import co.server.common.util.RedisKeyUtil;
import co.sms.common.SmsEntity;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("smsService")
public class SmsServiceImpl {

    @Value("${env}")
    private String env;

    @Autowired
    @Qualifier("rClient")
    private RedissonClient redissonClient;

    @Autowired
    private RedisKeyUtil redisKeyUtil;

    @Autowired
    private SmsEntity smsEntity;

    public Boolean sendSms(@Param("mobile") String mobile)
    {
        int code = 1234;
        boolean res = true;
        if (env == "prod") {
            code = (int) Math.random() * 10000;
            res = smsEntity.sendSms(mobile, String.valueOf(code));
        }

        if (res) {
            long time = System.currentTimeMillis()/1000;
            Map info = new HashMap();
            info.put("mobile", mobile);
            info.put("nexttime",  time + 25);
            info.put("expiretime",  time + 600);
            info.put("code",  code);
            info.put("count",  0);

            RBucket<Map> set = redissonClient.getBucket(redisKeyUtil.getKey("mobile_code_"+mobile));
            set.set(info);
            set.expire(600, TimeUnit.SECONDS);

            return true;
        }

        return false;
    }

    public int isActiveCode(@Param("code") int code, @Param("mobile") String mobile)
    {
        RBucket<Map> set = redissonClient.getBucket(redisKeyUtil.getKey("mobile_code_"+mobile));
        Map info = set.get();
        if (info != null) {
            long time = System.currentTimeMillis()/1000;
            if ((int) info.get("code") == code
                    && info.get("mobile").equals(mobile)
                    && time < (long) info.get("expiretime")) {
                set.delete();
                return 1;
            }

            int count = (int) info.get("count");
            info.put("count", count++);
            if (count > 5) {
                set.delete();
                return 1010;
            }

            set.set(info);
            set.expire(600, TimeUnit.SECONDS);
        }

        //验证码错误
        return 1009;
    }
}
