package hk.com.dycx.iptv.adapter;

import java.util.List;

import hk.com.dycx.iptv.R;
import hk.com.dycx.iptv.bean.VideoInfo;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-1-31 下午5:43:09
 * @version 1.0
 */
public class VideoInfoAdapter extends BaseAdapter {
	private List<VideoInfo> mVideoInfos;
	private Context mContext;
	private int mItem;
	public VideoInfoAdapter(Context context, List<VideoInfo> infos, final int item) {
		mContext = context;
		mItem = item;
		mVideoInfos = infos;
	}
	@Override
	public int getCount() {
		if (mVideoInfos == null || mVideoInfos.size() == 0) {
			return 0;
		}
		return mVideoInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return mVideoInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		VideoInfo sVideoInfo = mVideoInfos.get(position);
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
		return convertView;
	}
	
	private void bindView(VideoInfo sVideoInfo, ViewHolder holder) {
		holder.video_name.setText(sVideoInfo.getName());
	}

	private static final class ViewHolder {
		TextView video_name;
	}

}
