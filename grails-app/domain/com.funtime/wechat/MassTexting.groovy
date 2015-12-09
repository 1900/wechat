package com.shineshow.wechat

class MassTexting {
	String content
	String groupName
	Date createTime
	String sendTime
	String groupId
	String status = "1"
	String attr
	Long projectId

    static mapping={
        table 'wechat_mass_texting'
		version false
    } 

    static constraints = {
     	attr(nullable:true)
     	projectId(nullable:true)
    }
}
