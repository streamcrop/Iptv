package hk.com.dycx.iptv.player;

import hk.com.dycx.iptv.R;
import hk.com.dycx.iptv.adapter.VideoInfoAdapter;
import hk.com.dycx.iptv.bean.VideoInfo;
import hk.com.dycx.iptv.utils.Logger;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-1-31 下午7:07:49
 * @version 1.0
 */
public abstract class BasePlayer extends Activity {
	protected static final String TAG = "BasePlayer";
	protected static final boolean isDebug = true;
	protected static final int SHOW_CHANNEL_LOADING = 0;
	protected static final int HIDE_PROGRESS = SHOW_CHANNEL_LOADING + 1;
	protected static final int HIDE_CHANNELS = SHOW_CHANNEL_LOADING + 2;
	
	/** 共用对话框 */
	private AlertDialog.Builder adb;
	
	private VideoInfoAdapter mVideoInfoAdapter;
	
	/** 显示当前播放列表 */
	private ListView lv_system_player;
	
	/** 当前播放节目列表 */
	protected ArrayList<VideoInfo> mPlayList;
	
	/** 当前播放节目 */
	protected int mPosition;
	
	protected ProgressDialog mProgressDialog;
	protected Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_CHANNEL_LOADING:
				mHandler.removeMessages(HIDE_PROGRESS);
				String preMessage = (String) msg.obj;
				mProgressDialog.setMessage(preMessage);
				Logger.i(TAG, isDebug, "mProgressDialog.show");
				mProgressDialog.show();
				break;
			case HIDE_PROGRESS:
				Logger.i(TAG, isDebug, "removeMessages(HIDE_PROGRESS)");
				mHandler.removeMessages(HIDE_PROGRESS);
				if (mProgressDialog.isShowing()) {
					mProgressDialog.cancel();
					Logger.i(TAG, isDebug, "mProgressDialog.cancel");
				}
				break;
			case HIDE_CHANNELS:
				hideChannel();
				break;

			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (initContent()) 
			return;
		Logger.i(TAG, isDebug, "initContent");
		initView();
		Logger.i(TAG, isDebug, "initView");
		getStartInfo();
		Logger.i(TAG, isDebug, "getStartInfo");
		
		startPlay();
		setVideoViewListener();

//		mVideoView.setMediaController(new MediaController(SystemPlayer.this));
//		Logger.i(TAG, isDebug, "setMediaController");

	}

	@Override
	protected void onStop() {
		if (mProgressDialog != null) {
			mProgressDialog.cancel();
		}
		super.onStop();
	}
	
	private void initView() {
		findVideoView();
		lv_system_player = findListView();
		Logger.i(TAG, isDebug, "lv_system_player:"+lv_system_player.getId());
		mProgressDialog = new ProgressDialog(BasePlayer.this, ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				Logger.i(TAG, isDebug, "progress dialog keyCode:"+ keyCode);
				return false;
			}
		});
	}

	private void startPlay() {
		if (mPlayList != null && mPosition > 0 && mPlayList.size() > mPosition) {
			Message preMessage = Message.obtain();
			preMessage.obj = getString(R.string.load_channel);
			preMessage.what = SHOW_CHANNEL_LOADING;
			mHandler.sendMessage(preMessage);
			
			String path = mPlayList.get(mPosition).getUrl();
			if (path != null && !path.isEmpty()) {
				setVideoURI(path);
			} else {
				Toast.makeText(getApplicationContext(), R.string.path_null + mPlayList.get(mPosition).getName() , 0).show();
			}
		}
	}
 
	private void getStartInfo() {
		Intent intent = getIntent();
		if (intent != null) {
			mPlayList = (ArrayList<VideoInfo>) intent.getSerializableExtra("MediaIdList");
			mPosition = intent.getIntExtra("CurrentPosInMediaIdList", 0);
			if (mPlayList != null) {
				mVideoInfoAdapter = new VideoInfoAdapter(getApplicationContext(), mPlayList, R.layout.channel_item);
				lv_system_player.setAdapter(mVideoInfoAdapter);
				lv_system_player.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if (mPosition != position) {
							mPosition = position;
							startPlay();
							mHandler.sendEmptyMessage(HIDE_CHANNELS);
						}else {
							Toast.makeText(getApplicationContext(), R.string.select_channel_palying_now, 0).show();
						}
					}
				});
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Logger.i(TAG, isDebug, "activity keyCode:"+keyCode);
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			showAndHideChannel();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
			Logger.i(TAG, isDebug, "上一节目");
			mPosition--;
			if (mPosition < 0) {
				mPosition = 0;
				Toast.makeText(getApplicationContext(), R.string.first_item, 0).show();
			}else {
				startPlay();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
			Logger.i(TAG, isDebug, "下一节目");
			mPosition++;
			if (mPosition >= mPlayList.size()) {
				mPosition = mPlayList.size() - 1;
				Toast.makeText(getApplicationContext(), R.string.last_item, 0).show();
			}else {
				startPlay();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showChannel() {
		if (lv_system_player != null && lv_system_player.getVisibility() != View.VISIBLE) {
			Logger.i(TAG, isDebug, "显示列表");
			lv_system_player.setVisibility(View.VISIBLE);
			mHandler.removeMessages(HIDE_CHANNELS);
			mHandler.sendEmptyMessageDelayed(HIDE_CHANNELS, 5000);
			Logger.i(TAG, isDebug, "sendEmptyMessageDelayed(HIDE_CHANNELS, 5000)");
		}
	}
	
	private void hideChannel(){
		if (lv_system_player != null && lv_system_player.getVisibility() == View.VISIBLE) {
			Logger.i(TAG, isDebug, "隐藏列表");
			lv_system_player.setVisibility(View.GONE);
		}
	}
	
	private void showAndHideChannel(){
		if (lv_system_player != null && lv_system_player.getVisibility() != View.VISIBLE) {
			showChannel();
		}else {
			hideChannel();
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Logger.i(TAG, isDebug, "鼠标点下");
			showAndHideChannel();
			break;

		}
		return super.onTouchEvent(event);
	}
	
	/**
	 * 显示警告对话框
	 * @param message
	 */
	public void alert(String message) {
		
		alert(null,message,null,null,null,null,null,false);
	}

	/**
	 * 
	 * @param title
	 * @param message
	 * @param view
	 * @param btn1Text
	 * @param listener1
	 */
	public void alert(String title,String message,View view,
			String btn1Text,DialogInterface.OnClickListener listener1) {
		alert(title,message,view,btn1Text,listener1,null,null,true);
	}
	/**
	 * 对话框
	 * @param title		标题
	 * @param message	显示的信息
	 * @param view		显示的View
	 * @param btn1Text	按扭1的文字
	 * @param listener1	按钮1的监听
	 * @param btn1Text	按扭2的文字
	 * @param listener1	按钮2的监听
	 * @param b			是否显示取消按钮
	 */
	public void alert(String title,String message,View view,
			String btn1Text,DialogInterface.OnClickListener listener1,
			String btn2Text,DialogInterface.OnClickListener listener2,boolean b){
			adb = new AlertDialog.Builder(this);
		if(title==null){
			title = "提示";
		}
		adb.setTitle(title);
		
		if(message!=null){
			adb.setMessage(message);
		}
		if(view!=null){
			adb.setView(view);
		}
		if(btn1Text ==null){
			btn1Text="确定";
		}
		if (listener1 != null) {
			adb.setPositiveButton(btn1Text, listener1);
		}else{
			adb.setPositiveButton(btn1Text, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		if(btn2Text ==null){
			btn2Text="取消";
		}
		if (listener2 != null) {
			adb.setPositiveButton(btn2Text, listener1);
		}
		if(b){
			adb.setNegativeButton(btn2Text, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		adb.create();
		adb.show();
	}
	
	/** 检查是否安装插件，setContentView() */
	public abstract boolean initContent();
	
	/** 获得播放器 */
	public abstract void findVideoView();
	
	/** 获得ListView */
	protected abstract ListView findListView();
	
	/** 设置播放路径 */
	public abstract void setVideoURI(String path);
	
	public abstract void setVideoViewListener();
	
	public abstract void exit();
}
