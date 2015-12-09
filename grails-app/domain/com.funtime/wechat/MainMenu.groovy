package com.shineshow.wechat

class MainMenu {
    
    String name
    String type
    String eventKey
    String url
    String mark
    String status = "1"
    Long projectId

    static mapping={
        table 'wechat_main_menu'
        version false
    }

    static hasMany = [subMenus: SubMenu]

    static constraints = {
    	type(nullable:true)
    	eventKey(nullable:true)
    	url(nullable:true)
        projectId(nullable:true)
    }
}
