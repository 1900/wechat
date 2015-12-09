package com.shineshow.wechat

class Group {

    Long groupId
	String groupName
	Integer groupCount
    Date dateCreated
    Long projectId

    static mapping={
        table 'wechat_group'
        version false
    } 

    static constraints = {
       projectId(nullable:true)
    }
}
