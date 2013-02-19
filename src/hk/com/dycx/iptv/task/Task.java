package hk.com.dycx.iptv.task;

import hk.com.dycx.iptv.utils.ThreadPoolManager;


/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-1-3 ����9:08:30
 * @version 1.0
 */
public class Task implements Runnable {
	private static final String TAG = "Task";
	
	private static boolean isDebug = true;
	
    public static final int NO_ERROR = 0;
    
    protected int mErrorCode = NO_ERROR;

    public static final int RUNNING_STATUS_UNSTART = 0;

    public static final int RUNNING_STATUS_RUNNING = 1;

    public static final int RUNNING_STATUS_PAUSE = 2;

    public static final int RUNNING_STATUS_FINISH = 3;

    protected int mRunningStatus = RUNNING_STATUS_UNSTART;
    
    public void start() {
        if (mRunningStatus != RUNNING_STATUS_RUNNING) {
            mRunningStatus = RUNNING_STATUS_RUNNING;
            onStart();
            ThreadPoolManager.getInstance().addTask(this);
        }
    }
    
    protected void onStart(){
    	
    }
    
	@Override
	public void run() {
        onRunning();
        stop();
	}
	
    protected void onRunning(){
    	
    }
    
    protected void onStop() {
    	
    }
    
    public void stop() {
    	if(mRunningStatus != RUNNING_STATUS_UNSTART){            
            onStop();
            mRunningStatus = RUNNING_STATUS_FINISH;
    	}
    }
    public int getErrorCode() {
    	return mErrorCode;
    }

    public int getRunningStatus() {
    	return mRunningStatus;
    }
}
