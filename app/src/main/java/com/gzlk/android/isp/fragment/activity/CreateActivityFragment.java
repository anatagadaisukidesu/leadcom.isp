package com.gzlk.android.isp.fragment.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.organization.OrganizationContactPickFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.common.SimpleInputableViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.organization.Member;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * <b>功能描述：</b>新增活动<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/27 10:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/27 10:00 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CreateActivityFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_GROUP = "caf_group_id_params";
    private static final String PARAM_MEMBERS = "caf_members";
    private static final String PARAM_COVER = "caf_cover";
    private static final String PARAM_LABEL = "caf_label";
    private static final String PARAM_HAPPEN = "caf_happen_date";
    private static final String PARAM_TITLE = "caf_title";
    private static final String PARAM_ADDR = "caf_address";
    private static final String PARAM_CONTENT = "caf_content";
    private static final String PARAM_OPEN_STATUS = "caf_open_status";

    public static CreateActivityFragment newInstance(String params) {
        CreateActivityFragment caf = new CreateActivityFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        bundle.putString(PARAM_GROUP, strings[1]);
        caf.setArguments(bundle);
        return caf;
    }

    private String memberJson = "[]", labelJson = "[]";
    private String cover = "", title = "", address = "", content = "";
    // 默认只向组织内部开放
    private int openStatus = Activity.OpenStatus.NONE;
    private static final int REQ_MEMBER = ACTIVITY_BASE_REQUEST + 10;
    private static final int REQ_COVER = REQ_MEMBER + 1;
    private static final int REQ_LABEL = REQ_COVER + 1;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupId = bundle.getString(PARAM_GROUP, "");
        memberJson = bundle.getString(PARAM_MEMBERS, "[]");
        resetMembers();
        cover = bundle.getString(PARAM_COVER, "");
        labelJson = bundle.getString(PARAM_LABEL, "[]");
        resetLabels();
        happenDate = bundle.getString(PARAM_HAPPEN, "");
        title = bundle.getString(PARAM_TITLE, "");
        address = bundle.getString(PARAM_ADDR, "");
        content = bundle.getString(PARAM_CONTENT, "");
        openStatus = bundle.getInt(PARAM_OPEN_STATUS, Activity.OpenStatus.NONE);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_GROUP, mGroupId);
        bundle.putString(PARAM_MEMBERS, memberJson);
        bundle.putString(PARAM_COVER, cover);
        bundle.putString(PARAM_LABEL, labelJson);
        bundle.putString(PARAM_HAPPEN, happenDate);
        title = titleHolder.getValue();
        bundle.putString(PARAM_TITLE, title);
        address = addressHolder.getValue();
        bundle.putString(PARAM_ADDR, address);
        content = contentView.getValue();
        bundle.putString(PARAM_CONTENT, content);
        bundle.putInt(PARAM_OPEN_STATUS, openStatus);
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQ_MEMBER:
                // 活动成员选择返回了
                memberJson = getResultedData(data);
                resetMembers();
                break;
            case REQ_COVER:
                // 封面选择了
                cover = getResultedData(data);
                if (cover.charAt(0) == '/') {
                    // 如果照片选择的是本地图片则将其放入待上传列表
                    getWaitingForUploadFiles().clear();
                    getWaitingForUploadFiles().add(cover);
                }
                break;
            case REQ_LABEL:
                labelJson = getResultedData(data);
                resetLabels();
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private void resetMembers() {
        selectedMembers = Json.gson().fromJson(memberJson, new TypeToken<ArrayList<Member>>() {
        }.getType());
        if (null == selectedMembers) {
            selectedMembers = new ArrayList<>();
        }
    }

    private void resetLabels() {
        labelsId = Json.gson().fromJson(labelJson, new TypeToken<ArrayList<String>>() {
        }.getType());
        if (null == labelsId) {
            labelsId = new ArrayList<>();
        }
    }

    // view
    @ViewId(R.id.ui_activity_creator_cover)
    private View coverView;
    @ViewId(R.id.ui_activity_creator_title)
    private View titleView;
    @ViewId(R.id.ui_activity_creator_time)
    private View timeView;
    @ViewId(R.id.ui_activity_creator_address)
    private View addressView;
    @ViewId(R.id.ui_activity_creator_type)
    private View typeView;
    @ViewId(R.id.ui_activity_creator_privacy)
    private View privacyView;
    @ViewId(R.id.ui_activity_creator_member)
    private View memberView;
    @ViewId(R.id.ui_activity_creator_content)
    private ClearEditText contentView;
    @ViewId(R.id.ui_tool_attachment_button)
    private View attachmentView;

    // holder
    private SimpleClickableViewHolder coverHolder;
    private SimpleInputableViewHolder titleHolder;
    private SimpleClickableViewHolder timeHolder;
    private SimpleInputableViewHolder addressHolder;
    private SimpleClickableViewHolder typeHolder;
    private SimpleClickableViewHolder privacyHolder;
    private SimpleClickableViewHolder memberHolder;

    /**
     * 活动所属的组织id
     */
    private String mGroupId = "";
    private ArrayList<Member> selectedMembers;
    private ArrayList<String> labelsId;
    private String[] items, openStates;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        maxSelectable = 1;
        setCustomTitle(R.string.ui_activity_create_fragment_title);
        setRightText(R.string.ui_base_text_publish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryPublishActivity();
            }
        });
        enableSwipe(!isEmpty(mQueryId));
        setOnFileUploadingListener(mOnFileUploadingListener);
        tryLoadActivity();
    }

    private OnFileUploadingListener mOnFileUploadingListener = new OnFileUploadingListener() {

        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            publishActivity();
        }
    };

    @Override
    protected boolean onBackKeyPressed() {
        return super.onBackKeyPressed();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_creator;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
        fetchingActivity(true);
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    // 加载本地活动记录
    private void tryLoadActivity() {
        if (isEmpty(mQueryId)) {
            initializeHolder(null);
        } else {
            fetchingActivity(false);
        }
    }

    private void fetchingActivity(boolean fromRemote) {
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    if (null != activity) {
                        initializeHolder(activity);
                    }
                }
                stopRefreshing();
            }
        }).find(mQueryId, fromRemote);
    }

    private void initializeHolder(Activity activity) {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_create_items);
        }
        if (null == openStates) {
            openStates = StringHelper.getStringArray(R.array.ui_activity_open_status);
        }
        // cover
        if (null == coverHolder) {
            coverHolder = new SimpleClickableViewHolder(coverView, this);
            coverHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        boolean non = null == activity;
        if (isEmpty(cover)) {
            if (!non) {
                cover = activity.getImg();
            }
        }
        String value;// = format(items[0], (isEmpty(cover) ? none : ""));
        coverHolder.showContent(items[0]);
        coverHolder.showImage(cover);

        // title
        if (null == titleHolder) {
            titleHolder = new SimpleInputableViewHolder(titleView, this);
        }
        if (isEmpty(title)) {
            if (!non) {
                title = activity.getTitle();
            }
        }
        value = format(items[1], title);
        titleHolder.showContent(value);

        // time
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
            timeHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        if (isEmpty(happenDate)) {
            if (!non) {
                happenDate = activity.getCreateDate();
            }
        }
        value = format(items[2], isEmpty(happenDate) ? "" : formatDate(happenDate));
        timeHolder.showContent(value);

        // address
        if (null == addressHolder) {
            addressHolder = new SimpleInputableViewHolder(addressView, this);
        }
        if (isEmpty(address)) {
            if (!non) {
                address = activity.getContent();
            }
        }
        value = format(items[3], address);
        addressHolder.showContent(value);

        // type
        if (null == typeHolder) {
            typeHolder = new SimpleClickableViewHolder(typeView, this);
            typeHolder.addOnViewHolderClickListener(onViewHolderClickListener);
            if (!non) {
                if (null != activity.getLabel() && activity.getLabel().size() > 1) {
                    for (String id : activity.getLabel()) {
                        if (!labelsId.contains(id)) {
                            labelsId.add(id);
                        }
                    }
                }
            }
        }
        String tmp = labelsId.size() < 1 ? "选择标签" : format("%d个标签", labelsId.size());
        value = format(items[4], tmp);
        typeHolder.showContent(value);

        // privacy
        if (null == privacyHolder) {
            privacyHolder = new SimpleClickableViewHolder(privacyView, this);
            privacyHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        if (openStatus == Activity.OpenStatus.NONE) {
            if (!non) {
                openStatus = activity.getOpenStatus();
            }
        }
        value = format(items[5], openStates[openStatus]);
        privacyHolder.showContent(value);

        // member
        if (null == memberHolder) {
            memberHolder = new SimpleClickableViewHolder(memberView, this);
            memberHolder.addOnViewHolderClickListener(onViewHolderClickListener);
            if (!non) {
                if (null != activity.getMemberIdArray() && activity.getMemberIdArray().size() > 0) {
                    for (final String id : activity.getMemberIdArray()) {
                        Member member = new Member() {{
                            setId(id);
                        }};
                        if (!selectedMembers.contains(member)) {
                            selectedMembers.add(member);
                        }
                    }
                }
            }
        }
        value = format(items[6], getMembersInfo());
        memberHolder.showContent(value);

        if (isEmpty(content)) {
            if (!non) {
                content = activity.getContent();
            }
        }
        contentView.setValue(content);
    }

    private String getMembersInfo() {
        String string = "";
        if (selectedMembers.size() < 1) {
            string = "请选择参与人员";
        } else {
            int i = 0;
            for (Member member : selectedMembers) {
                String name = member.getUserName();
                string += (isEmpty(string) ? "" : "、") + (isEmpty(name) ? "" : name);
                if (i >= 1) {
                    string += format("%s%d人", (isEmpty(string) ? "" : "等"), selectedMembers.size());
                }
                i++;
            }
        }
        return string;
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                    // 到活动封面拾取器
                    openActivity(CoverPickFragment.class.getName(), "", REQ_COVER, true, false);
                    break;
                case 1:
                    // 选择活动时间
                    openDatePicker();
                    break;
                case 2:
                    // 选择活动标签
                    String string = format("%s,%s", mGroupId, replaceJson(labelJson, false));
                    openActivity(LabelPickFragment.class.getName(), string, REQ_LABEL, true, false);
                    break;
                case 3:
                    // 选择公开范围
                    openActivityOpenStatus();
                    break;
                case 4:
                    String params = format("%s,%s", mGroupId, replaceJson(memberJson, false));
                    openActivity(OrganizationContactPickFragment.class.getName(), params, REQ_MEMBER, true, false);
                    break;
            }
        }
    };

    private View openStatusView;

    private void openActivityOpenStatus() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == openStatusView) {
                    openStatusView = View.inflate(Activity(), R.layout.popup_dialog_activity_open_status, null);
                }
                return openStatusView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_activity_open_status_open, R.id.ui_dialog_button_activity_open_status_close};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_dialog_button_activity_open_status_open:
                        openStatus = Activity.OpenStatus.OPEN;
                        privacyHolder.showContent(format(items[5], format("(%s)", openStates[openStatus])));
                        break;
                    case R.id.ui_dialog_button_activity_open_status_close:
                        openStatus = Activity.OpenStatus.GROUP;
                        privacyHolder.showContent(format(items[5], format("(%s)", openStates[openStatus])));
                        break;
                }
                return true;
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.TYPE_SLID).show();
    }

    private String happenDate;

    private void showCreateDate(Date date) {
        happenDate = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), date);
        timeHolder.showContent(format(items[2], "(" + Utils.format(StringHelper.getString(R.string.ui_base_text_date_format_chs_min), date) + ")"));
    }

    private void openDatePicker() {
        Utils.hidingInputBoard(contentView);
        TimePickerView tpv = new TimePickerView.Builder(Activity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                showCreateDate(date);
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setTitleBgColor(getColor(R.color.colorPrimary))
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)
                .setContentSize(getFontDimension(R.dimen.ui_base_text_size))
                .setOutSideCancelable(false)
                .isCenterLabel(true).isDialog(false).build();
        if (isEmpty(happenDate)) {
            tpv.setDate(Calendar.getInstance());
            happenDate = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), Calendar.getInstance().getTime());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), happenDate));
            tpv.setDate(calendar);
        }
        tpv.show();
    }

    private void tryPublishActivity() {
        String title = titleHolder.getValue();
        if (isEmpty(title)) {
            ToastHelper.make().showMsg(R.string.ui_activity_create_title_invalid);
            return;
        }
        String address = addressHolder.getValue();
        if (isEmpty(address)) {
            ToastHelper.make().showMsg(R.string.ui_activity_create_address_invalid);
            return;
        }
        String content = contentView.getValue();
        if (isEmpty(content)) {
            ToastHelper.make().showMsg(R.string.ui_activity_create_content_invalid);
            return;
        }
        Utils.hidingInputBoard(contentView);
        if (getWaitingForUploadFiles().size() > 0) {
            uploadFiles();
        } else {
            publishActivity();
        }
    }

    private void publishActivity() {
        String title = titleHolder.getValue().trim();
        String address = addressHolder.getValue().trim();
        String content = contentView.getValue().trim();
        String logo = null;
        if (getUploadedFiles().size() > 0) {
            logo = getUploadedFiles().get(0).getUrl();
        }
        ArrayList<String> members = new ArrayList<>();
        for (Member member : selectedMembers) {
            members.add(member.getId());
        }
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
            }
        }).add(title, content, openStatus, address, happenDate, mGroupId, logo, members, labelsId, null);
    }
}
