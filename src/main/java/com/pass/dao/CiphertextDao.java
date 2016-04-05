package com.pass.dao;

import com.pass.util.ConnectionUtil;
import com.pass.util.PassUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/***
 * 密文区
 *
 * @author huangwei
 */
public class CiphertextDao {
    /***
     * @param ciphertext :明文
     * @throws Exception
     */
    public void add(String ciphertext) throws Exception {
        Connection conn = ConnectionUtil.getConn();
        // int id=getNextval(conn);
        String sql = null;
        sql = "insert into t_ciphertext(ciphertext2) values(?) ";
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setString(1, PassUtil.encrpytDES(ciphertext));
        pstm.executeUpdate();
        pstm.close();
        conn.close();
    }

    /***
     * update password according username(don't modify username)
     *
     * @param ciphertext : 明文
     * @throws Exception
     */
    public void update(String ciphertext, boolean isCiphertext) throws Exception {
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "update t_ciphertext set ciphertext2=?  ";
        PreparedStatement pstm = conn.prepareStatement(sql);
        if (!isCiphertext) {//若不是密文
            ciphertext = PassUtil.encrpytDES(ciphertext);
        }
        pstm.setString(1, ciphertext);
        pstm.executeUpdate();
        pstm.close();
        conn.close();
//		String formatSQL = sql.replaceAll("\\?", "'%s'").replace(")",
//				")" + SystemHWUtil.CRLF);
//		System.out.println(String.format(formatSQL, ciphertext));
    }

    /***
     * 返回密文
     *
     * @return
     * @throws Exception
     */
    public String getCiphertext() throws Exception {
        Connection conn = ConnectionUtil.getConn();
        String sql = null;
        sql = "select ciphertext2 from t_ciphertext ";
        PreparedStatement pstm = conn.prepareStatement(sql);
        ResultSet rs = pstm.executeQuery();
        String ciphertext2 = null;
        if (rs.next()) {
            ciphertext2 = rs.getString("ciphertext2");
        }
        pstm.close();
        conn.close();
        return ciphertext2;
    }

    /***
     * 返回明文
     *
     * @return
     * @throws Exception
     */
    public String getPlainCiphertext() throws Exception {
        return PassUtil.decrpytDES(getCiphertext());
    }
}
