package com.shineshow.wechat

import grails.converters.JSON
import com.shineshow.code.*
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpException
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.RequestEntity
import org.apache.commons.httpclient.methods.StringRequestEntity
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.util.HashMap
import javax.activation.MimetypesFileTypeMap

 

class WeChatService {

    def gainAccessToken(Long projectId){
        
        def now = Calendar.instance.getTimeInMillis()

        def accessInstance = AccessToken.createCriteria().list{
             between('createdTime', now-7100*1000, now)
             eq("projectId",projectId)
        }[0]

        if(accessInstance){
            return accessInstance.accessToken
        }else{
            println "-------------------------------"+projectId
            def basicInfo = Project.get(projectId)
            def accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+basicInfo.appid+"&secret="+basicInfo.appsecret
            def text = new URL(accessTokenUrl).getText(connectTimeout:5000, readTimeout:5000,'utf-8')

            def createdTime = Calendar.instance.getTimeInMillis()
            def baseInfo = JSON.parse(text)
            new AccessToken(accessToken:baseInfo.access_token,createdTime:createdTime,projectId:projectId).save(flush: true)          
            return baseInfo.access_token 
        }
    }


    def formUpload(String urlStr, Map<String, String> textMap,  Map<String, String> fileMap) {  
            String res = "";  
            HttpURLConnection conn = null;  
            String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符  
            try {  
                URL url = new URL(urlStr);  
                conn = (HttpURLConnection) url.openConnection();  
                conn.setConnectTimeout(5000);  
                conn.setReadTimeout(30000);  
                conn.setDoOutput(true);  
                conn.setDoInput(true);  
                conn.setUseCaches(false);  
                conn.setRequestMethod("POST");  
                conn.setRequestProperty("Connection", "Keep-Alive");  
                conn.setRequestProperty("User-Agent",  
                                "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");  
                conn.setRequestProperty("Content-Type",  
                        "multipart/form-data; boundary=" + BOUNDARY);  
      
                OutputStream out = new DataOutputStream(conn.getOutputStream());  
                // text  
                if (textMap != null) {  
                    StringBuffer strBuf = new StringBuffer();  
                    Iterator iter = textMap.entrySet().iterator();  
                    while (iter.hasNext()) {  
                        Map.Entry entry = (Map.Entry) iter.next();  
                        String inputName = (String) entry.getKey();  
                        String inputValue = (String) entry.getValue();  
                        if (inputValue == null) {  
                            continue;  
                        }  
                        strBuf.append("\r\n").append("--").append(BOUNDARY).append(  
                                "\r\n");  
                        strBuf.append("Content-Disposition: form-data; name=\""  
                                + inputName + "\"\r\n\r\n");  
                        strBuf.append(inputValue);  
                    }  
                    out.write(strBuf.toString().getBytes());  
                }  
      
                // file  
                if (fileMap != null) {  
                    Iterator iter = fileMap.entrySet().iterator();  
                    while (iter.hasNext()) {  
                        Map.Entry entry = (Map.Entry) iter.next();  
                        String inputName = (String) entry.getKey();  
                        String inputValue = (String) entry.getValue();  
                        if (inputValue == null) {  
                            continue;  
                        }  
                        File file = new File(inputValue);  
                        String filename = file.getName();  
                        String contentType = new MimetypesFileTypeMap().getContentType(file);  
                        if (filename.endsWith(".png")) {  
                            contentType = "image/png";  
                        }  
                        if (contentType == null || contentType.equals("")) {  
                            contentType = "application/octet-stream";  
                        }  
      
                        StringBuffer strBuf = new StringBuffer();  
                        strBuf.append("\r\n").append("--").append(BOUNDARY).append(  
                                "\r\n");  
                        strBuf.append("Content-Disposition: form-data; name=\""  
                                + inputName + "\"; filename=\"" + filename  
                                + "\"\r\n");  
                        strBuf.append("Content-Type:" + contentType + "\r\n\r\n");  
      
                        out.write(strBuf.toString().getBytes());  
      
                        DataInputStream inss = new DataInputStream(new FileInputStream(file));  
                        int bytes = 0;  
                        byte[] bufferOut = new byte[1024];  
                        while ((bytes = inss.read(bufferOut)) != -1) {  
                            out.write(bufferOut, 0, bytes);  
                        }  
                        inss.close();  
                    }  
                }  
      
                byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  
                out.write(endData);  
                out.flush();  
                out.close();  
      
                // 读取返回数据  
                StringBuffer strBuf = new StringBuffer();  
                BufferedReader reader = new BufferedReader(new InputStreamReader(  
                        conn.getInputStream()));  
                String line = null;  
                while ((line = reader.readLine()) != null) {  
                    strBuf.append(line).append("\n");  
                }  
                res = strBuf.toString();  
                reader.close();  
                reader = null;  
            } catch (Exception e) {  
                System.out.println("发送POST请求出错。" + urlStr);  
                e.printStackTrace();  
            } finally {  
                if (conn != null) {  
                    conn.disconnect();  
                    conn = null;  
                }  
            }  
            return res;  
        }  
 

    def postWeUrl(String dataStr,String sendUrl){

        RequestEntity entity = null

        try {
            entity = new StringRequestEntity(dataStr,"text/json","utf-8")
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace()
        } 
        
        HttpClient client = new HttpClient()
        PostMethod post = new PostMethod(sendUrl)

        post.setRequestEntity(entity)
        post.getParams().setContentCharset("utf-8")
        
        String respStr = ""
        try {
            client.executeMethod(post)
            respStr = post.getResponseBodyAsString()
        } catch (HttpException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        return respStr
    }

    def gainAccountInfo(String openId,def projectId){
    
        def ACCESS_TOKEN =  gainAccessToken(projectId)
        def OPENID = openId
        def infoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + ACCESS_TOKEN + "&openid=" + OPENID + "&lang=zh_CN"
        def text = new URL(infoUrl).getText(connectTimeout:5000, readTimeout:5000,'utf-8')
        def accountInfo = JSON.parse(text)
        return accountInfo
    }   
}
