package hk.com.dycx.iptv;


import java.util.ArrayList;

import hk.com.dycx.iptv.adapter.VideoInfoAdapter;
import hk.com.dycx.iptv.bean.VideoInfo;
import hk.com.dycx.iptv.player.SystemPlayer;
import hk.com.dycx.iptv.utils.Logger;
import hk.com.dycx.iptv.utils.Utils;
import hk.com.dycx.iptv.utils.VideoInfoProvider;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = "MainActivity";
    private static final boolean isDebug = true;
	private GridView mGv_main;
	private ArrayList<VideoInfo> mVideoInfos;
	private VideoInfoAdapter mVideoInfoAdapter;
	private Button btn_main_pre;
	private Button btn_main_next;
	private Button btn_main_decode;
	private TextView tv_user_name_main;
	private SharedPreferences mSharedPreferences;

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
        mVideoInfos = VideoInfoProvider.pullRead(jsonStr);
        for (int i = 0; i < mVideoInfos.size(); i++) {
        	VideoInfo videoInfo = mVideoInfos.get(i);
        	Logger.i(TAG, isDebug, "videoInfo:"+videoInfo.toString());
		}
        findView();
        setViewClickListener();
	}
	
	private void findView() {
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
		mVideoInfoAdapter = new VideoInfoAdapter(getApplicationContext(), mVideoInfos, R.layout.channel_item, 24);
		mGv_main.setAdapter(mVideoInfoAdapter);
		mGv_main.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pageIndex = mVideoInfoAdapter.getPageIndex();
				int currentPosition = ( (pageIndex -1) * mVideoInfoAdapter.mPageCount) + position;
				Logger.i(TAG, isDebug, "currentPosition:"+currentPosition);
				if (Utils.isCheckNetAvailable(getApplicationContext())) { //网络可用
					Intent playIntent = new Intent(MainActivity.this, SystemPlayer.class);
					Logger.i(TAG, isDebug, "Intent(MainActivity.this, SystemPlayer.class)");
					Bundle sBundle = new Bundle();
					sBundle.putSerializable("MediaIdList", mVideoInfos);
					playIntent.putExtras(sBundle);
					playIntent.putExtra("CurrentPosInMediaIdList", currentPosition);
					startActivity(playIntent);
				}
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_main_pre:
			mVideoInfoAdapter.setPrePage();
			break;
		case R.id.btn_main_next:
			mVideoInfoAdapter.setNextPage();
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
