package com.gzlk.android.isp.api.org;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.organization.Member;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>组织成员相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/10 09:11 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/10 09:11 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MemberRequest extends Request<Member> {

    public static MemberRequest request() {
        return new MemberRequest();
    }

    static class SingleMember extends Output<Member> {
    }

    static class MultipleMember extends Query<Member> {
    }

    // 成员
    private static final String MEMBER = "/group/groMember";

    @Override
    protected String url(String action) {
        return MEMBER + action;
    }

    @Override
    public MemberRequest setOnRequestListener(OnRequestListener<Member> listener) {
        onRequestListener = listener;
        return this;
    }

    @Override
    public MemberRequest setOnRequestListListener(OnRequestListListener<Member> listListener) {
        onRequestListListener = listListener;
        return this;
    }

    private String compound(String action, String groupId, String squadId) {
        String params = format("%s?groupId=%s", url(action), groupId);
        if (!StringHelper.isEmpty(squadId)) {
            params += format("&squadId=%s", squadId);
        }
        return params;
    }

    /**
     * 查找指定组织和指定小组内的成员列表
     */
    public void list(String groupId, String squadId) {
        httpRequest(getRequest(MultipleMember.class, compound(LIST, groupId, squadId), "", HttpMethods.Get));
    }

    /**
     * 在指定组织和指定小组内搜索成员名字
     */
    public void search(String groupId, String squadId, String memberName) {
        httpRequest(getRequest(MultipleMember.class, format("%s&info=%s", compound(SEARCH, groupId, squadId), memberName), "", HttpMethods.Get));
    }

    /**
     * 查询单个成员的详细信息
     */
    public void find(String memberId) {
        httpRequest(getRequest(SingleMember.class, format("%s?memberId=%s", url(FIND), memberId), "", HttpMethods.Get));
    }
}
