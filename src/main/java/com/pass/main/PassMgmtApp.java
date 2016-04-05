package com.pass.main;

import com.cmd.dos.hw.util.CMDUtil;
import com.common.util.ReflectHWUtils;
import com.common.util.SystemHWUtil;
import com.file.hw.props.PropUtil;
import com.kunlunsoft.excel.util.ExcelModelUtil;
import com.pass.bean.Pass;
import com.pass.bean.User;
import com.pass.common.MyTask;
import com.pass.component.MyButtonEditor;
import com.pass.component.MyButtonRender;
import com.pass.component.MyPassButton;
import com.pass.component.MyPassCheckBox;
import com.pass.dao.PassDao;
import com.pass.dao.UserDao;
import com.pass.dialog.ExportImportExcelDialog;
import com.pass.dialog.LoginDialog;
import com.pass.dialog.PassDialog;
import com.pass.dict.Constant;
import com.pass.listener.MyMenuBarActionListener;
import com.pass.util.PassUtil;
import com.string.widget.util.ValueWidget;
import com.swing.component.AssistPopupTextField;
import com.swing.component.ComponentUtil;
import com.swing.dialog.AbstractFrame;
import com.swing.dialog.GenericFrame;
import com.swing.menu.MenuUtil2;
import com.swing.messagebox.GUIUtil23;
import com.swing.table.TableUtil3;
import com.time.util.TimeHWUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.regex.Pattern;

public class PassMgmtApp implements ListSelectionListener, ItemListener {
    public static final int NUM_COLUMN = 9;
    public static final int SIZE_PAGE_5 = 5;
    public static final int SIZE_PAGE_10 = 10;
    public static final int SIZE_PAGE_15 = 15;
    public static final int SIZE_PAGE_20 = 20;
    public static final int SIZE_PAGE_30 = 30;
    /**
     * the value is relative with the sequeence of combobox
     */
    public static final int COMBOBOX_TITLE_INT = 0;
    public static final int COMBOBOX_DESC_INT = 1;
    public static final int COMBOBOX_USERNAME_INT = 2;
    public static final int COMBOBOX_PASSWORD_INT = 3;
    public static final String ACTION_COMMAND_EDIT = "edit";
    public static final String ACTION_COMMAND_DEL = "remove";
    public static final String ACTION_COMMAND_VIEW = "view";
    public static final String ACTION_COMMAND_ENCODING = "encoding";
    public static final String ACTION_COMMAND_OK = "OK";
    public static final String ACTION_COMMAND_CANCEL = "Cancel";
    public static final String ACTION_COMMAND_CLOSE = "Close";
    public static final String ACTION_COMMAND_IMPORT_EXCEL = "import excel";
    public static final String ACTION_COMMAND_READ_EXCEL = "read excel";
    public static final String ACTION_COMMAND_CIPHERTEXT_AREA = "Ciphertext area";
    public static final String CONF_PATH = "./config/conf_passwd.properties";
    public static final int STATUS_VIEW_ALL = 10;
    public static final int STATUS_SEARCH = 4;
    public static final int STATUS_NORMAL = 9;
    public static final int DIALOG_TYPE_ADD = 13;
    public static final int DIALOG_TYPE_EDIT = 14;
    public static final int DIALOG_TYPE_VIEW = 15;
    public final static int MAXTIMES_TRY = 4;
    public static final String KEY_PROP_SHOW_COPY_BUTTON = "is_show_copy_btn";
    public static final String KEY_PROP_SIZE_PER_PAGE = "size_per_page_display";
    public static final String KEY_PROP_WHETHER_NEED_LOGIN = "is_need_login34";
    /***
     * 访问密文区时是否需要重新登录
     */
    public static final String KEY_PROP_WHETHER_NEED_LOGIN_FOR_CIPHERTEXT = "is_need_login_for_Ciphertext";
    public static final String KEY_PROP_WHETHER_SHOW_WISH = "is_show_wish34";
    public static final String LABEL_PAGING_NUM_ZERO = "__/__";
    public static final String TITLE_LOGIN_DIALOG = "login platform for password mgmt";
    /***
     * C:\Users\Administrator\.password2.properties
     */
    public static final String configFilePath = System.getProperty("user.home") + File.separator + ".password2.properties";
    /***
     * 用户名存储的是明文
     */
    public static final String PROP_KEY_USERNAME = "username2";
    /***
     * 密码存储的是密文,采用3des加密
     */
    public static final String PROP_KEY_PASSWORD = "encrypted_password";
    public static final String PROP_KEY_IS_REMEMBER_PASSWORD = "is_remember_password";
    /***
     * Maximum number of attempts to login
     * <br>不能是static
     */
    public static int times_try = 4;
    public static Map<String, Object> session = new HashMap<String, Object>();
    private static int flag = 0;
    private static String combobox_title = "Title";
    private static String combobox_desc = "Description";
    private static String combobox_pwd = "Password";
    private static String combobox_username = "Username";
    boolean is_show_copy_btn = false;
    /***
     * the initial num per page dispaly can change according to conf.properties
     */
    private int size_per_page_show = SIZE_PAGE_5;
    private GenericFrame frame;
    private JTable table;
    private Object[][] datas;
    private String[] columnNames = {"No", "Title", "username", "password",
            "expiration time", "create time", "Description", "id", "delete",
            MenuUtil2.ACTION_STR_VIEW, "copy", "copy row"};// copy content
    private DefaultTableModel model;
    private PassDao passDao = new PassDao();
    private UserDao userDao = new UserDao();
    private PassDialog passDialog = null;
    private AssistPopupTextField searchTF;
    private Integer selectId = null;
    private JButton removeBtn = null;
    private JButton refreshBtn = null;
    private JButton viewBtn = null;
    private JButton addBtn = null;
    private JButton selAllBtn = null;
    private JButton deSelAllBtn = null;
    private AssistPopupTextField RowstextField = null;
    private JComboBox<String> typeComboBox = null;
    private ArrayList<MyPassCheckBox> checkBoxes = new ArrayList<MyPassCheckBox>(
            100);
    private JButton searchBtn = null;
    private JButton editBtn = null;
    private JRadioButton size5Radio = null;
    private JRadioButton size10Radio = null;
    private JRadioButton size15Radio = null;
    private JRadioButton size20Radio = null;
    private JRadioButton size30Radio = null;
    private JRadioButton[] radios = null;
    /***
     * from 0 on
     */
    private int currentPage = 0;
    private int size_per_page = 0;
    private JButton prevBtn = null;
    private JButton nextBtn = null;
    private int total_pages = 1;
    private JLabel pageStatusLbl = null;
    private JButton delAllBtn = null;
    private AssistPopupTextField pageJump2NumTF = null;
    /***
     * whether show login window
     */
//	private boolean is_need_login3 = true;
    private JButton jumpBtn = null;
    private int currentState = STATUS_NORMAL;
    private Object[] searchData = null;
    private JButton firstBtn = null;
    private JButton lastBtn = null;
    /***
     * 访问密文区时是否需要登录
     */
    private boolean is_need_login_for_Ciphertext = true;
    /**
     * whether show wish messagebox
     */
    private boolean is_show_wish = true;
    private User user;
    private JButton viewAllBtn;
    private JButton recentAllBtn;
    private JButton hideIDBtn;
    private JButton showIDBtn;
    private JButton deleteAllRecordsBtn;
    private LoginDialog loginDialog;
    private boolean isHasExpirationField = false;
    private int frameWidth = 1200;
    private JMenuItem fileM_login;
    /***
     * 关闭应用程序
     */
    private Timer timer = new Timer();
    /***
     * leave
     */
    private Timer leaveTimer = new Timer();
    /***
     * 用于关闭应用程序
     */
    private MyTask task = null;
    private MyTask leaveTask = null;
    /***
     * 是否计时在关闭应用程序了
     */
    private boolean isLocked = false;
    /***
     * 是否计时在leave了
     */
    private boolean isLeave = false;
    private int milliseconds_leave_before_time;
    private Properties props = new Properties();
    /***
     * 全量的密码项
     */
    private List<Pass> fullPasses = null;

    /**
     * Create the application.
     */
    public PassMgmtApp() {
        try {
            initialize();
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        get_size_per_page();
//		if (is_need_login3) {// whether Need login
        loginDialog(Constant.LOGIN_DIALOG_TYPE_LAUNCH_FIRST, true);
//		}
        if (is_show_wish) {
            showWishes();
        }

        // refresh(null, 0, 0, size_per_page);
        // frame.setVisible(true);
        // new Thread(new Runnable()
        // {
        // @Override
        // public void run()
        // {
        // try
        // {
        // Thread.sleep(20);
        // }
        // catch (InterruptedException e)
        // {
        // e.printStackTrace();
        // }
        // refreshBtn.doClick();
        // }
        // }).start();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    final PassMgmtApp window = new PassMgmtApp();

//					window.frame.setVisible(true);
                    // Add global shortcuts
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    // 注册应用程序全局键盘事件, 所有的键盘事件都会被此事件监听器处理.
                    toolkit.addAWTEventListener(
                            new java.awt.event.AWTEventListener() {
                                public void eventDispatched(AWTEvent event) {
                                    if (event.getClass() == KeyEvent.class) {
                                        KeyEvent kE = ((KeyEvent) event);
                                        // 处理按键事件 Ctrl+Enter
                                        if ((kE.getKeyCode() == KeyEvent.VK_ENTER)
                                                && (((InputEvent) event)
                                                .isControlDown())) {

                                            window.refreshAction();
                                        } /* page down */ else if (kE.getKeyCode() == KeyEvent.VK_PAGE_DOWN
                                                && kE.getID() == KeyEvent.KEY_PRESSED) {
                                            window.nextPageAction();
                                            // System.out.println("page down:"
                                            // + kE.getID());

                                        } /* page up */ else if (kE.getKeyCode() == KeyEvent.VK_PAGE_UP
                                                && kE.getID() == KeyEvent.KEY_PRESSED) {
                                            window.previousPageAction();
                                            // System.out.println("page up:"
                                            // + kE.getID());

                                        }/* Ctrl +K */ else if (kE.getKeyCode() == KeyEvent.VK_K
                                                && kE.isControlDown()
                                                && kE.getID() == KeyEvent.KEY_PRESSED) {
                                            System.out.println("Ctrl +K");
                                            JTextField searchTf = window.getSearchTF();
                                            searchTf.requestFocus();
                                            searchTf.selectAll();
                                        }
                                        /* home */
                                        else if (kE.getKeyCode() == KeyEvent.VK_HOME
                                                && kE.getID() == KeyEvent.KEY_PRESSED) {
                                            System.out.println("home");
                                            window.firstPageAction();
                                        }
										/* end */
                                        else if (kE.getKeyCode() == KeyEvent.VK_END
                                                && kE.getID() == KeyEvent.KEY_PRESSED) {
                                            System.out.println("end");
                                            window.lastPageAction();
                                        }
                                    }
                                }
                            }, java.awt.AWTEvent.KEY_EVENT_MASK);
                    // Focusing upon startup
                    window.getSearchTF().requestFocus();
                    // window.set_size_per_page();
                } catch (Exception e) {
                    e.printStackTrace();
                    GUIUtil23.errorDialog(e.getMessage());
                }
            }
        });
    }

    /***
     * show wish
     */
    private static void showWishes() {
        Calendar c = TimeHWUtil.getCalendar();
        int month2 = c.get(Calendar.MONTH);
        int day2 = c.get(Calendar.DAY_OF_MONTH);
        // System.out.println("month: " + month2);
        // System.out.println("day: " + day2);
        String messg = null;
        boolean isWish = false;
        if (month2 == 2) {
            if (day2 == 7) {
                isWish = true;
                messg = "Tomorrow is <font color=\"blue\">woman's day</font>! "
                        + "<br />Best wishes for you <font color=\"red\">^_^</font>";
            } else if (day2 == 8) {
                isWish = true;
                messg = "Happy woman's day! <br />Best wishes for you <font color=\"red\">^_^</font>";
            }
        }
        if (isWish) {
            GUIUtil23.infoDialog("<html>" + messg + "</html>");
        }
    }

    // public static void main(String[] args)
    // {
    // showWishes();
    // }

    /***
     * get current number per page display now. Note:it is current.
     */
    private void get_size_per_page() {

        for (int i = 0; i < radios.length; i++) {
            JRadioButton tmp_radio = radios[i];
            if (tmp_radio.isSelected()) {
                size_per_page = Integer.parseInt(tmp_radio.getText());
            }
        }
    }

    private void setMenuBar2() {
        JMenuBar menuBar = new JMenuBar();
        String modifyPasswdTitle = "modify password";
        JMenu fileM = new JMenu("File");
        JMenuItem fileM_leave = new JMenuItem("leave");
        JMenuItem fileM_modifyPassword = new JMenuItem(modifyPasswdTitle);
        JMenuItem fileM_clear = new JMenuItem("clear");
        JMenuItem fileM_import = new JMenuItem(ACTION_COMMAND_IMPORT_EXCEL);
        JMenuItem fileM_readExcel = new JMenuItem(ACTION_COMMAND_READ_EXCEL);
        // 密文区
        JMenuItem fileM_CiphertextArea = new JMenuItem(
                ACTION_COMMAND_CIPHERTEXT_AREA);
        JMenuItem fileM_not_save_password = new JMenuItem("not save password");
        //登录
        fileM_login = new JMenuItem("login");
        JMenuItem fileM_close = new JMenuItem("close");

        fileM_import.setActionCommand(ACTION_COMMAND_IMPORT_EXCEL);
        fileM_import.addActionListener(new MyMenuBarActionListener(this));

        fileM_readExcel.setActionCommand(ACTION_COMMAND_READ_EXCEL);
        fileM_readExcel.addActionListener(new MyMenuBarActionListener(this));

        fileM_CiphertextArea.setActionCommand(ACTION_COMMAND_CIPHERTEXT_AREA);
        fileM_CiphertextArea
                .addActionListener(new MyMenuBarActionListener(this));

        fileM_not_save_password.addActionListener(new MyMenuBarActionListener(this));


        fileM_close.setActionCommand("close");
        fileM_close.addActionListener(new MyMenuBarActionListener(this));

        fileM_leave.setActionCommand("leave");
        fileM_leave.addActionListener(new MyMenuBarActionListener(this));

        fileM_modifyPassword.setActionCommand(modifyPasswdTitle);
        fileM_modifyPassword
                .addActionListener(new MyMenuBarActionListener(this));

        fileM_clear.setActionCommand("clear");
        fileM_clear.addActionListener(new MyMenuBarActionListener(this));
        fileM_login.setActionCommand(MenuUtil2.ACTION_USER_LOGIN);
        fileM_login.addActionListener(new MyMenuBarActionListener(this));


        fileM.add(fileM_leave);
        fileM.add(fileM_modifyPassword);
        fileM.add(fileM_clear);
        fileM.add(fileM_import);
        fileM.add(fileM_readExcel);
        fileM.add(fileM_not_save_password);
//		fileM.add(fileM_CiphertextArea);
        fileM.add(fileM_login);
        fileM.add(fileM_close);

        JMenu helpM = new JMenu("Help");
        JMenuItem fileM_contact = new JMenuItem("contact");
        fileM_contact.setActionCommand("contact");
        fileM_contact.addActionListener(new MyMenuBarActionListener(this));
        helpM.add(fileM_contact);

        menuBar.add(fileM);

        JPopupMenu delMenu = new JPopupMenu("删除");
        menuBar.add(delMenu);
        menuBar.add(helpM);
        frame.setJMenuBar(menuBar);
    }

    /***
     * shutdown all window and frame
     */
    public void closeAllWindow() {
        frame.closeAllWindow();
//		System.exit(0);
    }

    /***
     * Pop-up dialog
     */
    public void importExcelAction() {
        ExportImportExcelDialog dialog = new ExportImportExcelDialog(
                PassMgmtApp.this, false);
        // dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public void cleanUpTable() {
        // TableModel model=this.table.getModel();
        // DefaultTableModel modol = new DefaultTableModel();
        if (model != null) {
            model.getDataVector().removeAllElements();
            model.fireTableDataChanged();
            paging2(null);
            this.datas = null;
        }
        searchTF.setText("");
    }

    /***
     * log in window
     *
     * @param isExitAfterFailed : 登录失败后,是否退出应用程序
     */
    public boolean loginDialog(boolean isExitAfterFailed, int dialogType, boolean autoLogin) {
        // show login window to login
        showLoginDialog(true, TITLE_LOGIN_DIALOG, isExitAfterFailed/*点击登录窗口的[close]是否退出整个应用*/, dialogType, autoLogin);
        if (!loginDialog.isAuthority()) {// failed to login,because username or
            // password is wrong
            if (isExitAfterFailed) {
                closeAllWindow();// Exit the application
                System.exit(0);
            }
            return false;
        } else {// log in successfully!
            this.user = loginDialog.getUser();
            //登录成功之后,把[登录]菜单置灰
            fileM_login.setEnabled(false);
        }
        loginDialog.setAuthority(false);//把变量置零
        return true;
    }

    public void loginDialog(int dialogType, boolean autoLogin) {
        loginDialog(true, dialogType, autoLogin);
    }

    /***
     * @param modal            : Whether it is a modal window
     * @param title
     * @param isExitClickClose : 点击[close]按钮是否退出整个应用
     */
    private void showLoginDialog(boolean modal, String title, boolean isExitClickClose, int dialogType, boolean autoLogin) {
        loginDialog = new LoginDialog(this, title, modal, isExitClickClose, dialogType, autoLogin);
    }

    private String getDialogTitle(int dialog_type) {
        String title = null;
        switch (dialog_type) {
            case DIALOG_TYPE_ADD:
                title = "Add";
                break;

            case DIALOG_TYPE_EDIT:
                title = "Edit";
                break;
            case DIALOG_TYPE_VIEW:
                title = "View";
                break;
            default:
                break;
        }
        return title;
    }

    /***
     * add a record
     *
     * @param dialog_type
     */
    public void dialogAdd(int dialog_type) {
        passDialog = new PassDialog(this.frame);
        // String title = getDialogTitle(dialog_type);
        passDialog.setTitle(getDialogTitle(dialog_type));
        passDialog.setVisible(true);
    }

    public void dialog(Pass pass, int dialog_type) {
        passDialog = new PassDialog(this.frame, pass, dialog_type);
        passDialog.setTitle(getDialogTitle(dialog_type));
        passDialog.setVisible(true);
    }

    public Object[][] getTableData() {
        return getTableData(null, 0);
    }

    /***
     * assemble list into table data
     *
     * @param passBeans : List
     * @return
     */
    private Object[][] getTableData(List<Pass> passBeans) {
        int length = passBeans.size();
        Object[][] datas = null;
        if (is_show_copy_btn) {
            datas = new Object[length][NUM_COLUMN + 1];
        } else {
            datas = new Object[length][NUM_COLUMN];
        }
        Object[] objs = null;
        if (length != 0 && checkBoxes.size() != 0) {
            checkBoxes.clear();
        }
        for (int i = 0; i < length; i++) {
            int column_num = 0;
            if (is_show_copy_btn) {
                column_num = NUM_COLUMN + NUM_COLUMN - 1;
            } else {
                column_num = NUM_COLUMN + NUM_COLUMN - 2;
            }
            objs = new Object[column_num];
            Pass pass = passBeans.get(i);
            MyPassCheckBox isSelCheckbox = new MyPassCheckBox(
                    String.valueOf(i + 1));
            isSelCheckbox.setOpaque(false);// 使复选框透明
            isSelCheckbox.setPass(pass);
            checkBoxes.add(isSelCheckbox);
            int rowIndex = 0;
            objs[rowIndex] = isSelCheckbox;
            rowIndex++;
            objs[rowIndex] = pass.getTitle();
            rowIndex++;
            objs[rowIndex] = pass.getUsername();
            rowIndex++;
            objs[rowIndex] = pass.getPwd();
            rowIndex++;
            objs[rowIndex] = pass.getExpirationTime();
            rowIndex++;
            objs[rowIndex] = TimeHWUtil.formatDateTime(pass.getCreateTime());
            rowIndex++;
            objs[rowIndex] = pass.getDescription();
            rowIndex++;
            objs[rowIndex] = Integer.valueOf(pass.getId());
            rowIndex++;
            MyPassButton delBtn = new MyPassButton(
                    MyPassButton.LABEL_BUTTON_DEL);
            delBtn.setPassDao(passDao);
            delBtn.setPassApp(PassMgmtApp.this);
            delBtn.setPass(pass);
            objs[rowIndex] = delBtn;
            rowIndex++;

            MyPassButton viewBtn = new MyPassButton(
                    MyPassButton.LABEL_BUTTON_VIEW);
            viewBtn.setPassDao(passDao);
            viewBtn.setPassApp(PassMgmtApp.this);
            viewBtn.setPass(pass);

            objs[rowIndex] = viewBtn;
            rowIndex++;

            MyPassButton copyContentBtn = new MyPassButton(
                    MyPassButton.LABEL_BUTTON_COPY_CONTENT);
            copyContentBtn.setPassDao(passDao);
            copyContentBtn.setPassApp(PassMgmtApp.this);
            copyContentBtn.setPass(pass);

            objs[rowIndex] = copyContentBtn;
            rowIndex++;
            if (is_show_copy_btn) {
                MyPassButton copyBtn = new MyPassButton(
                        MyPassButton.LABEL_BUTTON_COPY_ROW);
                copyBtn.setPassDao(passDao);
                copyBtn.setPassApp(PassMgmtApp.this);
                copyBtn.setPass(pass);
                objs[rowIndex] = copyBtn;
            }
            datas[i] = objs;
        }
        return datas;
    }

    public List<Pass> getPassList(String key_word, int seq) {
        List<Pass> passwordBeans = null;
        System.out.println("get datas");
        try {
            if (key_word == null) {
                passwordBeans = this.passDao.getAll();
                setFullPasses(passwordBeans);//added by huangweii @2015-06-14
            } else {
                if (seq == COMBOBOX_TITLE_INT) {
                    passwordBeans = this.passDao.getListByTitle(key_word);
                } else if (seq == COMBOBOX_DESC_INT) {
                    passwordBeans = this.passDao.getListByDesc(key_word);
                } else if (seq == COMBOBOX_USERNAME_INT) {
                    passwordBeans = this.passDao.getListByUsername(key_word);
                } else if (seq == COMBOBOX_PASSWORD_INT) {//搜索密码
//					passwordBeans = this.passDao.getListByPassword(key_word);
                    if (getFullPasses() == null) {
                        setFullPasses(this.passDao.getAll());
                    }
                    passwordBeans = ReflectHWUtils.search(getFullPasses(), key_word, "pwd");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            GUIUtil23.errorDialog(e.getMessage() + ",请确认数据库已经启动.");
            System.out.println("connect to database failed!");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            GUIUtil23.errorDialog(e.getMessage());
        }
        return passwordBeans;
    }

    /**
     * get data of table
     *
     * @return
     */
    public Object[][] getTableData(String key_word, int seq) {
        return getTableData(getPassList(key_word, seq));
    }

    /***
     * 获取最近新增的记录
     *
     * @param limit
     * @return
     * @throws Exception
     */
    public Object[][] getTableDataRecent(int limit) throws Exception {
        return getTableData(this.passDao.getAll(true, limit));
    }

    /***
     * @param passId : id of pass
     * @return
     * @throws Exception
     */
    public Object[][] getTableData(int passId) throws Exception {
        List<Pass> list = new ArrayList<Pass>();
        Pass pass = this.passDao.get(passId);
        list.add(pass);
        return getTableData(list);
    }

    public Object[][] getTableData(Pass pass) throws Exception {
        List<Pass> list = new ArrayList<Pass>();
        Pass pass2 = this.passDao.get(pass.getTitle(), pass.getPwd());
        if (pass2 != null) {
            list.add(pass2);
        }
        return getTableData(list);
    }

    // public Object[][] getTableData(int passId) {
    // List<Pass> list = new ArrayList<Pass>();
    // Pass pass2 = this.passDao.get(passId);
    // list.add(pass2);
    // return getTableData(list);
    // }

    public void refresh() {
        // refresh(null, 0);
        if (currentPage < 0) {
            currentPage = 0;
        }
        refresh(null, 0, currentPage * size_per_page, size_per_page);
    }

    private void setAbleBtn(boolean bool) {
        this.removeBtn.setEnabled(bool);
        // this.addBtn.setEnabled(bool);
        this.editBtn.setEnabled(bool);
        this.viewBtn.setEnabled(bool);
        this.selAllBtn.setEnabled(bool);
        this.deSelAllBtn.setEnabled(bool);
        this.delAllBtn.setEnabled(bool);
    }

    /***
     * @param key_word
     * @param seq
     * @param startIndex
     * @param pageSize
     */
    private void refresh(String key_word, int seq, int startIndex, int pageSize) {
        Object[][] datas2 = getTableData(key_word, seq);
        datas = datas2;
        paging2(datas2, startIndex, pageSize);
    }

    private int getSumPages(Object[][] datas) {
        if (datas == null) {
            return 0;
        }
        int datas_leng = datas.length;
        // exception: /zero
        int tmp_pageSize = size_per_page == 0 ? 0
                : (datas_leng / size_per_page);
        int total_pages;
        if (size_per_page == 0) {// exception: /zero
            total_pages = 0;
        } else {
            if (datas_leng % size_per_page == 0) {
                total_pages = tmp_pageSize;
            } else {
                total_pages = tmp_pageSize + 1;
            }
        }
        return total_pages;
    }

    private void paging2(Object[][] datas, int startIndex) {
        paging2(datas, startIndex, size_per_page);
    }

    private void paging2(Object[][] datas) {
        int length = 0;
        if (datas != null) {
            length = datas.length;
        }
        paging2(datas, 0, length);
    }

    private void setPageState(String total_pagesStr, String currentPageStr) {
        this.pageStatusLbl.setText("<html>" + total_pagesStr + "/"
                + String.format(SystemHWUtil.SWING_DIALOG_RED, currentPageStr)
                + "</html>");
    }

    /***
     * @param datas
     * @param indexOfpage : start from 1
     */
    public void paging_index(Object[][] datas, int indexOfpage) {
        if (datas != null && datas.length == 0) {
            return;
        }
        int startIndex = (indexOfpage - 1) * size_per_page;
        currentPage = indexOfpage - 1;
        paging2(datas, startIndex);
    }

    /***
     * @param key_word
     * @param seq
     * @param indexOfpage : start/begin from 1
     */
    public void refresh_index(String key_word, int seq, int indexOfpage) {
        Object[][] datas2 = getTableData(key_word, seq);
        datas = datas2;
        paging_index(datas2, indexOfpage);
    }

    /***
     * show the last page
     *
     * @param key_word : key word
     * @param seq      : search type: title;user name;description
     */
    public void refresh_last(String key_word, int seq) {
        Object[][] datas2 = getTableData(key_word, seq);
        datas = datas2;
        int total_pages = getSumPages(datas);
        paging_index(datas2, total_pages);
    }

    /***
     * @param datas
     * @param startIndex : if startIndex=-1,not keep status
     * @param pageSize
     */
    private void paging2(Object[][] datas, int startIndex, int pageSize) {
        if (datas == null) {
            System.out.println("[paging2]:datas is null");
            RowstextField.setText(String.valueOf(0));
            this.pageStatusLbl.setText(LABEL_PAGING_NUM_ZERO);
            return;
        }
        int datas_leng = datas.length;
        this.total_pages = getSumPages(datas);
        if (currentPage >= total_pages) {
            currentPage = total_pages - 1;
        }

        if ((currentState & STATUS_VIEW_ALL) != STATUS_VIEW_ALL)// if status is
        // view all,no
        // matter search
        {
            setPageState(String.valueOf(total_pages),
                    String.valueOf(currentPage + 1));
        }
        if (currentPage == 0) {
            prevBtn.setEnabled(false);
            firstBtn.setEnabled(false);
        } else {
            prevBtn.setEnabled(true);
            firstBtn.setEnabled(true);
        }
        if (currentPage == this.total_pages - 1) {
            nextBtn.setEnabled(false);
            lastBtn.setEnabled(false);
        } else {
            if (!nextBtn.isEnabled()) {
                nextBtn.setEnabled(true);
            }
            if (!lastBtn.isEnabled()) {
                lastBtn.setEnabled(true);
            }
        }
        if (datas_leng == 0) {

            // System.out.println("no data");
            setAbleBtn(false);
            prevBtn.setEnabled(false);
            firstBtn.setEnabled(false);
            // return;
        } else {
            if (!this.removeBtn.isEnabled()) {
                setAbleBtn(true);
            }
        }
        RowstextField.setText(String.valueOf(datas.length));
        String[] columns_tmp = null;
        if (is_show_copy_btn) {
            columns_tmp = columnNames;
        } else {
            int columns_sum_tmp = columnNames.length - 1;
            columns_tmp = new String[columns_sum_tmp];
            System.arraycopy(columnNames, 0, columns_tmp, 0, columns_sum_tmp);
        }
        if (startIndex != -1) {
            if ((startIndex == datas_leng && startIndex > 0)
                    || startIndex > datas_leng) {
                startIndex = startIndex - size_per_page;
            }
            int endIndex = startIndex + pageSize;
            if (endIndex > datas_leng) {
                endIndex = datas_leng;
            }
            Object[][] dest = null;
            int realLength = endIndex - startIndex;
            dest = new Object[realLength][NUM_COLUMN + 1];
            System.arraycopy(datas, startIndex, dest, 0, realLength);
            model = new DefaultTableModel(dest, columns_tmp);
        } else {
            model = new DefaultTableModel(datas, columns_tmp);
        }
        table.setModel(model);
        paintTable();
        this.selectId = null;
    }

    private void paintTable() {
        this.table.setShowGrid(false);

        this.table.getColumnModel().getColumn(0)
                .setCellEditor(new MyButtonEditor());
        this.table.getColumnModel().getColumn(0)
                .setCellRenderer(new MyButtonRender());

        this.table.getColumnModel().getColumn(NUM_COLUMN - 1)
                .setCellEditor(new MyButtonEditor());
        this.table.getColumnModel().getColumn(NUM_COLUMN - 1)
                .setCellRenderer(new MyButtonRender());

        this.table.getColumnModel().getColumn(NUM_COLUMN)
                .setCellEditor(new MyButtonEditor());
        this.table.getColumnModel().getColumn(NUM_COLUMN)
                .setCellRenderer(new MyButtonRender());
        if (is_show_copy_btn) {
            this.table.getColumnModel().getColumn(NUM_COLUMN + 2)
                    .setCellEditor(new MyButtonEditor());
            this.table.getColumnModel().getColumn(NUM_COLUMN + 2)
                    .setCellRenderer(new MyButtonRender());
        }
        this.table.getColumnModel().getColumn(NUM_COLUMN + 1)
                .setCellEditor(new MyButtonEditor());
        this.table.getColumnModel().getColumn(NUM_COLUMN + 1)
                .setCellRenderer(new MyButtonRender());

        TableColumnModel columns = table.getColumnModel();// column:id
        TableColumn column = columns.getColumn(NUM_COLUMN - 2);
        column.setPreferredWidth(50);
        TableUtil3.hideTableColumn(table, NUM_COLUMN - 2);
        if (!isHasExpirationField) {
            TableUtil3.hideTableColumn(table, 4);
        }
        setEnableIdBtn(true);
        repaintTable();
    }

    private void setTableTransparency() {
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setOpaque(false);
        this.table.setDefaultRenderer(Object.class, render);
    }

    /***
     * setting whether show [show id] and [hide id] button
     *
     * @param isEnable
     */
    private void setEnableIdBtn(boolean isEnable) {
        hideIDBtn.setEnabled(!isEnable);
        showIDBtn.setEnabled(isEnable);
    }

    private void readProp() {

        if (!new File(CONF_PATH).exists()) {
            return;
        }
        PropUtil propUtil = new PropUtil(CONF_PATH);
        String key2 = KEY_PROP_SHOW_COPY_BUTTON;
        String is_show_copybtn = propUtil.getStr(key2);
        if (is_show_copybtn != null && is_show_copybtn.equals("true")) {
            is_show_copy_btn = true;
        }
        // size_per_page_show
        int sizePerPageShowInt = propUtil.getInt(KEY_PROP_SIZE_PER_PAGE);
        if (sizePerPageShowInt == SIZE_PAGE_5
                || sizePerPageShowInt == SIZE_PAGE_10
                || sizePerPageShowInt == SIZE_PAGE_15
                || sizePerPageShowInt == SIZE_PAGE_20
                || sizePerPageShowInt == SIZE_PAGE_30) {
            size_per_page_show = sizePerPageShowInt;
        }
        // is_need_login3
		/*String is_need_login36 = propUtil.getStr(KEY_PROP_WHETHER_NEED_LOGIN);
		if (is_need_login36 != null && !(is_need_login36.equals(""))) {
			if (is_need_login36.equalsIgnoreCase("false")
														 * ||is_need_login36.
														 * equalsIgnoreCase("f")
														 ) {
				is_		need_login3 = false;
			}
		}*/
        // is_need_login3
        String is_need_login_for_CiphertextStr = propUtil.getStr(KEY_PROP_WHETHER_NEED_LOGIN_FOR_CIPHERTEXT);
        if (is_need_login_for_CiphertextStr != null && !(is_need_login_for_CiphertextStr.equals(""))) {
            if (is_need_login_for_CiphertextStr.equalsIgnoreCase("false")/*
																 * ||is_need_login36.
																 * equalsIgnoreCase("f")
																 */) {
                is_need_login_for_Ciphertext = false;
            }
        }
        // is_show_wish
        String is_show_wish36 = propUtil.getStr(KEY_PROP_WHETHER_SHOW_WISH);
        if (is_show_wish36 != null && !(is_show_wish36.equals(""))) {
            if (is_show_wish36.equalsIgnoreCase("false")/*
														 * ||is_need_login36.
														 * equalsIgnoreCase("f")
														 */) {
                is_show_wish = false;
            }
        }

        String leave_before_time = propUtil.getStr("milliseconds_leave_before_time");
        if (ValueWidget.isNullOrEmpty(leave_before_time)) {
            milliseconds_leave_before_time = Constant.MILLISECONDS_LEAVE_BEFORE_TIME;
        } else {
            milliseconds_leave_before_time = Integer.parseInt(leave_before_time);
        }

        propUtil.close();

        File configFile = new File(configFilePath);
        if (configFile.exists()) {
            try {
                InputStream inStream = new FileInputStream(configFile);
                props.load(inStream);
                inStream.close();//及时关闭资源
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String username22 = getPropValue(PROP_KEY_USERNAME);
        String password = getPropValue(PROP_KEY_PASSWORD);
        String isRememberPasswordStr = getPropValue(PROP_KEY_IS_REMEMBER_PASSWORD);
//		System.out.println("isRememberPasswordStr:"+isRememberPasswordStr);
        session.put(PROP_KEY_IS_REMEMBER_PASSWORD, isRememberPasswordStr);
        if (String.valueOf(true).equalsIgnoreCase(isRememberPasswordStr)) {
            session.put(PROP_KEY_USERNAME, username22);
            session.put(PROP_KEY_PASSWORD, password);
        }
        //TODO
    }

    /***
     * set select according size_per_page_show(read from conf.properties)
     */
    private void setSelectSizeRadio() {
        for (JRadioButton tmp_radio : radios) {
            if (size_per_page_show == Integer.parseInt(tmp_radio.getText())) {
                tmp_radio.setSelected(true);
            }
        }
    }

    private void addAction() {
        dialogAdd(DIALOG_TYPE_ADD);
        Pass pass = passDialog.getPass();
        if (pass != null) {
            try {
                int maxId = passDao.add(pass, false);
                // refresh_last(null, 0);
                Object[][] datas2 = getTableData(maxId);
                datas = datas2;
                int total_pages;
                if (currentState == STATUS_VIEW_ALL) {
                    size_per_page = datas2.length;
                    total_pages = 1;
                } else {
                    total_pages = getSumPages(datas);
                }
                paging_index(datas2, total_pages);// display the last
                // page
                searchData = null;
                resetCurrentState(false);
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void refreshAction() {
        setViewAllBtnState(true);
        nextBtn.setEnabled(true);
        currentPage = 0;
        get_size_per_page();
        resetCurrentState(true);// must before <code>refresh(null, 0, 0,
        // size_per_page)</code>
        refresh(null, 0, 0, size_per_page);
        searchData = null;
        // System.out.println("refresh");
    }

    private void removeAction() {
        int sum_sel = ComponentUtil.getSelSum(checkBoxes, currentPage
                * size_per_page, size_per_page);
        if (sum_sel > 0) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "<html>Are you sure to remove "
                            + String.format(SystemHWUtil.SWING_DIALOG_RED,
                            sum_sel) + " record(s)?</html>",
                    "question", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                /**
                 * whether has shown warning messagebox
                 */
                // boolean isShowWarning = false;
                for (int i = 0; i < checkBoxes.size(); i++) {
                    MyPassCheckBox che = checkBoxes.get(i);
                    if (che.isSelected()) {
                        Pass pass_tmp = che.getPass();
                        try {

                            // if (pass_tmp.isIssuperpwd())
                            // {
                            // if (!isShowWarning)
                            // {
                            // GUIUtil23
                            // .warningDialog("You must not delete admin's password");
                            // isShowWarning = true;
                            // }
                            // if (!is_continue_delete_superuser)
                            // {
                            // return;
                            // }
                            // else
                            // {
                            // continue;
                            // }
                            // }
                            passDao.delete(pass_tmp);
                        } catch (SQLException e1) {
                            GUIUtil23.errorDialog(e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                }
                refreshAfterDel();

            }
        } else {
            pleaseSelectOne();
        }
        return;
        // if (selectId != null)
        // {
        // try
        // {
        // int result = JOptionPane.showConfirmDialog(null,
        // "Are you sure to remove ?", "question",
        // JOptionPane.OK_CANCEL_OPTION);
        // if (result == JOptionPane.OK_OPTION)
        // {
        // passDao.deleteById(selectId);
        // refresh();
        // }
        //
        // }
        // catch (SQLException e1)
        // {
        // e1.printStackTrace();
        // }
        // }
        // else
        // {
        // selectOne();
        // return;
        // }
    }

    private void refreshAfterDel() {
        if (currentState == STATUS_SEARCH) {
            refresh((String) searchData[0], (Integer) searchData[1],
                    currentPage * size_per_page, size_per_page);
        } else {
            refresh();
        }
    }

    private void editAction() throws Exception {
        int sum_sel = ComponentUtil.getSelSum(checkBoxes, currentPage
                * size_per_page, size_per_page);
        if (sum_sel > 0) {
            if (sum_sel > 1) {
                selectOnlyOne();
                return;
            }
            Pass pass = null;

            // pass = passDao.get(selectId);
            pass = getSelectChk().getPass();
            dialog(pass, DIALOG_TYPE_EDIT);
            Pass pass_new = passDialog.getPass();
            if (pass_new == null) {// 'Cancel' button is pressed
                return;
            }
            // pass_new.setId(pass.getId());//放到PassDialog 中
            try {
                passDao.update(pass_new);
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            selectId = null;
            // if (searchData != null && searchData[0] != null)
            if (currentState == STATUS_SEARCH) {
                refresh((String) searchData[0], (Integer) searchData[1],
                        currentPage * size_per_page, size_per_page);
            } else {
                // refresh();
                if (currentPage < 0) {
                    currentPage = 0;
                }
                Object[][] datas2 = getTableData(pass.getId());
                if (datas != null && datas.length > 0) {
                    paging_index(datas2, 1);// display the last
                }

            }

        } else {
            pleaseSelectOne();
            return;
        }
    }

    private void viewAction() {
        int sum_sel = ComponentUtil.getSelSum(checkBoxes, currentPage
                * size_per_page, size_per_page);
        if (sum_sel > 0) {
            if (sum_sel > 1) {
                selectOnlyOne();
                return;
            }
            view2();
        } else {
            pleaseSelectOne();
            return;
        }
    }

    /***
     * @param isWantRefresh : Do you want to refresh
     */
    private void selectAllAction(boolean isWantRefresh) {
        int sum_sel = ComponentUtil.getSelSum(checkBoxes, currentPage
                * size_per_page, size_per_page);
        System.out.println("sum sel:" + sum_sel);

        if (sum_sel != ComponentUtil.getCurrentPageChkboxSum(checkBoxes,
                currentPage * size_per_page, size_per_page)) {
            if (checkBoxes != null && checkBoxes.size() != 0) {
                ComponentUtil.setSelect(checkBoxes,
                        currentPage * size_per_page, size_per_page);
                if (isWantRefresh) {
                    /**
                     * very useful
                     */
                    repaintTable();
                }
            }
        } else {
            // GUIUtil23.warningDialog("You have chosen all.");
            return;
        }
    }

    /***
     * @param isWantRefresh : Do you want to refresh
     */
    private void deSelectAllAction(boolean isWantRefresh) {
        int sum_sel = ComponentUtil.getSelSum(checkBoxes, currentPage
                * size_per_page, size_per_page);
        if (sum_sel > 0) {
            if (checkBoxes != null && checkBoxes.size() != 0) {
                for (JCheckBox chk : checkBoxes) {
                    chk.setSelected(false);
                }
                if (isWantRefresh) {
                    /**
                     * very useful
                     */
                    repaintTable();
                }
            }
        }
    }

    /***
     * 刷新表格
     */
    private void repaintTable() {
        table.repaint();
    }

    /***
     * create a new table
     */
    private void initializeTable() {
        table = new JTable() {
            private static final long serialVersionUID = 7062211385425500734L;

            public boolean isCellEditable(int row, int column) { // make table
                // uneditable
                if (column == 0 || column == NUM_COLUMN - 1
                        || column == NUM_COLUMN || column == NUM_COLUMN + 1
                        || column == NUM_COLUMN + 2/* copy button */) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        // set height of per row
        this.table.setRowHeight(30);
        this.table.setRowSelectionAllowed(true);
        this.table.getTableHeader().setReorderingAllowed(false);
        final MouseInputListener mouseInputListener = getMouseInputListener(table);//
        table.addMouseListener(mouseInputListener);
        ListSelectionModel selectionMode = table.getSelectionModel();

        selectionMode.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionMode.addListSelectionListener(this);

        setTableTransparency();

    }

    private void setBackgroudImag() {
        JPanel jpanel = (JPanel) frame.getContentPane();
        jpanel.setOpaque(false); // JPanel 透明模式

        URL url = this.getClass().getResource("/com/pass/img/b2.jpg");
        if (ValueWidget.isNullOrEmpty(url)) {
            return;
        }
        ImageIcon labIma = new ImageIcon(url);
        JLabel jlabel = new JLabel(labIma); // 创建个带背景图片的JLabel
        frame.getLayeredPane().add(jlabel, new Integer(Integer.MIN_VALUE));
        jlabel.setBounds(0, 0, labIma.getIconWidth(), labIma.getIconHeight());
    }

    /**
     * Initialize the contents of the frame.
     *
     * @throws IOException
     */
    private void initialize() throws IOException {
        // DialogUtil.lookAndFeel2();
        readProp();
        frame = new GenericFrame();
        init33();
        frame.setTitle("Password Management");
        frame.setLoc(frameWidth, 600);
        frame.setIcon("com/pass/img/posterous_uploader.png", PassMgmtApp.class);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // setting background image/picture
        setBackgroudImag();
        // setting menu bar
        setMenuBar2();

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout(0, 0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // JPanel 透明模式
        buttonPanel.setLayout(new GridLayout(2, 1));
        JPanel button01Pane = new JPanel();
        button01Pane.setOpaque(false); // JPanel 透明模式
        JPanel button02Pane = new JPanel();
        button02Pane.setOpaque(false); // JPanel 透明模式
        buttonPanel.add(button01Pane);
        buttonPanel.add(button02Pane);

        refreshBtn = new JButton("Refresh");
        refreshBtn.setMnemonic('R');
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        refreshBtn.setEnabled(false);
                        refreshAction();
                        refreshBtn.setEnabled(true);
                    }
                }).start();
            }
        });
        button01Pane.add(refreshBtn);

        addBtn = new JButton("Add");
        addBtn.setMnemonic('A');
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addAction();
            }
        });
        button01Pane.add(addBtn);

        removeBtn = new JButton(ACTION_COMMAND_DEL + "(D)");
        removeBtn.setMnemonic('D');
        removeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeAction();
            }
        });
        button01Pane.add(removeBtn);

        editBtn = new JButton("Edit");
        editBtn.setMnemonic('E');
        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    editAction();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        button01Pane.add(editBtn);

        viewBtn = new JButton("View");
        viewBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewAction();
            }
        });
        button01Pane.add(viewBtn);
        deleteAllRecordsBtn = new JButton("delete all record");
        deleteAllRecordsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int passwordCount = PassMgmtApp.this.passDao.getPassCount();
                    if (passwordCount <= 0) {
                        GUIUtil23.warningDialog(Constant.ERROR_MESSAGE_NO_PASS_YET_TO_DELETE);
                        return;
                    }
                    int result = JOptionPane
                            .showConfirmDialog(
                                    null,
                                    "<html>Are you sure to remove all record(s)?</html>",
                                    "question", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        PassMgmtApp.this.passDao.deleteAll();
                        refreshAfterDel();
                        GUIUtil23.infoDialog("delete successfully.");
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        button01Pane.add(deleteAllRecordsBtn);

        selAllBtn = new JButton("Select All");
        selAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                selectAllAction(true);
            }
        });
        button02Pane.add(selAllBtn);

        deSelAllBtn = new JButton("Deselect All");
        deSelAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                deSelectAllAction(true);
            }
        });
        button02Pane.add(deSelAllBtn);

        delAllBtn = new JButton("Delete all in current page");
        delAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int passwordCount;
                try {
                    passwordCount = PassMgmtApp.this.passDao.getPassCount();
                    if (passwordCount <= 0) {
                        GUIUtil23.warningDialog(Constant.ERROR_MESSAGE_NO_PASS_YET_TO_DELETE);
                        return;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                selAllBtn.doClick();
                removeBtn.doClick();
                deSelAllBtn.doClick();
            }
        });
        button02Pane.add(delAllBtn);

        // show id column
        showIDBtn = new JButton("show id");
        showIDBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableUtil3.showTableColumn(table, NUM_COLUMN - 2);// colunm:id
                setEnableIdBtn(false);
            }
        });
        button02Pane.add(showIDBtn);

        // hide id column
        hideIDBtn = new JButton("hide id");
        hideIDBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableUtil3.hideTableColumn(table, NUM_COLUMN - 2);// colunm:id
                setEnableIdBtn(true);
            }
        });
        button02Pane.add(hideIDBtn);

        JButton exportBtn = new JButton("export");
        exportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int passwordCount;
                try {
                    passwordCount = PassMgmtApp.this.passDao.getPassCount();
                    if (passwordCount <= 0) {
                        GUIUtil23.warningDialog(Constant.ERROR_MESSAGE_NO_PASS_YET_TO_EXPORT);
                        return;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                ExportImportExcelDialog dialog = new ExportImportExcelDialog(
                        PassMgmtApp.this, true);
                // dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setModal(true);
                dialog.setVisible(true);
            }
        });
        button02Pane.add(exportBtn);

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false); // JPanel 透明模式
        topPanel.setLayout(new BorderLayout());
        topPanel.add(buttonPanel, BorderLayout.NORTH);
        JPanel pageSizePanel = new JPanel();
        pageSizePanel.setOpaque(false); // JPanel 透明模式
        JLabel pageSizeLab = new JLabel("The number of each page display :");
        pageSizePanel.add(pageSizeLab);
        ButtonGroup btnGroup = new ButtonGroup();
        size5Radio = new JRadioButton(String.valueOf(SIZE_PAGE_5));
        size10Radio = new JRadioButton(String.valueOf(SIZE_PAGE_10));
        size15Radio = new JRadioButton(String.valueOf(SIZE_PAGE_15));
        size20Radio = new JRadioButton(String.valueOf(SIZE_PAGE_20));
        size30Radio = new JRadioButton(String.valueOf(SIZE_PAGE_30));
        radios = new JRadioButton[]{this.size5Radio, this.size10Radio,
                this.size15Radio, this.size20Radio, this.size30Radio};
        // size10Radio.setSelected(true);
        setSelectSizeRadio();
        btnGroup.add(size5Radio);
        btnGroup.add(size10Radio);
        btnGroup.add(size15Radio);
        btnGroup.add(size20Radio);
        btnGroup.add(size30Radio);
        pageSizePanel.add(size5Radio);
        pageSizePanel.add(size10Radio);
        pageSizePanel.add(size15Radio);
        pageSizePanel.add(size20Radio);
        pageSizePanel.add(size30Radio);
        size5Radio.addItemListener(this);
        size10Radio.addItemListener(this);
        size15Radio.addItemListener(this);
        size20Radio.addItemListener(this);
        size30Radio.addItemListener(this);

        contentPane.add(topPanel, BorderLayout.NORTH);

        topPanel.add(pageSizePanel, BorderLayout.SOUTH);

        JPanel tablePanel = new JPanel();
        tablePanel.setOpaque(false); // JPanel 透明模式
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(Color.red);
        contentPane.add(tablePanel, BorderLayout.CENTER);
        initializeTable();

        // hideTableColumn(table,4);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane);
        scrollPane.setOpaque(false);
        table.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);// important

        JPanel southPane = new JPanel();
        southPane.setOpaque(false); // JPanel 透明模式
        contentPane.add(southPane, BorderLayout.SOUTH);
        southPane.setLayout(new GridLayout(2, 1, 0, 0));

        JPanel statePanel = new JPanel();
        statePanel.setOpaque(false); // JPanel 透明模式
        statePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        southPane.add(statePanel);

        JLabel lblSumOfRows = new JLabel("sum of rows:");
        statePanel.add(lblSumOfRows);

        RowstextField = new AssistPopupTextField();
        RowstextField.setText("0");
        RowstextField.setEditable(false);
        statePanel.add(RowstextField);

        firstBtn = new JButton("first");
        prevBtn = new JButton("previous");
        nextBtn = new JButton("next");
        lastBtn = new JButton("last");
        statePanel.add(new JSeparator());
        pageStatusLbl = new JLabel(LABEL_PAGING_NUM_ZERO);

        JLabel jumpLabel = new JLabel("jump to");
        pageJump2NumTF = new AssistPopupTextField(3);
        pageJump2NumTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jumpBtn.doClick();
                reinput();
            }
        });
        jumpBtn = new JButton("ok");
        statePanel.add(jumpLabel);
        statePanel.add(pageJump2NumTF);
        statePanel.add(jumpBtn);
        statePanel.add(pageStatusLbl);
        statePanel.add(firstBtn);
        statePanel.add(prevBtn);
        statePanel.add(nextBtn);
        statePanel.add(lastBtn);
        jumpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jumpAction();
            }
        });
        nextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextPageAction();

            }
        });

        prevBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousPageAction();
            }
        });

        firstBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // refresh_index(null, 0, 1);
                // currentPage = 0;
                firstPageAction();
            }
        });
        lastBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lastPageAction();
            }
        });
        RowstextField.setColumns(10);

        searchTF = new AssistPopupTextField();
        // searchTF.setText("a");
        JPanel searchPanle = new JPanel();
        searchPanle.setOpaque(false); // JPanel 透明模式
        searchPanle.add(searchTF);
        searchTF.setColumns(10);
        searchTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBtn.doClick();
            }
        });

        searchBtn = new JButton("search");
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searchAction();
                    }
                }).start();
            }
        });

        typeComboBox = new JComboBox<String>();
        searchPanle.add(typeComboBox);
        typeComboBox.addItem(combobox_title);
        typeComboBox.addItem(combobox_desc);
        typeComboBox.addItem(combobox_username);
        typeComboBox.addItem(combobox_pwd);
        typeComboBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchBtn.doClick();
                }
            }
        });
        southPane.add(searchPanle);
        searchPanle.add(searchBtn);
        viewAllBtn = new JButton("view all");
        viewAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        viewAllBtn.setEnabled(false);
                        viewAllAction();
                        viewAllBtn.setEnabled(true);
                    }
                }).start();

            }
        });
        searchPanle.add(viewAllBtn);
        recentAllBtn = new JButton("recent");
        recentAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        viewAllBtn.setEnabled(false);
                        try {
                            viewRecentAction();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        viewAllBtn.setEnabled(true);
                    }
                }).start();

            }
        });
        searchPanle.add(recentAllBtn);
    }

    /***
     * click [view all] button .
     */
    private void viewAllAction() {
        Object[][] datas2 = getTableData();
        viewAllNotPaging(datas2);
    }

    /***
     * click [recent] button .
     *
     * @throws Exception
     */
    private void viewRecentAction() throws Exception {
        Object[][] datas2 = getTableDataRecent(10);
        viewAllNotPaging(datas2);
    }

    /***
     * show all password ,and No paging,same as click [view all] button.
     *
     * @param datas2
     */
    private void viewAllNotPaging(Object[][] datas2) {// List<Pass>passBeans
        datas = datas2;
        // paging2(datas2,);
        // refresh(null, 0, 0, size_per_page);
        size_per_page = datas.length;
        paging2(datas2);
        setPageState("--", "--");
        setViewAllBtnState(false);
        currentState = STATUS_VIEW_ALL;
    }

    private void searchAction() {
        String key_word = searchTF.getText();
        // System.out.println(typeComboBox.getSelectedIndex());
        if (key_word == null || key_word.equals("")) {
            GUIUtil23.warningDialog("Please input key word");
            searchTF.requestFocus();
            return;
        }

        datas = getTableData(key_word, typeComboBox.getSelectedIndex());
        searchData = new Object[]{key_word, typeComboBox.getSelectedIndex()};
        reset2();
        if (currentState == STATUS_VIEW_ALL) {
            currentState = STATUS_SEARCH | STATUS_VIEW_ALL;
        } else {
            currentState = STATUS_SEARCH;
        }
        paging2(datas, currentPage * size_per_page);
        // setPageState("--", "--");
    }

    private void jumpAction() {
        String jump2NumStr = pageJump2NumTF.getText();
        if (jump2NumStr == null || jump2NumStr.equals("")) {
            GUIUtil23.warningDialog("Please specify the page number.");
            reinput();
            return;
        }
        boolean result = Pattern.matches("[\\d]+", jump2NumStr);
        if (!result) {
            GUIUtil23.warningDialog("Must be digit.");
            reinput();
            return;
        }

        int jump2Num = 1;
        jump2Num = Integer.parseInt(jump2NumStr);
        if (jump2Num < 1) {
            GUIUtil23.warningDialog("The page number is too small");
            reinput();
            return;
        }
        if (jump2Num > getSumPages(datas)) {
            GUIUtil23.warningDialog("The page number is too big.");
            reinput();
            return;
        }
        currentPage = jump2Num - 1;
        paging2(datas, currentPage * size_per_page, size_per_page);
    }

    /***
     * set currentPage to zero
     */
    private void reset2() {
        currentPage = 0;
    }

    private void pleaseSelectOne() {
        JOptionPane.showMessageDialog(null, "Please select one", "warning",
                JOptionPane.WARNING_MESSAGE);
    }

    private void reinput() {
        pageJump2NumTF.requestFocus();
        pageJump2NumTF.selectAll();
    }

    private void selectOnlyOne() {
        JOptionPane.showMessageDialog(null, "You should only select one",
                "warning", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public synchronized void valueChanged(ListSelectionEvent e) {

        // flag++;
        // System.out.println("flag:"+flag);
        // if (flag % 2 != 0) {
        initSelectID();
        // }

    }

    private void initSelectID() {
        int row_sum = model.getRowCount();
        if (row_sum == 0) {
            return;
        }
        int selectedRowNum = table.getSelectedRow();
        // System.out.println("selectedRowNum: " + selectedRowNum);
        if (selectedRowNum != -1 && selectedRowNum < row_sum) {
            selectId = (Integer) model.getValueAt(selectedRowNum,
                    NUM_COLUMN - 2);
            // System.out.println("select id:"+selectId);
            // System.out.println(current_id);
        }
    }

    public JButton getViewBtn() {
        return viewBtn;
    }

    /*
     */
    private MouseInputListener getMouseInputListener(final JTable jTable) {
        return new MouseInputListener() {
            public void mouseClicked(MouseEvent e) {
                // processEvent(e);
                if (e.getClickCount() == 2) {
                    // System.out.println("double click");
                    try {
                        editAction();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        GUIUtil23.errorDialog(e1.getMessage());
                    }
                }

            }

            /***
             * //in order to trigger Left-click the event
             */
            public void mousePressed(MouseEvent e) {
                processEvent(e);// is necessary!!!
            }

            public void mouseReleased(MouseEvent e) {
                // processEvent(e);
                // if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0
                // && !e.isControlDown() && !e.isShiftDown())
                // {
                // popupMenu.show(tableLyz, e.getX(),
                // e.getY());//
                // }
                // super.mousePressed(e);
                if (e.getButton() == MouseEvent.BUTTON1) {// left click
                    int rowCount = jTable.getSelectedRow();
                    if (rowCount == SystemHWUtil.NEGATIVE_ONE) {
                        return;
                    }
                    // System.out.println("row:" + rowCount);
                    Object selectedChk = jTable.getValueAt(rowCount, 0);
                    if (selectedChk != null) {
                        if (!e.isControlDown()) {
                            deSelectAllAction(false);
                        }
                        MyPassCheckBox myChk = (MyPassCheckBox) selectedChk;
                        myChk.setSelected(true);
                        repaintTable();// it is necessary
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {// right click
                    // Object source=e.getSource();
                    // System.out.println(source);
                    int rowCount = jTable.getSelectedRow();
                    // System.out.println("row:" + rowCount);
                    Object selectedChk = jTable.getValueAt(rowCount, 0);
                    if (selectedChk != null) {
                        deSelectAllAction(false);
                        MyPassCheckBox myChk = (MyPassCheckBox) selectedChk;
                        myChk.setSelected(true);
                        repaintTable();// it is necessary
                    }
                    JPopupMenu popupmenu = new JPopupMenu();
                    JMenuItem editM = new JMenuItem(ACTION_COMMAND_EDIT);
                    JMenuItem delM = new JMenuItem(ACTION_COMMAND_DEL);
                    JMenuItem viewM = new JMenuItem(ACTION_COMMAND_VIEW);
                    JMenuItem encodingM = new JMenuItem(ACTION_COMMAND_ENCODING);
                    // JMenuItem editM=new JMenuItem("edit");
                    MyMenuActionListener yMenuActionListener = new MyMenuActionListener();
                    editM.addActionListener(yMenuActionListener);
                    delM.addActionListener(yMenuActionListener);
                    viewM.addActionListener(yMenuActionListener);
                    encodingM.addActionListener(yMenuActionListener);
                    popupmenu.add(editM);
                    popupmenu.add(viewM);
                    popupmenu.add(delM);
                    popupmenu.add(encodingM);
                    popupmenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            public void mouseEntered(MouseEvent e) {
                processEvent(e);
            }

            public void mouseExited(MouseEvent e) {
                processEvent(e);
            }

            public void mouseDragged(MouseEvent e) {
                processEvent(e);
            }

            public void mouseMoved(MouseEvent e) {
                processEvent(e);
            }

            private void processEvent(MouseEvent e) {
                // Right-click on
                if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                    // System.out.println(e.getModifiers());
                    // System.out.println("Right-click on");
                    int modifiers = e.getModifiers();
                    modifiers -= MouseEvent.BUTTON3_MASK;
                    modifiers |= MouseEvent.BUTTON1_MASK;
                    MouseEvent ne = new MouseEvent(e.getComponent(), e.getID(),
                            e.getWhen(), modifiers, e.getX(), e.getY(),
                            e.getClickCount(), false);
                    jTable.dispatchEvent(ne);// in order to trigger Left-click
                    // the event
                }
            }
        };
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        size_per_page = 0;
        if (this.size5Radio.isSelected()) {
            size_per_page = SIZE_PAGE_5;
        } else if (this.size10Radio.isSelected()) {
            size_per_page = SIZE_PAGE_10;
        } else if (this.size15Radio.isSelected()) {
            size_per_page = SIZE_PAGE_15;
        } else if (this.size20Radio.isSelected()) {
            size_per_page = SIZE_PAGE_20;
        } else {
            size_per_page = SIZE_PAGE_30;
        }
        // System.out.println("size:" + size);
        paging2(datas, 0, size_per_page);
    }

    private void setViewAllBtnState(boolean isEnable) {
        this.jumpBtn.setEnabled(isEnable);
        this.prevBtn.setEnabled(isEnable);
        this.nextBtn.setEnabled(isEnable);
        this.pageJump2NumTF.setEditable(isEnable);
        this.firstBtn.setEnabled(isEnable);
        this.lastBtn.setEnabled(isEnable);
        for (JRadioButton radio_button : radios) {
            radio_button.setEnabled(isEnable);
        }
    }

    private void view2() {
        if (selectId == null) {
            initSelectID();
        }
        view2(selectId);
        // selectId = null;
    }

    public void view2(int selectId) {
        Pass pass = null;

        try {
            pass = passDao.get(selectId);
            System.out.println("view pass:" + pass);
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog(pass, DIALOG_TYPE_VIEW);
        Pass pass_new = passDialog.getPass();
        if (pass_new == null) {// 'Cancel' button is pressed
            return;
        }
        pass_new.setId(pass.getId());
        try {
            passDao.update(pass_new);
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }

        refresh();
        // System.out.println("edit");
    }

    /***
     * @param isRefresh : whether is refresh button pressed
     */
    private void resetCurrentState(boolean isRefresh) {
        if (currentState == STATUS_SEARCH) {
            currentState = 0;
        }
        if (currentState == STATUS_VIEW_ALL && isRefresh) {
            currentState = STATUS_NORMAL;
        }
    }

    /***
     * Get selected checkbox
     *
     * @return
     */
    private MyPassCheckBox getSelectChk() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            MyPassCheckBox che = checkBoxes.get(i);
            // System.out.println(che.isSelected());
            if (che.isSelected()) {
                return che;
            }
        }
        return null;
    }

    public JTextField getSearchTF() {
        return searchTF;
    }

    public AbstractFrame getFrame() {
        return frame;
    }

    /***
     * modify password
     *
     * @throws Exception
     */
    public void updatePassword(String newPassword) throws Exception {
        if (this.user != null) {// Have suggested that user has logined
            this.user.setPassword(PassUtil
                    .encrpytDESPasswordOrUsername(newPassword));
            this.userDao.update(user);
        }
    }

    /***
     * get current login user
     *
     * @return
     */
    public User getUser() {
        return user;
    }

    /***
     * export database into a excel file.
     *
     * @param isEncripyPassword :Whether password is encrypted.
     * @param excelFile
     * @throws Exception
     */
    public boolean exportAll2Excel(boolean isEncripyPassword, File excelFile)
            throws Exception {
        List<Pass> passes = null;
        passes = this.passDao.getAll();
        if (isEncripyPassword) {
            for (Pass pass : passes) {
                PassUtil.encryptPasswordDES(pass);
            }
        }

        if (ValueWidget.isNullOrEmpty(passes)) {
            GUIUtil23.warningDialog("目前没有密码可导出.");
            return false;
        } else {
            ExcelModelUtil excelUtil = new ExcelModelUtil(true, null);// in
            // classpath
            excelUtil.init("pass");
            String parentFolder = SystemHWUtil.getParentDir(excelFile.getAbsolutePath());
            File parentFile = new File(parentFolder);
            if (!parentFile.exists()) {//若目录不存在就创建
                parentFile.mkdirs();
            }
            excelUtil.model2Excel(excelFile, passes, 1, 1);
            excelUtil.close();
            return true;
        }
    }

    /***
     * import excel to database.
     *
     * @param isEncripyPassword
     * @param excelFile
     * @throws Exception
     */
    public void importFromExcel(boolean isEncripyPassword, File excelFile)
            throws Exception {
        if (ValueWidget.isNullOrEmpty(excelFile)) {
            GUIUtil23.warningDialog("Please specify a excel file.");
            return;
        }
        ExcelModelUtil excelUtil = new ExcelModelUtil(true, null);
        boolean result = excelUtil.init("pass");
        if (result) {
            int count = 0;
            @SuppressWarnings("unchecked")
            List<Pass> modelList = excelUtil.getModelList(excelFile, 0, 1, 1);
            for (int i = 0; i < modelList.size(); i++) {
                Pass pass = modelList.get(i);
                Pass passTmp = this.passDao.getByTitle(pass.getTitle());
                // title is unique
                if (ValueWidget.isNullOrEmpty(passTmp)) {
                    this.passDao.add(pass, isEncripyPassword);
                    count++;
                }
            }
            System.out.println("save password :" + count + "/"
                    + modelList.size());
        }

    }

    /***
     * Jump to next page
     */
    private void nextPageAction() {
        if (currentPage + 1 == total_pages) {
            return;
        } else if (currentPage + 2 == total_pages) {
            nextBtn.setEnabled(false);
        }
        currentPage++;
        // System.out.println("cuurent page:" + currentPage);
        paging2(datas, currentPage * size_per_page);
        if (!prevBtn.isEnabled()) {
            prevBtn.setEnabled(true);
        }
        deSelectAllAction(true);// 去掉选中
    }

    /***
     * Jump to previouse page.
     */
    private void previousPageAction() {
        if (currentPage == 0) {
            return;
        } else if (currentPage == 1) {
            prevBtn.setEnabled(false);
        }
        currentPage--;
        // System.out.println("cuurent page:" + currentPage);
        paging2(datas, currentPage * size_per_page);
        if (!nextBtn.isEnabled()) {
            nextBtn.setEnabled(true);
        }
        deSelectAllAction(true);

    }

    /***
     * Jump to first page.
     */
    private void firstPageAction() {
        reset2();
        paging_index(datas, currentPage + 1);
    }

    /***
     * Jump to last page
     */
    private void lastPageAction() {
        currentPage = total_pages - 1;
        paging_index(datas, currentPage + 1);
    }

    /***
     * read excel file,but don't access db
     *
     * @param excelFile         : excel file
     * @param isEncripyPassword : Whether the password is encrypted
     * @throws Exception
     */
    public void readExcel(File excelFile, boolean isEncripyPassword)
            throws Exception {
        if (ValueWidget.isNullOrEmpty(excelFile)) {
            GUIUtil23.warningDialog("Please specify a excel file.");
            return;
        }
        ExcelModelUtil excelUtil = new ExcelModelUtil(true, null);
        boolean result = excelUtil.init("pass");
        if (result) {
            List<Pass> modelList = excelUtil.getModelList(excelFile, 0, 1, 1);
            if (isEncripyPassword) {// password is encrypted
                int length2 = modelList.size();
                for (int i = 0; i < length2; i++) {
                    Pass pa = modelList.get(i);
                    PassUtil.decrpytPasswordDES(pa);// decrpyt password
                }
            }
            Object[][] datas2 = getTableData(modelList);
            viewAllNotPaging(datas2);

        }
    }

    public boolean isIs_show_copy_btn() {
        return is_show_copy_btn;
    }

    public void setIs_show_copy_btn(boolean is_show_copy_btn) {
        this.is_show_copy_btn = is_show_copy_btn;
    }

    public boolean isIs_show_wish() {
        return is_show_wish;
    }

/*	public boolean isIs_need_login3() {
		return is_need_login3;
	}

	public void setIs_need_login3(boolean is_need_login3) {
		this.is_need_login3 = is_need_login3;
	}*/

    public void setIs_show_wish(boolean is_show_wish) {
        this.is_show_wish = is_show_wish;
    }

    public boolean isHasExpirationField() {
        return isHasExpirationField;
    }

    public void setHasExpirationField(boolean isHasExpirationField) {
        this.isHasExpirationField = isHasExpirationField;
    }

    /***
     * 访问密文区时是否需要重新登录
     *
     * @return
     */
    public boolean isIs_need_login_for_Ciphertext() {
        return is_need_login_for_Ciphertext;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    private void init33() {
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowActivated(WindowEvent e) {
                System.out.println("window Activated");
                if (task != null) {
                    task.cancel();
                    task = null;
                    isLocked = false;//窗口激活后,就解锁了.
                }
                if (leaveTask != null) {
                    leaveTask.cancel();
                    leaveTask = null;
                    isLeave = false;
                }
                super.windowActivated(e);
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                System.out.println("window Deactivated");
                if (isLocked) {// 如果已经锁了,说明已经开始计时了,此时需要重新开始计时
                    // try to log in
                    if (task != null) {
                        task.cancel();
                        task = null;
                    }
                } else {// first into this if clause(if (timesFail >=
                    // LoginUtil.MAX_LOGIN_FAIL_TIMES ))
                    task = null;
                }
                if (timer == null) {
                    timer = new Timer();
                }

                if (task == null) {
                    task = new MyTask(PassMgmtApp.this, true);
                }
                timer.schedule(task, Constant.MILLISECONDS_WAIT_WHEN_FAIL + milliseconds_leave_before_time * 1000);
                System.out.println("开始计时");
                isLocked = true;


                if (isLeave) {// 如果已经锁了,说明已经开始计时了,此时需要重新开始计时
                    System.out.println("已经开始leave 了,直接返回");
                    return;
                    // try to log in
//					if (leaveTask != null) {
//						leaveTask.cancel();
//						leaveTask = null;
//					}
                } else {// first into this if clause(if (timesFail >=
                    // LoginUtil.MAX_LOGIN_FAIL_TIMES ))
                    leaveTask = null;
                }
                if (leaveTimer == null) {
                    leaveTimer = new Timer();
                }

                if (leaveTask == null) {
                    leaveTask = new MyTask(PassMgmtApp.this, false);
                }
                System.out.println("milliseconds_leave_before_time:" + milliseconds_leave_before_time);
                leaveTimer.schedule(leaveTask, milliseconds_leave_before_time);//五分钟开始执行leaveTask
                System.out.println("开始leave");
                isLeave = true;

                super.windowDeactivated(e);
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                System.out.println("window GainedFocus");
                super.windowGainedFocus(e);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                System.out.println("window LostFocus");
                super.windowLostFocus(e);
            }

        });
    }

    public boolean isLeave() {
        return isLeave;
    }

    public void setLeave(boolean isLeave) {
        this.isLeave = isLeave;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props2) {
        this.props = props2;
    }

    public String getPropValue(String key) {
        if (ValueWidget.isNullOrEmpty(props)) {
            return null;
        }
        return this.props.getProperty(key);
    }

    public PassDialog getPassDialog() {
        return passDialog;
    }

    public List<Pass> getFullPasses() {
        return fullPasses;
    }

    public void setFullPasses(List<Pass> fullPasses) {
        this.fullPasses = fullPasses;
    }

    /***
     * 保存到配置文件中<br>
     * C:\Users\Administrator\.password2.properties
     *
     * @throws IOException
     */
    public void saveConfig(Properties prop) throws IOException {
        File configFile = new File(PassMgmtApp.configFilePath);
        if (!configFile.exists()) {
            try {
                SystemHWUtil.createEmptyFile(configFile);
            } catch (IOException e) {
                e.printStackTrace();
                GUIUtil23.errorDialog(e);
            }
        }
        CMDUtil.show(PassMgmtApp.configFilePath);//因为隐藏文件是只读的
//        FileUtils.writeToFile(configFilePath, content);
        OutputStream out = new FileOutputStream(configFile);
        prop.store(out, TimeHWUtil.formatDateTimeZH(null));
        out.close();//及时关闭资源
        CMDUtil.hide(PassMgmtApp.configFilePath);//隐藏文件:attrib ".mqtt_client.properties" +H
    }

    public void saveConfig() throws IOException {
        saveConfig(getProps());
    }

    class MyMenuActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            System.out.println(command);
            if (command.equalsIgnoreCase(ACTION_COMMAND_DEL)) {
                removeBtn.doClick();
            } else if (command.equalsIgnoreCase(ACTION_COMMAND_EDIT)) {
                editBtn.doClick();
            } else if (command.equalsIgnoreCase(ACTION_COMMAND_VIEW)) {
                viewBtn.doClick();
            } else if (command.equalsIgnoreCase(ACTION_COMMAND_ENCODING)) {

            }

        }

    }

}
