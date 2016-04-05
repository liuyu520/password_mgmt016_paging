package com.pass.dialog;

import com.pass.main.PassMgmtApp;
import com.string.widget.util.ValueWidget;
import com.swing.dialog.DialogUtil;
import com.swing.messagebox.GUIUtil23;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;

/***
 * read exlce file.
 *
 * @author huangwei
 * @since 2013-11-03
 */
public class ReadExcelDialog extends JDialog {

    private static final long serialVersionUID = 2823254689387594025L;
    private final JPanel contentPanel = new JPanel();
    private JTextField excelTF;
    private PassMgmtApp swingApp;

    /**
     * Create the dialog.
     */
    public ReadExcelDialog(PassMgmtApp swingApp) {
        this.setTitle("read excel");
        this.swingApp = swingApp;
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(450, 150);
        Dimension framesize = getSize();
        int x = (int) screensize.getWidth() / 2 - (int) framesize.getWidth() / 2;
        int y = (int) screensize.getHeight() / 2 - (int) framesize.getHeight() / 2;
        setLocation(x, y);

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0,
                Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);

        JLabel lblPassword = new JLabel("password:");
        GridBagConstraints gbc_lblPassword = new GridBagConstraints();
        gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
        gbc_lblPassword.gridx = 0;
        gbc_lblPassword.gridy = 0;
        contentPanel.add(lblPassword, gbc_lblPassword);

        final JCheckBox checkBox = new JCheckBox("明文");
        GridBagConstraints gbc_checkBox = new GridBagConstraints();
        gbc_checkBox.insets = new Insets(0, 0, 5, 5);
        gbc_checkBox.gridx = 2;
        gbc_checkBox.gridy = 0;
        contentPanel.add(checkBox, gbc_checkBox);
        JLabel lblExcelPath = new JLabel("excel path:");
        GridBagConstraints gbc_lblExcelPath = new GridBagConstraints();
        gbc_lblExcelPath.insets = new Insets(0, 0, 0, 5);
        gbc_lblExcelPath.gridx = 0;
        gbc_lblExcelPath.gridy = 1;
        contentPanel.add(lblExcelPath, gbc_lblExcelPath);
        excelTF = new JTextField();
        excelTF.setText("D:\\Temp\\a\\a\\a123.xls");
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(0, 0, 0, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 2;
        gbc_textField.gridy = 1;
        contentPanel.add(excelTF, gbc_textField);
        excelTF.setColumns(10);

        JButton btnNewButton = new JButton("Browser");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DialogUtil.browser3(excelTF,
                        JFileChooser.FILES_ONLY,
                        ReadExcelDialog.this);
            }
        });
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.gridx = 3;
        gbc_btnNewButton.gridy = 1;
        contentPanel.add(btnNewButton, gbc_btnNewButton);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String excelPath = excelTF.getText();
                if (ValueWidget.isNullOrEmpty(excelPath)) {
                    GUIUtil23.warningDialog("excel path can not be null");
                    DialogUtil.focusSelectAllTF(excelTF);
                    return;
                } else {
                    File excelPathFile = new File(excelPath);
                    if (!excelPathFile.exists()) {
                        GUIUtil23.warningDialog("File \"" + excelPath
                                + "\" does not exist");
                        DialogUtil.focusSelectAllTF(excelTF);
                        return;
                    }
                    try {
                        final boolean isSelected = checkBox.isSelected();
                        final boolean isEncripyPassword = !isSelected;
                        ReadExcelDialog.this.swingApp.readExcel(excelPathFile, isEncripyPassword);
                    } catch (ParseException e1) {
                        GUIUtil23.errorDialog(e1.getMessage());
                        e1.printStackTrace();
                    } catch (jxl.read.biff.BiffException e1) {
                        e1.printStackTrace();
                        GUIUtil23.warningDialog("This file is not a excel file.");
                        DialogUtil.focusSelectAllTF(excelTF);
                        return;
                    } catch (java.lang.IllegalArgumentException e2) {
                        String message = e2.getMessage();
                        if (message.contains("Null input buffer")) {
                            GUIUtil23.warningDialog("请选择 【明文】复选框");
                            return;
                        } else {
                            e2.printStackTrace();
                        }
                    } catch (Exception e3) {

                        e3.printStackTrace();
                    }
                    ReadExcelDialog.this.dispose();
                }
            }
        });
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ReadExcelDialog.this.dispose();
            }
        });
        buttonPane.add(cancelButton);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            ReadExcelDialog dialog = new ReadExcelDialog(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
