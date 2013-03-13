package hk.com.dycx.iptv.adapter;

import hk.com.dycx.iptv.R;
import hk.com.dycx.iptv.bean.VideoGroupInfo;
import hk.com.dycx.iptv.bean.VideoInfo;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-3-12 下午4:37:26
 * @version 1.0
 */
public class VideoGroupAdapter extends BaseAdapter {
	/** 上下文 */
	private Context mContext;
	
	/** 显示的分组 */
	private List<VideoGroupInfo> mGroupInfos;
	
	/** 子view 的 id */
	private int mItem;
	public VideoGroupAdapter(Context context, List<VideoGroupInfo> groupInfos, int item){
		mContext = context;
		mGroupInfos = groupInfos;
		mItem = item;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mGroupInfos == null ) {
			return 0;
		}
		return mGroupInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return mGroupInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setVideoInfos(List<VideoGroupInfo> groupInfos){
		mGroupInfos = groupInfos;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		VideoGroupInfo videoGroupInfo = mGroupInfos.get(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, mItem, null);
			holder.groupName = (TextView) convertView.findViewById(R.id.ibtn_main_gridview_item_video_name);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		bindView(videoGroupInfo, holder);
		return convertView;
	}
	
	private void bindView(VideoGroupInfo videoGroupInfo, ViewHolder holder) {
		holder.groupName.setText(videoGroupInfo.getGroupName());
	}
	
	private static final class ViewHolder {
		TextView groupName;
	}
}
