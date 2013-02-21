package hk.com.dycx.iptv.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-1-31 下午5:15:45
 * @version 1.0
 */
public class Utils {
	private static final String TAG = "Utils";
	
	private static final boolean isDebug = true;
	
    public static final String SP_SPNAME = "config";
    
    public static final String SP_DECODE = "decode";
    
    public static final String SP_USER_NAME = "username";
    
    public static final String SP_USER_PASSWORD = "userpassword";
    
	public static String readAssetsToString(Activity context,String assetsFileName) {
		InputStream is = null;
		String temStr = null;
		try {
			//查找data目录下的 assetsFileName 文件
			File dataTvFile = new File("data/" + assetsFileName);
			if (dataTvFile.exists()) {
				is = new FileInputStream(dataTvFile);
				Logger.i(TAG, isDebug, "data 可用");
			}
			
			//查找mnt/sdcard目录下的 assetsFileName 文件
			if (is == null) {
				File sdTvFile = new File(Environment.getExternalStorageDirectory() + "/" + assetsFileName);
				if (sdTvFile.exists()) {
					is = new FileInputStream(sdTvFile);
					Logger.i(TAG, isDebug, "sd card 可用");
				}
			}
			
			//从assert下面读取
			if (is == null) {
				is = context.getAssets().open(assetsFileName);
				Logger.i(TAG, isDebug, "context.getAssets().open(assetsFileName);");
			}
			
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
