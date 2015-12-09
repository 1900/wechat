package com.shineshow.wechat

class Article {
    String author
    boolean isShowPic=false
    String originUrl
	String title
	String description
	String picUrl
	String url
    String articleContent
    Date dateCreated
    Date lastUpdated
    Long projectId

    static mapping={
        table 'wechat_article'
        version false
        description type: 'text'     
        articleContent type: 'text'
    }
    
    static constraints = {
    	title(nullable:false)
    	description(nullable:false)
    	picUrl(nullable:false)
    	url(nullable:false)
        originUrl(nullable:true)
        author(nullable:true)
        articleContent(nullable:false)
        projectId(nullable:true)
    }
}
