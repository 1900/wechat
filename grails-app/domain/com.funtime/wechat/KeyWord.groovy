package com.shineshow.wechat

import grails.converters.JSON
import java.text.SimpleDateFormat
import org.apache.commons.lang.math.NumberUtils
import org.springframework.dao.DataIntegrityViolationException
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class KeyWord {
	String question
    boolean exactMatch
    String category
    String replyType
    boolean display=true
    Date dateCreated
    boolean isSingle
    Long projectId
    boolean kf =false

    Text text
    Articles articles

    static mapping={
        table 'wechat_key_word'
        version false
        text column: 'textId'
        articles column: 'articlesId'
    }

    static constraints = {
        display(nullable:true)
        category(nullable:true)
    	question(nullable:false)
    	replyType(nullable:false)
        exactMatch(nullable:true)
        text(nullable:true)
        articles(nullable:true)
        isSingle(nullable:true)
        projectId(nullable:true)
        kf(nullable:true)

    }
}
