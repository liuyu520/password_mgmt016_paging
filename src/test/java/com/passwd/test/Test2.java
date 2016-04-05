package com.passwd.test;

import com.pass.dao.PassDao;
import org.junit.Test;

import java.io.IOException;

public class Test2 {

    @Test
    public void test01() throws IOException, Exception {
//		UserDao userDao = new UserDao();
//		User user = new User("root", "root");
//		user = PassUtil.encrpytDES(user);
//		userDao.add(user);
    }

//	@Test
//	public void test02() throws Exception {
//		String input = FileUtils.getFullContent("d:\\download\\pass_02.sql",
//				"UTF-8");
//		String regex = "INSERT INTO";
//		String insertSql = SystemUtil.grepSimple(regex, input);
//		System.out.println(insertSql);
//		Pattern p = Pattern.compile("\\(([^)(]*)\\)");
//		Matcher m = p.matcher(insertSql);
//		List<String> grepResult = new ArrayList<String>();
//		while (m.find()) {
//			grepResult.add(m.group(1));
//		}
//		System.out.println(grepResult.size());
//		PassDao passDao=new PassDao();
//		 for (int i = 0; i < grepResult.size(); i++) {
//			 String[] record=grepResult.get(i).split(",");
//			 String title=SystemUtil.deleteSingleQuotes(record[1]);
//			 String username=SystemUtil.deleteSingleQuotes(record[2]);
//			 String password=SystemUtil.deleteSingleQuotes(record[3]);
//			 String desc=SystemUtil.deleteSingleQuotes(record[4]);
//			 Pass pass=new Pass();
//			 pass.setTitle(title);
//			 pass.setUsername(username);
//			 pass.setPwd(password);
//			 pass.setDescription(desc);
////			 pass=PassUtil.encrpytDES(pass);
//			 passDao.add(pass);
//		
//		 }
//	}
//	@Test
//	public void test0001() {
//		Class clazz = Pass.class;
//		System.out.println(ExcelHWUtil.generateXMLConfig(clazz));
//	}

    @Test
    public void test_isExistByTitle() {
        PassDao passDao = new PassDao();
        try {
            System.out.println(passDao.isExistByTitle("abc", 1));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
