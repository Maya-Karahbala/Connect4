/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connect4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JButton;

/**
 *
 * @author maya
 */
public class CirculerButton extends JButton {

    public int row, colum, lDigonal, rDigonal;
    public boolean isEmpty;

    public CirculerButton(int row,int colum) {
        this.row = row;
        this.colum = colum;
        isEmpty=true;
        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width, size.height);
        setPreferredSize(size);
        setContentAreaFilled(false);

    }

    protected void paintComponent(Graphics g) {

        if (getModel().isArmed()) {
            g.setColor(Color.red);

        } else {
            g.setColor(getBackground());
        }
        g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);

        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
    }
}
