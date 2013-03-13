package hk.com.dycx.iptv.utils;

import hk.com.dycx.iptv.bean.VideoGroupInfo;
import hk.com.dycx.iptv.bean.VideoInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-1-31 下午5:04:54
 * @version 1.0
 */
public class VideoInfoProvider {
/*    public static ArrayList<VideoInfo> pullRead(String jsonObject){
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
    }*/
	public static boolean isPullException = false;
    public static List<VideoGroupInfo> gobalGroupInfos = new ArrayList<VideoGroupInfo>();
    public static List<VideoGroupInfo> pullRead(String jsonObject){
    	try {
			JSONObject jsonObject_1 = new JSONObject(jsonObject);
			String video = jsonObject_1.get("video").toString();
			if(video !=null){
				 JSONArray  jsonarray_video_person = jsonObject_1.getJSONArray("video");
				 for(int i=0;i < jsonarray_video_person.length();i++){
					 VideoGroupInfo groupInfo = new VideoGroupInfo();
					 JSONObject jsonObject_video = (JSONObject)jsonarray_video_person.opt(i);
					 String videoNamePerson = jsonObject_video.getString("videoNamePerson");
					 groupInfo.setGroupName(videoNamePerson);
					 JSONArray jsonObject_son = jsonObject_video.getJSONArray("videos");
					 ArrayList<VideoInfo> videoInfos = new ArrayList<VideoInfo>(); 
					 for(int j= 0;j <jsonObject_son.length();j++ ){ 
						 JSONObject json = (JSONObject)jsonObject_son.opt(j);
						 VideoInfo videoInfo = new VideoInfo(); 
						 videoInfo = new VideoInfo(); 
						 videoInfo.setUrl(json.getString("url"));
						 videoInfo.setName(json.getString("title"));
						 videoInfos.add(videoInfo);
					 }
					 groupInfo.setChilds(videoInfos);
					 gobalGroupInfos.add(groupInfo);
					 videoInfos = null;
					 groupInfo = null;
				 }
			} 
		} catch (Exception e) {
			e.printStackTrace();
			isPullException = true;
		}
    	return gobalGroupInfos;
    }  
    
    
  /*  public void testPullRead(String jsonObject){  
        groups = new ArrayList<Map<String, String>>();  
        childs = new ArrayList<List<Map<String, String>>>();
        arr = new ArrayList<ArrayList<VideoInfo>>();
    	try {
			JSONObject jsonObject_1 = new JSONObject(jsonObject);
			String video = jsonObject_1.get("video").toString();
			if(video !=null){
				 JSONArray  jsonarray_video_person = jsonObject_1.getJSONArray("video");
				 for(int i=0;i < jsonarray_video_person.length();i++){
					 Map<String, String> group1 = new HashMap<String, String>();
					 List<Map<String, String>> child1 = new ArrayList<Map<String, String>>();
					 videoList = new ArrayList<VideoInfo>(); 
					 JSONObject jsonObject_video = (JSONObject)jsonarray_video_person.opt(i);
					 String videoNamePerson = jsonObject_video.getString("videoNamePerson");
					 group1.put("group", videoNamePerson);
					 JSONArray jsonObject_son = jsonObject_video.getJSONArray("videos");
					 for(int j= 0;j <jsonObject_son.length();j++ ){ 
						 JSONObject json = (JSONObject)jsonObject_son.opt(j); 
						 Map<String, String> group2 = new HashMap<String, String>();
						 VideoInfo videoInfo = new VideoInfo(); 
						 videoInfo.setImage(json.getString("image"));
						 videoInfo.setUrl(json.getString("url"));
						 videoInfo.setTitle( json.getString("title"));
						 group2.put("child", json.getString("title")); 
						 child1.add(group2) ;
						 videoList.add(videoInfo);
					 }
					 childs.add(child1);
					 groups.add(group1);
					 arr.add(videoList);
				 }
			} 
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	 
    }  */
}
