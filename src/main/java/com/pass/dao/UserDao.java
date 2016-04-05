package com.pass.dao;

import com.pass.bean.User;
import com.pass.util.ConnectionUtil;
import com.pass.util.PassUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public void add(User user) throws SQLException {
        Connection conn = ConnectionUtil.getConn();
        // int id=getNextval(conn);
        String sql = null;
        sql = "insert into t_user(username,password) values(?,?) ";
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setString(1, user.getUsername());
        pstm.setString(2, user.getPassword());
        pstm.executeUpdate();
        pstm.close();
        conn.close();
    }

    /***
     * update password according username(don't modify username)
     *
     * @param user
     */
    public void update(User user) throws SQLException {
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "update t_user set password=? where username=? ";
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setString(1, user.getPassword());
        pstm.setString(2, user.getUsername());
        pstm.executeUpdate();
        pstm.close();
        conn.close();
//		String formatSQL = sql.replaceAll("\\?", "'%s'").replace(")",
//				")" + SystemHWUtil.CRLF);
//		System.out.println(String.format(formatSQL, user.getPassword(),
//				user.getUsername()));
    }

    public User getByNameAndPasswd(User user) throws SQLException {
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "select * from t_user where username=? and password=?  ";
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setString(1, user.getUsername());
        pstm.setString(2, user.getPassword());
        ResultSet rs = pstm.executeQuery();
        User resultUser = null;
        if (rs.next()) {
            String username = rs.getString("username");
            String password = rs.getString("password");
            resultUser = new User();
            resultUser.setUsername(username);
            resultUser.setPassword(password);
        }
        pstm.close();
        conn.close();
        return resultUser;
    }

    public User getByNameAndPasswd(String username, String password)
            throws Exception {
        User user_tmp = new User(username, password);
        user_tmp = PassUtil.encrpytDES(user_tmp);
        return getByNameAndPasswd(user_tmp);
    }
}
