package com.shineshow.wechat

class ProjectUrl {
	
	String triggerItem
	String url
	Long projectId
	String type
	Date  dateCreated
	String status = "1"

    static mapping={
       table 'wechat_project_url'
		version false
	}

    static constraints = {
	    triggerItem(nullable:true)
		url(nullable:true)
		projectId(nullable:true)
    }
}
