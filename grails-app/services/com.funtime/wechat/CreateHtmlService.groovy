package com.shineshow.wechat
import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.commons.ConfigurationHolder;


class CreateHtmlService {

	def NewstoHtml(def htmltitle,def title,def newimagesPath,def isShowPic,def originUrl,def author,def content,def htmldir,def newPath) {
		println "-------------------NewstoHtml-----------------------"
		StringBuilder itemsSb = new StringBuilder();
		def engine = new groovy.text.SimpleTemplateEngine();
		def htmlTemp = engine.createTemplate(htmltemplate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		def editorTime=sdf.format(System.currentTimeMillis());
		//String htmltitle = ConfigurationHolder.config.weixin.html.title;
		def amap=[];
		amap = ["content":content]
		amap <<["title":title]
		amap <<["editorTime":editorTime]
		amap <<["htmltitle":htmltitle]
		if(originUrl){
			int index=originUrl.indexOf("http://");
			if(!(index==-1)){
				originUrl=originUrl.substring(index+7)
			}
			amap<<["originUrl":"<a class='left' href='http://${originUrl}'>阅读原文</a>"]
		}else{
			amap<<["originUrl":""]
		}
		if(author){
			amap<<["author":"作者：${author}<br>"]
		}else{
			amap<<["author":""]
		}
		if(isShowPic){
			amap<<["image":"<img width='100%' src='"+newimagesPath+"'/>"]
		}else{
			amap<<["image":""]
		}
		
		def contentTemp=htmlTemp.make(amap).toString();

			def dir=new File(htmldir)
			if(!dir.exists()){
				dir.mkdirs()
			}
			def htmltemp = new File(newPath)
			htmltemp.createNewFile()
		OutputStream out=new FileOutputStream(htmltemp);
		PrintWriter pw=new PrintWriter(new BufferedWriter(new OutputStreamWriter(out,"utf-8")));
		pw.print(contentTemp);
		pw.close();
	}


	def htmltemplate="""
	<!doctype html>
<html>
<head>
<meta http-equiv=Content-Type content="text/html;charset=utf-8">
<meta name="viewport" content="initial-scale=1, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0, width=device-width"/>
<link rel="stylesheet" type="text/css" href="../../style/style.css"  >
<title>\${htmltitle}</title>
</head>
<body>
	<header>
	</header>
	<div class="wrapper">
		<h1 id="h1title">\${title}</h1>
		<h5><span>\${editorTime}</span>
		\${author}
		</h5>
		<div>\${image}</div>
		\${content}
		<div class="footer clearfix">
		\${originUrl}
		</div>
	</div>

</body>
</html>
	"""
}
