package com.pass.util;

import com.file.hw.props.PropUtil;
import com.string.widget.util.ValueWidget;
import com.swing.dialog.toast.ToastMessage;
import com.swing.messagebox.GUIUtil23;

import java.awt.*;
import java.io.File;
import java.sql.*;

public class ConnectionUtil {
    //    public static final String MYSQL_DRIVE_CLASS = "com.mysql.jdbc.Driver";
//    public static final String MYSQL_URL         = "jdbc:mysql://localhost:3306/huangwei";
    public static final String MYSQL_DRIVE_CLASS = "org.postgresql.Driver";
    public static final String MYSQL_URL = "jdbc:postgresql://localhost:5432/passwd2";
    public static final String jdbc_config_file = "./config/jdbc_passwd.properties";
    private static Connection con_for_current_conn = null;
    private static PropUtil propUtil = null;

    static {
        File jdbcFile = new File(jdbc_config_file);
        if (!jdbcFile.exists()) {
            GUIUtil23.errorDialog("File \"" + jdbc_config_file + "\" does not exist");
        }
        propUtil = new PropUtil(jdbc_config_file);
    }

    public static void switchDb() {
        if (con_for_current_conn != null) {
            try {
                con_for_current_conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            con_for_current_conn = null;
        }
    }

    //    public static Connection getConn()
    //    {
    //        Connection conn = null;
    //        try
    //        {
    //            Class.forName(KINGBASE_DRIVE_CLASS);
    //        }
    //        catch (ClassNotFoundException e)
    //        {
    //            e.printStackTrace();
    //        }
    //        String url = KINGBASE_URL;
    //        try
    //        {
    //            conn = DriverManager.getConnection(url, "SYSTEM", "root");
    //        }
    //        catch (SQLException e)
    //        {
    //            e.printStackTrace();
    //        }
    //        return conn;
    //    }

    public static Connection getConn(String driveClass, String url,
                                     String username, String password) throws
            SQLException {
        Connection conn = null;
        if (!ValueWidget.isNullOrEmpty(driveClass)) {
            try {
                Class.forName(driveClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            conn = DriverManager.getConnection(url, username, password);
        }

        return conn;
    }

    public static Connection getConn(String username, String password) throws SQLException {
        Connection conn = null;
        String driver_class = null;
        String mysql_url = null;

        driver_class = propUtil.getStr("driver_class");
        mysql_url = propUtil.getStr("db_url");
        if (ValueWidget.isNullOrEmpty(driver_class)) {
            ToastMessage.toast("driver_class is null", 2000, Color.red);
        } else {
            conn = getConn(driver_class, mysql_url, username, password);
        }
        return conn;
    }

    public static Connection getConn() throws SQLException {
        Connection conn = null;
        conn = getConn(propUtil.getStr("username"), propUtil.getStr("password"));
        propUtil.close();
        return conn;
    }

    public static boolean closeDB(ResultSet rs, Statement stmt, Connection conn) {
        boolean init_rs = true;
        boolean init_stmt = true;
        boolean init_conn = true;
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                init_rs = false;
                e.printStackTrace();
            } finally {
                rs = null;
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                init_stmt = false;
                e.printStackTrace();
            } finally {
                stmt = null;
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                init_conn = false;
                e.printStackTrace();
            } finally {
                conn = null;
            }
        }
        return init_conn && init_stmt && init_rs;
    }

    public static int connectionNum(String driveClass, String url,
                                    String username, String password) throws ClassNotFoundException,
            SQLException {
        if (con_for_current_conn == null) {
            con_for_current_conn = getConn(driveClass, url, username, password);
        }
        Statement stmt = con_for_current_conn.createStatement();
        String sql = "select connections();";
        ResultSet rs = null;
        rs = stmt.executeQuery(sql);
        int connections = 0;
        if (rs.next()) {
            connections = rs.getInt(1);
        }
        closeDB(rs, stmt, null);
        return connections;
    }
}
