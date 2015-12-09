package com.shineshow.wechat

class Articles {
	Integer articleCount
	String title
	String description
	Date dateCreated 
	Date lastUpdated
	Integer[] sequence
	Long projectId

	static hasMany = [articles: Article]
	
	static mapping={
		table 'wechat_articles'
		version false
		articles column: 'articles_articleId',lazy:false,joinTable:'wechat_articles_article'
	}

    static constraints = {
    	title(nullable:true)
    	articleCount(nullable:false)
    	description(nullable:true)
    	sequence(nullable:true)
    	projectId(nullable:true)
    }
}
