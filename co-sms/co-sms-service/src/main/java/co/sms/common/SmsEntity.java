package co.sms.common;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import java.io.Serializable;
import java.util.Map;

public class SmsEntity implements Serializable {

    private String appKey;

    private String appSecret;

    private String signName;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Map getTemplateids() {
        return templateids;
    }

    public void setTemplateids(Map templateids) {
        this.templateids = templateids;
    }

    private Map<String, String> templateids;

    public Boolean sendSms(String mobile, String code)
    {
        CommonResponse response = this.aliyunSms(templateids.get("code"), mobile, JSON.toJSONString(new CodeTpl(code)));
        SmsAliyunResponse smsAliyunResponse = JSON.parseObject(response.getData(), SmsAliyunResponse.class);
        if (smsAliyunResponse.getCode() == "OK") {
            return true;
        }
        return false;
    }

    public Boolean sendTemplate(String type, String mobile, Object param) {
        CommonResponse response = this.aliyunSms(templateids.get(type), mobile, JSON.toJSONString(param));
        SmsAliyunResponse smsAliyunResponse = JSON.parseObject(response.getData(), SmsAliyunResponse.class);
        if (smsAliyunResponse.getCode() == "OK") {
            return true;
        }
        return false;
    }

    private CommonResponse aliyunSms(String templateCode, String mobile, String templateParam) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", appKey, appSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", templateParam);

        try {
            return client.getCommonResponse(request);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getSignName() {
        return signName;
    }
}

class CodeTpl {
    public String code;

    public CodeTpl(String code) {
        this.code = code;
    }
}
