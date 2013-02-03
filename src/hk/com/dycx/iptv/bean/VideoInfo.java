package hk.com.dycx.iptv.bean;

import java.io.Serializable;

/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-1-31 下午4:57:32
 * @version 1.0
 */
public class VideoInfo implements Serializable{

	/** 视频路径 */
	private String url;
	
	/** 视频名称 */
	private String name;

	
	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return "VideoInfo [url=" + url + ", name=" + name + "]";
	}
	
}
