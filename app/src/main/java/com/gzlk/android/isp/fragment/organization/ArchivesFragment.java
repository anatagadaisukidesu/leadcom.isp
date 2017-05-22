package com.gzlk.android.isp.fragment.organization;

import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.GroupArchiveRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.fragment.individual.ArchiveNewFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.TooltipHelper;
import com.gzlk.android.isp.holder.ArchiveViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.organization.GroupArchive;
import com.gzlk.android.isp.model.organization.Organization;

import java.util.List;

/**
 * <b>功能描述：</b>组织档案列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 10:39 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 10:39 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchivesFragment extends BaseOrganizationFragment {

    private ArchiveAdapter mAdapter;

    @Override
    protected void onSwipeRefreshing() {
        fetchingRemoteArchives();
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        if (StringHelper.isEmpty(mQueryId)) return null;
        return format("af_grp_archive_%s", mQueryId);
    }

    @Override
    public void doingInResume() {
        if (!StringHelper.isEmpty(mQueryId)) {
            //if (isNeedRefresh()) {
            loadingLocalArchive();
            //}
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    /**
     * 设置新的组织id并查找该组织的档案列表
     */
    public void setNewQueryId(String queryId) {
        if (!StringHelper.isEmpty(mQueryId) && mQueryId.equals(queryId)) {
            return;
        }
        mQueryId = queryId;
        loadingLocalArchive();
    }

    /**
     * 打开新建、管理菜单
     */
    public void openTooltipMenu(View view) {
        showTooltip(view, R.id.ui_tool_view_tooltip_menu_organization_document, true, TooltipHelper.TYPE_RIGHT, onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ui_tool_popup_menu_organization_document_new:
                    // 新建组织档案
                    openActivity(ArchiveNewFragment.class.getName(), format("%d,,%s", Archive.Type.ORGANIZATION, mQueryId), true, true);
                    break;
                case R.id.ui_tool_popup_menu_organization_document_manage:
                    // 管理组织档案
                    break;
            }
        }
    };

    private void fetchingRemoteArchives() {
        GroupArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<GroupArchive>() {
            @Override
            public void onResponse(List<GroupArchive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (null != list) {
                        for (GroupArchive archive : list) {
                            archive.resetAdditional(archive.getAddition());
                        }
                        dao.save(list);
                        mAdapter.add(list, false);
                    }
                }
                stopRefreshing();
            }
        }).list(mQueryId, remotePageNumber);
    }

    private Dao<GroupArchive> dao;

    private void loadingLocalArchive() {
        initializeAdapter();
        if (null == dao) {
            dao = new Dao<>(GroupArchive.class);
        }
        List<GroupArchive> temp = dao.query(Organization.Field.GroupId, mQueryId);
        if (null != temp) {
            mAdapter.add(temp, false);
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ArchiveAdapter();
        }
        if (null != mRecyclerView) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 打开组织档案详情，一个webview框架
            GroupArchive groupArchive = mAdapter.get(index);
            openActivity(ArchiveDetailsWebViewFragment.class.getName(), groupArchive.getId(), true, false);
        }
    };

    private class ArchiveAdapter extends RecyclerViewAdapter<ArchiveViewHolder, GroupArchive> {

        @Override
        public ArchiveViewHolder onCreateViewHolder(View itemView, int viewType) {
            ArchiveViewHolder holder = new ArchiveViewHolder(itemView, ArchivesFragment.this);
            holder.addOnViewHolderClickListener(viewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_document;
        }

        @Override
        public void onBindHolderOfView(ArchiveViewHolder holder, int position, @Nullable GroupArchive item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(GroupArchive item1, GroupArchive item2) {
            return 0;
        }
    }
}
