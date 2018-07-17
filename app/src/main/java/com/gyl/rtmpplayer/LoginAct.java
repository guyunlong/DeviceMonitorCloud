package com.gyl.rtmpplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.Util.Utils;
import com.gyl.view.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginAct  extends Activity implements OnClickListener{

	EditText usr;
	EditText pwd;

	private InputMethodManager _inputMethodManager;

	private ScrollView mScrollView;
	RelativeLayout lay_main;
	LoadingDialog lod = null;
	private Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);

		if(!Utils.hasConnection(this)){
			Utils.showDialog(this, getString(R.string.network_disable));

		}
		_inputMethodManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		usr = (EditText)findViewById(R.id.et_usr);
		pwd = (EditText)findViewById(R.id.et_pwd);
		mScrollView = (ScrollView) findViewById(R.id.scroll);
		lay_main =  (RelativeLayout) findViewById(R.id.lay_main);
		lay_main.setOnClickListener(this);


		SharedPreferences preferences = getSharedPreferences(Utils.PREFERENCES_NAME, Activity.MODE_PRIVATE);
		String usr_value = preferences.getString("usr", "");
		String pwd_value = preferences.getString("pwd", "");
		int id_value = preferences.getInt("usr_id", -1);
		usr.setText(usr_value);
		pwd.setText(pwd_value);
		usr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("log","scroll to down ---------------0");
				//mScrollView.fullScroll(View.FOCUS_DOWN); 


				// TODO Auto-generated method stub
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						//将ScrollView滚动到底  
						//mScrollView.fullScroll(View.FOCUS_DOWN); 
						scrollDown();
					}
				}, 100);
			}
		});
		pwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//mScrollView.fullScroll(View.FOCUS_DOWN); 
				Log.e("log","scroll to down ---------------1");
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						//将ScrollView滚动到底  
						//mScrollView.fullScroll(View.FOCUS_DOWN); 
						scrollDown();

					}
				}, 100);
			}
		});
		usr.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){//如果组件获得焦点
					scrollDown();
					Log.e("log","scroll to down ---------------2");
				}else{

				}
			}
		});
		pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){//如果组件获得焦点
					scrollDown();
					Log.e("log","scroll to down ---------------3");
				}else{

				}
			}
		});
	}
	void scrollDown()
	{
		Thread scrollThread = new Thread(){
			public void run(){
				try {
					sleep(500);
					LoginAct.this.runOnUiThread(new Runnable() {
						public void run() {
							//mScrollView.fullScroll(View.FOCUS_DOWN);
							Log.e("log","scroll to down ---------------5");
							mScrollView.scrollTo(0, mScrollView.getBottom());
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		scrollThread.start();
	}
	public void toWeb(View v){
		Intent intent=new Intent(LoginAct.this, webActivity.class);
		startActivity(intent);
	}
	public void login(View v) {

		final String username = usr.getText().toString().replace(" ", "");
		final String password = pwd.getText().toString().replace(" ", "");
		AsyncHttpClient client = new AsyncHttpClient();
		//http://my.dragra.com/svr_mob.php?act=cls_grx&id=2
		//app.php?act=log&usr=gyl&pwd=123456

		String url = "http://"+Utils.sIp+"/svr/app.php?act=log&usr="+username+"&pwd="+password;

		if (lod == null){
			lod = new LoadingDialog(this);
		}
		lod.dialogShow();

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
				// TODO Auto-generated method stub
				lod.dismiss();
				String res = new String(responseBody);
				try {
					JSONObject jo = new JSONObject(res);
					if(jo.getString("uid").equals("0")){
						Utils.showDialog(LoginAct.this, "login failed,please confirm user or password！");
					}
					else{

						SharedPreferences agPreferences = getSharedPreferences(Utils.PREFERENCES_NAME, Activity.MODE_PRIVATE);
						SharedPreferences.Editor editor = agPreferences.edit();

						editor.putString("usr", username);
						editor.putString("pwd", password);
						editor.commit();

						Intent intent=new Intent(LoginAct.this, BoxListActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("usr",username);
						bundle.putString("pwd", password);
						bundle.putString("uid", jo.getString("uid"));
						intent.putExtras(bundle);
						startActivity(intent);
						LoginAct.this.finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  byte[] responseBody, Throwable error) {
				Log.e("xx",error.toString());
				lod.dismiss();


				// TODO Auto-generated method stub

			}
			@Override
			public void onRetry(int retryNo) {

			}

		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.e("","xxx");
		_inputMethodManager.hideSoftInputFromWindow(LoginAct.this
						.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

	}

}
