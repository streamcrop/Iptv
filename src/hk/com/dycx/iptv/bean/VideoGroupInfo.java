package hk.com.dycx.iptv.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-3-12 下午3:52:18
 * @version 1.0
 */
public class VideoGroupInfo implements Serializable{
	private String groupName;
	
	private ArrayList<VideoInfo> childs;
	
	public VideoGroupInfo(){
		
	}

	public VideoGroupInfo(String groupName, ArrayList<VideoInfo> childs) {
		super();
		this.groupName = groupName;
		this.childs = childs;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public ArrayList<VideoInfo> getChilds() {
		return childs;
	}

	public void setChilds(ArrayList<VideoInfo> childs) {
		this.childs = childs;
	}

	@Override
	public String toString() {
		return "VideoGroupInfo [groupName=" + groupName + ", childs=" + childs + "]";
	}
	
	
}
