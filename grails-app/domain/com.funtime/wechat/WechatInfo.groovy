package com.shineshow.wechat

class WechatInfo {
	
	String nickName
	String wechatNo
	Date  dateCreated
	String headimgUrl
	String sex
	String city
	String status = "1"
	Long projectId

    static mapping={
       table 'wechat_wechat_info'
		version false
	}

    static constraints = {
    	nickName(nullable:true)
    	wechatNo(unique:true)
    	headimgUrl(nullable:true)
	    sex(nullable:true)
	    city(nullable:true)
	    projectId(nullable:true)
    }
}
