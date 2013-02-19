package hk.com.dycx.iptv.utils;

import hk.com.dycx.iptv.bean.VideoInfo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-1-31 下午5:04:54
 * @version 1.0
 */
public class VideoInfoProvider {
    public static ArrayList<VideoInfo> pullRead(String jsonObject){
    	ArrayList<VideoInfo> videoInfos = new ArrayList<VideoInfo>(); 
    	try {
			JSONObject jsonObject_1 = new JSONObject(jsonObject);
			String video = jsonObject_1.get("video").toString();
			if(video !=null){
				 JSONArray  jsonarray_video_person = jsonObject_1.getJSONArray("video");
				 for(int i=0;i < jsonarray_video_person.length();i++){
					 JSONObject jsonObject_video = (JSONObject)jsonarray_video_person.opt(i);
					 JSONArray jsonObject_son = jsonObject_video.getJSONArray("videos");
					 VideoInfo videoInfo;
					 for(int j= 0;j <jsonObject_son.length();j++ ){ 
						 JSONObject json = (JSONObject)jsonObject_son.opt(j); 
						 videoInfo = new VideoInfo(); 
						 videoInfo.setUrl(json.getString("url"));
						 videoInfo.setName(json.getString("title"));
						 videoInfos.add(videoInfo);
					 }
				 }
			} 
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	 return videoInfos;
    }  
}
