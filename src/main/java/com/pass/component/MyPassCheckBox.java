package com.pass.component;

import com.pass.bean.Pass;

import javax.swing.*;

public class MyPassCheckBox extends JCheckBox {
    private static final long serialVersionUID = -5655090604649008682L;
    private Pass pass;

    public MyPassCheckBox(String string) {
        super(string);
    }

    public Pass getPass() {
        return pass;
    }

    public void setPass(Pass pass) {
        this.pass = pass;
    }

}
