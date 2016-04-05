package com.pass.dialog;

import com.common.util.ReflectHWUtils;
import com.common.util.SystemHWUtil;
import com.common.util.WindowUtil;
import com.file.hw.props.PropUtil;
import com.pass.bean.Pass;
import com.pass.component.MyDropListTextField;
import com.pass.dao.PassDao;
import com.pass.main.PassMgmtApp;
import com.pass.util.PassUtil;
import com.string.widget.util.ValueWidget;
import com.swing.component.AssistPopupTextArea;
import com.swing.component.AssistPopupTextField;
import com.swing.component.ComponentUtil;
import com.swing.component.ModifiedFlowLayout;
import com.swing.dialog.GenericDialog;
import com.swing.dialog.toast.ToastMessage;
import com.swing.messagebox.GUIUtil23;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

public class PassDialog extends GenericDialog {
    private static final long serialVersionUID = -5847478493290361291L;
    private final JPanel contentPanel = new JPanel();
    private Pass pass = null;
    private AssistPopupTextField titleTF;
    private AssistPopupTextField usernameTF;
    private AssistPopupTextField passwordTF;
    private AssistPopupTextArea descTextArea;
    private JButton cancelButton = null;
    private JButton okButton = null;
    private String titleStr = null;
    private String usernameStr = null;
    private String passwordStr = null;
    private String descStr = null;
    private JTextField expiration_timeTF = null;
    private PassDao passDao = new PassDao();
    /***
     * Prevent two bomb box
     */
    private boolean isShowTitleDialog = true;
    private boolean isHasExpirationField = false;
    private Pass oldPass;
    private String oldDesc = null;

    // public PassDialog(Frame owner, boolean modal)
    // {
    // super(owner, modal);
    // }

    /***
     * Add does not offer pass ,indicate that is add operate
     *
     * @param owner
     */
    public PassDialog(Frame owner) {
        this(owner, true, null, PassMgmtApp.DIALOG_TYPE_ADD);
    }

    /***
     * @param owner
     * @param modal
     * @param pass
     */
    public PassDialog(Frame owner, Pass pass, int dialog_type) {
        this(owner, true, pass, dialog_type);

        // okButton.setVisible(isEditable);
        if (dialog_type == PassMgmtApp.DIALOG_TYPE_VIEW) {// view
            this.cancelButton.setText(PassMgmtApp.ACTION_COMMAND_CLOSE);
        }
        setPass(pass);

    }

    // public PassDialog(Frame owner, boolean modal) {
    // super(owner, modal);
    // }

    /**
     * Create the dialog.
     */
    public PassDialog(Frame owner, boolean modal, final Pass pass2,
                      final int dialog_type) {

        super(owner, modal);
        readConf();
        setLoc(536, 420);

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        JPanel panel = new JPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0,
                Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                1.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JLabel lblTitle = new JLabel("Title");
        GridBagConstraints gbc_lblTitle = new GridBagConstraints();
        gbc_lblTitle.anchor = GridBagConstraints.WEST;
        gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
        gbc_lblTitle.gridx = 1;
        gbc_lblTitle.gridy = 0;
        panel.add(lblTitle, gbc_lblTitle);
        titleTF = new MyDropListTextField();
        titleTF.placeHolder("标题不能重复");
        if (titleStr != null && !titleStr.equals("")) {
            titleTF.setText(titleStr);
        }
        final boolean isEdit = dialog_type == PassMgmtApp.DIALOG_TYPE_EDIT;
        final boolean isView = dialog_type == PassMgmtApp.DIALOG_TYPE_VIEW;
        final boolean isAdd = dialog_type == PassMgmtApp.DIALOG_TYPE_ADD;
        if (dialog_type == PassMgmtApp.DIALOG_TYPE_ADD || dialog_type == PassMgmtApp.DIALOG_TYPE_EDIT) {
            titleTF.getDocument().addDocumentListener(new DocumentListener() {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            checkTitle(titleTF, pass2, dialog_type);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                };

                @Override
                public void removeUpdate(DocumentEvent e) {
                    System.out.println("remove");
                    new Thread(run).start();
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    System.out.println("insert");
                    new Thread(run).start();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    // System.out.println("change");
                }
            });
            titleTF.addFocusListener(new FocusListener() {

                @Override
                public void focusLost(FocusEvent e) {
                    try {
//			 checkTitle(titleTF);
                        ComponentUtil.trim(titleTF);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                @Override
                public void focusGained(FocusEvent e) {
                }
            });
        }//if
        // titleTF.stopUndo();
        GridBagConstraints gbc_titleTF = new GridBagConstraints();
        gbc_titleTF.insets = new Insets(0, 0, 5, 5);
        gbc_titleTF.fill = GridBagConstraints.HORIZONTAL;
        gbc_titleTF.gridx = 3;
        gbc_titleTF.gridy = 0;
        panel.add(titleTF, gbc_titleTF);
        titleTF.setColumns(10);
        JLabel usernameLabel = new JLabel("username");
        GridBagConstraints gbc_usernameLabel = new GridBagConstraints();
        gbc_usernameLabel.anchor = GridBagConstraints.WEST;
        gbc_usernameLabel.insets = new Insets(0, 0, 5, 5);
        gbc_usernameLabel.gridx = 1;
        gbc_usernameLabel.gridy = 2;
        panel.add(usernameLabel, gbc_usernameLabel);

        usernameTF = new MyDropListTextField();
        if (usernameStr != null && !usernameStr.equals("")) {
            usernameTF.setText(usernameStr);
        }
        // usernameTF.stopUndo();
        GridBagConstraints gbc_usernameTF = new GridBagConstraints();
        gbc_usernameTF.insets = new Insets(0, 0, 5, 5);
        gbc_usernameTF.fill = GridBagConstraints.HORIZONTAL;
        gbc_usernameTF.gridx = 3;
        gbc_usernameTF.gridy = 2;
        panel.add(usernameTF, gbc_usernameTF);
        usernameTF.setColumns(10);


        String buttonTitleTmp = null;
        if (isView || isEdit) {
            buttonTitleTmp = "copy";
        } else {
            buttonTitleTmp = "paste";
        }
        final JButton copyButton = new JButton(buttonTitleTmp);// copy or paste
        // button
        copyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isView || isEdit) {
                    String username = usernameTF.getText();
                    if (ValueWidget.isHasValue(username)) {
                        WindowUtil.setSysClipboardText(username);
                    }
                } else {// add operate ,paste
                    String username = WindowUtil.getSysClipboardText();
                    if (ValueWidget.isHasValue(username)) {
                        usernameTF.setText(username.trim());// trim
                    }
                    copyButton.setText("paste");// paste
                }
            }
        });
        GridBagConstraints gbc_copyButton = new GridBagConstraints();
        gbc_copyButton.insets = new Insets(0, 0, 5, 5);
        gbc_copyButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_copyButton.gridx = 4;
        gbc_copyButton.gridy = 2;
        panel.add(copyButton, gbc_copyButton);

        JLabel passwordLabel = new JLabel("password");
        GridBagConstraints gbc_passwordLabel = new GridBagConstraints();
        gbc_passwordLabel.anchor = GridBagConstraints.WEST;
        gbc_passwordLabel.insets = new Insets(0, 0, 5, 5);
        gbc_passwordLabel.gridx = 1;
        gbc_passwordLabel.gridy = 4;
        panel.add(passwordLabel, gbc_passwordLabel);

        passwordTF = new AssistPopupTextField();
        if (passwordStr != null && !passwordStr.equals("")) {
            passwordTF.setText(passwordStr);
        }
        // passwordTF.stopUndo();
        GridBagConstraints gbc_passwordTF = new GridBagConstraints();
        gbc_passwordTF.insets = new Insets(0, 0, 5, 5);
        gbc_passwordTF.fill = GridBagConstraints.HORIZONTAL;
        gbc_passwordTF.gridx = 3;
        gbc_passwordTF.gridy = 4;
        panel.add(passwordTF, gbc_passwordTF);
        passwordTF.setColumns(10);

        JButton copyPassBtn = new JButton("copy");
        GridBagConstraints gbc_copyPassBtn = new GridBagConstraints();
        gbc_copyPassBtn.insets = new Insets(0, 0, 5, 5);
        gbc_copyPassBtn.fill = GridBagConstraints.HORIZONTAL;
        gbc_copyPassBtn.gridx = 4;
        gbc_copyPassBtn.gridy = 4;
        panel.add(copyPassBtn, gbc_copyPassBtn);
        copyPassBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String passwd = passwordTF.getText();
                if (!ValueWidget.isNullOrEmpty(passwd)) {
                    WindowUtil.setSysClipboardText(passwd);
                }
            }
        });

        // JLabel expiration_timeLabel = new JLabel("expiration time");
        // GridBagConstraints gbc_expiration_timeLabel = new
        // GridBagConstraints();
        // gbc_expiration_timeLabel.anchor = GridBagConstraints.WEST;
        // gbc_expiration_timeLabel.insets = new Insets(0, 0, 5, 5);
        // gbc_expiration_timeLabel.gridx = 1;
        // gbc_expiration_timeLabel.gridy = 5;
        // panel.add(expiration_timeLabel, gbc_expiration_timeLabel);
        //
        // expiration_timeTF = new JTextField();
        // if (passwordStr != null && !passwordStr.equals("")) {
        // expiration_timeTF.setText(passwordStr);
        // }
        // passwordTF.stopUndo();
        // GridBagConstraints gbc_expiration_timeTF = new GridBagConstraints();
        // gbc_expiration_timeTF.insets = new Insets(0, 0, 5, 5);
        // gbc_expiration_timeTF.fill = GridBagConstraints.HORIZONTAL;
        // gbc_expiration_timeTF.gridx = 3;
        // gbc_expiration_timeTF.gridy = 5;
        // panel.add(expiration_timeTF, gbc_expiration_timeTF);
        // expiration_timeTF.setColumns(10);
        // expiration_timeTF.getDocument().addDocumentListener(
        // new DocumentListener() {
        // @Override
        // public void removeUpdate(DocumentEvent e) {
        // System.out.println("remove");
        // }
        //
        // private void assistDateText() {
        // Runnable doAssist = new Runnable() {
        // @Override
        // public void run() {
        // // when input "2013",will add to "2013-";when
        // // input "2013-10",will add to "2013-10-"
        // String input = expiration_timeTF.getText();
        // if (input.matches("^[0-9]{4}")) {
        // expiration_timeTF.setText(input + "-");
        // } else if (input.matches("^[0-9]{4}-[0-9]{2}")) {
        // expiration_timeTF.setText(input + "-");
        // }
        // }
        // };
        // SwingUtilities.invokeLater(doAssist);
        // }
        //
        // @Override
        // public void insertUpdate(DocumentEvent e) {
        // // System.out.println("insert");
        // assistDateText();
        // }
        //
        // @Override
        // public void changedUpdate(DocumentEvent e) {
        // // System.out.println("change");
        // }
        // });

        JLabel descriptionLabel = new JLabel("description");
        GridBagConstraints gbc_descriptionLabel = new GridBagConstraints();
        gbc_descriptionLabel.anchor = GridBagConstraints.WEST;
        gbc_descriptionLabel.insets = new Insets(0, 0, 0, 5);
        gbc_descriptionLabel.gridx = 1;
        gbc_descriptionLabel.gridy = 6;
        panel.add(descriptionLabel, gbc_descriptionLabel);

        descTextArea = new AssistPopupTextArea();
        if (descStr != null && !descStr.equals("")) {
            descTextArea.setText(descStr);
        }
        // descTextArea.stopUndo();
        // descTextArea.stopUndo();
        descTextArea.setLineWrap(true);
        descTextArea.setWrapStyleWord(true);

        GridBagConstraints gbc_descTextArea = new GridBagConstraints();
        gbc_descTextArea.insets = new Insets(0, 0, 0, 5);
        gbc_descTextArea.fill = GridBagConstraints.BOTH;
        gbc_descTextArea.gridx = 3;
        gbc_descTextArea.gridy = 6;
        Border border2 = BorderFactory.createEtchedBorder(Color.white,
                new Color(148, 145, 140));
        TitledBorder chatTitle = new TitledBorder(border2, "description");
        JScrollPane chatScroll = new JScrollPane(descTextArea);
        chatScroll.setBorder(chatTitle);
        panel.add(chatScroll, gbc_descTextArea);

        JButton encodingBtn = new JButton("转码");
        GridBagConstraints gbc_encodingBtn = new GridBagConstraints();
        gbc_encodingBtn.insets = new Insets(0, 0, 5, 5);
        gbc_encodingBtn.fill = GridBagConstraints.HORIZONTAL;
        gbc_encodingBtn.gridx = 4;
        gbc_encodingBtn.gridy = 6;
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new ModifiedFlowLayout());
        btnPanel.add(encodingBtn);
        panel.add(btnPanel, gbc_encodingBtn);
        encodingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (oldDesc == null) {//gbk-->utf-8
                    String desc22 = descTextArea.getText();
                    oldDesc = desc22;
                    descTextArea.setText(SystemHWUtil.convertGBK2UTF(desc22));
                } else {//第二次点击时,utf-->gbk
                    descTextArea.setText(SystemHWUtil.convertUTF2GBK(oldDesc));
                }
            }
        });

        JButton pasteDesBtn = ComponentUtil.getPasteBtn(descTextArea);
        btnPanel.add(pasteDesBtn);

        this.titleTF.setEditable(!isView);
        this.usernameTF.setEditable(!isView);
        this.passwordTF.setEditable(!isView);
        this.descTextArea.setEditable(!isView);
        // this.expiration_timeTF.setEditable(!isView);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        if (!isView) {// [ok]button will be hidden when view pass
            okButton = new JButton(PassMgmtApp.ACTION_COMMAND_OK + "(S)");
            okButton.setMnemonic('S');

            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (isAdd || isEdit) {
                        try {
                            if (!checkTitle(titleTF, pass2, dialog_type)) {
                                return;
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    String title = titleTF.getText2();
                    String username = usernameTF.getText2();
                    String password = passwordTF.getText2();
                    String description = descTextArea.getText2();

                    if (ValueWidget.isNullOrEmpty(title)) {
                        GUIUtil23.warningDialog("title can not be empty.");
                        titleTF.requestFocus();
                        return;
                    }
                    if (ValueWidget.isNullOrEmpty(username)) {
                        GUIUtil23.warningDialog("username can not be empty.");
                        usernameTF.requestFocus();
                        return;
                    }
                    //密码非必需,因为有时候记录的不是密码项
                    /*if (ValueWidget.isNullOrEmpty(password)) {
						GUIUtil23.warningDialog("password can not be empty.");
						passwordTF.requestFocus();
						return;
					}*/
                    String expirationTime = null;
                    if (isHasExpirationField) {
                        expirationTime = expiration_timeTF.getText();

                        if (ValueWidget.isHasValue(expirationTime)) {
                            if (!SystemHWUtil.isDate(expirationTime)) {
                                GUIUtil23
                                        .warningDialog("expiration time Must be a date format(yyyy-MM-dd).");
                                expiration_timeTF.requestFocus();
                                return;
                            }
                        }
                    }

                    // 此处并没有持久化到数据库，持久化操作是在PassMgmtApp 中完成的。
                    pass = new Pass();
                    pass.setTitle(title);
                    pass.setDescription(description);
                    pass.setUsername(username);
                    pass.setPwd(password);
                    if (isHasExpirationField) {
                        pass.setExpirationTime(expirationTime);
                    }
                    if (!isAdd) {
                        pass.setCreateTime(oldPass.getCreateTime());
                        pass.setId(oldPass.getId());
                    }

                    // pass.setIssuperpwd(issuperpwd);
                    // System.out.println(pass);
                    if (isEdit) {
                        // 判断edit是，是否有改变
                        try {
                            // 比较是否修改时，过滤掉两个属性：expirationTime,createTime
                            if (ReflectHWUtils.isSamePropertyValue(pass,
                                    oldPass, "expirationTime", "createTime")) {
//								GUIUtil23.warningDialog();
                                ToastMessage.toast("您什么都没有修改", 1000, Color.red);
                                return;
                            }
                        } catch (SecurityException e1) {
                            e1.printStackTrace();
                            GUIUtil23.warningDialog(e1.getMessage());
                        } catch (IllegalArgumentException e1) {
                            e1.printStackTrace();
                            GUIUtil23.warningDialog(e1.getMessage());
                        } catch (NoSuchFieldException e1) {
                            e1.printStackTrace();
                            GUIUtil23.warningDialog(e1.getMessage());
                        } catch (IllegalAccessException e1) {
                            e1.printStackTrace();
                            GUIUtil23.warningDialog(e1.getMessage());
                        }
                    }
                    PassDialog.this.dispose();

                }
            });
            okButton.setActionCommand(PassMgmtApp.ACTION_COMMAND_OK);
            buttonPane.add(okButton);
            getRootPane().setDefaultButton(okButton);
        }

        cancelButton = new JButton(PassMgmtApp.ACTION_COMMAND_CANCEL + "(C)");
        cancelButton.setMnemonic('C');
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 因为在PassMgmtApp 中是根据 dialog.getPass() 来判断是否是update的，如果
                // dialog.getPass()返回为null,则不update
                pass = null;
                PassDialog.this.dispose();
            }
        });
        cancelButton.setActionCommand(PassMgmtApp.ACTION_COMMAND_CANCEL);
        buttonPane.add(cancelButton);

        setPass(pass2);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            PassDialog dialog = new PassDialog(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readConf() {
        if (!new File(PassMgmtApp.CONF_PATH).exists()) {
            return;
        }
        PropUtil propUtil = new PropUtil(PassMgmtApp.CONF_PATH);
        String key2 = "is_show_copy_btn";
        if (!propUtil.isHasKey(key2)) {
            return;
        }
        titleStr = propUtil.getStr("title3");
        usernameStr = propUtil.getStr("username3");
        passwordStr = propUtil.getStr("password3");
        descStr = propUtil.getStr("desc3");
        // String issuperpwdStr2 = propUtil.getStr("issuperpwd3");
        // if (issuperpwdStr2 != null &&
        // issuperpwdStr2.equalsIgnoreCase("true")) {
        // issuperpwd = true;
        // }
        propUtil.close();
    }

    /**
     * Note : the operator is edit and view ,not add
     *
     * @param owner
     * @param title
     * @param modal
     * @param pass
     */
    // public PassDialog(Frame owner, String title, Pass pass) {
    // super(owner, title, true);
    // setPass(pass);
    // }
    public Pass getPass() {
        return pass;
    }

    /***
     * once invoke this method ,indicate that operator is edit or view.
     *
     * @param pass
     */
    public void setPass(Pass pass) {
        if (pass == null) {
            return;
        }
        if (this.titleTF == null) {
            System.out.println("[error]:this.titleTF is null.");
            return;
        }
        try {
            // 保存一份原始的信息，只限于“edit”
            oldPass = pass.clone();
        } catch (CloneNotSupportedException e) {
            GUIUtil23.warningDialog(e.getMessage());
            e.printStackTrace();
        }
        this.titleTF.setText(pass.getTitle());
        this.usernameTF.setText(pass.getUsername());
        this.passwordTF.setText(pass.getPwd());
        this.descTextArea.setText(pass.getDescription());
        String expirationTime = pass.getExpirationTime();
        if (expirationTime != null
                && expirationTime.equals(PassUtil.HAS_NOT_YET_EXPIRED_FLAG)) {
            expirationTime = "";
        }
        if (isHasExpirationField) {
            this.expiration_timeTF.setText(expirationTime);
        }
        // this.titleTF.stopUndo();
        // this.usernameTF.stopUndo();
        // this.passwordTF.stopUndo();
        // this.descTextArea.stopUndo();

    }

    private boolean checkTitle(AssistPopupTextField titleTF, Pass pass2, int dialog_type) throws Exception {
        int id;
        if (dialog_type == PassMgmtApp.DIALOG_TYPE_EDIT) {
            id = pass2.getId();
        } else {
            id = SystemHWUtil.NEGATIVE_ONE;
        }
        return checkTitle(titleTF, id);
    }

    /***
     * @param titleTF
     * @return true-->pass;false--not pass
     * @throws Exception
     */
    private boolean checkTitle(AssistPopupTextField titleTF, int id) throws Exception {
        String title = titleTF.getText2();
        if (StringUtils.isNotEmpty(title) && title.length() > 2) {// if is blank
            // ,ignore
            boolean isExist = this.passDao.isExistByTitle(title, id);
            if (isExist) {
                titleTF.requestFocus();
                titleTF.selectAll();
                if (isShowTitleDialog) {
                    isShowTitleDialog = false;
                    GUIUtil23
                            .warningDialog("title \"" + title + "\" has exist");
                    isShowTitleDialog = true;
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
