package com.pass.test;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Demo {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        String[] tableTitleArray = {"ID", "Name", "Sex"};
        Object[][] body = new Object[6][tableTitleArray.length];
        for (int i = 0; i < 6; i++) {
            body[i][0] = i;
            body[i][1] = "����";
            body[i][2] = "��";
        }
        final JTable table = new JTable(new DefaultTableModel(body, tableTitleArray));
        final MouseInputListener mouseInputListener = getMouseInputListener(table);//�������Ҽ�ѡ����
        table.addMouseListener(mouseInputListener);
//        table.addMouseMotionListener(mouseInputListener);

        frame.getContentPane().add(table, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(640, 480));
        frame.setVisible(true);
    }

    /*
     * �������Ҽ��ѡ�������ʾ�Ҽ�˵�
     */
    private static MouseInputListener getMouseInputListener(final JTable jTable) {
        return new MouseInputListener() {
            public void mouseClicked(MouseEvent e) {
                processEvent(e);
            }

            public void mousePressed(MouseEvent e) {
                processEvent(e);
            }

            public void mouseReleased(MouseEvent e) {
                processEvent(e);
                if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0
                        && !e.isControlDown() && !e.isShiftDown()) {
//                 popupMenu.show(tableLyz, e.getX(), e.getY());//�Ҽ�˵���ʾ
                }
            }

            public void mouseEntered(MouseEvent e) {
                processEvent(e);
            }

            public void mouseExited(MouseEvent e) {
                processEvent(e);
            }

            public void mouseDragged(MouseEvent e) {
                processEvent(e);
            }

            public void mouseMoved(MouseEvent e) {
                processEvent(e);
            }

            private void processEvent(MouseEvent e) {
                if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                    int modifiers = e.getModifiers();
                    modifiers -= MouseEvent.BUTTON3_MASK;
                    modifiers |= MouseEvent.BUTTON1_MASK;
                    MouseEvent ne = new MouseEvent(e.getComponent(), e.getID(),
                            e.getWhen(), modifiers, e.getX(), e.getY(), e
                            .getClickCount(), false);
                    jTable.dispatchEvent(ne);
                }
            }
        };
    }
}
