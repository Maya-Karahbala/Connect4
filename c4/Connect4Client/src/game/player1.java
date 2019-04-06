/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 *
 * @author maya
 */
public class player1 {

    public static int row = 6, colum = 7, g, c;

    public static int total = row * colum;
    public static JButton[][] buttons = new JButton[row][colum];
 public static Thread ini;
 public static boolean b;
   /* public static Timer timer = new Timer(2000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
           buttons[g][c].setBackground(Color.GREEN);
        }
    });*/

    public static void main(String[] args) {
        
        //timer.setRepeats(false);
           ini = new Thread(() -> {
            //soket bağlıysa dönsün
            while (true) {
                try {
                    //
                    Thread.sleep(100);
                    //eğer ikisinden biri -1 ise resim dönmeye devam etsin sonucu göstermesin
                    if (b) {
                       
                    }// eğer iki seçim yapılmışsa sonuç gösterilebilir. 
                }
                  catch (InterruptedException ex) {
                   
                }
            }
        });
        JFrame frame = new JFrame("Connect 4");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(row, colum, 10, 10));
        panel.setBackground(new Color(30, 144, 255));

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < colum; j++) {
                buttons[i][j] = new CirculerButton(j);
                buttons[i][j].addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //your actions
                        CirculerButton pressedButton = (CirculerButton) e.getSource();
                        System.out.println("colum = " + pressedButton.colum);
                        fill(pressedButton.colum);
                    }
                });
                buttons[i][j].setPreferredSize(new Dimension(130, 130));
                buttons[i][j].setBackground(new Color(30, 144, 255));
                buttons[i][j].setBackground(Color.white);
                panel.add(buttons[i][j]);
            }

        }
        buttons[row - 1][0].setBackground(Color.red);
        ((CirculerButton) buttons[row - 1][0]).isEmpty = false;

        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);

    }

    public static void fill(int colum) {

        for (int i = row - 1; i >= 0; i--) {

            if (((CirculerButton) buttons[i][colum]).isEmpty) {

                c = colum;
                for (int j = 0; j < i; j++) {
                    buttons[j][c].setBackground(Color.red);
                    System.out.println("red" + j);
                    g = j;
                    //timer.start();

                   
                }

                //timer.start();
                buttons[i][colum].setBackground(Color.red);
                ((CirculerButton) buttons[i][colum]).isEmpty = false;

                break;
            }

        }
    }
}
