package com.pass.test;

import com.swing.component.UndoTextArea;

import javax.swing.*;
import java.awt.*;

public class FrameMain extends JFrame {
    private static final long serialVersionUID = 3526577815017461757L;

    public static void main(String[] args) {
        new FrameMain().launchFrame();
    }

    public void launchFrame() {
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(2, 1));
        UndoTextArea txt1 = new UndoTextArea();
        txt1.setBackground(new Color(255, 255, 100));
        txt1.setText("txt1");
        txt1.stopUndo();
        UndoTextArea txt2 = new UndoTextArea();
        txt2.setBackground(new Color(255, 100, 100));
        txt2.setText("txt2");
        txt2.stopUndo();
        pane.add(txt1);
        pane.add(txt2);
        this.getContentPane().add(pane);
        this.setBounds(100, 100, 400, 400);
        this.setDefaultCloseOperation(3);
        this.setVisible(true);
    }

}
