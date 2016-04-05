package com.pass.dialog;

import com.common.util.SystemHWUtil;
import com.io.hw.file.util.FileUtils;
import com.pass.dao.CiphertextDao;
import com.swing.component.AssistPopupTextArea;
import com.swing.dialog.DialogUtil;
import com.swing.messagebox.GUIUtil23;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class CiphertextAreaDialog extends JDialog {

    private static final long serialVersionUID = -3884101575227442754L;
    private final JPanel contentPanel = new JPanel();
    private JTextArea ciphertextArea;
    private CiphertextDao ciphertextDao = new CiphertextDao();
    private JButton okButton;
    /***
     * 导出的路径
     */
    private JTextField exportFileTF;
    /***
     * 导出按钮
     */
    private JButton exportBtn;
    private JTextField importTF;
    private JButton editBtn;

    /**
     * Create the dialog.
     */
    public CiphertextAreaDialog(boolean modal) {
        setModal(modal);
        setTitle("密文区");
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(450, 300);
        Dimension framesize = getSize();
        int x = (int) screensize.getWidth() / 2 - (int) framesize.getWidth() / 2;
        int y = (int) screensize.getHeight() / 2 - (int) framesize.getHeight() / 2;
        setLocation(x, y);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        JScrollPane scrollPane = new JScrollPane();
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        ciphertextArea = new AssistPopupTextArea();
        ciphertextArea.setEditable(false);
        ciphertextArea.setLineWrap(true);
        ciphertextArea.setWrapStyleWord(true);
        ciphertextArea.getActionMap().put("save4", new AbstractAction("save4111") {
            private static final long serialVersionUID = -3548620001691220571L;

            public void actionPerformed(ActionEvent evt) {
                okButton.doClick();
                System.out.println("save");
            }
        });
        ciphertextArea.getInputMap().put(KeyStroke.getKeyStroke("control S"), "save4");

        ciphertextArea.getActionMap().put("edit4", new AbstractAction("edit4111") {
            private static final long serialVersionUID = -3548620001691220571L;

            public void actionPerformed(ActionEvent evt) {
                editBtn.doClick();
                System.out.println("edit");
            }
        });
        ciphertextArea.getInputMap().put(KeyStroke.getKeyStroke("control E"), "edit4");

        scrollPane.setViewportView(ciphertextArea);

        JPanel panel = new JPanel();
        contentPanel.add(panel, BorderLayout.SOUTH);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0, 10};
        gbl_panel.rowHeights = new int[]{0, 0, 0};
        gbl_panel.columnWeights = new double[]{0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0, 0.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JLabel label = new JLabel("导出");
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 5, 5);
        gbc_label.anchor = GridBagConstraints.EAST;
        gbc_label.gridx = 0;
        gbc_label.gridy = 0;
        panel.add(label, gbc_label);

        exportFileTF = new JTextField();// 导出文件的路径
        GridBagConstraints gbc_exportFileTF = new GridBagConstraints();
        gbc_exportFileTF.insets = new Insets(0, 0, 5, 5);
        gbc_exportFileTF.fill = GridBagConstraints.HORIZONTAL;
        gbc_exportFileTF.gridx = 1;
        gbc_exportFileTF.gridy = 0;
        panel.add(exportFileTF, gbc_exportFileTF);
        exportFileTF.setColumns(10);

        exportBtn = new JButton("导出");
        GridBagConstraints gbc_browserBtn = new GridBagConstraints();
        gbc_browserBtn.insets = new Insets(0, 0, 5, 0);
        gbc_browserBtn.gridx = 2;
        gbc_browserBtn.gridy = 0;
        panel.add(exportBtn, gbc_browserBtn);

        JLabel label_1 = new JLabel("导入");
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.anchor = GridBagConstraints.EAST;
        gbc_label_1.insets = new Insets(0, 0, 0, 5);
        gbc_label_1.gridx = 0;
        gbc_label_1.gridy = 1;
        panel.add(label_1, gbc_label_1);

        importTF = new JTextField();
        GridBagConstraints gbc_importTF = new GridBagConstraints();
        gbc_importTF.insets = new Insets(0, 0, 0, 5);
        gbc_importTF.fill = GridBagConstraints.HORIZONTAL;
        gbc_importTF.gridx = 1;
        gbc_importTF.gridy = 1;
        panel.add(importTF, gbc_importTF);
        importTF.setColumns(10);

        JButton importButton = new JButton("导入");
        GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.gridx = 2;
        gbc_button.gridy = 1;
        panel.add(importButton, gbc_button);
        importButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isValid = DialogUtil.verifyTFAndExist(importTF,
                        "要导入的文件");
                if (!isValid) {
                    return;
                }
                try {
                    String ciphertext = FileUtils.getFullContent2(new File(
                            importTF.getText()), SystemHWUtil.CHARSET_UTF);
                    int result = JOptionPane.showConfirmDialog(null,
                            "将会覆盖原来的密文，确认要导入吗 ?", "确认",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        ciphertextDao.update(ciphertext, true);
                        ciphertextArea.setText(ciphertextDao.getPlainCiphertext());
                        GUIUtil23.infoDialog("导入成功");
                    } else {
                        GUIUtil23.infoDialog("你已经取消导入");
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        });

        exportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isValid = DialogUtil.verifyTFEmpty(exportFileTF,
                        "导出文件的路径");// 检验是否为空
                if (!isValid) {
                    return;
                }
                String exportFilePath = exportFileTF.getText();
                try {
                    FileUtils.writeToFile(exportFilePath,
                            ciphertextDao.getCiphertext());
                    GUIUtil23.infoDialog("导出成功!");
                } catch (Exception e1) {
                    e1.printStackTrace();
                    GUIUtil23.errorDialog(e1.getMessage());
                }
            }
        });

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        editBtn = new JButton("Edit");
        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ciphertextArea.setEditable(true);
                ciphertextArea.requestFocus();
                okButton.setEnabled(true);
                okButton.setVisible(true);
            }
        });
        buttonPane.add(editBtn);
        okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        okButton.setEnabled(false);
        okButton.setVisible(false);
        buttonPane.add(okButton);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ciphertext2 = ciphertextArea.getText();
                try {
                    if (ciphertextDao.getCiphertext() == null) {
                        ciphertextDao.add(ciphertext2);
                        System.out.println("insert ciphertext2");
                    } else {
                        ciphertextDao.update(ciphertext2, false);
                        System.out.println("update ciphertext2");
                    }
                    ciphertextArea.setEditable(false);
                    okButton.setEnabled(false);
                    okButton.setVisible(false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });

        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ciphertextArea.setText("");
                CiphertextAreaDialog.this.dispose();
            }
        });
        try {
            init();
        } catch (Exception e1) {
            e1.printStackTrace();
            GUIUtil23.errorDialog(e1.getMessage());
        }
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        // try {
        // CiphertextAreaDialog dialog = new CiphertextAreaDialog();
        // dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // dialog.setVisible(true);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }

    private void init() throws Exception {
        ciphertextArea.setText(ciphertextDao.getPlainCiphertext());
    }

}
