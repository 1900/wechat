package com.shineshow.wechat

class InMessage {
	String toUserName
	String fromUserName
	String msgType
	String openId
	String msgId 
	String content
	String createTime
	Date dateCreated 
	String event 
	String message
	String mediaId
	Long projectId

	static mapping={
		table 'wechat_in_message'
		version false
	 	content type: 'text'
	 	message type: 'text'	
	}

	static constraints = {		
		content(nullable:false)
		toUserName(nullable:false)
		fromUserName(nullable:false)
		msgType(nullable:false)
		openId(nullable:false)
		msgId(nullable:true)
		createTime(nullable:false)
		event(nullable:true)
		message(nullable:true)
		mediaId(nullable:true)
		projectId(nullable:true)
	}
}
