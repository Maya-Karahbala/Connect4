/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import static game.Client.Stop;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author maya
 */
public class game {

    // setting number of rpw and colıms in the game
    public static int row = 6, colum = 7;
    public static JButton[][] buttons = new JButton[row][colum];
    public static JFrame frame = new JFrame("Connect 4");
    //karşı tarafın seçimi  seçim -1 deyse seçilmemiş
    public static int RivalSelection, myselection;
    //benim seçimim seçim -1 deyse seçilmemiş

    public static JPanel panel = new JPanel(new GridLayout(row + 1, colum, 20, 20));
    //karşı tarıf
    public static Thread control;
    public static JLabel lblRivalName, lblResult, lblResult2;
    public static JTextField txtName;
    public static ImageIcon imgThisImg = new ImageIcon("images/wait.png");
    public static boolean finish;

    public static void main(String[] args) {
        // code for interface

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel.setBackground(new Color(30, 144, 255));
        // adding header
        //name 
        JLabel lblName = new JLabel("Name");
        panel.add(lblName);
        txtName = new JTextField("  player1");
        panel.add(txtName);
        //connect 
        JButton btnstart = new JButton("Start");
        panel.add(btnstart);
        btnstart.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //your actions
                
                Client.Start("127.0.0.1", 2000);
                btnstart.setEnabled(false);
                txtName.setEnabled(false);
                Thread t = new control();
                t.start();
                System.out.println("çıktı");

            }
        });

        //label
        lblResult = new JLabel();
        lblResult.setIcon(imgThisImg);
        panel.add(lblResult);
        lblResult2 = new JLabel();
        panel.add(lblResult2);
        //Rival labels 
        JLabel lblRvlName = new JLabel("Rival Name");
        panel.add(lblRvlName);
        lblRivalName = new JLabel();

        panel.add(lblRivalName);

        // adding circls to interface and buttons array
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < colum; j++) {

                buttons[i][j] = new CirculerButton( j);

                buttons[i][j].addActionListener(new ActionListener() {
                    // selecting circle
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //my actions
                        // get selected button
                        CirculerButton pressedButton = (CirculerButton) e.getSource();
                        myselection = pressedButton.colum;
                        //disable all buttons
                        enableButtons(false);
                        //
                        // send msg to player2    
                        Message msg = new Message(Message.Message_Type.Selected);
                        msg.content = myselection;
                        Client.Send(msg);
                        // change my interface                        

                        fill(pressedButton.colum, Client.color);
                       
                    }
                });
                buttons[i][j].setPreferredSize(new Dimension(130, 130));
                panel.add(buttons[i][j]);
               
            }

        }
        reset(false);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);

    }


    // when player select a buuton circle fill the first empty circle in that colum
    public static void fill(int colum, Color color) {

        for (int i = row - 1; i >= 0; i--) {
            if (((CirculerButton) buttons[i][colum]).isEmpty) {
                buttons[i][colum].setBackground(color);
                ((CirculerButton) buttons[i][colum]).color = color;
                ((CirculerButton) buttons[i][colum]).isEmpty = false;
                if (horizantalControl(i) || verticalControl(colum) || leftDigonalControl() || rightDigonalControl()) {
                    Client.Send(new Message(Message.Message_Type.Bitis));
                    lblResult.setText("  you");
                    lblResult.setFont(new Font("Arial", Font.PLAIN, 40));
                    lblResult2.setText("win");
                    lblResult2.setFont(new Font("Arial", Font.PLAIN, 40));

                    terminate();

                }
                break;

            }
        }
    }
// listen to rival selections 

    public static class control extends Thread {

        @Override
        public void run() {
            while (Client.socket.isConnected()) {

                try {
                    Thread.sleep(250);

                    if (RivalSelection != -1) {
                        fill(RivalSelection, Client.rivalColor);
                        enableButtons(true);
                        RivalSelection = -1;
                    }
                    if (finish) {
                        enableButtons(false);
                        lblResult.setText("   you");
                        lblResult.setFont(new Font("Arial", Font.PLAIN, 40));
                        lblResult2.setText("lose");
                        lblResult2.setFont(new Font("Arial", Font.PLAIN, 40));

                        terminate();

                    }

                } catch (InterruptedException ex) {
                    Logger.getLogger(game.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

    }
    ///////////////control fonksyonları

    public static boolean horizantalControl(int row) {
        for (int i = 0; i < colum - 3; i++) {
            if (((CirculerButton) buttons[row][i]).color == Client.color
                    && ((CirculerButton) buttons[row][i + 1]).color == Client.color
                    && ((CirculerButton) buttons[row][i + 2]).color == Client.color
                    && ((CirculerButton) buttons[row][i + 3]).color == Client.color) {
                return true;

            }
        }
        return false;
    }

    public static boolean verticalControl(int colum) {
        for (int i = 0; i < row - 3; i++) {
            if (((CirculerButton) buttons[i][colum]).color == Client.color
                    && ((CirculerButton) buttons[i + 1][colum]).color == Client.color
                    && ((CirculerButton) buttons[i + 2][colum]).color == Client.color
                    && ((CirculerButton) buttons[i + 3][colum]).color == Client.color) {
                return true;

            }
        }
        return false;
    }

    public static boolean leftDigonalControl() {
        for (int i = 0; i < row - 3; i++) {
            for (int j = 3; j < colum; j++) {
                if (((CirculerButton) buttons[i][j]).color == Client.color
                        && ((CirculerButton) buttons[i + 1][j - 1]).color == Client.color
                        && ((CirculerButton) buttons[i + 2][j - 2]).color == Client.color
                        && ((CirculerButton) buttons[i + 3][j - 3]).color == Client.color) {
                    return true;
                }

            }
        }
        return false;
    }

    public static boolean rightDigonalControl() {
        for (int i = 0; i < row - 3; i++) {
            for (int j = 0; j < colum - 3; j++) {
                if (((CirculerButton) buttons[i][j]).color == Client.color
                        && ((CirculerButton) buttons[i + 1][j + 1]).color == Client.color
                        && ((CirculerButton) buttons[i + 2][j + 2]).color == Client.color
                        && ((CirculerButton) buttons[i + 3][j + 3]).color == Client.color) {
                    return true;
                }

            }
        }
        return false;
    }

    public static void enableButtons(boolean b) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < colum; j++) {
                buttons[i][j].setEnabled(b);
            }
        }
    }

    public static void reset(boolean b) {
        RivalSelection = -1;
        myselection = -1;
        finish = false;
        lblResult.setText("");

        lblResult2.setText("");
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < colum; j++) {
                ((CirculerButton) buttons[i][j]).color = null;
                ((CirculerButton) buttons[i][j]).isEmpty = true;
                buttons[i][j].setBackground(Color.white);
                buttons[i][j].setEnabled(b);
            }
        }
    }

    public static void terminate() {
        int reply = JOptionPane.showConfirmDialog(null, "Do you want to play again", "Close?", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.NO_OPTION) {
            Stop();
            System.exit(0);
        } else {
            reset(true);
        }
    }
}
