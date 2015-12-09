package com.shineshow.wechat

class AccessToken {

	String accessToken
	Long createdTime
	Long projectId

	static mapping={
        table 'wechat_access_token'
		version false
        accessToken type: 'text'
    }

    static constraints = {
    	accessToken(nullable:true)
    	createdTime(nullable:true)
    	projectId(nullable:true)
    }
}
