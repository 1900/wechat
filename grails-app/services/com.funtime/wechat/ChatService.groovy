package com.shineshow.wechat

class ChatService {

 def saveTextInmessageLogs (def node, def postStr,Long projectId) {
  StringBuilder sb = new StringBuilder()
  sb.append("Save InMessage:\n")
  InMessage inmessage = new InMessage(
   msgId:node.MsgId.text(),
   msgType:node.MsgType.text(),
   toUserName:node.ToUserName.text(),
   fromUserName:node.FromUserName.text(),
   openId:node.FromUserName.text(),
   createTime:node.CreateTime.text(),
   message:postStr,
   content:node.Content.text(),
   projectId:projectId)
  if (!inmessage.save(failOnError:true)) {
    inmessage.errors.each {
      sb.append(it).append("\n")
    }
    log.warn(sb.toString())
    return null
    } else {
      return inmessage
    }
  }
  def saveTextOutmessageLogs (def outmessage, def postStr) {
    outmessage.message=postStr
    // def outmessage=new OutMessage("msgType":answerType,"fromUserName":toUserName,"toUserName":fromUserName,"openId":toUserName,"createTime":System.currentTimeMillis(),"content":answerContent,"message":"returnXml")
    if (!outmessage.save(failOnError:true)){
       outmessage.errors.each {
       println it
        sb.append(it).append("\n")
    }
    log.warn(sb.toString())
    return null
    } else {
      return outmessage
    }
  }
  def saveNewsOutmessageLogs(def outmessage, def postStr) {
    outmessage.message=postStr
    def node = new XmlParser().parseText(postStr)
    outmessage.articlesContent=node.Articles
    if (!outmessage.save(failOnError:true)){
       outmessage.errors.each {
       println it
        sb.append(it).append("\n")
    }
    log.warn(sb.toString())
    return null
    } else {
      return outmessage
    }
  }
}
