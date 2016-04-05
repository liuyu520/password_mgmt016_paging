package com.pass.dao;

import com.common.util.SystemHWUtil;
import com.pass.bean.Pass;
import com.pass.dict.Constant;
import com.pass.util.ConnectionUtil;
import com.pass.util.PassUtil;
import com.string.widget.util.ValueWidget;
import com.time.util.TimeHWUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassDao {
    private static String password_column = "pwd";
    private static String where_status_on_clause = "status=" + Constant.NEWS_STATUS_ON;
    private static String where_status_on = " where " + where_status_on_clause;
    private String tableName = "pass";
    // private int getNextval (Connection conn) throws SQLException{
    // Statement stmt=conn.createStatement();
    // ResultSet rs=stmt.executeQuery("select nextval ('hibernate_sequence')");
    // Integer next=null;
    // if(rs.next()){
    // next=rs.getInt("nextval");
    // }
    // rs.close();
    // stmt.close();
    // return next;
    // }

    private static Pass getFromRS(ResultSet rs) throws Exception {
        String username = rs.getString("username");
        int id_tmp = rs.getInt("id");
        String description = rs.getString("description");
        String title = rs.getString("title");
        String pwd = rs.getString("pwd");
        String expirationTime = rs.getString("expiration_time");
        Timestamp createTime = rs.getTimestamp("createTime");
        // boolean issuperpwd = rs.getBoolean("issuperpwd");
        Pass pass = new Pass();
        pass.setId(id_tmp);
        pass.setDescription(description);
        pass.setPwd(pwd);
        pass.setTitle(title);
        pass.setUsername(username);
        pass.setCreateTime(createTime);
        if (ValueWidget.isHasValue(expirationTime)) {
            pass.setExpirationTime(expirationTime);
        } else {
            pass.setExpirationTime(PassUtil.HAS_NOT_YET_EXPIRED_FLAG);
        }
        pass = PassUtil.decrpytPasswordDES(pass);
        return pass;
    }

    /***
     * @param pass
     * @return :max id
     * @throws Exception
     */
    public int add(Pass pass, boolean isEncripyedPassword) throws Exception {
        Connection conn = ConnectionUtil.getConn();
        if (!isEncripyedPassword) {
            pass = PassUtil.encryptPasswordDES(pass);
        }
        String sql = null;
        sql = "insert into " + tableName + "(description,pwd,title,username,expiration_time,createTime,status) values(?,?,?,?,?,?,?) ";
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setString(1, pass.getDescription());
        pstm.setString(2, pass.getPwd());
        pstm.setString(3, pass.getTitle());
        pstm.setString(4, pass.getUsername());
        pstm.setString(5, pass.getExpirationTime());
        if (ValueWidget.isNullOrEmpty(pass.getCreateTime())) {
            pass.setCreateTime(TimeHWUtil.getCurrentTimestamp());
        }
        pstm.setTimestamp(6, pass.getCreateTime());
        pstm.setInt(7, Constant.NEWS_STATUS_ON);
        pstm.executeUpdate();
        // ResultSet rs= pstm.executeQuery("select max(id) from pass");
//		String formatSQL = sql.replaceAll("\\?", "'%s'").replace(")",
//				")" + SystemHWUtil.CRLF);
//		System.out.println(String.format(formatSQL, pass.getDescription(),
//				pass.getPwd(), pass.getTitle(), pass.getUsername(),
//				pass.getExpirationTime(), pass.getCreateTime()));
        pstm.close();
        // conn.close();
        // conn = null;
        pstm = null;

        // conn = ConnectionUtil.getConn();
        pstm = conn.prepareStatement("select max(id) as id from " + tableName +
                where_status_on);
        ResultSet rs = pstm.executeQuery();
        rs.next();
        int resultId = rs.getInt("id");
        pstm.close();
        conn.close();
        System.out.println("max id:" + resultId);

        return resultId;
    }

    public void delete(Pass pass) throws SQLException {
        deleteById(pass.getId());
    }

    /***
     * @throws SQLException
     */
    public void deleteAll() throws SQLException {
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "delete from " + tableName;
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.executeUpdate();
        pstm.close();
        conn.close();
    }

    public void deleteById(int id) throws SQLException {
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
//		sql = "delete from "+tableName+" where id=? ";
        sql = "update " + tableName + " set status=" + Constant.NEWS_STATUS_OFF + " where id=?";
        String formatSQL = sql.replaceAll("\\?", "'%s'").replace(")",
                ")" + SystemHWUtil.CRLF);
        System.out.println(String.format(formatSQL, id));
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.executeUpdate();
        pstm.close();
        conn.close();

    }

    public Pass get(int id) throws Exception {
        Pass pass = null;
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "select * from " + tableName + " where id=" + id + " ";
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            pass = getFromRS(rs);
        }
        rs.close();
        stmt.close();
        conn.close();
        return pass;
    }

    /***
     * Get pass by title.
     *
     * @param title
     * @return
     * @throws Exception
     */
    public Pass getByTitle(String title) throws Exception {
        Pass pass = null;
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "select * from " + tableName + " where title='" + title + "' ";
        // System.out.println("sql:"+sql);
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            pass = getFromRS(rs);
        }
        rs.close();
        stmt.close();
        conn.close();
        return pass;
    }

    public Pass get(String title, String password) throws Exception {
        Pass pass = null;
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "select * from " + tableName + " where title='" + title + "' and pwd='"
                + password + "' ";
        // System.out.println("sql:"+sql);
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            pass = getFromRS(rs);
        }
        rs.close();
        stmt.close();
        conn.close();
        return pass;
    }

    public void update(Pass pass) throws Exception {
        Connection conn = ConnectionUtil.getConn();
        pass = PassUtil.encryptPasswordDES(pass);
        String sql = null;
        PreparedStatement pstm = null;
        boolean passwdEmpty = ValueWidget.isNullOrEmpty(pass.getPwd());
        if (passwdEmpty) {//密码为空
            sql = "update " + tableName + " set description=?,title=?,username=?,expiration_time=?,createTime=? where id=? ";
        } else {
            sql = "update " + tableName + " set description=?,pwd=?,title=?,username=?,expiration_time=?,createTime=? where id=? ";
        }
        pstm = conn.prepareStatement(sql);
        int index = 1;
        pstm.setString(index, pass.getDescription());
        index++;
        if (passwdEmpty) {//密码为空
            index = 2;
        } else {
            pstm.setString(2, pass.getPwd());
            index = 3;
        }
        pstm.setString(index, pass.getTitle());
        index++;
        pstm.setString(index, pass.getUsername());
        index++;
        pstm.setString(index, pass.getExpirationTime());

        index++;
        pstm.setTimestamp(index, TimeHWUtil.getCurrentTimestamp());

        index++;
        pstm.setInt(index, pass.getId());

        pstm.executeUpdate();
        pstm.close();
        conn.close();
//		String formatSQL = sql.replaceAll("\\?", "'%s'").replace(")",
//				")" + SystemHWUtil.CRLF);
//		System.out.println(formatSQL);
    }

    public void flush() {
    }

    public List<Pass> getAll() throws Exception {
        return getAll(false, SystemHWUtil.NEGATIVE_ONE);
    }

    public List<Pass> getAll(boolean recently, int limit) throws Exception {
        List<Pass> list = new ArrayList<Pass>();
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "select * from " + tableName + where_status_on;
        if (recently) {
            sql += " order by createTime desc ,id desc  limit " + limit;
        }
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(sql);
        Pass pass = null;
        while (rs.next()) {
            pass = getFromRS(rs);
            list.add(pass);
        }
        rs.close();
        stmt.close();
        conn.close();
        return list;
    }

    /***
     * decide whether title exist
     *
     * @param title
     * @return
     * @throws Exception
     */
    public boolean isExistByTitle(String title, int id) throws Exception {
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
//		sql = "select * from " + tableName + " where title = '" + title + "' and " + where_status_on_clause;//因为判断title的唯一性,所以不能再加条件
        sql = "select * from " + tableName + " where title = '" + title + "' ";
        if (id != SystemHWUtil.NEGATIVE_ONE) {
            sql += " and id!=" + id;
        }
        System.out.println(sql);//TODO
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(sql);
        Pass pass = null;
        while (rs.next()) {
            pass = getFromRS(rs);
        }
        rs.close();
        stmt.close();
        conn.close();
        return pass != null;
    }

    public List<Pass> getListByTitle(String title_key_word) throws Exception {
        List<Pass> list = new ArrayList<Pass>();
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "select * from " + tableName + " where title like  '%" + title_key_word
                + "%' and " + where_status_on_clause;
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(sql);
        Pass pass = null;
        while (rs.next()) {
            pass = getFromRS(rs);
            list.add(pass);
        }
        rs.close();
        stmt.close();
        conn.close();
        return list;
    }

    public List<Pass> getListByDesc(String title_key_word) throws Exception {
        List<Pass> list = new ArrayList<Pass>();
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "select * from " + tableName + " where description like  '%" + title_key_word
                + "%' and " + where_status_on_clause;
//		System.out.println("desc");
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(sql);
        Pass pass = null;
        while (rs.next()) {
            pass = getFromRS(rs);
            list.add(pass);
        }
        rs.close();
        stmt.close();
        conn.close();
        return list;
    }

    public List<Pass> getListByUsername(String title_key_word) throws Exception {
        List<Pass> list = new ArrayList<Pass>();
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "select * from " + tableName + " where username like  '%" + title_key_word
                + "%' and " + where_status_on_clause;
        System.out.println("username");
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(sql);
        Pass pass = null;
        while (rs.next()) {
            pass = getFromRS(rs);
            list.add(pass);
        }
        rs.close();
        stmt.close();
        conn.close();
        return list;
    }

    public List<Pass> getListByIsSuper(String username, String password)
            throws Exception {
        List<Pass> list = new ArrayList<Pass>();
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "select * from " + tableName + " where  and username=? and " + password_column + "=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        // stmt.setBoolean(1, issuperpwd);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = null;
        rs = stmt.executeQuery();
        Pass pass = null;
        while (rs.next()) {
            pass = getFromRS(rs);
            list.add(pass);
        }
        rs.close();
        stmt.close();
        conn.close();
        return list;
    }

    public int getPassCount()
            throws Exception {
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "select count(*) from " + tableName + where_status_on;
        PreparedStatement stmt = conn.prepareStatement(sql);
        // stmt.setBoolean(1, issuperpwd);
        ResultSet rs = null;
        rs = stmt.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);//from one on
        }
        rs.close();
        stmt.close();
        conn.close();
        return count;
    }


}
