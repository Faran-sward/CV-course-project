package com.example.myapplication.base;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
/**
 * Activity的基类
 *
 */

public abstract class BaseActivity extends AppCompatActivity {
    private ProgressDialog dialog;

    protected View statusBarView;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceId());
        initView();
        initData();
        initListener();

    }



    /**
     * 初始化监听
     */
    protected void initListener() {};

    /**
     * 设置布局文件
     *
     * @return
     */
    protected abstract int getResourceId();

    /**
     * 初始化控件
     */
    protected void initView() {}

    /**
     * 初始化数据
     */
    protected void initData() {}


    /**
     * 显示dialog
     */
    protected void showDialog(String s) {
        dialog = new ProgressDialog(this);
        dialog.setMessage(s);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }



    /**
     * dialog停止
     */
    protected void stopDialog() {
        if (null != dialog ) {
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDialog();
    }

    public void onBack(View view){
        finish();
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
        return ;
    }



}
