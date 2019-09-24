package com.sk.demo_common_base.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.sk.demo_common_base.Constant;
import com.sk.demo_common_base.utils.ActivityManager;
import com.sk.demo_common_base.utils.AppUtils;
import com.sk.demo_common_base.utils.SoftKeyBoardListener;
import com.sk.demo_common_base.utils.TransformMvpUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * @author sk
 */
public abstract class BaseActivity<T extends BasePresenter, E extends BaseModel> extends AppCompatActivity {

    public T mPresenter;
    public E mModel;
    public boolean isShowingKeyboard = false;
    protected String tag = getClass().getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppUtils.setCustomDensity(this, getApplication());
        super.onCreate(savedInstanceState);
        setContentView(getLaouytId());
        ButterKnife.bind(this);
        mPresenter = TransformMvpUtils.getT(this, 0);
        mModel = TransformMvpUtils.getT(this, 1);
        if (mPresenter != null) {
            mPresenter.mContext = this;
        }
        this.initPresenter();
        this.initView();
        ActivityManager.getInstance().addActivity(this);

        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {

            @Override
            public void keyBoardShow(int height) {
                isShowingKeyboard = true;
            }

            @Override
            public void keyBoardHide(int height) {
                isShowingKeyboard = false;
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        //友盟埋点
        MobclickAgent.onResume(this);

        Glide.with(this).resumeRequests();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟埋点
        MobclickAgent.onPause(this);

        Glide.with(this).pauseRequests();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.onDestroy();
        }

        Glide.get(this).clearMemory();
    }

    /**
     * 获取View布局
     *
     * @return
     */
    public abstract int getLaouytId();

    /**
     * 简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
     */
    public abstract void initPresenter();

    //初始化view
    public abstract void initView();


    /**
     * 软盘弹隐切换
     */
    public void showSoftDisk() {
        InputMethodManager imms = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imms.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    //判断是否有SD卡
    protected boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * don't close current activity
     *
     * @param clz
     * @param ex
     */
    public void gotoActivity(Class<?> clz, Bundle ex) {
        gotoActivity(clz, false, ex);
    }

    public void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity) {
        gotoActivity(clz, isCloseCurrentActivity, null);
    }

    /**
     * 打开一个Activity 默认 不关闭当前activity
     *
     * @param clz
     */
    public void gotoActivity(Class<?> clz) {
        gotoActivity(clz, false, null);
    }

    public void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity, Bundle ex) {
        Intent intent = new Intent(this, clz);
        if (ex != null) {
            intent.putExtras(ex);
        }
        startActivity(intent);
        if (isCloseCurrentActivity) {
            finish();
        }
    }

}

