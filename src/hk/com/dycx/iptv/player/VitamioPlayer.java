package hk.com.dycx.iptv.player;

import android.net.Uri;
import android.widget.ListView;
import android.widget.Toast;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;
import hk.com.dycx.iptv.R;
import hk.com.dycx.iptv.utils.Logger;
import hk.com.dycx.iptv.utils.Utils;

/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-2-3 上午11:47:09
 * @version 1.0
 */
public class VitamioPlayer extends BasePlayer {
	private static final String TAG = "VitamioPlayer";
	private static final boolean isDebug = true;
	private int errorWhat;
	/** Vitamio播放器 */
	private VideoView mVideoView;
	@Override
	protected void onResume() {
		if (mVideoView != null) {
			mVideoView.resume();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (mVideoView != null) {
			mVideoView.pause();
		}
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		if (mVideoView != null){
			mVideoView.stopPlayback();
		}
		super.onDestroy();
	}
	@Override
	public boolean initContent() {
		if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this, getClass().getName(), R.string.init_decoders, R.raw.libarm))
			return true;
		setContentView(R.layout.vitamio_player);
		Logger.i(TAG, isDebug, "我是：VitamioPlayer");
		return false;
	}
	@Override
	public void findVideoView() {
		mVideoView = (VideoView) findViewById(R.id.vitamio_vv);
	}
	@Override
	public void setVideoURI(String path) {
		if (path.startsWith("http:"))
			mVideoView.setVideoURI(Uri.parse(path));
		else
			mVideoView.setVideoPath(path);
		Logger.i(TAG, isDebug, "zzzzzzzzzUri:" + Uri.parse(path));
	}
	@Override
	public void setVideoViewListener() {
		mVideoView.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mVideoView.stopPlayback();
				errorWhat = what;
				Logger.i(TAG, isDebug, "errorWhat:"+errorWhat + "extra:"+ extra);
				if (Utils.isCheckNetAvailable(getApplicationContext())) { //网络可用
					Toast.makeText(getApplicationContext(), R.string.error_path, 1).show();
				}else { //网络不可用
					Toast.makeText(getApplicationContext(), R.string.net_not_work, 1).show();
				}
				exit();
				return true;
			}
		});
		mVideoView.setOnPreparedListener( new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mVideoView.start();
				mHandler.sendEmptyMessage(HIDE_PROGRESS);
				Logger.i(TAG, isDebug, "mVideoView.start()");
			}
		});
		
		mVideoView.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				exit();
			}
		});
	}

	@Override
	protected ListView findListView() {
		
		return (ListView) findViewById(R.id.lv_vitamio_player);
	}

	@Override
	public void exit() {
		try {
			if (mVideoView != null) {
				mVideoView.stopPlayback();
			}
			finish();
		} catch (Exception e) {
			finish();
		}
	}
}
