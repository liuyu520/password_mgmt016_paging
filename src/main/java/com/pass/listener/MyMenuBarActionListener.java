package com.pass.listener;

import com.common.util.SystemHWUtil;
import com.pass.bean.User;
import com.pass.dialog.ModifyPasswordDialog;
import com.pass.dialog.ReadExcelDialog;
import com.pass.dict.Constant;
import com.pass.main.PassMgmtApp;
import com.swing.dialog.CustomDefaultDialog;
import com.swing.dialog.toast.ToastMessage;
import com.swing.menu.MenuUtil2;
import com.swing.messagebox.GUIUtil23;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/***
 * listen to menu.
 *
 * @author huangwei
 */
public class MyMenuBarActionListener implements ActionListener {
    private PassMgmtApp swingApp = null;

    public MyMenuBarActionListener(PassMgmtApp swingApp) {
        super();
        this.swingApp = swingApp;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        // System.out.println(command);
        if (command.equals("leave")) {
            // JFileChooser chooser = new JFileChooser(new
            // File("/home/whuang2"));
            // chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            // int result = chooser.showOpenDialog(swingApp);
            // System.out.println("新建文件:" + result);
            // if (result == 0)
            // {
            // File selectedFile = chooser.getSelectedFile();
            // System.out.println("select file:" + selectedFile);
            // }
            // chooser.setVisible(true);
            System.out.println("leave");
            User user = this.swingApp.getUser();
            if (user != null && StringUtils.isNotEmpty(user.getUsername())) {
                this.swingApp.cleanUpTable();
                this.swingApp.loginDialog(Constant.LOGIN_DIALOG_TYPE_LEAVE, false);
            } else {
                GUIUtil23.warningDialog(Constant.ERROR_MESSAGE_NOT_LOGINED);
                return;
            }
        } else if (command.equals("clear")) {// clear pass data
            System.out.println("clear");
            this.swingApp.cleanUpTable();
        } else if (command.equals(MenuUtil2.ACTION_STR_CLOSE)) {// close the
            // whole window

            System.out.println(MenuUtil2.ACTION_STR_CLOSE);
            int result = JOptionPane.showConfirmDialog(null, "您确定要退出吗 ?",
                    "确认提示框", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {

                this.swingApp.closeAllWindow();
                System.exit(0);
            }
        } else if (command.equals("contact")) {
//			GUIUtil23.infoDialog("QQ:1287789687");
            CustomDefaultDialog customDefaultDialog = new CustomDefaultDialog("蜜罐系统", null, false);
            customDefaultDialog.setVisible(true);
        } else if (command.equals(PassMgmtApp.ACTION_COMMAND_IMPORT_EXCEL)) {
            // import a excel file into database
            System.out.println("import excel");
            dealLeave();
            this.swingApp.importExcelAction();

        } else if (command.equals(PassMgmtApp.ACTION_COMMAND_READ_EXCEL)) {//注意:该弹出框不是模态的.
            // import a excel file into database
            ReadExcelDialog readExcelDialog = new ReadExcelDialog(this.swingApp);
            readExcelDialog.setVisible(true);
            System.out.println("read excel");
            dealLeave();
        } /*else if (command.equals(PassMgmtApp.ACTION_COMMAND_CIPHERTEXT_AREA)) {
            boolean isLogined = (!this.swingApp.isIs_need_login_for_Ciphertext())|| this.swingApp.loginDialog(false);
			if (isLogined) {
				CiphertextAreaDialog ciphertextAreaDialog = new CiphertextAreaDialog(
						true);
				ciphertextAreaDialog.setVisible(true);
			}
		}*/ else if (command.equals(MenuUtil2.ACTION_USER_LOGIN)) {
            dealLeave();
            this.swingApp.loginDialog(Constant.LOGIN_DIALOG_TYPE_LEAVE, false);
            JMenuItem menuItem = (JMenuItem) event.getSource();
            menuItem.setEnabled(false);

        } else if (command.equals("modify password")) {// modify /update password
            User user = this.swingApp.getUser();
            dealLeave();
            if (user != null && StringUtils.isNotEmpty(user.getUsername())) {
                ModifyPasswordDialog dialog = new ModifyPasswordDialog(
                        this.swingApp.getFrame(), "修改密码", true, user/*
																	 * current
																	 * logined
																	 * user
																	 */);
                String newPassword = dialog.getNewPassword();

                // String newPassword = JOptionPane.showInputDialog(
                // this.swingApp.getFrame(), "Please input new password",
                // "new password");
                if (StringUtils.isNotEmpty(newPassword)) {
                    try {
                        this.swingApp.updatePassword(newPassword);
                        GUIUtil23.infoDialog("密码修改成功!");
                    } catch (SQLException e) {
                        GUIUtil23.errorDialog(e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        GUIUtil23.errorDialog(e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                GUIUtil23.warningDialog(Constant.ERROR_MESSAGE_NOT_LOGINED);
                return;
            }
        } else if (command.equals("not save password")) {
            System.out.println("not save password");
            Properties prop = this.swingApp.getProps();
            prop.setProperty(PassMgmtApp.PROP_KEY_PASSWORD, SystemHWUtil.EMPTY);
            prop.setProperty(PassMgmtApp.PROP_KEY_IS_REMEMBER_PASSWORD, String.valueOf(false));
            try {
                this.swingApp.saveConfig();
                ToastMessage.toast("操作成功", 1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void dealLeave() {
        swingApp.setLeave(true);//为了不弹登录框,这种处理不正规,应该使用额外的变量的.
    }

}
