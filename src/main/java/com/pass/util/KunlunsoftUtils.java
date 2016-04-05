package com.pass.util;

import javax.swing.*;

public class KunlunsoftUtils {
    public static final String WARNING_TITLE = "warn";
    public static final String INFO_TITLE = "infomation";
    public static final String ERROR_TITLE = "error";
    public static final String QUESTION_TITLE = "question";
    public static final String CANT_NOT_NULL = "should not be blank !";
    public static final String CONNECTIONS_ZH = "Concurrent connection number:";
    public static final String DRIVERCLASS_ZH = "Driver class name";
    public static final String USERNAME_ZH = "username";
    public static final String PASSWORD_ZH = "password";
    public static final String ERROR_CONNECT = "Connect to database failure";

    public static boolean launchDialog(JTextField tf, String info) {
        return launchDialog(tf.getText(), info);
    }

    public static boolean launchDialog(String text, String info) {
        boolean init_bool = true;
        if (text == null || text.equals("")) {
            init_bool = false;
            JOptionPane.showMessageDialog(null, info == null ? CANT_NOT_NULL : info + CANT_NOT_NULL, WARNING_TITLE,
                    JOptionPane.WARNING_MESSAGE);
        }
        return init_bool;
    }

    public static boolean launchDialog(String text) {
        return launchDialog(text, null);
    }
}
