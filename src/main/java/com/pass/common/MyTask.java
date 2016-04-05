package com.pass.common;

import com.pass.dialog.PassDialog;
import com.pass.dict.Constant;
import com.pass.main.PassMgmtApp;

public class MyTask extends java.util.TimerTask {
    private PassMgmtApp frame;
    /***
     * true:关闭应用程序;<br> false:相当于执行leave
     */
    private boolean isClose;

    /***
     * @param frame
     * @param isClose : true:关闭应用程序;<br> false:相当于执行leave
     */
    public MyTask(PassMgmtApp frame, boolean isClose) {
        super();
        this.frame = frame;
        this.isClose = isClose;
    }

    @Override
    public void run() {
        if (isClose) {
            frame.setLocked(false);
            System.out.println("$$$$$");
            //关闭应用程序
            frame.closeAllWindow();
            ;
            System.exit(0);
        } else {
            System.out.println("leave");
            PassDialog passDialog = frame.getPassDialog();
            if (passDialog != null) {
                passDialog.dispose();
            }
            frame.cleanUpTable();
            frame.loginDialog(Constant.LOGIN_DIALOG_TYPE_LEAVE, false);
        }
    }
}
