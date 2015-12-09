package com.shineshow.wechat
import com.shineshow.code.*
import grails.converters.JSON
class EditorModeService {

  /**
	 getWXUser by WXPort
	 **/
		def gainWXUser(def code,def projectId){
			//println "gainWXUser:_-------------------------:${code}"
		    def openid=gainOpenId(code,projectId as Long)    //getOpenID
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
            def accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+basicInfo.appid+"&secret="+basicInfo.appsecret
            def text = new URL(accessTokenUrl).getText(connectTimeout:5000, readTimeout:5000,'utf-8')

            def createdTime = Calendar.instance.getTimeInMillis()
            def baseInfo = JSON.parse(text)
            new AccessToken(accessToken:baseInfo.access_token,createdTime:createdTime).save(flush: true)          
            return baseInfo.access_token 
        }
    }

    def gainAccountInfo(String openId,def projectId){
    
        def ACCESS_TOKEN =  gainAccessToken(projectId)
        def OPENID = openId
        def infoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + ACCESS_TOKEN + "&openid=" + OPENID + "&lang=zh_CN"
        def text = new URL(infoUrl).getText(connectTimeout:5000, readTimeout:5000,'utf-8')
        
        def accountInfo = JSON.parse(text)
        if(!accountInfo.subscribe){
            return JSON.parse("{'nickName':"+false+",'openId':"+openId+"}")
        }
        def wechatInfoInstance=WechatInfo.findByWechatNo(openId)
        if(wechatInfoInstance){
            wechatInfoInstance.nickName=accountInfo.nickname
            wechatInfoInstance.headimgUrl=accountInfo.headimgurl
            wechatInfoInstance.sex
            wechatInfoInstance.city
            wechatInfoInstance.projectId
            wechatInfoInstance.save(flush: true)  
        }else{
            new WechatInfo(nickName:accountInfo.nickname,wechatNo:openId,headimgUrl:accountInfo.headimgurl,sex:accountInfo.sex,city:accountInfo.city,projectId:projectId).save(flush: true)   
        }
        def accountInstance=Account.findByOpenId(openId)
        if(accountInstance){
            accountInstance.openId=openId
            accountInstance.gender=accountInfo.sex
            accountInstance.nickName=accountInfo.nickname
            accountInstance.address=accountInfo.city
            accountInstance.accountIcon=accountInfo.headimgurl
            accountInstance.save(flush: true)  
        }else{
        def projectInstance=Project.get(projectId)
        accountInstance=new Account(openId:openId,gender:accountInfo.sex,nickName:accountInfo.nickname,address:accountInfo.city,accountIcon:accountInfo.headimgurl,project:projectInstance).save(flush: true)  
        }
        return accountInstance
    }  
}
