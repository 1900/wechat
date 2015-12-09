package com.shineshow.wechat

class Text {

	String content
	Date dateCreated
	Long projectId
	
	static mapping={
		table 'wechat_text'
		version false
		content type: 'text'
	}
	
    static constraints = {
    	content(nullable:false)
    	projectId(nullable:true)
    }
}
