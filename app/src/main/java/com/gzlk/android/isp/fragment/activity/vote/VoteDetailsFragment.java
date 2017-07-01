package com.gzlk.android.isp.fragment.activity.vote;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.AppVoteRecordRequest;
import com.gzlk.android.isp.api.activity.AppVoteRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.activity.VoteOptionViewHolder;
import com.gzlk.android.isp.holder.activity.VoteViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.activity.AppVote;
import com.gzlk.android.isp.model.activity.AppVoteItem;
import com.gzlk.android.isp.model.activity.AppVoteRecord;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>投票详情页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/30 16:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/30 16:14 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VoteDetailsFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_TID = "vdf_param_tid";

    public static VoteDetailsFragment newInstance(String params) {
        VoteDetailsFragment vdf = new VoteDetailsFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 投票应用的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // tid
        bundle.putString(PARAM_TID, strings[1]);
        vdf.setArguments(bundle);
        return vdf;
    }

    public static void open(BaseFragment fragment, String tid, String setupId) {
        fragment.openActivity(VoteDetailsFragment.class.getName(), format("%s,%s", setupId, tid), true, false);
    }

    public static void open(Context context, int requestCode, String tid, String setupId) {
        BaseActivity.openActivity(context, VoteDetailsFragment.class.getName(), format("%s,%s", setupId, tid), requestCode, true, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        tid = bundle.getString(PARAM_TID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TID, tid);
    }

    @ViewId(R.id.ui_tool_swipe_refreshable_recycler_view)
    private RecyclerView voteOptions;
    @ViewId(R.id.ui_activity_vote_details_end_time)
    private TextView endTime;
    @ViewId(R.id.ui_tool_view_bottom_buttons_1)
    private CorneredButton bottomButton1;
    @ViewId(R.id.ui_tool_view_bottom_buttons_2)
    private CorneredButton bottomButton2;

    private VoteViewHolder voteViewHolder;
    private AppVote mAppVote;
    private String activityId = "", tid;

    private VoteItemAdapter mAdapter;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_vote_details;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomButton1.setVisibility(View.GONE);
        bottomButton2.setVisibility(View.GONE);
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_vote_details_fragment_title);
        initializeHolder();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_tool_view_bottom_buttons_1, R.id.ui_tool_view_bottom_buttons_2})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_tool_view_bottom_buttons_1:
                finish();
                break;
            case R.id.ui_tool_view_bottom_buttons_2:
                vote();
                break;
        }
    }

    private void fetchingActivity() {
        if (isEmpty(activityId)) {
            Activity act = Activity.getByTid(tid);
            if (null != act) {
                activityId = act.getId();
            }
        }
    }

    private int voteIndex = 0;

    private void vote() {
        fetchingActivity();
        if (isEmpty(activityId)) {
            ToastHelper.make().showMsg(R.string.ui_activity_vote_creator_invalid_activity);
            return;
        }
        if (selectedItems() > 0) {
            showImageHandlingDialog(R.string.ui_activity_vote_details_voting);
            voteIndex = 0;
            voting();
        } else {
            ToastHelper.make().showMsg(R.string.ui_activity_vote_details_nothing_selected);
        }
    }

    private int selectedItems() {
        int cnt = 0;
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            cnt += mAdapter.get(i).isSelected() ? 1 : 0;
        }
        return cnt;
    }

    private void voting() {
        if (voteIndex >= mAdapter.getItemCount()) {
            // 投票完毕
            hideImageHandlingDialog();
        } else {
            AppVoteItem item = mAdapter.get(voteIndex);
            if (item.isSelected()) {
                voting(item.getId());
            } else {
                voteIndex++;
                voting();
            }
        }
    }

    private void voting(String itemId) {
        AppVoteRecordRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppVoteRecord>() {
            @Override
            public void onResponse(AppVoteRecord appVoteRecord, boolean success, String message) {
                super.onResponse(appVoteRecord, success, message);
                if (success) {
                    voteIndex++;
                    voting();
                } else {
                    hideImageHandlingDialog();
                    ToastHelper.make().showMsg(R.string.ui_activity_vote_details_voting_failed);
                }
            }
        }).add(activityId, mQueryId, itemId);
    }

    private void initializeHolder() {
        if (null == voteViewHolder) {
            voteViewHolder = new VoteViewHolder(mRootView, this);
            voteOptions.setLayoutManager(new CustomLinearLayoutManager(voteOptions.getContext()));
            voteOptions.setNestedScrollingEnabled(false);
            bottomButton1.setText(R.string.ui_activity_vote_details_button_reject);
            bottomButton2.setText(R.string.ui_activity_vote_details_button_vote);
        }
        if (null == mAdapter) {
            mAdapter = new VoteItemAdapter();
            voteOptions.setAdapter(mAdapter);
            loadingVoteDetails();
        }
    }

    private void loadingVoteDetails() {
        showImageHandlingDialog(R.string.ui_activity_vote_details_loading);
        AppVoteRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppVote>() {
            @Override
            public void onResponse(AppVote appVote, boolean success, String message) {
                super.onResponse(appVote, success, message);
                hideImageHandlingDialog();
                if (success) {
                    mAppVote = appVote;
                    showDetails();
                } else {
                    ToastHelper.make().showMsg(message);
                    finish();
                }
            }
        }).find(mQueryId, 3, remotePageNumber);
    }

    private void showDetails() {
        voteViewHolder.showContent(mAppVote);
        voteViewHolder.showVoteType(mAppVote);
        mAdapter.update(mAppVote.getItemListData(), false);
        endTime.setText(getString(R.string.ui_activity_vote_details_end_time, formatDateTime(mAppVote.getEndTime())));
        boolean ended = mAppVote.isEnded();
        bottomButton1.setVisibility(ended ? View.GONE : View.VISIBLE);
        bottomButton2.setVisibility(ended ? View.GONE : View.VISIBLE);
        boolean isMe = !isEmpty(mAppVote.getCreatorId()) && mAppVote.getCreatorId().equals(Cache.cache().userId);
        if (isMe) {
            resetRightIcon();
        }
    }

    private void resetRightIcon() {
        setRightText(R.string.ui_base_text_delete);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                warningDelete();
            }
        });
    }

    private void warningDelete() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_vote_details_delete, R.string.ui_base_text_delete, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteVote();
                return true;
            }
        }, null);
    }

    private void deleteVote() {
        showImageHandlingDialog(R.string.ui_activity_vote_details_deleting);
        AppVoteRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppVote>() {
            @Override
            public void onResponse(AppVote appVote, boolean success, String message) {
                super.onResponse(appVote, success, message);
                hideImageHandlingDialog();
                if (success) {
                    finish();
                }
            }
        }).delete(mQueryId);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            if (mAppVote.isEnded()) {
                return;
            }
            if (mAppVote.getType() <= 1) {
                // 单选
                for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
                    AppVoteItem item = mAdapter.get(i);
                    item.setSelected(i == index);
                    mAdapter.notifyItemChanged(i);
                }
            } else {
                // 多选
                AppVoteItem item = mAdapter.get(index);
                item.setSelected(!item.isSelected());
                mAdapter.notifyItemChanged(index);
            }
        }
    };

    private class VoteItemAdapter extends RecyclerViewAdapter<VoteOptionViewHolder, AppVoteItem> {

        @Override
        public VoteOptionViewHolder onCreateViewHolder(View itemView, int viewType) {
            VoteOptionViewHolder holder = new VoteOptionViewHolder(itemView, VoteDetailsFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_vote_option;
        }

        @Override
        public void onBindHolderOfView(VoteOptionViewHolder holder, int position, @Nullable AppVoteItem item) {
            holder.showContent(item, mAppVote.getType() > 1);
            holder.showEnded(mAppVote.isEnded());
        }

        @Override
        protected int comparator(AppVoteItem item1, AppVoteItem item2) {
            return 0;
        }
    }
}