package co.sms.service;

import co.server.annotation.Param;
import co.server.common.util.RedisKeyUtil;
import co.sms.common.SmsEntity;
import com.alibaba.fastjson.JSON;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
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
            Map<String, String> info = new HashMap();
            info.put("mobile", mobile);
            info.put("nexttime",  String.valueOf(time + 25));
            info.put("expiretime",  String.valueOf(time + 600));
            info.put("code",  String.valueOf(code));
            info.put("count",  "0");

            RBucket<String> set = redissonClient.getBucket(redisKeyUtil.getKey("mobile_code_"+mobile));
            set.set(JSON.toJSONString(info));
            set.expire(600, TimeUnit.SECONDS);

            return true;
        }

        return false;
    }
}
