package com.shineshow.wechat

import java.util.Date;

class OutMessage {
	String toUserName
	String fromUserName
	String msgType
	String openId
	String msgId 
	String createTime
	Date dateCreated 
	String message
	String content
	String articlesContent
	Integer articleCount
	String mediaId
	Long projectId

	static mapping={
        table 'wechat_out_message'
		version false
		message type: 'text'
		content type: 'text'
		articlesContent type: 'text'
	}
	
    static constraints = {
    	toUserName(nullable:false)
		fromUserName(nullable:false)
		msgType(nullable:false)
		openId(nullable:false)
		msgId(nullable:true)
		content(nullable:true)
		message(nullable:true)
		createTime(nullable:false)
		articlesContent(nullable:true)
		articleCount(nullable:true)
		mediaId(nullable:true)
		projectId(nullable:true)
    }
}
