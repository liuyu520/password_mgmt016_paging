package com.pass.bean;

import java.sql.Timestamp;

//@Entity
//@Table(name = "pass")
public class Pass implements Cloneable {
    private Integer id;
    private String title;
    private String username;
    private String pwd;
    private String description;
    private String expirationTime;
    /***
     * 创建的时间，以后不能修改
     */
    private Timestamp createTime;
    /***
     * 状态,删除密码时仅设置状态,不会真正删除.<br>
     * 1:可用;2:已被删除
     */
    private int status;

    public void setId(Integer id) {
        this.id = id;
    }

    // @Id
    // @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("title: ").append(title).append("\n");
        sb.append("username: ").append(username).append("\n");
        sb.append("pwd: ").append(pwd).append("\n");
        sb.append("description: ").append(description).append("\n");
        return sb.toString();
    }

    public Timestamp getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Pass clone() throws CloneNotSupportedException {
        return (Pass) super.clone();
    }

    /***
     * 1:可用;2:已被删除
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
