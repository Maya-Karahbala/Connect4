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
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

/**
 *
 * @author maya
 */
public class game {

    // setting number of rpw and colıms in the game
    public static int row = 5, colum = 3, selectionNo;
    public static JButton[][] buttons = new JButton[row][colum];
    public static JFrame frame = new JFrame("Connect 4");
    //rakibin  seçimi  seçim -1 deyse seçilmemiş
    public static int RivalSelection, myselection;
    //benim seçimim seçim -1 deyse seçilmemiş

    public static JPanel panel = new JPanel(new GridLayout(row, colum, 20, 20));
    //karşı tarıf
    public static Thread control;

    public static JTextField txtName, txtResult, txtRivalName;

    public static boolean colse;
    public static JButton btnstart;
    public static Font font = new Font("Arial", Font.PLAIN, 25);
    public static Color backColor = new Color(0, 0, 153);

    public static void main(String[] args) {
        // code for interface
        //main panel

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        //

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // adding header spaces
        JPanel header0 = new JPanel(new GridLayout(1, 3, 50, 50));
        header0.add(new JLabel(" "));
        JPanel header00 = new JPanel(new GridLayout(1, 3, 50, 50));
        header00.add(new JLabel(" "));
        ///////////////
        JPanel header = new JPanel(new GridLayout(1, 3, 50, 50));

        //name 
        JLabel lblName = new JLabel(" Name");
        lblName.setForeground(Color.white);
        lblName.setFont(font);

        header.add(lblName);
        txtName = new JTextField("  player1");
        txtName.setFont(font);
        header.add(txtName);
        //connect 
        btnstart = new JButton("Start");
        btnstart.setPreferredSize(new Dimension(40, 60));
        btnstart.setFont(font);
        btnstart.setBackground(new Color(
                255, 255, 153));
        btnstart.setBorder(BorderFactory.createLineBorder(Color.white, 7));
        header.add(btnstart);

        btnstart.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //your actions

                Client.Start("127.0.0.1", 2000);
                btnstart.setEnabled(false);
                txtName.setEnabled(false);
                Thread t = new control();
                t.start();

            }
        });
// adding header2
        JPanel header2 = new JPanel(new GridLayout(1, 3, 20, 20));

        //Rival labels 
        JLabel rname = new JLabel(" Rival Name");
        rname.setForeground(Color.white);
        rname.setFont(font);
        header2.add(rname);

        txtRivalName = new JTextField();
        txtRivalName.setFont(font);
        //  txtRivalName.setEditable(false);

        header2.add(txtRivalName);
        //label
        txtResult = new JTextField();

        txtResult.setPreferredSize(new Dimension(40, 60));
        txtResult.setFont(new Font("Arial", Font.PLAIN, 40));
        txtResult.setEditable(false);
        txtResult.setBackground((new Color(0, 0, 153)));
        header2.add(txtResult);

        // adding circls to interface and buttons array
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < colum; j++) {

                buttons[i][j] = new CirculerButton(j);

                buttons[i][j].addActionListener(new ActionListener() {
                    // selecting circle
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //my actions,
                        selectionNo++;
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
        // set panels background color
        header.setBackground(backColor);
        header2.setBackground(backColor);
        header0.setBackground(backColor);
        header00.setBackground(backColor);
        panel.setBackground(backColor);

        ///////////
        mainPanel.add(header);
        mainPanel.add(header0);
        mainPanel.add(header2);
        mainPanel.add(header00);
        mainPanel.add(panel);

        frame.setContentPane(mainPanel);
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
                    txtResult.setText("     you win");

                    terminate();

                }
                break;

            }
        }
    }
// rakibin hamleleri bekliyor

    public static class control extends Thread {

        @Override
        public void run() {
            while (Client.socket.isConnected()) {

                try {
                    Thread.sleep(250);
                    if (selectionNo > (row * colum) / 2) {
                        txtResult.setText("     Draw");
                        Client.Send(new Message(Message.Message_Type.Disconnect));

                        terminate();
                    }
                    if (RivalSelection != -1) {
                        fill(RivalSelection, Client.rivalColor);
                        enableButtons(true);
                        RivalSelection = -1;
                    }
                    if (colse) {
                        terminate();
                    }
                    // tahata dolmuşsa 

                } catch (InterruptedException ex) {
                    Logger.getLogger(game.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

    }
    ///////////////kontrol fonksyonları

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
        colse = false;
        selectionNo = 0;

        txtResult.setText("");

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
        enableButtons(false);
        int reply = JOptionPane.showConfirmDialog(null, "Do you want to play again", "Close?", JOptionPane.YES_NO_OPTION);

        if (reply == JOptionPane.NO_OPTION) {
            closeFrame();
        } else {
            reset(true);

        }
    }

    public static void closeFrame() {
        Stop();

        System.exit(0);
    }
}
