package com.pass.component;

import com.swing.component.DropListTextField;

import java.util.ArrayList;
import java.util.List;

/***
 * 继承DropListTextField
 *
 * @author huangweii
 *         2015年10月28日
 */
public class MyDropListTextField extends DropListTextField {

    private static final long serialVersionUID = -6527320903816651789L;

    /***
     * 下拉菜单
     */
    @Override
    protected List<String> getDropListValue() {
        List<String> list = new ArrayList<String>();
        list.add("18701670186");
        list.add("whuang");
        list.add("17001098093");
        list.add("1287789687@qq.com");
        list.add("whuanghkl");
        list.add("15102775755");
        list.add("13429827916");
        list.add("13718486139");
        return list;
    }

}
