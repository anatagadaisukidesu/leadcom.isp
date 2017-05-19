package com.gzlk.android.isp.model;

import com.gzlk.android.isp.helper.StringHelper;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * <b>功能描述：</b>所有mode基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 12:55 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 12:55 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Model extends BaseModel {

    /**
     * 字段名
     */
    public static class Field {
        public static final String Id = "id";
        public static final String CreateDate = "createDate";
        public static final String Name = "name";
        public static final String UserId = "userId";
        public static final String UserName = "userName";
        public static final String LocalDeleted = "localDeleted";
        public static final String CreatorId = "creatorId";
        public static final String CreatorName = "creatorName";
        public static final String UUID = "uuid";
        public static final String AccessToken = "accessToken";
    }

    @PrimaryKey(AssignType.BY_MYSELF)
    @NotNull
    @Column(Field.Id)
    private String id;

    @Column(Model.Field.AccessToken)
    private String accessToken;        //用户令牌

    @Column(Field.LocalDeleted)
    private boolean localDeleted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    /**
     * 本地是否已删除
     */
    public boolean isLocalDeleted() {
        return localDeleted;
    }

    /**
     * 本地是否已删除
     */
    public void setLocalDeleted(boolean localDeleted) {
        this.localDeleted = localDeleted;
    }

    @Override
    public boolean equals(Object object) {
        return null != object && getClass() == object.getClass() && object instanceof Model && equals((Model) object);
    }

    public boolean equals(Model model) {
        return null != model && !StringHelper.isEmpty(getId()) && !StringHelper.isEmpty(model.getId()) && model.getId().equals(getId());
    }
}
