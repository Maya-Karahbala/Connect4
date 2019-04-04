/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jankenponclient;

import game.CirculerButton;
import game.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jankenponclient.Client.sInput;
import game.Game;
import static game.game.buttons;
import static game.game.colum;
import static game.game.row;
import java.awt.Color;
import javax.swing.JButton;

/**
 *
 * @author INSECT
 */
// serverdan gelecek mesajları dinleyen thread
class Listen extends Thread {

    public void run() {
        //soket bağlı olduğu sürece dön
        while (Client.socket.isConnected()) {
            try {
                //mesaj gelmesini bloking olarak dinyelen komut
                Message received = (Message) (sInput.readObject());
                //mesaj gelirse bu satıra geçer
                //mesaj tipine göre yapılacak işlemi ayır.
                switch (received.type) {
                    ////////////////*************************
                    case Name:
                        break;
                    case Bitis:
                        game.game.enableButtons(false);
                        game.game.finish = true;
                        System.out.println("client bitis aldı");
                        break;
                    case RivalConnected:

                        String name = received.content.toString();
                        /////******************************************editted
                        // Game.ThisGame.txt_rival_name.setText(name);
                        game.game.enableButtons(true);
                        game.game.lblRivalName.setText(name);
                        game.game.frame.setTitle(name);

                        break;
                    case Disconnect:
                        break;
                    /**
                     * **********************
                     */
                    case Text://change only rival colors
                        Client.color = Color.YELLOW;
                        Client.rivalColor = Color.red;
                        break;
                    case Selected:
                        game.game.RivalSelection = (int) received.content;
                        break;

                }

            } catch (IOException ex) {

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            }
        }

    }
}

public class Client {

    public static Color color, rivalColor;
    //her clientın bir soketi olmalı
    public static Socket socket;

    //verileri almak için gerekli nesne
    public static ObjectInputStream sInput;
    //verileri göndermek için gerekli nesne
    public static ObjectOutputStream sOutput;
    //serverı dinleme thredi 
    public static Listen listenMe;

    public static void Start(String ip, int port) {
        try {
            color = Color.red;
            rivalColor = Color.YELLOW;
            // Client Soket nesnesi
            Client.socket = new Socket(ip, port);
            Client.Display("Servera bağlandı");
            // input stream
            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            // output stream
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listenMe = new Listen();
            Client.listenMe.start();

            //ilk mesaj olarak isim gönderiyorum
           // System.out.println("ilk msg gonderildi");
            Message msg = new Message(Message.Message_Type.Name);
            msg.content = game.game.txtName.getText();
            Client.Send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //client durdurma fonksiyonu
    public static void Stop() {
        try {
            if (Client.socket != null) {
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();

                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * **********************
     */
    public static void Display(String msg) {

        System.out.println(msg);

    }

    //mesaj gönderme fonksiyonu
    public static void Send(Message msg) {
        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
