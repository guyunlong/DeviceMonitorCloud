package com.gyl.rtmpplayer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.Util.Utils;
import com.gyl.adapter.BoxAdapter;
import com.gyl.view.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BoxListActivity extends Activity{
	ListView list;
	String inf;
	String usr;
	String pwd;
	String uid;
	LoadingDialog lod = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_boxlist);
		
		
		
		Bundle bundle = this.getIntent().getExtras();
		usr = bundle.getString("usr");
		pwd = bundle.getString("pwd");
		uid = bundle.getString("uid");
		list =(ListView)findViewById(R.id.list); 

		//list.setAdapter(new LearnAdapter(this,));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
				Log.i("xxx", "onListItemClick call shod:"+position);

				try {
					JSONObject jo = new JSONObject(inf);
					JSONArray ja = jo.getJSONArray("itm");
					JSONObject node = ja.getJSONObject(position);
					if(node.getString("ste").equals("1")){
						Intent intent=new Intent(BoxListActivity.this, MainActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("sip", node.getString("sip"));
						bundle.putString("bid", node.getString("bid"));
						bundle.putString("uid", uid);
						intent.putExtras(bundle);
						startActivity(intent);
					}
					else{
						Utils.showDialog(BoxListActivity.this,getString(R.string.offline));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		loadvalue();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

	}
	void loadvalue(){
		if (lod == null){
			lod = new LoadingDialog(this);
		}
		lod.dialogShow();
		AsyncHttpClient client = new AsyncHttpClient();
		//http://my.dragra.com/svr_mob.php?act=cls_grx&id=2
		//http://box.carvedge.com/svr/app.php?act=lst&usr=gyl&pwd=123456
		String url = "http://"+Utils.sIp+"/svr/app.php?act=lst&usr="+usr+"&pwd="+pwd;
		
		Log.e("xx",url); 
		client.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				Log.e("xx","onstart");
				
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				lod.dismiss();
				inf  = new String(responseBody);
				list.setAdapter(new BoxAdapter(BoxListActivity.this,inf));
				
				
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				Log.e("xx",error.toString());
				lod.dismiss();
				Utils.showToast(BoxListActivity.this, getString(R.string.load_failed));
				
				// TODO Auto-generated method stub
				
			}
			@Override
            public void onRetry(int retryNo) {
              
            }
		 
		});
	}
	public void  refresh(View v){
		loadvalue();
	}
	public void back(View v){
		this.finish();
	}
	
}


