package com.cyh.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

import java.rmi.ServerException;

public class SmsUtil
{

    private static final String ACCESSKEY = "";
    private static final String SECRET = "";
    public static void sendRegistCode(String mobile,String code){
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "ABC商城");
        request.putQueryParameter("TemplateCode", "SMS_205397731");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
        try {
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESSKEY, SECRET);
            IAcsClient client = new DefaultAcsClient(profile);
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        }
        catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
