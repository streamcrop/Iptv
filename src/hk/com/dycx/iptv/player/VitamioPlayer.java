package hk.com.dycx.iptv.player;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;
import hk.com.dycx.iptv.MainActivity;
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
				setVideoScale(SCREEN_DEFAULT);
				//改到到用 setOnInfoListener 解决progress dialog 和显示同步 和 退出播放后直接退出的问题
//				mVideoView.start();
//				mHandler.sendEmptyMessage(HIDE_PROGRESS);
				Logger.i(TAG, isDebug, "mVideoView.start()");
			}
		});
		
		mVideoView.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				exit();
			}
		});
		
		mVideoView.setOnInfoListener(new OnInfoListener() {
			
			@Override
			public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
				switch (arg1) {
				case MediaPlayer.MEDIA_INFO_BUFFERING_START:
					Logger.i(TAG, isDebug, "MediaPlayer.MEDIA_INFO_BUFFERING_START");
					//开始缓存，暂停播放
					if (isPlaying()) {
						stopPlayer();
						needResume = true;
					}
//					showProgressDialog();
					break;
				case MediaPlayer.MEDIA_INFO_BUFFERING_END:
					Logger.i(TAG, isDebug, "MediaPlayer.MEDIA_INFO_BUFFERING_END");
					//缓存完成，继续播放
					if (needResume)
						startPlayer();
					mHandler.sendEmptyMessage(HIDE_PROGRESS);
					break;
				case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
					Logger.i(TAG, isDebug, "MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED download rate:" + arg2);
					break;

				}
				return false;
			}
		});
	}

	/** 是否需要自动恢复播放，用于自动暂停，恢复播放 */
	private boolean needResume;
	
	private void stopPlayer() {
		if (mVideoView != null)
			mVideoView.pause();
	}
	
	private void startPlayer() {
		if (mVideoView != null)
			mVideoView.start();
	}

	private boolean isPlaying() {
		return mVideoView != null && mVideoView.isPlaying();
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

	@Override
	public void setVideoScale(int type) {
		switch (type) {
		case SCREEN_FULL:
			Logger.i(TAG, isDebug, "Vitamio : setVideoViewFullScreen");
			mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_ZOOM, 0);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			break;
			
		case SCREEN_DEFAULT:
			Logger.i(TAG, isDebug, "Vitamio : setVideoViewNormal");
			int videoWidth = mVideoView.getVideoWidth();
			int videoHeight = mVideoView.getVideoHeight();
			int mWidth = mScreenWidth;
			int mHeight = mScreenHeight;
			
			if (videoWidth > 0 && videoHeight > 0) {
				if (videoWidth * mHeight > mWidth * videoHeight) {
					
					mHeight = mWidth * videoHeight / videoWidth;
				} else if (videoWidth * mHeight < mWidth * videoHeight) {
					
					mWidth = mHeight * videoWidth / videoHeight;
				} else {
					
				}
			}
			mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
			// mVideoView.setVideoScale(mWidth, mHeight);
			
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			break;

		}
	}

	@Override
	public boolean isSystemPlay() {
		return false;
	}
}
