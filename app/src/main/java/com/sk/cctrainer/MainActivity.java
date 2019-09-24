package com.sk.cctrainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCUtil;
import com.sk.cctrainer.event.RefreshEvent;
import com.sk.demo_common_base.base.BaseActivity;
import com.sk.demo_common_base.utils.RxBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity {

    @BindView(R.id.go_login_text)
    AppCompatTextView goLoginText;
    @BindView(R.id.title_text)
    AppCompatTextView titleText;

    private Disposable refreshDisposable;


    @Override
    public int getLaouytId() {
        return R.layout.activity_main;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {

        bindRxBusEvent();
    }

    @OnClick(R.id.go_login_text)
    public void goLogin() {
        CC.obtainBuilder("ComponentLogin")
                .setActionName("showLoginActivity")
                .build()
                .call();
    }

    private void bindRxBusEvent() {
        refreshDisposable = RxBus.getDefault().register(RefreshEvent.class, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                RefreshEvent refreshEvent = (RefreshEvent) o;
                titleText.setText(refreshEvent.getText() + "，Hello World!");
            }
        });
    }
}
