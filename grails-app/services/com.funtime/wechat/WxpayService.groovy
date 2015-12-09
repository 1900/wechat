package com.shineshow.wechat
import com.shineshow.code.*
import grails.converters.JSON
import com.shineshow.utils.*
import java.io.*
import java.util.Scanner
class WxpayService {
	
	//def wxpayobj=wxpayService(orderNo,projectId,orderTotalPrice,productName)

   def createOrderAndWXJsapi(def projectId,def orderNo,def orderTotalPrice,def productName,def spbill_create_ip) { 
    	def projectInstance=Project.get(projectId)
    	def total=orderTotalPrice
       	//生成jsapi支付package
		//设置package订单参数
		WxPayHelper wxPayHelper = new WxPayHelper();
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("bank_type", "WX");  //支付类型   
		packageParams.put("body", productName); //商品描述   
		packageParams.put("fee_type","1"); 	  //银行币种
		packageParams.put("input_charset", "UTF-8"); //字符集    
		packageParams.put("notify_url", projectInstance.payReturnUrl+projectId); //通知地址  
		packageParams.put("out_trade_no", orderNo); //商户订单号  
		packageParams.put("partner", projectInstance.partnerId); //设置商户号
		packageParams.put("total_fee",  total as String); //商品总金额,以分为单位
		packageParams.put("spbill_create_ip",  spbill_create_ip); //订单生成的机器IP，指用户浏览器端IP
		
		//获取package包
		String packageValue = wxPayHelper.genPackage(packageParams,projectInstance.partnerKey,"UTF-8");
		String noncestr = wxPayHelper.getNonceStr();
        String timestamp = wxPayHelper.getTimeStamp();
        
		//设置支付参数
		SortedMap<String, String> payParams = new TreeMap<String, String>();
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		payParams.put("appid", projectInstance.appid);
		payParams.put("noncestr", noncestr);
		payParams.put("package", packageValue);
		payParams.put("timestamp", timestamp);
		signParams.put("appid", projectInstance.appid);
		signParams.put("appkey", projectInstance.paySignKey);
		signParams.put("noncestr", noncestr);
		signParams.put("package", packageValue);
		signParams.put("timestamp", timestamp);
		//生成支付签名，要采用URLENCODER的原始值进行SHA1算法！timestamp
		String sign = wxPayHelper.createSHA1Sign(signParams);
		//增加非参与签名的额外参数
		payParams.put("paySign", sign);
		payParams.put("signType", "sha1");
		payParams.put("orderNo", orderNo);
		return payParams
    }
}
