package com.gzlk.android.isp.fragment.login;

import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.SystemRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.model.user.User;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CleanableEditText;
import com.hlk.hlklib.lib.view.ClearEditText;

/**
 * <b>功能描述：</b>注册新账号页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/11 19:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/11 19:54 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SignUpFragment extends BaseVerifyFragment {

    public static SignUpFragment newInstance(String params) {
        SignUpFragment suf = new SignUpFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_VERIFY_TYPE, Integer.valueOf(strings[0]));
        bundle.putString(PARAM_VERIFY_PHONE, strings[1]);
        bundle.putString(PARAM_VERIFY_CODE, strings[2]);
        suf.setArguments(bundle);
        return suf;
    }

    @ViewId(R.id.ui_sign_up_name)
    private ClearEditText nameText;
    @ViewId(R.id.ui_sign_up_password)
    private CleanableEditText passwordText;

    @Override
    public int getLayout() {
        return R.layout.fragment_verify_sign_up;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_sign_up_fragment_title);
        setLeftText(R.string.ui_text_reset_password_fragment_title_left_text);
        if (verifyType != VT_SIGN_UP) {
            ToastHelper.make(Activity()).showMsg(R.string.ui_text_sign_up_incorrect_params);
            finishToSignIn();
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_sign_up_finish})
    private void elementClick(View view) {
        String name = nameText.getValue();
        if (StringHelper.isEmpty(name)) {
            ToastHelper.make().showMsg(R.string.ui_text_sign_up_name_value_incorrect);
            return;
        }
        String password = passwordText.getText().toString();
        if (!passwordText.verifyValue()) {
            password = "";
        }
        if (StringHelper.isEmpty(password)) {
            ToastHelper.make().showMsg(R.string.ui_text_sign_up_password_value_incorrect);
            return;
        }
        trySignUp(name, password);
    }

    private void trySignUp(String name, String password) {
        SystemRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                    // 转到登录
                    finishToSignIn();
                }
            }
        }).signUp(verifyPhone, "", password, name);
    }
}
