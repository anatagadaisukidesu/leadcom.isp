package com.gzlk.android.isp.model.user;

import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>用户信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/14 20:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/14 20:15 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Model.Table.USER)
public class User extends Model {

    /**
     * 表内字段名称
     */
    public static class Field {
        public static final String Name = "name";
        public static final String Sex = "sex";
        public static final String LoginId = "loginId";
        public static final String QQ = "qq";
        public static final String QQAccessToken = "qqAccessToken";
        public static final String Phone = "phone";
        public static final String Email = "email";
        public static final String Password = "password";
        public static final String Birthday = "birthday";
        public static final String IsAuth = "isAuth";
        public static final String HeadPhoto = "headPhoto";
        public static final String LastModifiedTime = "lastModifiedTime";
        public static final String LastLoginTime = "lastLoginTime";
        public static final String Captcha = "captcha";
        public static final String AccessToken = "accessToken";
    }

    //用户姓名
    @Column(Field.Name)
    private String name;
    //性别
    @Column(Field.Sex)
    private String sex;
    //登录的Id
    @Column(Field.LoginId)
    private String loginId;
    //qq
    @Column(Field.QQ)
    private String qq;
    //qq授权指令，用于QQ登录
    @Column(Field.QQAccessToken)
    private String qqAccessToken;
    //手机
    @Column(Field.Phone)
    private String phone;
    //邮箱
    @Column(Field.Email)
    private String email;
    //密码
    @Column(Field.Password)
    private String password;
    //生日
    @Column(Field.Birthday)
    private String brithday;
    //是否实名认证
    @Column(Field.IsAuth)
    private boolean isAuth;
    //头像
    @Column(Field.HeadPhoto)
    private String headPhoto;
    //最后修改时间
    @Column(Field.LastModifiedTime)
    private String lastModifiedTime;
    //最后登录时间
    @Column(Field.LastLoginTime)
    private String lastLoginTime;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //验证码
    @Column(Field.Captcha)
    private String captcha;
    //访问令牌：用于移动端访问的唯标志
    @Column(Field.AccessToken)
    private String accessToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getQqAccessToken() {
        return qqAccessToken;
    }

    public void setQqAccessToken(String qqAccessToken) {
        this.qqAccessToken = qqAccessToken;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBrithday() {
        return brithday;
    }

    public void setBrithday(String brithday) {
        this.brithday = brithday;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}