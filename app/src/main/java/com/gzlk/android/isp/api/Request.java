package com.gzlk.android.isp.api;

import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnHttpListener;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.request.JsonRequest;
import com.litesuits.http.request.content.JsonBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.Response;

import java.lang.reflect.Type;
import java.util.List;

/**
 * <b>功能描述：</b>所有网络请求的基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/24 16:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/24 16:03 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class Request<T> {

    /**
     * 默认多页查询时页大小
     */
    public static final int PAGE_SIZE = 10;

    private static final String URL = BaseApi.URL;
    /**
     * 新增
     */
    protected static final String ADD = "/add";
    /**
     * 删除
     */
    protected static final String DELETE = "/delete";
    /**
     * 更新
     */
    protected static final String UPDATE = "/update";
    /**
     * 单查找
     */
    protected static final String FIND = "/find";
    /**
     * 列表
     */
    protected static final String LIST = "/list";
    /**
     * 搜索
     */
    protected static final String SEARCH = "/search";
    /**
     * 保存
     */
    protected static final String SAVE = "/save";

    private static final int ABSTR_SIZE = 100;
    private static final int ABSTR_ROW = 5;
    protected static final String SUMMARY = format("abstrSize=%d&abstrRow=%d", ABSTR_SIZE, ABSTR_ROW);

    /**
     * 组合url
     */
    protected abstract String url(String action);

    /**
     * http网络访问层
     */
    private LiteHttp liteHttp;

    public Request() {
        liteHttp = LiteHttp.build(App.app()).create();
        // 10秒网络超时
        liteHttp.getConfig().setConnectTimeout(5000);
    }

    protected static String format(String fmt, Object... args) {
        return StringHelper.format(fmt, args);
    }

    protected static boolean isEmpty(String text) {
        return StringHelper.isEmpty(text);
    }

    protected void log(String log) {
        LogHelper.log(this.getClass().getSimpleName(), log);
    }

    /**
     * 检测给定值是否为null，是则返回""字符串，而不是null
     */
    protected String checkNull(String value) {
        return isEmpty(value) ? "" : value;
    }

    /**
     * 发起网络请求
     */
    protected void httpRequest(JsonRequest request) {
        liteHttp.executeAsync(request);
    }

    /**
     * 组合请求
     */
    protected JsonRequest<Api<T>> getRequest(Type resultType, final String action, String body, HttpMethods methods) {
        String fullUrl = format("%s%s", URL, action);
        log(format("url(%s): ", methods, fullUrl));
        if (!isEmpty(body)) {
            log("body: " + body);
        }
        return new JsonRequest<Api<T>>(fullUrl, resultType)
                .setHttpListener(new OnHttpListener<Api<T>>() {

                    @Override
                    public void onSucceed(Api<T> data, Response<Api<T>> response) {
                        super.onSucceed(data, response);
                        if (data.success()) {
                            if (data instanceof Query) {
                                if (null != onMultipleRequestListener) {
                                    Query<T> query = (Query<T>) data;
                                    Pagination<T> pagination = query.getData();
                                    onMultipleRequestListener.onResponse(pagination.getList(), data.success(),
                                            pagination.getTotalPages(), pagination.getPageSize(),
                                            pagination.getTotal(), pagination.getPageNumber());
                                }
                            } else if (data instanceof Special) {
                                if (null != onMultipleRequestListener) {
                                    Special<T> special = (Special<T>) data;
                                    onMultipleRequestListener.onResponse(special.getData(), data.success(),
                                            1, PAGE_SIZE, special.getData().size(), 1);
                                }
                            } else if (data instanceof Output) {
                                if (null != onSingleRequestListener) {
                                    onSingleRequestListener.onResponse(((Output<T>) data).getData(), data.success(), data.getMsg());
                                }
                            }
                        } else {
                            log(format("url: %s, response failed %s", action, data.getMsg()));
                            ToastHelper.make().showMsg(data.getMsg());
                            fireFailedListenerEvents(data.getMsg());
                        }
                    }

                    @Override
                    public void onFailed() {
                        super.onFailed();
                        fireFailedListenerEvents("");
                    }
                }).setHttpBody(new JsonBody(body), methods);
    }

    /**
     * 通知失败
     */
    private void fireFailedListenerEvents(String message) {
        if (null != onSingleRequestListener) {
            onSingleRequestListener.onResponse(null, false, message);
        }
        if (null != onMultipleRequestListener) {
            onMultipleRequestListener.onResponse(null, false, 0, 0, 0, 0);
        }
    }

    protected OnSingleRequestListener<T> onSingleRequestListener;

    protected void fireOnSingleRequestListener(T t) {
        if (null != onSingleRequestListener) {
            onSingleRequestListener.onResponse(t, true, "");
        }
    }

    /**
     * 设置网络调用成功之后的回调
     */
    public abstract Request<T> setOnSingleRequestListener(OnSingleRequestListener<T> listener);

    protected OnMultipleRequestListener<T> onMultipleRequestListener;

    protected void fireOnMultipleRequestListener(List<T> list) {
        if (null != onMultipleRequestListener) {
            onMultipleRequestListener.onResponse(list, true, 1, 1, list.size(), 1);
        }
    }

    /**
     * 添加请求列表时的处理回调
     */
    public abstract Request<T> setOnMultipleRequestListener(OnMultipleRequestListener<T> listListener);

}
