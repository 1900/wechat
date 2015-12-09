package com.shineshow.wechat

import com.shineshow.wechat.*

class KeyWordService {

    def getAnswer(def keyword,Long projectId) {
    	def an=new ArrayList();
		def answers=KeyWord.withCriteria{
			eq("projectId", projectId)
		}
		if(!answers){
			return KeyWord.createCriteria().list{
			eq("projectId", projectId)
			eq("question", "no_keyword")
			order("id","desc")
			}
		}

		for(int i=answers.size()-1;i>0;i--){
			if(keyword.contains(answers[i].question)) {
                   an.add(answers[i])
              }
		}
		Collections.sort(an, new Comparator<KeyWord>() {
			public int compare(KeyWord key1, KeyWord key2) {
				return key2.question.length() - key1.question.length();
			}
		});

		def answer=an.collect{
			if(it.exactMatch==true){
				if(keyword.equals(it.question)){
					[
                     	keyword:it
					]
				}
			}else{
				if(keyword.contains(it.question)){
					[
						keyword:it
					]
				}
			}
		}

		if(!answer.keyword[0]){
			return KeyWord.createCriteria().list{
			eq("projectId", projectId)
			eq("question", "no_keyword")
			order("id","desc")
			}
		}
		return answer.keyword
    }
}
