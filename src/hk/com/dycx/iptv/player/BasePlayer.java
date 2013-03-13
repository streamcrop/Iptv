package hk.com.dycx.iptv.player;

import hk.com.dycx.iptv.R;
import hk.com.dycx.iptv.UserDialogActivity;
import hk.com.dycx.iptv.adapter.VideoGroupAdapter;
import hk.com.dycx.iptv.adapter.VideoInfoAdapter;
import hk.com.dycx.iptv.adapter.VideoInfoAdapterTwo;
import hk.com.dycx.iptv.adapter.VideoInfoChildAdapter;
import hk.com.dycx.iptv.bean.VideoInfo;
import hk.com.dycx.iptv.utils.Logger;
import hk.com.dycx.iptv.utils.Utils;
import hk.com.dycx.iptv.utils.VideoInfoProvider;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-1-31 下午7:07:49
 * @version 1.0
 */
public abstract class BasePlayer extends Activity implements android.view.View.OnClickListener {
	protected static final String TAG = "BasePlayer";
	protected static final boolean isDebug = true;
	protected static final int SHOW_CHANNEL_LOADING = 0;
	protected static final int HIDE_PROGRESS = SHOW_CHANNEL_LOADING + 1;
	protected static final int HIDE_CHANNELS = SHOW_CHANNEL_LOADING + 2;

	protected SharedPreferences mSharedPreferences;

	/** 共用对话框 */
	private AlertDialog.Builder adb;

	private VideoInfoAdapterTwo mVideoInfoChildAdapter;

	/** 显示当前播放列表和上下页按钮 */
	private RelativeLayout mListviewPanel;

	/** 当前播放列表group */
	private ListView lv_play_group;

	/** 当前播放列表child */
	private ListView lv_play_child;
	
	/** 当前播放频道Group */
	private TextView tv_playing_group;
	
	/** 当前播放频道 child */
	private TextView tv_playing_child;
	
	private ToggleButton tbtn_full_screen;

	/** 当前播放节目列表 */
	protected ArrayList<VideoInfo> mVideoInfos;

	/** 当前播放节目 */
	protected int mChildSelectPosition;

	protected int mGroupSelectPosition;

	/** 上一页 */
	private Button btn_list_pre;

	/** 下一页 */
	private Button btn_list_next;

	/** 进度条 */
	protected ProgressDialog mProgressDialog;

	private VideoGroupAdapter mVideoGroupAdapter;

	protected Handler mHandler = new Handler() {
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
		mSharedPreferences = getSharedPreferences(Utils.SP_SPNAME, Context.MODE_PRIVATE);
		Logger.i(TAG, isDebug, "initContent");
		initView();
		Logger.i(TAG, isDebug, "initView");
		getStartInfo();
		Logger.i(TAG, isDebug, "getStartInfo");

		startPlay();
		setVideoViewListener();

		// mVideoView.setMediaController(new
		// MediaController(SystemPlayer.this));
		// Logger.i(TAG, isDebug, "setMediaController");

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
		mListviewPanel = (RelativeLayout) findViewById(R.id.rl_list);
		lv_play_group = (ListView) mListviewPanel.findViewById(R.id.lv_play_group);
//		lv_play_group.requestFocus();
		lv_play_child = (ListView) mListviewPanel.findViewById(R.id.lv_play_child);
		tv_playing_group = (TextView) mListviewPanel.findViewById(R.id.tv_playing_group);
		tv_playing_child = (TextView) mListviewPanel.findViewById(R.id.tv_playing_child);
		tbtn_full_screen = (ToggleButton) mListviewPanel.findViewById(R.id.tbtn_full_screen);
//		btn_list_pre = (Button) mListviewPanel.findViewById(R.id.btn_list_pre);
//		btn_list_next = (Button) mListviewPanel.findViewById(R.id.btn_list_next);
//		btn_list_pre.setOnClickListener(this);
//		btn_list_next.setOnClickListener(this);
		mProgressDialog = new ProgressDialog(BasePlayer.this, ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				Logger.i(TAG, isDebug, "progress dialog keyCode:" + keyCode);
				return false;
			}
		});
	}

	protected void startPlay() {
		if (mVideoInfos != null && mChildSelectPosition >= 0 && mVideoInfos.size() > mChildSelectPosition) {
			// 设置当前播放节目为高亮显示
			// mVideoInfoAdapterTwo.setPlayPosition(mPosition);
			String path = mVideoInfos.get(mChildSelectPosition).getUrl();
			if (path != null && !path.isEmpty()) {
				// 判断URI是不是需要输入帐号和密码
				checkUri(path);
			} else {
				Toast.makeText(getApplicationContext(), R.string.path_null + mVideoInfos.get(mChildSelectPosition).getName(), 0).show();
			}
		}
	}

	private void checkUri(String path) {
		if (path != null && path.contains("viplive2.zapto.org:5001")) {
			String userName = mSharedPreferences.getString(Utils.SP_USER_NAME, null);
			String userPassword = mSharedPreferences.getString(Utils.SP_USER_PASSWORD, null);
			if (userName == null || userName.isEmpty() || userPassword == null || userPassword.isEmpty()) {
				// 弹出对话框
				Intent userIntent = new Intent(BasePlayer.this, UserDialogActivity.class);
				startActivityForResult(userIntent, 0);
			} else {
				Logger.i(TAG, isDebug, "userName:" + userName + ";userPassword:" + userPassword);
				// 拼装URI
				Message preMessage = Message.obtain();
				preMessage.obj = getString(R.string.load_channel) + mVideoInfos.get(mChildSelectPosition).getName();
				preMessage.what = SHOW_CHANNEL_LOADING;
				mHandler.sendMessage(preMessage);
				String[] split = path.split("@");
				path = split[0] + userName + ":" + userPassword + "@" + split[1];
				Logger.i(TAG, isDebug, "path.replace:" + path);
				openVideo(path);
				// setVideoURI("mnt/sdcard/test/1.wma"); // .mp4 .asf .avi .f4v
				// .flv .mov .mp3 .ts .wma
			}
		} else {
			Message preMessage = Message.obtain();
			preMessage.obj = getString(R.string.load_channel) + mVideoInfos.get(mChildSelectPosition).getName();
			preMessage.what = SHOW_CHANNEL_LOADING;
			mHandler.sendMessage(preMessage);
			openVideo(path);
		}
	}
	
	private void openVideo(String path){
		setVideoURI(path);
		String playingGroupName = VideoInfoProvider.gobalGroupInfos.get(mGroupSelectPosition).getGroupName();
		String playingVideoName = mVideoInfos.get(mChildSelectPosition).getName();
		tv_playing_group.setText(playingGroupName);
		tv_playing_child.setText(playingVideoName);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			Logger.i(TAG, isDebug, "onActivityResult:data !=null");
			// 再查找加载
			startPlay();
		} else {
			Logger.i(TAG, isDebug, "onActivityResult:data ==null");
			exit();
		}
	}

	private void getStartInfo() {
		Intent intent = getIntent();
		if (intent != null) {
			mGroupSelectPosition = intent.getIntExtra(Utils.GROUP_SELECT_POSITION, 0);
			mChildSelectPosition = intent.getIntExtra(Utils.CHILD_SELECT_POSITION, 0);
			Logger.i(TAG, isDebug, "mGroupSelectPosition:" + mGroupSelectPosition);
			Logger.i(TAG, isDebug, "mChildSelectPosition:" + mChildSelectPosition);
			mVideoInfos = VideoInfoProvider.gobalGroupInfos.get(mGroupSelectPosition).getChilds();
			mVideoGroupAdapter = new VideoGroupAdapter(getApplicationContext(), VideoInfoProvider.gobalGroupInfos, R.layout.channel_item_main_list_view);
			lv_play_group.setAdapter(mVideoGroupAdapter);
			lv_play_group.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			lv_play_group.setSelection(mGroupSelectPosition);
			lv_play_group.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					Logger.i(TAG, isDebug, "ItemSelected position:" + position);
					mVideoInfos = VideoInfoProvider.gobalGroupInfos.get(position).getChilds();
					mVideoInfoChildAdapter.setVideoInfos(mVideoInfos);
					mGroupSelectPosition = position;
					lv_play_group.setItemChecked(position, true);
					if (mListviewPanel != null && mListviewPanel.getVisibility() == View.VISIBLE) {
						mHandler.removeMessages(HIDE_CHANNELS);
						mHandler.sendEmptyMessageDelayed(HIDE_CHANNELS, 5000);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});
			
			lv_play_group.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Logger.i(TAG, isDebug, "click position:" + position);
					mVideoInfos = VideoInfoProvider.gobalGroupInfos.get(position).getChilds();
					mVideoInfoChildAdapter.setVideoInfos(mVideoInfos);
					mGroupSelectPosition = position;
					lv_play_group.setItemChecked(position, true);
					if (mListviewPanel != null && mListviewPanel.getVisibility() == View.VISIBLE) {
						mHandler.removeMessages(HIDE_CHANNELS);
						mHandler.sendEmptyMessageDelayed(HIDE_CHANNELS, 5000);
					}
				}
			});
			
			lv_play_group.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (KeyEvent.KEYCODE_DPAD_CENTER == keyCode) {
						showAndHideChannel();
						return true;
					}
					return false;
				}
			});
			
//			mVideoInfoChildAdapter = new VideoInfoChildAdapter(getApplicationContext(), mVideoInfos, R.layout.channel_item, mVideoInfos.get(mChildSelectPosition));
			mVideoInfoChildAdapter = new VideoInfoAdapterTwo(getApplicationContext(), mVideoInfos, R.layout.channel_item_main_grad_view);
			lv_play_child.setAdapter(mVideoInfoChildAdapter);
			lv_play_child.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (mChildSelectPosition != position) {
						lv_play_child.setItemChecked(position, true);
						mChildSelectPosition = position;
						startPlay();
						mHandler.sendEmptyMessage(HIDE_CHANNELS);
					} else {
//						Toast.makeText(getApplicationContext(), R.string.select_channel_palying_now, 0).show();
						hideChannel();
					}
				}
			});
			
			lv_play_child.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					if (mListviewPanel != null && mListviewPanel.getVisibility() == View.VISIBLE) {
						mHandler.removeMessages(HIDE_CHANNELS);
						mHandler.sendEmptyMessageDelayed(HIDE_CHANNELS, 5000);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Logger.i(TAG, isDebug, "activity keyCode:" + keyCode);
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			showAndHideChannel();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mListviewPanel != null && mListviewPanel.getVisibility() == View.VISIBLE) {
				hideChannel();
				return true;
			}
		}
//			else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
//			Logger.i(TAG, isDebug, "上一节目");
//			mChildSelectPosition--;
//			if (mChildSelectPosition < 0) {
//				mChildSelectPosition = 0;
//				Toast.makeText(getApplicationContext(), R.string.first_item, 0).show();
//			} else {
//				startPlay();
//			}
//			return true;
//		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
//			Logger.i(TAG, isDebug, "下一节目");
//			mChildSelectPosition++;
//			if (mChildSelectPosition >= mVideoInfos.size()) {
//				mChildSelectPosition = mVideoInfos.size() - 1;
//				Toast.makeText(getApplicationContext(), R.string.last_item, 0).show();
//			} else {
//				startPlay();
//			}
//			return true;
//		}
		return super.onKeyDown(keyCode, event);
	}

	private void showChannel() {
		if (mListviewPanel != null) {
			Logger.i(TAG, isDebug, "显示列表");
			mListviewPanel.setVisibility(View.VISIBLE);
			lv_play_child.requestFocus();
			mHandler.removeMessages(HIDE_CHANNELS);
			mHandler.sendEmptyMessageDelayed(HIDE_CHANNELS, 5000);
			Logger.i(TAG, isDebug, "sendEmptyMessageDelayed(HIDE_CHANNELS, 5000)");
		}
	}

	private void hideChannel() {
		if (mListviewPanel != null && mListviewPanel.getVisibility() == View.VISIBLE) {
			Logger.i(TAG, isDebug, "隐藏列表");
			mListviewPanel.setVisibility(View.GONE);
		}
	}

	private void showAndHideChannel() {
		if (mListviewPanel != null && mListviewPanel.getVisibility() != View.VISIBLE) {
			showChannel();
		} else {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.btn_list_pre:
//			// mVideoInfoAdapterTwo.setPrePage();
//			showChannel();
//			break;
//		case R.id.btn_list_next:
//			// mVideoInfoAdapterTwo.setNextPage();
//			showChannel();
//			break;
		case R.id.tbtn_full_screen:
			if (tbtn_full_screen.isChecked()) {
				tbtn_full_screen.setChecked(false);
				//TODO 设置为normal
				Toast.makeText(getApplicationContext(), "设置为normal", 0).show();
			} else {
				tbtn_full_screen.setChecked(true);
				//TODO 设置为全屏
				Toast.makeText(getApplicationContext(), "置为全屏", 0).show();
			}
			break;
		}
	}

	/**
	 * 显示警告对话框
	 * 
	 * @param message
	 */
	public void alert(String message) {

		alert(null, message, null, null, null, null, null, false);
	}

	/**
	 * 
	 * @param title
	 * @param message
	 * @param view
	 * @param btn1Text
	 * @param listener1
	 */
	public void alert(String title, String message, View view, String btn1Text, DialogInterface.OnClickListener listener1) {
		alert(title, message, view, btn1Text, listener1, null, null, true);
	}

	/**
	 * 对话框
	 * 
	 * @param title
	 *            标题
	 * @param message
	 *            显示的信息
	 * @param view
	 *            显示的View
	 * @param btn1Text
	 *            按扭1的文字
	 * @param listener1
	 *            按钮1的监听
	 * @param btn1Text
	 *            按扭2的文字
	 * @param listener1
	 *            按钮2的监听
	 * @param b
	 *            是否显示取消按钮
	 */
	public void alert(String title, String message, View view, String btn1Text, DialogInterface.OnClickListener listener1, String btn2Text, DialogInterface.OnClickListener listener2, boolean b) {
		adb = new AlertDialog.Builder(this);
		if (title == null) {
			title = "提示";
		}
		adb.setTitle(title);

		if (message != null) {
			adb.setMessage(message);
		}
		if (view != null) {
			adb.setView(view);
		}
		if (btn1Text == null) {
			btn1Text = "确定";
		}
		if (listener1 != null) {
			adb.setPositiveButton(btn1Text, listener1);
		} else {
			adb.setPositiveButton(btn1Text, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		if (btn2Text == null) {
			btn2Text = "取消";
		}
		if (listener2 != null) {
			adb.setPositiveButton(btn2Text, listener1);
		}
		if (b) {
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

	/** 设置播放路径 */
	public abstract void setVideoURI(String path);

	public abstract void setVideoViewListener();

	public abstract void exit();
}
