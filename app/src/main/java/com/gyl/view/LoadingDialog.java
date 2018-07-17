package com.gyl.view;


import com.gyl.rtmpplayer.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;
public class LoadingDialog {
    private Dialog loadingDialog;
    private TextView textView;
    public LoadingDialog(Context context){
        loadingDialog=new Dialog(context,R.style.myDialogTheme);
        loadingDialog.setContentView(R.layout.view_loadingdlg);
        textView= (TextView) loadingDialog.findViewById(R.id.loading_message);
    }

    /**
     * 设置消息显示
     * @param message
     */
    public void setMessage(String message) {
        textView.setText(message);
    }
    /**
     * 关闭对话框
     */
    public void dismiss(){
        loadingDialog.dismiss();
    }
    /**
     * 显示对话框
     */
    public void dialogShow(){
        loadingDialog.show();
    }
}
