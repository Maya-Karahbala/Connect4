/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;


import static game.game.frame;
import static game.game.panel;
import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author maya
 */
public class test {

    public static void main(String[] args) {
        int row = 6, colum = 7;
        JButton[][] buttons = new JButton[row][colum];
        JFrame frame = new JFrame("Connect 4");
        frame.setLayout(new BorderLayout());
        JPanel panel1 = new JPanel();
         JLabel lblName = new JLabel("Name");
        panel1.add(lblName);
        JTextField txtName = new JTextField("  player1");
        panel1.add(txtName);
        JPanel panel = new JPanel(new GridLayout(row, colum, 20, 20));
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < colum; j++) {

                buttons[i][j] = new CirculerButton( j);

               
                       

                buttons[i][j].setPreferredSize(new Dimension(130, 130));
            
                panel.add(buttons[i][j]);
                buttons[i][j].setEnabled(false);
            }

        }
       frame.add(panel1,BorderLayout.NORTH);
        frame.add(panel,BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
        /*
        Frame myFrame;  

myFrame.setLayout(new BorderLayout() );  

Panel p1;  
Panel p2;  

myFrame.add(p1, BorderLayout.NORTH);  
myFrame.add(p2, BorderLayout.CENTER);  */
    }
}
