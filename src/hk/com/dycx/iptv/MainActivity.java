package hk.com.dycx.iptv;


import java.util.ArrayList;

import hk.com.dycx.iptv.adapter.VideoInfoAdapter;
import hk.com.dycx.iptv.bean.VideoInfo;
import hk.com.dycx.iptv.player.SystemPlayer;
import hk.com.dycx.iptv.utils.Logger;
import hk.com.dycx.iptv.utils.Utils;
import hk.com.dycx.iptv.utils.VideoInfoProvider;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = "MainActivity";
    private static final boolean isDebug = true;
	private GridView mGv_main;
	private ArrayList<VideoInfo> mVideoInfos;
	private VideoInfoAdapter mVideoInfoAdapter;
	private Button btn_main_pre;
	private Button btn_main_next;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
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
	}

	private void setViewClickListener() {
		btn_main_pre.setOnClickListener(this);
		btn_main_next.setOnClickListener(this);
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

		}
	}

}
