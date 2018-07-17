package com.gyl.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import com.gyl.rtmpplayer.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BoxAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;

	private final int TYPE_COUNT=1;
	private final int FIRST_TYPE=0;
	private int currentType;
	String inf;
	JSONObject jo;
	JSONArray ja ;
	Context context;
	public  ArrayList<String> resortarray =new ArrayList<String>();
	public BoxAdapter(Context con, String info) {
		context = con;
		mLayoutInflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		inf=info;
		try {
			jo = new JSONObject(inf);
			ja = jo.getJSONArray("itm");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@Override
	public int getCount() {
		return ja.length();
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	///////////////////////////////////////////////////////  
	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {

		return FIRST_TYPE;
	}
	///////////////////////////////////////////////////////  

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View firstItemView = null;
		View othersItemView=null;
		//获取到当前位置所对应的Type  
		currentType= getItemViewType(position);
		System.out.println("type="+currentType);
		if (currentType== FIRST_TYPE) {
			firstItemView = convertView;
			ViewHolder firstItemViewHolder=null;
			if (firstItemView==null) {
				System.out.println("firstItemView==null ");
				firstItemView = mLayoutInflater.inflate(R.layout.list_box,null);
				firstItemViewHolder=new ViewHolder();
				firstItemViewHolder.tit=(TextView) firstItemView.findViewById(R.id.tx_tit);
				firstItemViewHolder.ste = (TextView) firstItemView.findViewById(R.id.tx_ste);
				firstItemView.setTag(firstItemViewHolder);

			} else {
				firstItemViewHolder=(ViewHolder) firstItemView.getTag();
			}
			if (firstItemViewHolder.tit!=null) {

				try {
					JSONObject jo_tmp =  ja.getJSONObject(position);
					firstItemViewHolder.tit.setText(jo_tmp.getString("tit"));
					firstItemViewHolder.ste.setText(jo_tmp.getString("ste").equals("1")?context.getString(R.string.on):context.getString(R.string.off));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			convertView=firstItemView;

		}
		return convertView;
	}

	//第一个Item的ViewHolder  
	private class ViewHolder{
		TextView tit; //节点名称 
		TextView ste;//在线状态
	}


}  
