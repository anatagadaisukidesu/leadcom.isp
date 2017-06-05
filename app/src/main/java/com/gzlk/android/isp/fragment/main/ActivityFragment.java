package com.gzlk.android.isp.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.fragment.activity.ActivityDetailsMainFragment;
import com.gzlk.android.isp.fragment.activity.ActivityManagementFragment;
import com.gzlk.android.isp.fragment.activity.ActivityPropertiesFragment;
import com.gzlk.android.isp.fragment.activity.CreateActivityFragment;
import com.gzlk.android.isp.fragment.activity.UnHandledInviteFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.organization.BaseOrganizationFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.helper.TooltipHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.OrganizationStructureConcernedViewHolder;
import com.gzlk.android.isp.holder.activity.ActivityViewHolder;
import com.gzlk.android.isp.lib.DepthViewPager;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.common.SimpleClickableItem;
import com.gzlk.android.isp.model.organization.Invitation;
import com.gzlk.android.isp.model.organization.Organization;
import com.netease.nim.uikit.NimUIKit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <b>功能描述：</b>主页活动<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 10:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 10:46 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityFragment extends BaseOrganizationFragment {

    private static final String PARAM_SELECTED_ = "act_selected_index";

    private String[] items;
    private int selectedIndex = -1;
    private ActivityAdapter mAdapter;
    private OrganizationStructureConcernedViewHolder concernedViewHolder;

    public MainFragment mainFragment;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        selectedIndex = bundle.getInt(PARAM_SELECTED_, -1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tryPaddingContent(true);
    }

    @Override
    public void doingInResume() {
        initializeAdapter();
        resetTitle();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        if (null != concernedViewHolder) {
            selectedIndex = concernedViewHolder.getSelected();
        }
        bundle.putInt(PARAM_SELECTED_, selectedIndex);
    }

    @Override
    protected void onSwipeRefreshing() {
        refreshingItems();
    }

    private void refreshingItems() {
        displayLoading(true);
        fetchingJoinedRemoteOrganizations();
        if (!isEmpty(mQueryId)) {
            fetchingActivity(true);
            // 拉取我为处理的群活动邀请
            fetchingUnHandledActivityInvite(mQueryId);
        } else {
            displayLoading(false);
        }
    }

    @Override
    protected void onFetchingJoinedRemoteOrganizationsComplete(List<Organization> list) {
        if (null != list && list.size() > 0) {
            concernedViewHolder.add(list);
        } else {
            // 当前显示本fragment时才提示用户
            if (getUserVisibleHint()) {
                ToastHelper.make().showMsg(R.string.ui_organization_structure_no_group_exist);
            }
        }
        stopRefreshing();
        setSupportLoadingMore(false);
    }

    @Override
    protected void onFetchingActivityComplete(List<Activity> list) {
        if (null != list) {
            updateActivityList(list);
        } else {
            // 当前显示本fragment时才提示用户
            if (getUserVisibleHint()) {
                ToastHelper.make().showMsg(R.string.ui_activity_main_not_exist_any_more);
            }
        }
        stopRefreshing();
        setSupportLoadingMore(false);
    }

    @Override
    protected void onFetchingUnHandledActivityInviteComplete(List<Invitation> list) {
        super.onFetchingUnHandledActivityInviteComplete(list);
        int cnt = null == list ? 0 : list.size();
        String string = format(items[1], cnt);
        SimpleClickableItem item = (SimpleClickableItem) mAdapter.get(1);
        item.setSource(string);
        mAdapter.notifyItemChanged(1);
        displayLoading(false);
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    public void rightIconClick(View view) {
        showTooltip(view, R.id.ui_tooltip_activity_management, true, TooltipHelper.TYPE_RIGHT, onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ui_tooltip_menu_activity_add:
                    openActivity(CreateActivityFragment.class.getName(), format(",%s", mQueryId), true, true);
                    break;
                case R.id.ui_tooltip_menu_activity_manage:
                    openActivity(ActivityManagementFragment.class.getName(), mQueryId, false, false);
                    break;
            }
        }
    };

    private void initializeAdapter() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_home_page_items);
        }
        if (null == mAdapter) {
            mAdapter = new ActivityAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
        initializeItems();
    }

    private void initializeItems() {
        for (String string : items) {
            String text;
            if (string.contains("%d")) {
                // 未参加的活动
                if (string.charAt(0) == '1') {
                    text = format(string, 0);
                } else {
                    // 议题
                    text = format(string, 0);
                }
            } else {
                text = string;
            }
            if (!isEmpty(text)) {
                SimpleClickableItem item = new SimpleClickableItem(text);
                mAdapter.update(item);
            }
        }
    }

    private void updateActivityList(List<Activity> list) {
        if (null == list) return;
        for (Activity activity : list) {
            if (!activities.contains(activity)) {
                activities.add(activity);
            }
        }
        Collections.sort(activities, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                return -o1.getCreateDate().compareTo(o2.getCreateDate());
            }
        });
        for (Activity activity : activities) {
            mAdapter.update(activity);
        }
    }

    @Override
    protected void onViewPagerDisplayedChanged(boolean visible) {
        super.onViewPagerDisplayedChanged(visible);
        if (visible) {
            resetTitle();
            refreshingItems();
        }
    }

    private void resetTitle() {
        if (getUserVisibleHint()) {
            mainFragment.showRightIcon(true);
        }
        changeSelectedActivity();
    }

    private ArrayList<Activity> activities = new ArrayList<>();

    private void changeSelectedActivity() {
        if (selectedIndex < 0) return;
        // 加载本地该组织的活动列表
        Organization org = concernedViewHolder.get(selectedIndex);
        mQueryId = org.getId();
        mOrganizationId = mQueryId;
        // 更改标题栏上的文字和icon
        if (getUserVisibleHint()) {
            // 如果当前显示的是组织页面才更改标题栏文字，否则不需要
            mainFragment.setTitleText(org.getName());
        }
        for (Activity activity : activities) {
            mAdapter.remove(activity);
        }
        activities.clear();
        refreshingItems();
    }

    private DepthViewPager.OnPageChangeListener onPageChangeListener = new DepthViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            selectedIndex = position;
            Handler().post(new Runnable() {
                @Override
                public void run() {
                    changeSelectedActivity();
                }
            });
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof Activity) {
                Activity act = (Activity) model;
                //openActivity(CreateActivityFragment.class.getName(), format("%s,%s", act.getId(), act.getGroupId()), true, true);
                //openActivity(ActivityPropertiesFragment.class.getName(), act.getId(), false, false, true);
                if (act.getStatus() == Activity.Status.ACTIVE) {
                    // 未结束的活动打开群聊窗口
                    NimUIKit.startTeamSession(Activity(), act.getTid(), null);
                } else {
                    // 已结束的活动打开活动详情页
                    openActivity(ActivityDetailsMainFragment.class.getName(), act.getId(), false, false);
                }
            } else if (model instanceof SimpleClickableItem) {
                // 打开未参加的活动列表
                if (index == 1) {
                    openActivity(UnHandledInviteFragment.class.getName(), mQueryId, true, false);
                }
            }
        }
    };

    private class ActivityAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEAD = 0, VT_ACT = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = ActivityFragment.this;
            switch (viewType) {
                case VT_HEAD:
                    if (null == concernedViewHolder) {
                        concernedViewHolder = new OrganizationStructureConcernedViewHolder(itemView, fragment);
                        concernedViewHolder.setPageChangeListener(onPageChangeListener);
                        concernedViewHolder.loadingLocal();
                    }
                    return concernedViewHolder;
                default:
                    ActivityViewHolder holder = new ActivityViewHolder(itemView, fragment);
                    holder.addOnViewHolderClickListener(onViewHolderClickListener);
                    return holder;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEAD:
                    return R.layout.holder_view_organization_concerned;
                default:
                    return R.layout.holder_view_activity_home_item;
            }
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return VT_HEAD;
                default:
                    return VT_ACT;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ActivityViewHolder) {
                if (item instanceof SimpleClickableItem) {
                    ((ActivityViewHolder) holder).showContent(((SimpleClickableItem) item).getSource());
                } else {
                    ((ActivityViewHolder) holder).showContent((Activity) item);
                }
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
