package com.shineshow.wechat

class KnowledgeBaseService {

	def getDirName(){
		Calendar c = new GregorianCalendar();
		c.add(Calendar.MONTH, +1);
		def month=c.get(Calendar.MONTH)
		def year=c.get(Calendar.YEAR)
		def year_month;
		if(c.get(Calendar.MONTH)<10){
			year_month=year+"0"+month
			}else{
				year_month=year+""+month
			}
			return year_month
	}

	def createImage(def newimagesPath,def imagefile){
		def temp = new File(newimagesPath)
		if(!temp.exists()){
			temp.mkdirs()
		}
		imagefile.transferTo(temp)
	}

	def deleteFile(def path){
		def temp=new File(path)
		if(!temp.exists()){
			temp.delete()
		}
	}
}
