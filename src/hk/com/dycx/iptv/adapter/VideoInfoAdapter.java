package hk.com.dycx.iptv.adapter;

import java.util.List;

import hk.com.dycx.iptv.R;
import hk.com.dycx.iptv.bean.VideoInfo;
import hk.com.dycx.iptv.player.BasePlayer;
import hk.com.dycx.iptv.utils.Logger;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-1-31 下午5:43:09
 * @version 1.0
 */
public class VideoInfoAdapter extends BaseAdapter {
	
	private static final String TAG = "VideoInfoAdapter";
	
	private static final boolean isDebug = true;

	private List<VideoInfo> mVideoInfos;
	
	private Context mContext;
	
	private int mItem;
	
	/** 一页显示多少条目 */
	public int mPageCount;
	
	/** 共有多少页 */
	private int mMaxCount;
	
	/** 页码是从第一页开始的 */
	private int mPageIndex = 1;

	/** 最后一页剩下的条目个数 */
	private int mLastPageCount;
	
	/** 当前播放的条目 */
	private int mPlayPosition = -1;
	
	/** 当前页第一个position */
	private int mPageStartPosition;
	
	/** 当前页最后一个position */
	private int mPageEndPosition;
	
	public VideoInfoAdapter(Context context, List<VideoInfo> infos, final int item, int pageCount) {
		mContext = context;
		mItem = item;
		mVideoInfos = infos;
		mPageCount = pageCount;
		
		computeStartAndEndPosition(mPageIndex);
		mMaxCount = (int) Math.ceil(mVideoInfos.size() / (double)mPageCount);
		mLastPageCount = mVideoInfos.size() % mPageCount;
		Logger.i(TAG, isDebug, "mVideoInfos.size:"+mVideoInfos.size());
		Logger.i(TAG, isDebug, "mPageCount:"+mPageCount);
		Logger.i(TAG, isDebug, "mMaxCount:"+mMaxCount);
	}
	
	public int getmPlayPosition() {
		return mPlayPosition;
	}

	/** 设置当前播放第几个,并高亮显示,查检是否要自动翻页 */
	public void setPlayPosition(int playPosition) {
		this.mPlayPosition = playPosition;
		//查检是否是自动翻页
		Logger.i(TAG, isDebug, "mPlayPosition:"+mPlayPosition);
		autoPager();
		notifyDataSetChanged();
	}

	private void autoPager() {
		while (mPlayPosition < mPageStartPosition && mPageIndex > 1) { // 跳到上一页
			setPrePage();
		}
		while (mPlayPosition > mPageEndPosition && mPageIndex < mMaxCount) { // 跳到下一页
			setNextPage();
		}
	}

	public int getPageIndex() {
		return mPageIndex;
	}

	/** 设置当前页 */
	public void setPageIndex(int pageIndex) {
		mPageIndex = pageIndex;
	}
	
	/** 计算当前页的第一个 position 和最后一个 position */
	private void computeStartAndEndPosition(int pageIndex) {
		mPageStartPosition = (pageIndex - 1) * mPageCount;
		mPageEndPosition = pageIndex * mPageCount - 1;
		Logger.i(TAG, isDebug, "mPageStartPosition:"+mPageStartPosition);
		Logger.i(TAG, isDebug, "mPageEndPosition:"+mPageEndPosition);
	}
	
	/** 设置上一页 */
	public int setPrePage(){
		mPageIndex -= 1;
		if (mPageIndex >= 1) {
			computeStartAndEndPosition(mPageIndex);
			notifyDataSetChanged();
		}else {
			mPageIndex = 1;
			Toast.makeText(mContext, R.string.first_page, 0).show();
		}
		Logger.i(TAG, isDebug, "mPageIndex:"+mPageIndex + "   mMaxCount:"+mMaxCount);
		return mPageIndex;
	}

	/** 设置下一页 */
	public int setNextPage(){
		mPageIndex += 1;
		if (mPageIndex <= mMaxCount) {
			computeStartAndEndPosition(mPageIndex);
			notifyDataSetChanged();
		}else {
			mPageIndex = mMaxCount;
			Toast.makeText(mContext, R.string.last_page, 0).show();
		}
		Logger.i(TAG, isDebug, "mPageIndex:"+mPageIndex + "   mMaxCount:"+mMaxCount);
		return mPageIndex;
	}
	@Override
	public int getCount() {
		if (mVideoInfos == null || mVideoInfos.size() == 0) {
			return 0;
		}
		int count = mVideoInfos.size() > mPageCount ? mPageCount : mVideoInfos.size(); //小于一页的情况
		Logger.i(TAG, isDebug, "lastcount:"+mLastPageCount);
		return mPageIndex == mMaxCount ? mLastPageCount : count;//最后一页的情况
	}

	@Override
	public Object getItem(int position) {
		return mVideoInfos.get( ( (mPageIndex -1) * mPageCount) + position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int currentPosition = ( (mPageIndex -1) * mPageCount) + position;
		Logger.i(TAG, isDebug, "current position"+ currentPosition);
		VideoInfo sVideoInfo = mVideoInfos.get(currentPosition);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, mItem, null);
			holder.video_name = (TextView) convertView.findViewById(R.id.ibtn_main_gridview_item_video_name);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		bindView(sVideoInfo, holder);
		if (mPlayPosition == currentPosition) {
			holder.video_name.setSelected(true);
		}else {
			holder.video_name.setSelected(false);
		}
		return convertView;
	}
	
	private void bindView(VideoInfo sVideoInfo, ViewHolder holder) {
		holder.video_name.setText(sVideoInfo.getName());
	}

	private static final class ViewHolder {
		TextView video_name;
	}

}
