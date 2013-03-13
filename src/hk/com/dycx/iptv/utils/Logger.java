package hk.com.dycx.iptv.utils;




import android.util.Log;

public class Logger {
	private static int LOG_LEVEL=6;
	private static int ERROR = 1;
	private static int WARN = 2;
	private static int INFO =3;
	private static int DEBUG =4;
	private static int VERBOS=5;
	
	public static void e(String tag ,Boolean isDebug ,String msg){
		if(LOG_LEVEL>ERROR && isDebug){
			Log.e(tag,msg);
		}
	}
	public static void w(String tag ,Boolean isDebug ,String msg){
		if(LOG_LEVEL>WARN && isDebug){
			Log.w(tag,msg);
		}
	}
	public static void i(String tag ,Boolean isDebug ,String msg){
		if(LOG_LEVEL>INFO && isDebug){
			Log.i(tag,msg);
		}
	}
	public static void d(String tag ,Boolean isDebug ,String msg){
		if(LOG_LEVEL>DEBUG && isDebug){
			Log.d(tag,msg);
		}
	}
	public static void v(String tag ,Boolean isDebug ,String msg){
		if(LOG_LEVEL>VERBOS && isDebug){
			Log.v(tag,msg);
		}
	}
	
}
