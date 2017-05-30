package com.gzlk.android.isp.fragment.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.activity.Activity;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>活动详情页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/30 18:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/30 18:14 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityDetailsSingleFragment extends BaseTransparentSupportFragment {

    public static ActivityDetailsSingleFragment newInstance(String params) {
        ActivityDetailsSingleFragment adsf = new ActivityDetailsSingleFragment();
        Bundle bundle = new Bundle();
        // 活动的id
        bundle.putString(PARAM_QUERY_ID, params);
        adsf.setArguments(bundle);
        return adsf;
    }

    // ui
    @ViewId(R.id.ui_activity_details_image)
    private ImageDisplayer imageDisplayer;
    @ViewId(R.id.ui_activity_details_title)
    private View titleView;
    @ViewId(R.id.ui_activity_details_time)
    private View timeView;
    @ViewId(R.id.ui_activity_details_address)
    private View addressView;
    @ViewId(R.id.ui_activity_details_close_button)
    private CorneredButton endButton;
    // holder
    private SimpleClickableViewHolder titleHolder, timeHolder, addressHolder;

    private String[] items;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_details_details;
    }

    @Override
    public void doingInResume() {
        initializeHolder();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_activity_details_close_button})
    private void elementClick(View view) {
        endButton.setEnabled(false);
        // 结束该活动
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    endButton.setEnabled(false);
                    endButton.setText(R.string.ui_activity_details_ended_activity);
                }
            }
        }).end(mQueryId);
    }

    private void initializeHolder() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_details_items);
        }
        if (null == titleHolder) {
            titleHolder = new SimpleClickableViewHolder(titleView, this);
        }
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
        }
        if (null == addressHolder) {
            addressHolder = new SimpleClickableViewHolder(addressView, this);
            imageDisplayer.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            loadingActivity();
        }
    }

    private void display(Activity activity) {
        imageDisplayer.displayImage(activity.getImg(), getScreenWidth(), getDimension(R.dimen.ui_static_dp_200), false, false);
        titleHolder.showContent(format(items[0], activity.getTitle()));
        String time = Utils.format(activity.getCreateDate(), StringHelper.getString(R.string.ui_base_text_date_time_format), StringHelper.getString(R.string.ui_base_text_date_format_chs_min));
        timeHolder.showContent(format(items[1], time));
        addressHolder.showContent(format(items[2], activity.getContent()));
        endButton.setEnabled(activity.getStatus() == Activity.Status.ACTIVE);
        endButton.setText(activity.getStatus() == Activity.Status.ACTIVE ? R.string.ui_activity_details_button_close_text : R.string.ui_activity_details_ended_activity);
    }

    private void loadingActivity() {
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    if (null != activity) {
                        display(activity);
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_activity_details_invalid_parameter);
                    }
                }
            }
        }).find(mQueryId);
    }
}
