package com.gzlk.android.isp.api.org;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.organization.archive.ArchiveLike;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>组织档案点赞<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/15 16:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/15 16:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveLikeRequest extends Request<ArchiveLike> {

    private static class SingleLike extends Output<ArchiveLike> {
    }

    private static class MultipleLike extends Query<ArchiveLike> {
    }

    // 组织档案点赞
    private static final String LIKE = "/group/groDocLike";

    @Override
    protected String url(String action) {
        return format("%s%s", LIKE, action);
    }

    @Override
    public ArchiveLikeRequest setOnRequestListener(OnRequestListener<ArchiveLike> listener) {
        onRequestListener = listener;
        return this;
    }

    @Override
    public ArchiveLikeRequest setOnRequestListListener(OnRequestListListener<ArchiveLike> listListener) {
        onRequestListListener = listListener;
        return this;
    }

    /**
     * 点赞档案
     */
    public void like(String archiveId) {
        //{groDocId,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("groDocId", archiveId)
                    .put("accessToken", Cache.cache().userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleLike.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 取消赞
     */
    public void unlike(String archiveId) {
        JSONObject object = new JSONObject();
        try {
            object.put("groDocId", archiveId)
                    .put("accessToken", Cache.cache().userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleLike.class, url(DELETE), object.toString(), HttpMethods.Post));
    }

    /**
     * 档案的点赞列表
     */
    public void list(String archiveId) {
        httpRequest(getRequest(MultipleLike.class, format("%s?groDocId=%s", url(LIST), archiveId), "", HttpMethods.Get));
    }

    public void exist(String archiveId) {
        httpRequest(getRequest(SingleLike.class, format("%s?groDocId=%s", url("/isExist"), archiveId), "", HttpMethods.Get));
    }
}