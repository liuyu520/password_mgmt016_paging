package com.pass.util;

import com.common.util.SystemHWUtil;
import com.pass.bean.Pass;
import com.pass.bean.User;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class PassUtil {
    public static final String desKey4User = "jingning!@#$%";
    public static final String desKey4Password = "huangwei!@#$%";
    public static final String HAS_NOT_YET_EXPIRED_FLAG = "    *";

    /***
     * password
     *
     * @param input
     * @return
     * @throws Exception
     */
    public static String encrpytDES(String input) throws Exception {
        if (input == null) {
            return input;
        } else {
            return SystemHWUtil.encryptDES(input, desKey4Password);
        }
    }

    /***
     * password
     *
     * @param input
     * @return
     * @throws Exception
     */
    public static String decrpytDES(String input) throws Exception {
        if (input == null) {
            return input;
        } else {
            return SystemHWUtil.decryptDES(input, desKey4Password);
        }
    }

    /***
     * only decrypt property password
     *
     * @param pass
     * @return
     * @throws Exception
     */
    public static Pass decrpytPasswordDES(Pass pass) throws Exception {
        String password = pass.getPwd();
        pass.setPwd(decrpytDES(password));
        return pass;
    }

    /***
     * only encrypt property password
     *
     * @param pass
     * @return
     * @throws Exception
     */
    public static Pass encryptPasswordDES(Pass pass) throws Exception {
        String password = pass.getPwd();
        pass.setPwd(encrpytDES(password));
        return pass;
    }

    public static User encrpytDES(User user) throws IOException, Exception {
        if (user == null) {
            return null;
        } else {
            String username = user.getUsername();
            String password = user.getPassword();
            if (username != null) {
                user.setUsername(SystemHWUtil.encryptDES(username, desKey4User));
            }
            if (password != null) {
                user.setPassword(SystemHWUtil.encryptDES(password, desKey4User));
            }
            return user;
        }
    }

    /***
     * encrpyt username or password
     *
     * @param input
     * @return
     * @throws Exception
     */
    public static String encrpytDESPasswordOrUsername(String input) throws Exception {
        if (StringUtils.isEmpty(input)) {
            return input;
        }
        return SystemHWUtil.encryptDES(input, desKey4User);
    }
}
