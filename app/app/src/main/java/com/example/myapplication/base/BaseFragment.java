package com.example.myapplication.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


/**
 * Fragment的基类
 * 1.规范代码结构
 * 2.精简代码
 *
 * @author wangdh
 */
public abstract class BaseFragment extends Fragment {
    protected View statusBarView;
    protected Toolbar toolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), getLayoutResID(), null);
        initView(view);
        initData();
        initListener();
        fitsLayoutOverlap();
        return view;
    }

    /**
     * 获取Activity显示的布局：
     *
     * @return：布局id
     */
    public abstract int getLayoutResID();

    /**
     * 初始化View
     */
    public void initView(View view){

    }

    /**
     * 初始化监听：点击监听、设置适配器、设置条目点击监听
     */
    public void initListener() {

    }

    /**
     * 初始化数据
     * @param
     */
    public void initData() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();



    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    private void fitsLayoutOverlap() {

    }

}
