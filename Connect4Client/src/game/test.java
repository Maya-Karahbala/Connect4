/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import static game.game.buttons;
import jankenponclient.Client;
import java.awt.Color;

/**
 *
 * @author maya
 */
public class test {
    public static void main(String[] args) {
        System.out.println(Color.red==Color.red);
        System.out.println(Color.red);
         System.out.println(Color.yellow);
         System.out.println(Client.color);
          System.out.println(game.horizantalControl(0));
    }
   public static boolean inButtons(int row,int colum){
        if(row<game.row&& colum<game.colum ){
            return true;
        }
        else return false;
    }
   

}
