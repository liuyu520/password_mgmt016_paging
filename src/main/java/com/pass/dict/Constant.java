package com.pass.dict;

public class Constant {
    public static final String ERROR_MESSAGE_NO_PASS_YET_TO_DELETE = "没有密码可以删除";
    public static final String ERROR_MESSAGE_NO_PASS_YET_TO_EXPORT = "没有密码可以导出";
    public static final String ERROR_MESSAGE_NOT_LOGINED = "您还没有登录,请重新登录.";
    public static final int MILLISECONDS_WAIT_WHEN_FAIL = 1200000;
    /***
     * 过多长时间,离开,并弹出登录窗口<br>单位:毫秒
     */
    public static final int MILLISECONDS_LEAVE_BEFORE_TIME = 6000;
    /***
     * 启动应用程序时(java -jar password_mgmt016_paging-0.0.3-SNAPSHOT.jar)
     */
    public static final short LOGIN_DIALOG_TYPE_LAUNCH_FIRST = 1;
    /***
     * 超时离开
     */
    public static final short LOGIN_DIALOG_TYPE_LEAVE = 2;
    /***
     * 新闻的状态:打开
     */
    public static final int NEWS_STATUS_ON = 1;
    /***
     * 新闻的状态:关闭
     */
    public static final int NEWS_STATUS_OFF = 2;
}
