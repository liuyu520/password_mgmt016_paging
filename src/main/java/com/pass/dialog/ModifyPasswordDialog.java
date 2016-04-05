package com.pass.dialog;

import com.pass.bean.User;
import com.pass.util.PassUtil;
import com.string.widget.util.ValueWidget;
import com.swing.messagebox.GUIUtil23;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModifyPasswordDialog extends JDialog {

    private static final long serialVersionUID = -8467148207449453631L;
    private JTextField passwordTF;
    private JTextField rePasswdTF;
    private String newPassword;
    private JButton okButton;
    private JPasswordField oldPasswordTF;

    public ModifyPasswordDialog(final JFrame owner, String title, boolean modal, final User user) {
        super(owner, title, modal);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(450, 180);
        Dimension framesize = this.getSize();
        int x = (int) screensize.getWidth() / 2 - (int) framesize.getWidth()
                / 2;
        int y = (int) screensize.getHeight() / 2 - (int) framesize.getHeight()
                / 2;
        this.setLocation(x, y - 100);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{20, 135, 311, 51, 0};
        gridBagLayout.rowHeights = new int[]{17, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                51, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0,
                Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        this.setLayout(gridBagLayout);
        okButton = new JButton("ok");
        final JButton cancelBtn = new JButton("close");

        JLabel oldPasswordLabel = new JLabel("old password");
        GridBagConstraints gbc_oldPasswordLabel = new GridBagConstraints();
        gbc_oldPasswordLabel.anchor = GridBagConstraints.WEST;
        gbc_oldPasswordLabel.insets = new Insets(0, 0, 5, 5);
        gbc_oldPasswordLabel.gridx = 1;
        gbc_oldPasswordLabel.gridy = 2;
        this.add(oldPasswordLabel, gbc_oldPasswordLabel);

        oldPasswordTF = new JPasswordField();
        GridBagConstraints gbc_oldPasswordTF = new GridBagConstraints();
        gbc_oldPasswordTF.insets = new Insets(0, 0, 5, 5);
        gbc_oldPasswordTF.fill = GridBagConstraints.HORIZONTAL;
        gbc_oldPasswordTF.gridx = 2;
        gbc_oldPasswordTF.gridy = 2;
        this.add(oldPasswordTF, gbc_oldPasswordTF);

        JLabel passwordLabel = new JLabel("password");
        GridBagConstraints gbc_passwordLabel = new GridBagConstraints();
        gbc_passwordLabel.anchor = GridBagConstraints.WEST;
        gbc_passwordLabel.insets = new Insets(0, 0, 5, 5);
        gbc_passwordLabel.gridx = 1;
        gbc_passwordLabel.gridy = 3;
        this.add(passwordLabel, gbc_passwordLabel);

        passwordTF = new JPasswordField();
        GridBagConstraints gbc_urlTF = new GridBagConstraints();
        gbc_urlTF.insets = new Insets(0, 0, 5, 5);
        gbc_urlTF.fill = GridBagConstraints.HORIZONTAL;
        gbc_urlTF.gridx = 2;
        gbc_urlTF.gridy = 3;
        this.add(passwordTF, gbc_urlTF);
        passwordTF.setColumns(10);
        passwordTF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                String passwd = passwordTF.getText();
                if (okButton.isEnabled() && ValueWidget.isNullOrEmpty(passwd)) {
                    okButton.setEnabled(false);
                }
                System.out.println("remove");
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                String passwd = passwordTF.getText();
                if (!okButton.isEnabled()
                        && !ValueWidget.isNullOrEmpty(passwd)
                        && passwd.length() > 3) {
                    okButton.setEnabled(true);
                }
                System.out.println("insert");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // System.out.println("change");
            }
        });

        // passwordTF.addActionListener(new ActionListener() {
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // //
        // }
        // });

        JLabel rePasswordLabel = new JLabel("re password");
        GridBagConstraints gbc_rePasswordLabel = new GridBagConstraints();
        gbc_rePasswordLabel.anchor = GridBagConstraints.WEST;
        gbc_rePasswordLabel.insets = new Insets(0, 0, 5, 5);
        gbc_rePasswordLabel.gridx = 1;
        gbc_rePasswordLabel.gridy = 4;
        this.add(rePasswordLabel, gbc_rePasswordLabel);

        rePasswdTF = new JPasswordField(10);

        final GridBagConstraints gbc_rePasswdTf01 = new GridBagConstraints();
        gbc_rePasswdTf01.insets = new Insets(0, 0, 5, 5);
        gbc_rePasswdTf01.fill = GridBagConstraints.HORIZONTAL;
        gbc_rePasswdTf01.gridx = 2;
        gbc_rePasswdTf01.gridy = 4;
        this.add(rePasswdTF, gbc_rePasswdTf01);
        rePasswdTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okButton.requestFocus();
                okButton.doClick();
            }
        });

        GridBagConstraints gbc_okButton = new GridBagConstraints();
        gbc_okButton.insets = new Insets(0, 0, 0, 5);
        gbc_okButton.anchor = GridBagConstraints.EAST;
        gbc_okButton.gridx = 1;
        gbc_okButton.gridy = 10;
        this.add(okButton, gbc_okButton);
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取旧密码
                char[] oldPasswd = oldPasswordTF.getPassword();
                if (ValueWidget.isNullOrEmpty(oldPasswd)) {
                    GUIUtil23.warningDialog("请输入旧密码");
                    oldPasswordTF.requestFocus();
                    oldPasswordTF.selectAll();
                    return;
                }
                String oldPassword = new String(oldPasswd);
                if (!ValueWidget.isNullOrEmpty(user)) {
                    try {
                        if (!PassUtil.encrpytDESPasswordOrUsername(oldPassword).equals(user.getPassword())) {
                            System.out.println("realPass:" + user.getPassword());
                            GUIUtil23.warningDialog("旧密码不正确，请重新输入.");
                            oldPasswordTF.requestFocus();
                            oldPasswordTF.selectAll();
                            return;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        GUIUtil23.errorDialog(e1.getMessage());
                    }
                }
                String password = passwordTF.getText();
                if (StringUtils.isEmpty(password)) {
                    GUIUtil23.warningDialog("新密码不能为空");
                    passwordTF.requestFocus();
                    passwordTF.selectAll();
                    return;
                }
                String rePassword = rePasswdTF.getText();
                if (!password.equals(rePassword)) {
                    GUIUtil23.warningDialog("新密码不一致。");
                    return;
                }
                newPassword = password;
                closeDialog();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // JFrame frame_tmp = owner;
                // if (frame_tmp != null) {
                // frame_tmp.dispose();
                // }
                // System.exit(0);
                newPassword = null;
                closeDialog();
            }
        });
        GridBagConstraints gbc_cancelBtn2 = new GridBagConstraints();
        gbc_cancelBtn2.insets = new Insets(0, 0, 0, 5);
        gbc_cancelBtn2.anchor = GridBagConstraints.WEST;
        gbc_cancelBtn2.gridx = 2;
        gbc_cancelBtn2.gridy = 10;
        this.add(cancelBtn, gbc_cancelBtn2);
        this.setVisible(true);

    }

    public static void main(String[] args) {
        new ModifyPasswordDialog(null, "", false, null);
    }

    private void closeDialog() {
        ModifyPasswordDialog.this.dispose();
    }

    public String getNewPassword() {
        return newPassword;
    }


}
