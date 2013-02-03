package hk.com.dycx.iptv.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-1-31 下午5:15:45
 * @version 1.0
 */
public class Utils {
	public static String readAssetsToString(Activity context,String assetsFileName) {
		InputStream is = null;
		String temStr = null;
		try {
			is = context.getAssets().open(assetsFileName);
			
			if (is != null) {
	               StringBuilder sb = new StringBuilder();
	               String line;
	                  BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	                   while ((line = reader.readLine()) != null) {
	                     sb.append(line);
	                   }
	                   if(sb !=null){
	      				 temStr = sb.toString();
	      		      }
	      		     return temStr;

	             } 
			

		} catch (IOException e) {
			e.printStackTrace();
			//TODO 显示解析出错
			  return temStr;

		} finally {
			try {
				if(is != null)
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return temStr;

	}
	
	/**
	 * 检测是否有网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isCheckNetAvailable(Context context) {
		boolean isCheckNet = false;
		try {
			final ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mobNetInfoActivity = connectivityManager
					.getActiveNetworkInfo();
			if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
				isCheckNet = false;
				return isCheckNet;
			} else {
				isCheckNet = true;
				return isCheckNet;
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return isCheckNet;
	}
}
