package com.pass.dialog;

import com.common.bean.DialogBean;
import com.common.util.SystemHWUtil;
import com.io.hw.file.util.FileUtils;
import com.pass.main.PassMgmtApp;
import com.string.widget.util.RandomUtils;
import com.string.widget.util.ValueWidget;
import com.swing.component.AssistPopupTextField;
import com.swing.component.ComponentUtil;
import com.swing.dialog.DialogUtil;
import com.swing.dialog.GenericDialog;
import com.swing.messagebox.GUIUtil23;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/***
 * export or import excel file.
 *
 * @author huangwei
 */
public class ExportImportExcelDialog extends GenericDialog {

    private static final long serialVersionUID = -5626054101594145780L;
    private final JPanel contentPanel = new JPanel();
    private AssistPopupTextField excelTF;
    private JCheckBox checkBox;
    private PassMgmtApp swingApp;
    private JButton okButton;
    private JButton deleteButton;

    /**
     * Create the dialog.
     */
    public ExportImportExcelDialog(PassMgmtApp swingApp, final boolean isExport) {
        this.swingApp = swingApp;
        String checkboxLabel = null;
        if (isExport) {
            setTitle("导出为excel");
            checkboxLabel = "明文导出";
        } else {
            setTitle("导入excel");
            checkboxLabel = "明文导入";
        }

        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(750, 150);
        Dimension framesize = getSize();
        int x = (int) screensize.getWidth() / 2 - (int) framesize.getWidth() / 2;
        int y = (int) screensize.getHeight() / 2 - (int) framesize.getHeight() / 2;
        setLocation(x, y);

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0,
                0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);
        JLabel passwordLabel = new JLabel("密码");
        GridBagConstraints gbc_passwordLabel = new GridBagConstraints();
        gbc_passwordLabel.anchor = GridBagConstraints.EAST;
        gbc_passwordLabel.insets = new Insets(0, 0, 5, 5);
        gbc_passwordLabel.gridx = 1;
        gbc_passwordLabel.gridy = 0;
        contentPanel.add(passwordLabel, gbc_passwordLabel);
        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.anchor = GridBagConstraints.WEST;
        gbc_panel.insets = new Insets(0, 0, 5, 5);
        gbc_panel.fill = GridBagConstraints.VERTICAL;
        gbc_panel.gridx = 3;
        gbc_panel.gridy = 0;
        contentPanel.add(panel, gbc_panel);
        checkBox = new JCheckBox(checkboxLabel);
        panel.add(checkBox);

        String labelMsg = null;
        if (isExport) {
            labelMsg = "导出为";
        } else {
            labelMsg = "导入";
        }
        JLabel label = new JLabel(labelMsg);
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 0, 5);
        gbc_label.gridx = 1;
        gbc_label.gridy = 1;
        contentPanel.add(label, gbc_label);
        excelTF = new AssistPopupTextField();
        excelTF.setText("D:\\Temp\\a\\a\\password" + RandomUtils.getTimeStr() + ".xls");
        drag(excelTF);
        GridBagConstraints gbc_excelTF = new GridBagConstraints();
        gbc_excelTF.insets = new Insets(0, 0, 0, 5);
        gbc_excelTF.fill = GridBagConstraints.HORIZONTAL;
        gbc_excelTF.gridx = 3;
        gbc_excelTF.gridy = 1;
        contentPanel.add(excelTF, gbc_excelTF);
        excelTF.setColumns(10);

        JButton browserButton = new JButton("浏览");
        GridBagConstraints gbc_browserButton = new GridBagConstraints();
        gbc_browserButton.insets = new Insets(0, 0, 0, 5);
        gbc_browserButton.gridx = 4;
        gbc_browserButton.gridy = 1;
        contentPanel.add(browserButton, gbc_browserButton);
        browserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isExport) {//export database data to excel file
                    DialogBean dialogBean = DialogUtil.showSaveDialog(excelTF,
                            ExportImportExcelDialog.this, null);
                    boolean isSuccess = dialogBean.isSuccess();
                } else {//import excel file into database
                    boolean isSuccess = DialogUtil.browser3(excelTF,
                            JFileChooser.FILES_ONLY,
                            ExportImportExcelDialog.this);
                }
                excelTF.requestFocus();
            }
        });

        // 删除已经存在的excel 文件
        deleteButton = new JButton("删除");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String excelFileStr = excelTF.getText();
                final File excelFile = new File(excelFileStr);
                if (excelFile.exists()) {
                    int result = JOptionPane.showConfirmDialog(
                            null,
                            "<html>"
                                    + "Are you sure to remove "
                                    + String.format(
                                    SystemHWUtil.SWING_DIALOG_RED,
                                    excelFileStr) + " ?</html>",
                            "question", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {//sure to delete file
                        boolean isSuccess = excelFile.delete();
                        if (!isSuccess) {// 删除失败
                            GUIUtil23.warningDialog("删除失败!请检查该文件是否已经被打开.");
                            return;
                        } else {
                            GUIUtil23.infoDialog("删除 \""
                                    + String.format(
                                    SystemHWUtil.SWING_DIALOG_RED,
                                    excelFile.getAbsoluteFile())
                                    + "\" 成功!");
                            return;
                        }
                    }
                } else {//excel file does not exist.
                    GUIUtil23.warningDialog(String.format(
                            SystemHWUtil.SWING_DIALOG_RED,
                            excelFile.getAbsoluteFile())
                            + " 不存在.");
                }
            }
        });
        GridBagConstraints gbc_deleteButton = new GridBagConstraints();
        gbc_deleteButton.insets = new Insets(0, 0, 0, 5);
        gbc_deleteButton.gridx = 5;
        gbc_deleteButton.gridy = 1;
        contentPanel.add(deleteButton, gbc_deleteButton);

        //open folder where is excel file
        JButton openButton = new JButton("打开文件夹");
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String excelFileStr = excelTF.getText();
                boolean isSuccess = FileUtils.open_file(excelFileStr);
                if (!isSuccess) {
                    GUIUtil23.warningDialog("File \"" + excelFileStr
                            + " \" does not exist.");
                    return;
                }
            }
        });
        GridBagConstraints gbc_openButton = new GridBagConstraints();
        gbc_openButton.insets = new Insets(0, 0, 0, 5);
        gbc_openButton.gridx = 6;
        gbc_openButton.gridy = 1;
        contentPanel.add(openButton, gbc_openButton);

        JButton copyBtn = ComponentUtil.getCopyBtn(excelTF);
        GridBagConstraints gbc_copyBtn22 = new GridBagConstraints();
        gbc_copyBtn22.gridx = 7;
        gbc_copyBtn22.gridy = 1;
        contentPanel.add(copyBtn, gbc_copyBtn22);


        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        String mesg = null;
        if (isExport) {
            mesg = "导出";
        } else {
            mesg = "导入";
        }
        okButton = new JButton(mesg);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final boolean isSelected = checkBox.isSelected();
                final boolean isEncripyPassword = !isSelected;
                String excelFileStr = excelTF.getText();
                if (ValueWidget.isNullOrEmpty(excelFileStr)) {
                    GUIUtil23.warningDialog("请指定excel的路径!");
                    excelTF.requestFocus();
                    return;
                }
                final File excelFile = new File(excelFileStr);
                if (excelFile.exists() && isExport) {
                    deleteButton.doClick();
                    // System.out.println("cancel");
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        okButton.setEnabled(false);
                        if (isExport) {// export as excel
                            try {
                                boolean isSuccess = ExportImportExcelDialog.this.swingApp
                                        .exportAll2Excel(isEncripyPassword,
                                                excelFile);
                                if (isSuccess) {
                                    GUIUtil23.infoDialog("导出成功!");
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        } else { // import a excel to database
                            if (!excelFile.exists()) {
                                GUIUtil23.warningDialog("File \""
                                        + excelFile.getAbsolutePath()
                                        + "\" does not exist.");
                                okButton.setEnabled(true);
                                return;
                            }
                            if (excelFile.isDirectory()) {
                                GUIUtil23.warningDialog("File \""
                                        + excelFile.getAbsolutePath()
                                        + "\" is folder(Must be a regular file).");
                                okButton.setEnabled(true);
                                return;
                            }
                            // import a excel to database
                            try {
                                ExportImportExcelDialog.this.swingApp
                                        .importFromExcel(isEncripyPassword,
                                                excelFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        okButton.setEnabled(true);
//						closeDialog();
                    }
                }).start();
            }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            ExportImportExcelDialog dialog = new ExportImportExcelDialog(null,
                    false);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * close the dialog window
     */
    private void closeDialog() {
        ExportImportExcelDialog.this.dispose();
        // System.exit(0);
    }

}
