package hk.com.dycx.iptv;


import java.util.ArrayList;

import hk.com.dycx.iptv.adapter.VideoInfoAdapter;
import hk.com.dycx.iptv.bean.VideoInfo;
import hk.com.dycx.iptv.player.BasePlayer;
import hk.com.dycx.iptv.player.SystemPlayer;
import hk.com.dycx.iptv.player.VideoPlayerActivity;
import hk.com.dycx.iptv.player.VitamioPlayer;
import hk.com.dycx.iptv.utils.Logger;
import hk.com.dycx.iptv.utils.Utils;
import hk.com.dycx.iptv.utils.VideoInfoProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final boolean isDebug = true;
	private GridView mGv_main;
	private ArrayList<VideoInfo> mVideoInfos;
	private VideoInfoAdapter mVideoInfoAdapter;

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
	}

	private void setViewClickListener() {
		mVideoInfoAdapter = new VideoInfoAdapter(getApplicationContext(), mVideoInfos, R.layout.channel_item);
		mGv_main.setAdapter(mVideoInfoAdapter);
		mGv_main.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Logger.i(TAG, isDebug, "onItemClick:"+position);
				if (Utils.isCheckNetAvailable(getApplicationContext())) { //网络可用
					Intent playIntent = new Intent(MainActivity.this, SystemPlayer.class);
					Logger.i(TAG, isDebug, "Intent(MainActivity.this, SystemPlayer.class)");
					Bundle sBundle = new Bundle();
					sBundle.putSerializable("MediaIdList", mVideoInfos);
					playIntent.putExtras(sBundle);
					playIntent.putExtra("CurrentPosInMediaIdList", position);
					startActivity(playIntent);
				}
			}
			
		});
	}

}
