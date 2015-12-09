package com.shineshow.wechat

class WeixinTemplateService {

    def customerServiceXml(def message){
        println "-------------------customerServiceXml-----------------------"
        StringBuilder itemsSb = new StringBuilder();
        def engine = new groovy.text.SimpleTemplateEngine();
        def xmlTemp = engine.createTemplate(customerTemplet);
        def amap=[];
        amap = ["toUserName":message.toUserName,
                    "fromUserName":message.fromUserName,
                    "createTime":message.createTime]
        return xmlTemp.make(amap).toString();

    }

    def textMessagetoXml(def message) {
        println "-------------------textMessagetoXml-----------------------"
        StringBuilder itemsSb = new StringBuilder();
        def engine = new groovy.text.SimpleTemplateEngine();
        def xmlTemp = engine.createTemplate(textTemplet);
        def amap=[];
        amap = ["toUserName":message.toUserName,
                    "fromUserName":message.fromUserName,
                    "createTime":message.createTime,
                    "content":message.content]
        return xmlTemp.make(amap).toString();
    }

    def newsMessagetoXml(def message,def articles,def sequence){
        println "-------------------newsMessagetoXml-----------------------"
        StringBuilder itemsSb = new StringBuilder();
        if(!sequence){
            for (Article item : articles.articles) {
            def engine = new groovy.text.SimpleTemplateEngine();
            def map = ["title":item.title, 
                        "description":item.description, 
                        "picUrl":item.picUrl, 
                        "url":item.url]
            def itemTemp = engine.createTemplate(itemTemplate);
            String result = itemTemp.make(map).toString()
            itemsSb.append(result).append("\n")
        }
        }else{
            for(int i=0;i<sequence.size();i++){
            Article item=Article.get(sequence[i])
            def engine = new groovy.text.SimpleTemplateEngine();
            def map = ["title":item.title, 
                        "description":item.description, 
                        "picUrl":item.picUrl, 
                        "url":item.url]
            def itemTemp = engine.createTemplate(itemTemplate);
            String result = itemTemp.make(map).toString()
            itemsSb.append(result).append("\n")
            }
        }
    
        def engine = new groovy.text.SimpleTemplateEngine();
        def xmlTemp = engine.createTemplate(newsTemplate);
        def amap = ["toUserName":message.toUserName,
                    "fromUserName":message.fromUserName,
                    "createTime":message.createTime,
                    "msgType":message.msgType,
                    "articleCount":message.articleCount,
                    "items":itemsSb.toString()]
        return xmlTemp.make(amap).toString();
    }

     def textTemplet = """<xml>
         <ToUserName><![CDATA[\${toUserName}]]></ToUserName>
         <FromUserName><![CDATA[\${fromUserName}]]></FromUserName>
         <CreateTime>\${createTime}</CreateTime>
         <MsgType><![CDATA[text]]></MsgType>
         <Content><![CDATA[\${content}]]></Content>
         </xml>"""


    def newsTemplate = """<xml>
         <ToUserName><![CDATA[\${toUserName}]]></ToUserName>
         <FromUserName><![CDATA[\${fromUserName}]]></FromUserName>
         <CreateTime>\${createTime}</CreateTime>
         <MsgType><![CDATA[\${msgType}]]></MsgType>
         <ArticleCount>\${articleCount}</ArticleCount>
         <Articles>
            \${items}</Articles>
         <FuncFlag>1</FuncFlag>
         </xml>"""

    def itemTemplate = """<item>
         <Title><![CDATA[\${title}]]></Title> 
         <Description><![CDATA[\${description}]]></Description>
         <PicUrl><![CDATA[\${picUrl}]]></PicUrl>
         <Url><![CDATA[\${url}]]></Url>
         </item>"""

    def customerTemplet = """<xml>
         <ToUserName><![CDATA[\${toUserName}]]></ToUserName>
         <FromUserName><![CDATA[\${fromUserName}]]></FromUserName>
         <CreateTime>\${createTime}</CreateTime>
         <MsgType><![CDATA[transfer_customer_service]]></MsgType>
         </xml>"""
}
