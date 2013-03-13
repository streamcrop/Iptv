package hk.com.dycx.iptv;


import java.util.ArrayList;
import java.util.List;

import hk.com.dycx.iptv.adapter.VideoGroupAdapter;
import hk.com.dycx.iptv.adapter.VideoInfoAdapterTwo;
import hk.com.dycx.iptv.bean.VideoGroupInfo;
import hk.com.dycx.iptv.bean.VideoInfo;
import hk.com.dycx.iptv.player.SystemPlayer;
import hk.com.dycx.iptv.utils.Logger;
import hk.com.dycx.iptv.utils.Utils;
import hk.com.dycx.iptv.utils.VideoInfoProvider;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = "MainActivity";
    private static final boolean isDebug = true;
	private ListView mLv_main;
	private GridView mGv_main;
	private ArrayList<VideoInfo> mVideoInfos;
	private VideoInfoAdapterTwo mVideoInfoAdapterTwo;
	private Button btn_main_pre;
	private Button btn_main_next;
	private Button btn_main_decode;
	private TextView tv_user_name_main;
	private SharedPreferences mSharedPreferences;
	private VideoGroupAdapter mVideoGroupAdapter;
	private int mGroupSelectPosition;
	private int mChildSelectPosition;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mSharedPreferences = getSharedPreferences(Utils.SP_SPNAME, Context.MODE_PRIVATE);
        Logger.i(TAG, isDebug, "onCreate");
        init();
    }

	private void init() {
        String jsonStr = Utils.readAssetsToString(this, "tv.txt");
        
        if (VideoInfoProvider.gobalGroupInfos == null || VideoInfoProvider.gobalGroupInfos.size() <= 0 || VideoInfoProvider.isPullException) {
        	VideoInfoProvider.isPullException = false;
        	VideoInfoProvider.gobalGroupInfos = VideoInfoProvider.pullRead(jsonStr);
        	checkPullError();
		}
//        Logger.i(TAG, isDebug, "mVideoGroupInfos.size():" + mVideoGroupInfos.size());
//        for (int i = 0; i < mVideoGroupInfos.size(); i++) {
//        	VideoGroupInfo videoGroupInfo = mVideoGroupInfos.get(i);
//        	Logger.i(TAG, isDebug, "VideoGroupInfo:" + videoGroupInfo.getGroupName());
//        	ArrayList<VideoInfo> childs = videoGroupInfo.getChilds();
//        	for (int j = 0; j < childs.size(); j++) {
//        		Logger.i(TAG, isDebug, "childs.get(j):" + childs.get(j).toString());
//			}
//        	Logger.i(TAG, isDebug, "-----------------------------------------------------");
//		}
        findView();
        setViewClickListener();
	}

	private void checkPullError() {
		if (VideoInfoProvider.isPullException) {
			new AlertDialog.Builder(this)
			.setCancelable(true)
			.setMessage(R.string.pull_error)
			.create()
			.show();
		}
	}
	
	private void findView() {
		mLv_main = (ListView) findViewById(R.id.lv_main);
		mGv_main = (GridView) findViewById(R.id.gv_main);
		btn_main_pre = (Button) findViewById(R.id.btn_main_pre);
		btn_main_next = (Button) findViewById(R.id.btn_main_next);
		btn_main_decode = (Button) findViewById(R.id.btn_main_decode);
		tv_user_name_main = (TextView) findViewById(R.id.tv_user_name_main);
	}

	private void setViewClickListener() {
		btn_main_pre.setOnClickListener(this);
		btn_main_next.setOnClickListener(this);
		btn_main_decode.setOnClickListener(this);
		tv_user_name_main.setOnClickListener(this);
		showDecodeButtonText();
		
		mVideoGroupAdapter = new VideoGroupAdapter(this, VideoInfoProvider.gobalGroupInfos, R.layout.channel_item_main_list_view);
		mLv_main.setAdapter(mVideoGroupAdapter);
		mLv_main.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mLv_main.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Logger.i(TAG, isDebug, "click position:"+position);
				mVideoInfos = VideoInfoProvider.gobalGroupInfos.get(position).getChilds();
				mVideoInfoAdapterTwo.setVideoInfos(mVideoInfos);
				mGroupSelectPosition = position;
				mLv_main.setItemChecked(position, true);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		if (VideoInfoProvider.gobalGroupInfos != null && VideoInfoProvider.gobalGroupInfos.size() > 0 ) {
			mLv_main.requestFocus();
			mLv_main.setItemChecked(0, true);
			Logger.i(TAG, isDebug, "mLv_main.setItemChecked(0, true)");
		}
		
		mLv_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Logger.i(TAG, isDebug, "click position:"+position);
				mVideoInfos = VideoInfoProvider.gobalGroupInfos.get(position).getChilds();
				mVideoInfoAdapterTwo.setVideoInfos(mVideoInfos);
				mGroupSelectPosition = position;
				mLv_main.setItemChecked(position, true);
			}
		});
		
//		mVideoInfoAdapter = new VideoInfoAdapter(getApplicationContext(), mVideoInfos, R.layout.channel_item, 24);
		mVideoInfoAdapterTwo = new VideoInfoAdapterTwo(this, VideoInfoProvider.gobalGroupInfos.get(0).getChilds(), R.layout.channel_item_main_grad_view);
		mGv_main.setAdapter(mVideoInfoAdapterTwo);
		mGv_main.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Logger.i(TAG, isDebug, "currentPosition:"+position);
				mChildSelectPosition = position;
				if (Utils.isCheckNetAvailable(getApplicationContext())) { //网络可用
					Intent playIntent = new Intent(MainActivity.this, SystemPlayer.class);
					Logger.i(TAG, isDebug, "Intent(MainActivity.this, SystemPlayer.class)");
					playIntent.putExtra(Utils.CHILD_SELECT_POSITION, mChildSelectPosition);
					playIntent.putExtra(Utils.GROUP_SELECT_POSITION, mGroupSelectPosition);
					startActivity(playIntent);
				}else {
					Toast.makeText(getApplicationContext(), R.string.net_not_work, 1).show();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_main_pre:
//			mVideoInfoAdapter.setPrePage();
			break;
		case R.id.btn_main_next:
//			mVideoInfoAdapter.setNextPage();
			break;
		case R.id.tv_user_name_main:
			//弹出更改帐号密码对话框
			Logger.i(TAG, isDebug, "tv_user_name_main:click");
			String userName = mSharedPreferences.getString(Utils.SP_USER_NAME, null);
			String userPassword = mSharedPreferences.getString(Utils.SP_USER_PASSWORD, null);
			Intent setUserInfoIntent = new Intent(getApplicationContext(), UserDialogActivity.class);
			setUserInfoIntent.putExtra(Utils.SP_USER_NAME, userName);
			setUserInfoIntent.putExtra(Utils.SP_USER_PASSWORD, userPassword);
			startActivity(setUserInfoIntent);
			break;
		case R.id.btn_main_decode:
			//改变sp里面的值
			boolean isDecode = mSharedPreferences.getBoolean(Utils.SP_DECODE, false);
			Editor edit = mSharedPreferences.edit();
			edit.putBoolean(Utils.SP_DECODE, !isDecode);
			edit.commit();
			showDecodeButtonText();
			break;

		}
	}
	@Override
	protected void onResume() {
		super.onResume();
//		showDecodeButtonText();
		showUserName();
	}

	private void showUserName() {
		if (mSharedPreferences != null) {
			String userName = mSharedPreferences.getString(Utils.SP_USER_NAME, " ");
			userName = getString(R.string.user) + userName;
			tv_user_name_main.setText(userName);
		}
	}

	private void showDecodeButtonText() {
		if (mSharedPreferences != null) {
			boolean isDecode = mSharedPreferences.getBoolean(Utils.SP_DECODE, false);
			btn_main_decode.setText(isDecode ? R.string.decode_open : R.string.decode_close);
		}
	}
}
