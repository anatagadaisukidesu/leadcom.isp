package com.gzlk.android.isp.fragment.individual;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.api.user.MomentRequest;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.AttachItemViewHolder;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.ImageViewHolder;
import com.gzlk.android.isp.holder.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.TextViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.lib.view.LoadingMoreSupportedRecyclerView;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.listener.RecycleAdapter;
import com.gzlk.android.isp.model.BaiduLocation;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.user.moment.Moment;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>个人 - 添加新的动态<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/16 14:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/16 14:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MomentNewFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_IMAGE = "mnf_initialized_image";
    private static final String PARAM_ADDRESS = "mnf_fetched_address";

    // UI
    @ViewId(R.id.ui_moment_new_text_content)
    private ClearEditText momentContent;

    private SimpleClickableViewHolder privacyHolder;
    private String[] textItems;
    private String address = "";

    public static MomentNewFragment newInstance(String params) {
        MomentNewFragment mnf = new MomentNewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_IMAGE, params);
        mnf.setArguments(bundle);
        return mnf;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        layoutType = TYPE_GRID;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        address = bundle.getString(PARAM_ADDRESS, "");
        String json = bundle.getString(PARAM_IMAGE, EMPTY_ARRAY);
        ArrayList<String> images = Json.gson().fromJson(json, new TypeToken<ArrayList<String>>() {
        }.getType());
        getSelectedImages().clear();
        getSelectedImages().addAll(images);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(address, PARAM_ADDRESS);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_moment_new;
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public void doingInResume() {
        setLeftIcon(0);
        setLeftText(R.string.ui_base_text_cancel);
        setCustomTitle(R.string.ui_text_new_moment_fragment_title);
        setRightIcon(0);
        setRightText(R.string.ui_base_text_send);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                ToastHelper.make().showMsg("暂时无法上传图片");
                tryAddMoment();

            }
        });
        initializeHolder();
        initializeAdapter();
        if (StringHelper.isEmpty(address)) {
            tryFetchingLocation();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void tryAddMoment() {
        //httpRequest(newMoment());
        MomentRequest.request().setOnRequestListener(new OnRequestListener<Moment>() {
            @Override
            public void onResponse(Moment moment, boolean success, String message) {
                super.onResponse(moment, success, message);
                if (success) {
                    new Dao<>(Moment.class).save(moment);
                    finish();
                }
            }
        }).add(App.app().UserId(), App.app().Me().getName(), address, momentContent.getValue(), getSelectedImages());
    }

//    private JsonRequest<SingleMoment> newMoment() {
//        String[] images = new String[]{
//                "https://img6.cache.netease.com/photo/0005/2017-04-16/CI59JMD900DE0005.jpg",
//                "https://img6.cache.netease.com/photo/0001/2017-04-20/CIG93MBI00AN0001.jpg"};
//        AddMoment param = new AddMoment(App.app().UserId(), App.app().Me().getName(), address,
//                momentContent.getValue(), new ArrayList<>(Arrays.asList(images)));
//        String json = Json.gson(HttpRichParamModel.class).toJson(param, new TypeToken<AddMoment>() {
//        }.getType());
//        log(json);
//        JsonRequest<SingleMoment> add = new JsonRequest<>(param, SingleMoment.class);
//        add.setHttpListener(new OnHttpListener<SingleMoment>() {
//            @Override
//            public void onSucceed(SingleMoment data, Response<SingleMoment> response) {
//                super.onSucceed(data, response);
//                new Dao<>(Moment.class).save(data.getData());
//            }
//
//            @Override
//            public void onFailed() {
//                super.onFailed();
//            }
//
//            @Override
//            public void onEnd(Response<SingleMoment> response) {
//                super.onEnd(response);
//            }
//        }).setHttpBody(new JsonBody(json), HttpMethods.Post);
//        return add;
//    }

    @Override
    protected void onFetchingLocationComplete(boolean success, BaiduLocation location) {
        address = location.getAddress();
        log(address);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private void initializeHolder() {
        if (null == textItems) {
            textItems = StringHelper.getStringArray(R.array.ui_individual_new_moment);
        }

        if (null == privacyHolder) {
            privacyHolder = new SimpleClickableViewHolder(mRootView, MomentNewFragment.this);
            privacyHolder.addOnViewHolderClickListener(privacyListener);
            privacyHolder.showContent(format(textItems[0], "公开"));
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            // 这里不需要直接上传，只需要把选择的图片传递给新建动态页面即可，上传在那里实现
            isSupportDirectlyUpload = false;
            // 添加图片选择
            addOnImageSelectedListener(albumImageSelectedListener);
            // 不需要下拉加载更多
            mRecyclerView.setSupportLoadingMore(false);
            mRecyclerView.addItemDecoration(new SpacesItemDecoration());
            mAdapter = new ImageAdapter();
            mRecyclerView.setAdapter(mAdapter);
            resetImages();
        }
    }

    private void resetImages() {
        mAdapter.clear();
        for (String string : getSelectedImages()) {
            mAdapter.add(string);
        }
        appendAttacher();
    }

    private void appendAttacher() {
        if (getSelectedImages().size() < getMaxSelectable()) {
            mAdapter.add("");
        }
    }

    // 相册选择返回了
    private OnImageSelectedListener albumImageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            resetImages();
        }
    };

    // 隐私设置点击了
    private OnViewHolderClickListener privacyListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            ToastHelper.make(Activity()).showMsg("隐私设置");
        }
    };

    // 需要增加照片
    private OnViewHolderClickListener imagePickClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 需要重新再选择图片
            startGalleryForResult();
        }
    };

    // 照片删除
    private ImageDisplayer.OnDeleteClickListener imageDeleteClickListener = new ImageDisplayer.OnDeleteClickListener() {
        @Override
        public void onDeleteClick(String url) {
            getSelectedImages().remove(url);
            mAdapter.remove(url);
            appendAttacher();
        }
    };

    private BaseViewHolder.OnHandlerBoundDataListener<String> handlerBoundDataListener = new BaseViewHolder.OnHandlerBoundDataListener<String>() {
        @Override
        public String onHandlerBoundData(BaseViewHolder holder) {
            return mAdapter.get(holder.getAdapterPosition());
        }
    };

    // 照片预览点击
    private ImageDisplayer.OnImageClickListener imagePreviewClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(String url) {
            // 相册预览
            startGalleryPreview(getSelectedImages().indexOf(url));
        }
    };

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int dimen = getDimension(R.dimen.ui_base_dimen_margin_padding);
            int position = parent.getChildAdapterPosition(view);
            outRect.bottom = 0;
            outRect.left = 0;
            GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
            int spanCount = manager.getSpanCount();
            // 第一行有顶部无空白，其余行顶部有空白
            outRect.top = (position / spanCount == 0) ? 0 : dimen;
            // 最后列右侧无空白，其余列右侧有空白
            outRect.right = (position % spanCount < (spanCount - 1)) ? dimen : 0;
        }
    }

    private ImageAdapter mAdapter;

    private class ImageAdapter extends LoadingMoreSupportedRecyclerView.LoadingMoreAdapter<BaseViewHolder, String> implements RecycleAdapter<String> {
        private static final int VT_IMAGE = 0, VT_ATTACH = 1;

        private int width, height;

        private void gotSize() {
            if (width == 0) {
                int _width = getScreenWidth();
                int padding = getDimension(R.dimen.ui_base_dimen_margin_padding) * (2 + gridSpanCount - 1);
                int size = (_width - padding) / gridSpanCount;
                width = size;
                height = size;
            }
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_IMAGE ? R.layout.holder_view_image : R.layout.holder_view_attach_item;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, String item) {
            if (holder instanceof ImageViewHolder) {
                gotSize();
                ImageViewHolder ivh = (ImageViewHolder) holder;
                ivh.addOnDeleteClickListener(imageDeleteClickListener);
                ivh.addOnImageClickListener(imagePreviewClickListener);
                ivh.addOnHandlerBoundDataListener(handlerBoundDataListener);
                ivh.setImageSize(width, height);
                ivh.showContent(getSelectedImages().get(position));
            }
        }

        @Override
        public int gotItemViewType(int position) {
            if (StringHelper.isEmpty(get(position))) {
                return VT_ATTACH;
            } else {
                return VT_IMAGE;
            }
        }

        @Override
        public BaseViewHolder footerViewHolder(View itemView) {
            return new TextViewHolder(itemView, MomentNewFragment.this);
        }

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            gotSize();
            return viewType == VT_IMAGE ? new ImageViewHolder(itemView, MomentNewFragment.this) :
                    new AttachItemViewHolder(itemView, MomentNewFragment.this)
                            .setSize(width, height).setOnViewHolderClickListener(imagePickClickListener);
        }

        @Override
        protected int comparator(String item1, String item2) {
            return item1.compareTo(item2);
        }
    }
}