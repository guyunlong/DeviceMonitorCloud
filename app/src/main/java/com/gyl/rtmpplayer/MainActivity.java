package com.gyl.rtmpplayer;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.Util.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnBufferingUpdateListener;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;
import com.pili.pldroid.player.widget.PLVideoView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements OnTouchListener,OnClickListener{

	String TAG = "MainActivityTag";

	//private MediaController mMediaController;
	private PLVideoView mVideoView;
	private View clickView;
	private ProgressBar probar;
	String rtmpUrl ;


	String sip;
	String bid;
	ImageButton ptz1;
	ImageButton ptz2;
	ImageButton ptz3;
	ImageButton ptz4;
	ImageButton ptz5;
	ImageButton ptz6;
	ImageButton imgbtn_preset;
	ImageButton exitbutton;
	TextView tx_resolution;
	TableLayout ctlBottom;
	TableLayout ctlTop;
	//定时器
	public int TIME = 4000;
	boolean entry = false;//是否进入当前页面

	boolean showCtl = false;
	JSONArray ja_presets ;
	int qxd;//1:cif 2:d1 3:720p

	String uid;

	private CharSequence[] items = new CharSequence[3];

	Handler handler_vid = new Handler();
	Runnable runnable_vid = new Runnable() {
		@Override
		public void run() {
		if (mVideoView != null ) {
			mVideoView.start();
		}

		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//setContentView(R.layout.activity_main);
		LayoutInflater infalter = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=infalter.inflate(R.layout.activity_main, null);

		setContentView(view);
		Bundle bundle = this.getIntent().getExtras();
		sip = bundle.getString("sip");
		bid = bundle.getString("bid");
		uid = bundle.getString("uid");
		rtmpUrl = "rtmp://"+sip+":1935/live/"+bid;
		initViews();
		items[0] =getString(R.string.fluency);
		items[1] =getString(R.string.Balanced);




		callVideo();//立刻打开视频指令 通过 cts发送指令
		heartBeat();
		handler.postDelayed(runnable, TIME);

		redesign();
	}
	public void initViews(){
		ptz1 =  (ImageButton)findViewById(R.id.ptz1);
		ptz2 =  (ImageButton)findViewById(R.id.ptz2);
		ptz3 =  (ImageButton)findViewById(R.id.ptz3);
		ptz4 =  (ImageButton)findViewById(R.id.ptz4);
		ptz5 =  (ImageButton)findViewById(R.id.ptz5);
		ptz6 =  (ImageButton)findViewById(R.id.ptz6);
		imgbtn_preset =  (ImageButton)findViewById(R.id.preset);
		exitbutton =  (ImageButton)findViewById(R.id.exitbutton);

		tx_resolution= (TextView)findViewById(R.id.resolution);
		ctlBottom = (TableLayout)findViewById(R.id.ctlBottom);
		ctlTop = (TableLayout)findViewById(R.id.ctltop);
		showCtl=true;

		if (uid.equals("120")){
			//特殊对待，如果用户的uid为120，不给控制
			ctlBottom.setVisibility(View.GONE);
		}

		ptz1.setOnTouchListener(this);
		ptz2.setOnTouchListener(this);
		ptz3.setOnTouchListener(this);
		ptz4.setOnTouchListener(this);
		ptz5.setOnTouchListener(this);
		ptz6.setOnTouchListener(this);

		//关于视频内容
		//mMediaController = new MediaController(this, false, false);
		mVideoView = (PLVideoView) findViewById(R.id.video_view);
		mStatInfoTextView = (TextView) findViewById(R.id.mStatInfoTextView);
		setMediaPlayer();
		clickView = findViewById(R.id.clickview);
		//mMediaController.setMediaPlayer(mVideoView);
		//mVideoView.setMediaController(mMediaController);






		//mVideoView.requestFocus();
		clickView.setOnClickListener(this);



	}

	void setMediaPlayer(){

		mVideoView.setVideoPath(rtmpUrl);





        // 1 -> hw codec enable, 0 -> disable [recommended]

        AVOptions options = new AVOptions();
        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, 0);
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);

//        options.setString(AVOptions.KEY_DNS_SERVER, "127.0.0.1");
        options.setInteger(AVOptions.KEY_LOG_LEVEL, 5);


//        boolean vcallback = getIntent().getBooleanExtra("video-data-callback", false);
//        if (vcallback) {
//            options.setInteger(AVOptions.KEY_VIDEO_DATA_CALLBACK, 1);
//        }
//        boolean acallback = getIntent().getBooleanExtra("audio-data-callback", false);
//        if (acallback) {
//            options.setInteger(AVOptions.KEY_AUDIO_DATA_CALLBACK, 1);
//        }
//        if (!mIsLiveStreaming) {
//            int startPos = getIntent().getIntExtra("start-pos", 0);
//            options.setInteger(AVOptions.KEY_START_POSITION, startPos * 1000);
//        }
        mVideoView.setAVOptions(options);





		// Set some listeners
		mVideoView.setOnInfoListener(mOnInfoListener);
		mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
		mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
		mVideoView.setOnCompletionListener(mOnCompletionListener);
		mVideoView.setOnErrorListener(mOnErrorListener);

		probar = (ProgressBar) findViewById(R.id.probar);
		probar.setVisibility(View.VISIBLE);

	}


	@Override
	public void onResume() {
		super.onResume();
		entry = true;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		if (mVideoView != null ) {
//			mVideoView.start();
//		}
	}
	@Override
	public void onPause() {
		entry = false;
		if (mVideoView != null) {
			mVideoView.pause();
		}
		super.onPause();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//预置位

		menu.setHeaderTitle("Preset");
		for(int i=0;i<ja_presets.length();i++){
			try {
				JSONObject tmp = ja_presets.getJSONObject(i);
				menu.add(0, i, 0,tmp.getString("tit") );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int i=item.getItemId();
		try {
			JSONObject tmp = ja_presets.getJSONObject(i);
			toPreset(tmp.getString("nbh"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// handler自带方法实现定时器  

			heartBeat();
			if(entry)
			handler.postDelayed(runnable, TIME); //每隔4s执行

		}
	};
	/*
	 * 重新布局，针对视频控件大小
	 */
	void redesign(){
		WindowManager manage=this.getWindowManager();
		Display display=manage.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;
		int width = size.x;
		RelativeLayout.LayoutParams pam = new RelativeLayout.LayoutParams(height*5/4,height);
		pam.addRule(RelativeLayout.CENTER_HORIZONTAL);



		int wid = dip2px(this,44);
		TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(wid, wid);
		layoutParams1.leftMargin = (width-wid*8)/7;
		ptz1.setLayoutParams(layoutParams1);
		ptz2.setLayoutParams(layoutParams1);
		ptz3.setLayoutParams(layoutParams1);
		ptz4.setLayoutParams(layoutParams1);
		ptz5.setLayoutParams(layoutParams1);
		ptz6.setLayoutParams(layoutParams1);
		ptz6.setLayoutParams(layoutParams1);
		imgbtn_preset.setLayoutParams(layoutParams1);
		int wid_resolution = dip2px(this,100);
		int hei_resolution = dip2px(this,30);
		int hei_margin = dip2px(this,7);
		TableRow.LayoutParams layoutParams2 = new TableRow.LayoutParams(wid_resolution, hei_resolution);
		layoutParams2.leftMargin=(width-wid*8)/7;
		layoutParams2.topMargin = hei_margin;
		tx_resolution.setLayoutParams(layoutParams2);

		TableRow.LayoutParams layoutParams3 = new TableRow.LayoutParams(wid, wid);
		layoutParams3.leftMargin = width-(width-wid*8)/7-wid-(width-wid*8)/7-wid_resolution;
		exitbutton.setLayoutParams(layoutParams3);

	}
	public  int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{


			Log.e("MainActivity","stop play");
			entry = false;
			this.finish();
		}
		if (keyCode == KeyEvent.KEYCODE_MENU){

		}
		return false;
	}



	void ptz_control(int type){
		AsyncHttpClient client = new AsyncHttpClient();
		//http://222.186.24.219:1936/svr.php?act=cts&bid=1436196841&pm1=2&pm2=4
		String url = "";
		if (Utils.sIp.equals("ivs2.carvedge.com")){
			url="http://"+sip+":1936/svr.php?act=cts&bid="+bid+"&pm1=2&pm2="+type;
		}
		else{
			if (0 == type){
				url="http://"+sip+":1936/svr.php?act=cts&bid="+bid+"&pm1=2&pm2="+type;
			}
			else{
				url="http://"+sip+":1936/svr.php?act=cts&bid="+bid+"&pm1=2&pm2="+(type*100+2);
			}
		}




		Log.e(TAG,url);
		client.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				Log.e("xx", "onstart");

			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
								  byte[] responseBody) {
				// TODO Auto-generated method stub
				Log.e("xx", new String(responseBody));

			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  byte[] responseBody, Throwable error) {
				Log.e("xx", error.toString());


			}

			@Override
			public void onRetry(int retryNo) {

			}

		});
	}
	void toPreset(String nbh){
		AsyncHttpClient client = new AsyncHttpClient();
		//http://222.186.24.219:1936/svr.php?act=cts&bid=1436196841&pm1=2&pm2=4
		String url = "";

		url ="http://"+sip+":1936/svr.php?act=cts&bid="+bid+"&pm1=4&pm2="+nbh;


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
				Log.e("xx", new String(responseBody));

			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  byte[] responseBody, Throwable error) {
				Log.e("xx",error.toString());


			}
			@Override
			public void onRetry(int retryNo) {

			}

		});
	}
	void callVideo(){
		AsyncHttpClient client = new AsyncHttpClient();
		//http://222.186.24.219:1936/svr.php?act=cts&bid=1436196841&pm1=2&pm2=4
		String url = "http://"+sip+":1936/svr.php?act=cts&bid="+bid+"&pm1=1&pm2=1";


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

				//1秒后，打开视频
				handler_vid.postDelayed(runnable_vid,1500);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  byte[] responseBody, Throwable error) {
				Log.e("xx",error.toString());


			}
			@Override
			public void onRetry(int retryNo) {

			}

		});
	}
	//发送查看视频心跳
	void heartBeat(){
		AsyncHttpClient client = new AsyncHttpClient();

		String url ="http://"+Utils.sIp+"/svr/fls.php?act=hbt&bid="+bid;


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
				Log.e("xx",new String(responseBody));


			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  byte[] responseBody, Throwable error) {
				Log.e("xx",error.toString());


			}
			@Override
			public void onRetry(int retryNo) {

			}

		});
	}
	//让前端立刻获取心跳
	void setImdHeartBeat(){
		AsyncHttpClient client = new AsyncHttpClient();

		String url ="http://"+sip+":1936/svr.php?act=cts&bid="+bid+"&pm1=6";


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
				Log.e("xx", new String(responseBody));




			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  byte[] responseBody, Throwable error) {
				Log.e("xx",error.toString());


			}
			@Override
			public void onRetry(int retryNo) {

			}

		});
	}
	void setResolution(int tpe){
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "";
		url ="http://box.carvedge.com/svr/fls.php?act=qxd&bid="+bid+"&qxd="+tpe;
		tx_resolution.setText(items[tpe-1]);


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
				setImdHeartBeat();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  byte[] responseBody, Throwable error) {
				Log.e("xx",error.toString());


			}
			@Override
			public void onRetry(int retryNo) {

			}

		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//do something
			if(v == ptz1){
				ptz_control(1);
			}
			if(v == ptz2){
				ptz_control(2);
			}
			if(v == ptz3){
				ptz_control(3);
			}
			if(v == ptz4){
				ptz_control(4);
			}
			if(v == ptz5){
				ptz_control(5);
			}
			if(v == ptz6){
				ptz_control(6);
			}
		}else if (event.getAction() == MotionEvent.ACTION_UP){
			// do something
			ptz_control(0);
		}

		return false;
	};
	public void onResolution(View v){
		//设置分辨率
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("Set Resolution")
				.setSingleChoiceItems(items, qxd-1, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						qxd = item+1;
						setResolution(qxd);
						dialog.cancel();
					}
				})
				.show();
	}
	public void onClose(View v){

		Log.e("MainActivity","stop play");
		entry = false;
		this.finish();
	}


	public void onPreset(View v){
		//置预置位
		openContextMenu(v);

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.e(TAG,"video view click");
		if (v.getId() == R.id.clickview){
			showCtl = !showCtl;
			if(showCtl){
				ctlTop.startAnimation(AnimationUtils.loadAnimation(this, R.anim.show_top));
				ctlBottom.startAnimation(AnimationUtils.loadAnimation(this, R.anim.show_btm));
				if (!uid.equals("120")){
					ctlBottom.setVisibility(View.VISIBLE);
				}
				ctlTop.setVisibility(View.VISIBLE);
			}
			else{
				ctlTop.startAnimation(AnimationUtils.loadAnimation(this, R.anim.gone_top));
				ctlBottom.startAnimation(AnimationUtils.loadAnimation(this, R.anim.gone_btm));

				ctlTop.setVisibility(View.GONE);
				ctlBottom.setVisibility(View.GONE);
			}
		}

	}


    private PLOnInfoListener mOnInfoListener = new PLOnInfoListener() {
        @Override
        public void onInfo(int what, int extra) {
            Log.i(TAG, "OnInfo, what = " + what + ", extra = " + extra);

            switch (what) {
                case PLOnInfoListener.MEDIA_INFO_BUFFERING_START:
                    break;
                case PLOnInfoListener.MEDIA_INFO_BUFFERING_END:
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_RENDERING_START:
                    //showToastTips( "first video render time: " + extra + "ms");
                    probar.setVisibility(View.GONE);
                    break;
                case PLOnInfoListener.MEDIA_INFO_AUDIO_RENDERING_START:
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_FRAME_RENDERING:
                    Log.i(TAG, "video frame rendering, ts = " + extra);
                    break;
                case PLOnInfoListener.MEDIA_INFO_AUDIO_FRAME_RENDERING:
                    Log.i(TAG, "audio frame rendering, ts = " + extra);
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_GOP_TIME:
                    Log.i(TAG, "Gop Time: " + extra);
                    break;
                case PLOnInfoListener.MEDIA_INFO_SWITCHING_SW_DECODE:
                    Log.i(TAG, "Hardware decoding failure, switching software decoding!");
                    break;
                case PLOnInfoListener.MEDIA_INFO_METADATA:
                    Log.i(TAG, mVideoView.getMetadata().toString());
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_BITRATE:
                case PLOnInfoListener.MEDIA_INFO_VIDEO_FPS:
                    updateStatInfo();
                    break;
                case PLOnInfoListener.MEDIA_INFO_CONNECTED:
                    Log.i(TAG, "Connected !");
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                    Log.i(TAG, "Rotation changed: " + extra);
                default:
                    break;
            }
        }
    };




    private PLOnErrorListener mOnErrorListener = new PLOnErrorListener() {
        @Override
        public boolean onError(int errorCode) {
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLOnErrorListener.ERROR_CODE_IO_ERROR:
                    /**
                     * SDK will do reconnecting automatically
                     */
                    Log.e(TAG, "IO Error!");
                    return false;
                case PLOnErrorListener.ERROR_CODE_OPEN_FAILED:
                    showToastTips( "failed to open player !");
                    break;
                case PLOnErrorListener.ERROR_CODE_SEEK_FAILED:
                    showToastTips("failed to seek !");
                    break;
                default:
                    showToastTips( "unknown error !");
                    break;
            }
            finish();
            return true;
        }
    };




    private PLOnCompletionListener mOnCompletionListener = new PLOnCompletionListener() {
        @Override
        public void onCompletion() {
            Log.i(TAG, "Play Completed !");

        }
    };
    private PLOnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLOnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(int width, int height) {
            Log.i(TAG, "onVideoSizeChanged: width = " + width + ", height = " + height);
        }
    };
    private PLOnBufferingUpdateListener mOnBufferingUpdateListener = new PLOnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(int precent) {
            Log.i(TAG, "onBufferingUpdate: " + precent);
        }
    };

	private void showToastTips(final String tips) {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mToast != null) {
					mToast.cancel();
				}
				mToast = Toast.makeText(MainActivity.this, tips, Toast.LENGTH_SHORT);
				mToast.show();
			}
		});
	}

	private void updateStatInfo() {
		long bitrate = mVideoView.getVideoBitrate() / 1024;
		final String stat = bitrate + "kbps, " + mVideoView.getVideoFps() + "fps";
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mStatInfoTextView.setText(stat);
			}
		});
	}

	TextView mStatInfoTextView;
	private Toast mToast = null;

}

