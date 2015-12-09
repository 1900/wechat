package com.shineshow.wechat

class SubMenu {

	String name
	String type
	String eventKey
	String url
	String mark
	String status = "1"
	Long projectId

	static belongsTo = [mainMenu:MainMenu]

	static mapping={
        table 'wechat_sub_menu'
		version false
        mainMenu column: 'mainMenuId'
    }

    static constraints = {
    	eventKey(nullable:true)
    	url(nullable:true)
    	projectId(nullable:true)
    }
}
