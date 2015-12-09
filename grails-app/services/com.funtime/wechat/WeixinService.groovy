package com.shineshow.wechat

import com.shineshow.code.*
import java.io.Writer
import java.math.BigDecimal
import groovy.text.SimpleTemplateEngine
import sswechat.WeixinUtil
import com.shineshow.utils.*
import java.net.URLEncoder
import grails.converters.JSON

class WeixinService {

    def genPassword(String token, String timestamp, String nonce) {
            String[] ArrTmp =  [token, timestamp, nonce ];
            Arrays.sort(ArrTmp);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < ArrTmp.length; i++) {
                sb.append(ArrTmp[i]);
            }
            return WeixinUtil.Encrypt(sb.toString());
    }

    def arrayDeleteNull(def array) {
       	StringBuffer sb = new StringBuffer();
        for(int i=0; i<array.length; i++) {
            if("".equals(array[i])) {
                continue;
            }
            sb.append(array[i]);
            if(i != array.length - 1) {
                sb.append(";");
            }
        }
        return sb.toString().split(";");
    }    


    /**
     getWXUser by WXPort
     **/
        def gainWXUser(def code,def projectId){
            //println "gainWXUser:_-------------------------:${code}"
            def openid=gainOpenId(code,projectId as Long)    //getOpenID
            println "openid===================================="+openid
            return gainAccountInfo(openid,projectId as Long)
        }

        def gainWXUserForShare(def code,def projectId){
            def openid=gainOpenId(code,projectId as Long)
            if(!openid){
                return false
            }else{
                return gainAccountInfo(openid,projectId as Long)
            }      //getOpenID

        }

        def gainOpenId(def code,def projectId){

            def basicInfo = Project.get(projectId)
            def appid = basicInfo.appid
            def appsecret = basicInfo.appsecret
            println appid
            println appsecret

            def rel_code="code="+code
            def addr = "https://api.weixin.qq.com/sns/oauth2/access_token?"
            def qs = []
            qs << "appid="+appid
            qs << "secret="+appsecret
            qs << rel_code
            qs << "grant_type=authorization_code"
            def text = new URL(addr + qs.join("&")).getText([connectTimeout:5000, readTimeout:5000])           
            def userBase=JSON.parse(text)
            return userBase.openid
        }

    def gainAccessToken(Long projectId){
        
        def now = Calendar.instance.getTimeInMillis()

        def accessInstance = AccessToken.createCriteria().list{
             between('createdTime', now-7100*1000, now)
             eq("projectId",projectId)
        }[0]

        if(accessInstance){
            return accessInstance.accessToken
        }else{

            def basicInfo = Project.get(projectId)
            println basicInfo.appid
            def accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+basicInfo.appid+"&secret="+basicInfo.appsecret
            def text = new URL(accessTokenUrl).getText(connectTimeout:5000, readTimeout:5000,'utf-8')

            def createdTime = Calendar.instance.getTimeInMillis()
            def baseInfo = JSON.parse(text)
            new AccessToken(accessToken:baseInfo.access_token,createdTime:createdTime,projectId:projectId).save(flush: true)          
            return baseInfo.access_token 
        }
    }

    def gainJsApiTicket(Long projectId){
        println "::::::::::::::::::::::::::::::::::::::::::::::"+projectId
        def accessToken=gainAccessToken(projectId)
        
        def now = Calendar.instance.getTimeInMillis()

        def jsApiTicketInstance = JsApiTicket.createCriteria().list{
             between('createdTime', now-7100*1000, now)
             eq("projectId",projectId)
        }[0]

        if(jsApiTicketInstance){
            return jsApiTicketInstance.jsApiTicket
        }else{
            def jsApiTicketObj
            def jsApiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+accessToken+"&type=jsapi"
            def text = new URL(jsApiTicketUrl).getText(connectTimeout:5000, readTimeout:5000,'utf-8')
            def createdTime = Calendar.instance.getTimeInMillis()
            println text
            jsApiTicketObj = JSON.parse(text)
            new JsApiTicket(jsApiTicket:jsApiTicketObj.ticket,createdTime:createdTime,projectId:projectId).save(flush: true)          
            return jsApiTicketObj.ticket 
        }
    }

//def wxshareObj=WeixinService.createShareInfo(projectId,url)
//render wxshareObj as JSON
 def createShareInfo(Long projectId,String url){
    WxPayHelper wxPayHelper = new WxPayHelper();
    def projectInstance=Project.get(projectId)
    SortedMap<String, String> shareParams = new TreeMap<String, String>();
    def jsApiTicket=gainJsApiTicket(projectId)
    String noncestr = wxPayHelper.getNonceStr();
    String timestamp = wxPayHelper.getTimeStamp();
    shareParams.put("jsapi_ticket", jsApiTicket);
    shareParams.put("noncestr", noncestr);
    shareParams.put("timestamp", timestamp);
    shareParams.put("url", url);
    String sign = wxPayHelper.createSHA1Sign(shareParams);
    SortedMap<String, String> returnSignParams = new TreeMap<String, String>();
    returnSignParams.put("appId",projectInstance.appid)
    returnSignParams.put("noncestr", noncestr);
    returnSignParams.put("timestamp", timestamp);
    returnSignParams.put("signature", sign);
    returnSignParams.put("jsApiTicket", jsApiTicket);
    return returnSignParams
 }


def gainAccountInfo(String openId,def projectId){
    
        def ACCESS_TOKEN =  gainAccessToken(projectId)
        def OPENID = openId
        def infoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + ACCESS_TOKEN + "&openid=" + OPENID + "&lang=zh_CN"
        def text = new URL(infoUrl).getText(connectTimeout:5000, readTimeout:5000,'utf-8')
        println text+"-----------------------------text"
         if(text.contains("40001")){
              def accessTokenInstance=AccessToken.findByProjectId(projectId,[max:1,sort:"id",order:"desc"])
                accessTokenInstance.createdTime=null
                if(accessTokenInstance.save(failOnError:true)){
                   return false
                }  
            }
        def accountInfo = JSON.parse(text)
        if(!accountInfo.subscribe){
            return JSON.parse("{'nickName':"+false+",'openId':"+openId+"}")
        }
        accountInfo.nickname=chinaFilter(accountInfo.nickname)                                             
        def accountInstance=Account.findByOpenId(openId)
        def wechatInfoInstance=WechatInfo.findByWechatNo(openId)
        if(wechatInfoInstance){
            wechatInfoInstance.nickName=accountInfo.nickname
            wechatInfoInstance.headimgUrl=accountInfo.headimgurl
            wechatInfoInstance.sex
            wechatInfoInstance.city
            wechatInfoInstance.projectId
            wechatInfoInstance.save(failOnError: true)  
        }else{
            new WechatInfo(nickName:accountInfo.nickname,wechatNo:openId,headimgUrl:accountInfo.headimgurl,sex:accountInfo.sex,city:accountInfo.city,projectId:projectId).save(flush: true)   
        }
        if(accountInstance){
            accountInstance.openId=openId
            accountInstance.gender=accountInfo.sex
            accountInstance.nickName=accountInfo.nickname
            accountInstance.address=accountInfo.city
            accountInstance.accountIcon=accountInfo.headimgurl
            accountInstance.save(failOnError: true)  
        }else{
        def projectInstance=Project.get(projectId)
        accountInstance=new Account(openId:openId,gender:accountInfo.sex,nickName:accountInfo.nickname,address:accountInfo.city,accountIcon:accountInfo.headimgurl,project:projectInstance).save(failOnError: true)  
        }
        return accountInstance
    } 


    //https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1e12d1c0cf5f41b3&redirect_uri=http%3a%2f%2f140.207.17.98%2fTradeRecordShop%2fgetAccountId&response_type=code&scope=snsapi_base&state=8#wechat_redirect
    def createWxUrl(def projectId,def redirect_uri){
       def projectInstance=Project.get(projectId as Long)
       StringBuffer sb = new StringBuffer()
        sb.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=")
        sb.append(projectInstance.appid)
        sb.append("&redirect_uri=")
        sb.append(URLEncoder.encode(redirect_uri, "utf-8"))
        sb.append("&response_type=code&scope=snsapi_base&state=")
        sb.append(projectId)
        sb.append("#wechat_redirect")
        return sb
    }

    def chinaFilter(String str){  
        String result="";  
        for (int i = 0; i < str.length(); i++){  
            int chr1 = (char) str.charAt(i);  
            if(chr1>=19968&&chr1<=40868){//汉字范围 \u4e00-\u9fa5 (中文)  
                result+=str.charAt(i);  
            }else if(chr1>=40869&&chr1<=85503){
                continue;
            }else if(chr1>=85504&&chr1<=171941){
                result+=str.charAt(i);  
            }
            else{  
                result+=str.charAt(i);  
            }  
        }

        if(!result){
            result="表情会员"
        }  
        return result;  
    }  
}
