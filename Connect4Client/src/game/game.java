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
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

/**
 *
 * @author maya
 */
public class game {

    // setting number of row and colums in the game
    public static int row = 6, colum = 6, selectionNo;
    //arry of circular buttons
    public static JButton[][] buttons = new JButton[row][colum];
    public static JFrame frame = new JFrame("Connect 4");    
    public static int RivalSelection, myselection;

    public static JPanel panel = new JPanel(new GridLayout(row, colum, 20, 20));
    //thread for messages recived from rival
    public static Thread control;
    public static JTextField txtName, txtResult, txtRivalName;
    //disconnected :rival connecting statues,if rival disconnected then disconnected=true
    //lose=true if rival sent messages that he wins
    public static boolean disconnected,lose;
    public static JButton btnstart;
    public static Font font = new Font("Arial", Font.PLAIN, 25);
    public static Color backColor = new Color(0, 0, 153);

    public static void main(String[] args) {
        // code for interface
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        //////////////
        JPanel header1 = new JPanel(new GridLayout(1, 3, 50, 50));
        JLabel lblName = new JLabel("   Name");
        lblName.setForeground(Color.white);
        lblName.setFont(font);
         //player name 
        header1.add(lblName);
        txtName = new JTextField("  player1");
        txtName.setFont(font);
        header1.add(txtName);
        //connect  button
        btnstart = new JButton("Start");
        btnstart.setPreferredSize(new Dimension(40, 60));
        btnstart.setFont(font);
              btnstart.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //your actions

                Client.Start("127.0.0.1", 2000);
                btnstart.setEnabled(false);
                txtName.setEnabled(false);
                // start measage reciving thread
                Thread t = new control();
                t.start();

            }
        });
        header1.add(btnstart);
        // adding header spaces
        JPanel space1 = new JPanel(new GridLayout(1, 3, 50, 50));
        space1.add(new JLabel(" "));
        /////////////////////////////////////////////////
        //second header line
        JPanel header2 = new JPanel(new GridLayout(1, 3, 20, 20));

        //Rival labels 
        JLabel rname = new JLabel("   Rival Name");
        rname.setForeground(Color.white);
        rname.setFont(font);
        header2.add(rname);

        txtRivalName = new JTextField();
        txtRivalName.setEditable(false);
        txtRivalName.setFont(font);
        header2.add(txtRivalName);
        //result label for win,lose,turn and waite statues
        txtResult = new JTextField();
        txtResult.setPreferredSize(new Dimension(40, 60));
        txtResult.setFont(new Font("Arial", Font.PLAIN, 40));
        txtResult.setEditable(false);
        txtResult.setBackground((new Color(0, 0, 153)));
        header2.add(txtResult);
        // adding space
        JPanel space2 = new JPanel(new GridLayout(1, 3, 50, 50));
        space2.add(new JLabel(" "));
        ///////////////////////////////
        // adding circls to interface and  buttons array
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < colum; j++) {
                buttons[i][j] = new CirculerButton(j);
                buttons[i][j].addActionListener(new ActionListener() {
                    // alllow player to chose colum 
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // if no one win then stop game and send draw msg
                        selectionNo++;
                        txtResult.setText("wait ...");
                        // get selected button
                        CirculerButton pressedButton = (CirculerButton) e.getSource();
                        myselection = pressedButton.colum;
                        //disable all buttons
                        enableButtons(false);                     
                        // send msg to player2  w,th selected colum  
                        Message msg = new Message(Message.Message_Type.Selected);
                        msg.content = myselection;
                        Client.Send(msg);
                        // change selected circle color in my interface                        
                        fill(pressedButton.colum, Client.color);

                    }
                });
                buttons[i][j].setPreferredSize(new Dimension(130, 130));
                panel.add(buttons[i][j]);

            }

        }
        // set panels background color
        header1.setBackground(backColor);
        header2.setBackground(backColor);
        space1.setBackground(backColor);
        space2.setBackground(backColor);
        panel.setBackground(backColor);

        ///////////
        mainPanel.add(header1);
        mainPanel.add(space1);
        mainPanel.add(header2);
        mainPanel.add(space2);
        mainPanel.add(panel);
        //
        reset(false);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);

    }

    // when player select a colum fill the first empty circle in that colum
    public static void fill(int colum, Color color) {
        for (int i = row - 1; i >= 0; i--) {
            if (((CirculerButton) buttons[i][colum]).isEmpty) {
                buttons[i][colum].setBackground(color);
                ((CirculerButton) buttons[i][colum]).color = color;
                ((CirculerButton) buttons[i][colum]).isEmpty = false;
                // check if the player win
                if (horizantalControl(i) || verticalControl(colum) || leftDigonalControl() || rightDigonalControl()) {
                    //send msg to rival that he lose and stop game
                    Client.Send(new Message(Message.Message_Type.Bitis));
                    txtResult.setText("     you win");
                    terminate();
                }
                break;

            }
        }
    }
// waiting messages from rival
    public static class control extends Thread {

        @Override
        public void run() {
            while (Client.socket.isConnected()) {
                try {
                    Thread.sleep(250);
                    //player lose the game
                    if(lose){
                        game.txtResult.setText("     you lose");
                        game.terminate();
                        lose=false;
                    }
                    //draw statues
                    else if (selectionNo >= (row * colum) / 2) {
                        txtResult.setText("     Draw");
                        Client.Send(new Message(Message.Message_Type.Draw));
                        selectionNo=0;
                        terminate();
                    }
                    // rival has select a coloum update my interface with rival selection
                    else if (RivalSelection != -1) {
                        fill(RivalSelection, Client.rivalColor);
                        enableButtons(true);
                        RivalSelection = -1;
                        txtResult.setText("it's your turn");
                    }
                    //rical dont want to play again
                    else if (disconnected) {//rakip ayrılmışsa
                        txtResult.setBackground((new Color(0, 0, 153)));
                        txtResult.setText("");
                        btnstart.setEnabled(true);
                        enableButtons(false);
                        disconnected=false;
               
                    }                  
                } catch (InterruptedException ex) {
                    Logger.getLogger(game.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

    }
    /////////////////////////////// game rules control function//////////////////
// chaeck if player has  sequential 4 circle in row take last affected row  as parameter and check only for it
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
    // chaeck if player has  sequential 4 circle in colum take last affected colum  as parameter   and check only for it
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
 // chaeck if player has  sequential 4 circle in leftDigonals 
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
 // chaeck if player has  sequential 4 circle in right Digonals 
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
// disable or enables all buttons according to its parameter
    public static void enableButtons(boolean b) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < colum; j++) {
                buttons[i][j].setEnabled(b);
            }
        }
    }
// rest game 
    public static void reset(boolean b) {
        RivalSelection = -1;
        myselection = -1;
        disconnected = false;
        selectionNo = 0;
        lose=false;
        txtResult.setText("");
        txtRivalName.setText("");
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < colum; j++) {
                ((CirculerButton) buttons[i][j]).color = null;
                ((CirculerButton) buttons[i][j]).isEmpty = true;
                buttons[i][j].setBackground(Color.white);
                buttons[i][j].setEnabled(b);
            }
        }
    }
// ask player if he want to play again
    public static void terminate() {
        enableButtons(false);
        
        int reply = JOptionPane.showConfirmDialog(null, "Do you want to play again", "Close?", JOptionPane.YES_NO_OPTION);

        if (reply == JOptionPane.NO_OPTION) {
            Client.Send(new Message(Message.Message_Type.Disconnected));
            Stop();
           System.exit(0);
        } else {
            Client.Send(new Message(Message.Message_Type.playAgain));
            if(disconnected){
                btnstart.setEnabled(true);
                
            }
            reset(false);
            

        }
    }
//close client threads and   frame 
    public static void closeFrame() {
        Stop();
        System.exit(0);
    }
}
