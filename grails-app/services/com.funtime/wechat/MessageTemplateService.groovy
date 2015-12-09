package com.shineshow.wechat

import com.shineshow.wechat.WeChatService

class MessageTemplateService {

    WeChatService weChatService

    def messageTemplateUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="

    def instantStatement(Long projectId,String openId,String name,String cardNo,String time,String type,String money) {
         
        def ACCESS_TOKEN = weChatService.gainAccessToken(projectId)
        def instantStatementmUrl = messageTemplateUrl + ACCESS_TOKEN
		def instantStatementStr = "{" +
			"\"touser\":\"${openId}\"," + 
			"\"template_id\":\"R4nopJInGTS3veRrY7I6QaVzQjxIfxjRxzCFJA4emB0\"," +
			"\"url\":\"\"," +
			"\"topcolor\":\"#0000FF\"," +
			"\"data\":{" +
				"\"first\":{" +
					"\"value\":\"尊敬的${name}微信银行用户\"}," +
				"\"headinfo\":{" +
					"\"value\":\"您尾号${cardNo}的借记卡最新交易信息：\"}," +
				"\"Date\":{" +
					"\"value\":\"${time}\"}," +
				"\"Type\":{" +
					"\"value\":\"${type}\"}," +
				"\"Money\":{" +
					"\"value\":\"人民币${money}元\",\"color\":\"#0000FF\"}," +
				"\"remark\":{" +
					"\"value\":\"注：金额开头符号‘+’：表示您的账户存入或转入一笔金额；金额开头符号‘-’：表示您卡账户取出或转出一笔金额。\"}}}"

	    def respStr = weChatService.postWeUrl(instantStatementStr,instantStatementmUrl)

        return respStr
    }
}
