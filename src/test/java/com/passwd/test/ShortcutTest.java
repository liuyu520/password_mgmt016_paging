package com.passwd.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ShortcutTest {
    /**
     * 测试使用
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();

        JButton button = new JButton("Button");
        JTextArea textArea = new JTextArea();
        JScrollPane scroller = new JScrollPane(textArea);
        frame.getContentPane().add(button, BorderLayout.NORTH);
        frame.getContentPane().add(scroller, BorderLayout.CENTER);

        // ////////////////////////////////////////////////////////////////////////////////////
        // 注册快捷键方法
        ShortcutManager.getInstance().addShortcutListener(
                new ShortcutManager.ShortcutListener() {
                    public void handle() {
                        System.out.println("Meta + I");
                    }
                }, KeyEvent.VK_META, KeyEvent.VK_I);

        ShortcutManager.getInstance().addShortcutListener(
                new ShortcutManager.ShortcutListener() {
                    public void handle() {
                        System.out.println("Ctrl + Meta + M");
                    }
                }, KeyEvent.VK_CONTROL, KeyEvent.VK_META, KeyEvent.VK_M);

        // 测试忽略文本输入里面的快捷键事件, 当按钮得到焦点时, 快捷键事件能正常发生
        ShortcutManager.getInstance().addIgnoredComponent(textArea);
        // /////////////////////////////////////////////////////////////////////////////////////

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}
