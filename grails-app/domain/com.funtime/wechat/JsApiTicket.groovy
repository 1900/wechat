package com.shineshow.wechat

class JsApiTicket {

   String jsApiTicket
	Long createdTime
	Long projectId

	static mapping={
        table 'wechat_JsApi_ticket'
        version false
        jsApiTicket type: 'text'
    }

    static constraints = {
    	jsApiTicket(nullable:true)
    	createdTime(nullable:true)
    	projectId(nullable:true)
    }
}
