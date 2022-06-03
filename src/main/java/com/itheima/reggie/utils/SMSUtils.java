package com.itheima.reggie.utils;

import com.alibaba.fastjson.JSONException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;

/**
 * 短信发送工具类
 */
public class SMSUtils {

	/**
	 * 发送短信
	 * @param signName 签名
	 * @param templateCode 模板
	 * @param phoneNumbers 手机号
	 * @param param 参数
	 */
	public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param){
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "", "");
		IAcsClient client = new DefaultAcsClient(profile);

		SendSmsRequest request = new SendSmsRequest();
		request.setSysRegionId("cn-hangzhou");
		request.setPhoneNumbers(phoneNumbers);
		request.setSignName(signName);
		request.setTemplateCode(templateCode);
		request.setTemplateParam("{\"code\":\""+param+"\"}");
		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println("短信发送成功");
		}catch (ClientException e) {
			e.printStackTrace();
		}
	}

	public static String sendSMS(String phoneNumber,String code) {
		String reStr = ""; //定义返回值
		// 短信应用SDK AppID   // 1400开头
		int appid = 1400683383;
		// 短信应用SDK AppKey
		String appkey = "7c95ee1ece267a072369cfe3eeab679d";
		// 短信模板ID，需要在短信应用中申请
		int templateId = 1411790 ;
		// 签名，使用的是`签名内容`，而不是`签名ID`
		String smsSign = "kinking福禄boy";
		try {
			//参数，一定要对应短信模板中的参数顺序和个数，
			String[] params = {code};
			//创建ssender对象
			SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
			//发送
			System.out.println("---------sendSMS phone number"+phoneNumber+"---------");
			SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumber,templateId, params, smsSign, "", "");
			// 签名参数未提供或者为空时，会使用默认签名发送短信
			System.out.println(result.toString());
			if(result.result==0){
				reStr = "success";
			}else{
				reStr = "error";
			}
		} catch (HTTPException e) {
			// HTTP响应码错误
			e.printStackTrace();
		} catch (JSONException e) {
			// json解析错误
			e.printStackTrace();
		} catch (IOException e) {
			// 网络IO错误
			e.printStackTrace();
		}catch (Exception e) {
			// 网络IO错误
			e.printStackTrace();
		}
		return reStr;
	}

}
