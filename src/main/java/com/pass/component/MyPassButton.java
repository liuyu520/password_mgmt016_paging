package com.pass.component;

import com.common.util.JarFileHWUtil;
import com.common.util.WindowUtil;
import com.pass.bean.Pass;
import com.pass.dao.PassDao;
import com.pass.main.PassMgmtApp;
import com.swing.messagebox.GUIUtil23;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class MyPassButton extends JButton {
    public final static String LABEL_BUTTON_DEL = "del";
    public final static String LABEL_BUTTON_COPY_ROW = "copy record";
    public final static String LABEL_BUTTON_COPY_CONTENT = "copy content";
    public final static String LABEL_BUTTON_VIEW = "view";
    public final static String LABEL_BUTTON_EDIT = "edit";
    private static final long serialVersionUID = -1412900149837103628L;
    /***
     * 判断是否是在jar包中
     */
    private static boolean isInJar = JarFileHWUtil.isInJar(MyPassButton.class);
    private Pass pass;
    private PassDao passDao = null;
    private PassMgmtApp passApp = null;

    public MyPassButton(String arg0) {
        super(arg0);
    }

    public void setPassApp(PassMgmtApp passApp) {
        this.passApp = passApp;
//		if(urlPath.contains(".jar!")){
////			System.out.println("在jar包中");
//			isInJar=true;
//		}else{
////			System.out.println("不在jar包中");
//			isInJar=false;
//		}
//		System.out.println("isInJar:"+isInJar);

    }

    public Pass getPass() {
        return pass;
    }

    public void setPass(Pass pass) {
        this.pass = pass;
    }

    public void setPassDao(PassDao passDao) {
        this.passDao = passDao;
    }

    public int getPassId() {
        return getPass().getId();
    }

    @Override
    protected void processMouseEvent(MouseEvent event) {
        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
            //右键：3，左键：1
//        	System.out.println("MOUSE_PRESSED");
//        	System.out.println("event.getButton():"+event.getButton());
            int buttonTarget;
            if (isInJar) {
                buttonTarget = 0;
            } else {
                buttonTarget = MouseEvent.BUTTON1;
            }//打成jar包运行，左键单击的event.getButton() 的值为0，不知道为什么
            if (event.getButton() == buttonTarget || event.getButton() == MouseEvent.BUTTON1) {
//            	System.out.println("BUTTON1");
                String label2 = getText();
                if (label2.equalsIgnoreCase(LABEL_BUTTON_DEL)) {
                    System.out.println(LABEL_BUTTON_DEL);
                    if (passDao != null) {
                        try {
                            int result = JOptionPane.showConfirmDialog(null,
                                    "Are you sure to remove ?", "question",
                                    JOptionPane.OK_CANCEL_OPTION);
                            if (result == JOptionPane.OK_OPTION) {
                                //                                if (!passApp.isIs_allow_delete_superuser())
                                //                                {
//                                if (getPass().isIssuperpwd())
//                                {
//                                    GUIUtil23
//                                            .warningDialog("You must not delete admin's password");
//                                    return;
//                                }
                                //                                }
                                passDao.deleteById(getPassId());
                                passApp.refresh();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (label2.equalsIgnoreCase(LABEL_BUTTON_VIEW)) {
                    System.out.println("LABEL_BUTTON_VIEW");
                    System.out.println("view");
                    passApp.view2(getPassId());

                } else if (label2.equalsIgnoreCase(LABEL_BUTTON_COPY_ROW)) {
                    try {
                        passDao.add(getPass(), false);
                        passApp.refresh_last(null, 0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (label2.equalsIgnoreCase(LABEL_BUTTON_COPY_CONTENT)) {
                    Pass selectedPass = getPass();
                    WindowUtil.setSysClipboardText(selectedPass.toString());
                    GUIUtil23.infoDialog("复制成功!");
                }
            }
        }
    }
}
