/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testSerialization;


import extractor.Couple;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class CoupleDeserialization {
    
    static public void main(String ...args) {
        Couple couple = null;
        try {
            // ouverture d'un flux d'entrée depuis le fichier "personne.serial"
            FileInputStream fis = new FileInputStream("couple.serial");
            // création d'un "flux objet" avec le flux fichier
            ObjectInputStream ois= new ObjectInputStream(fis);
            try {
                // désérialisation : lecture de l'objet depuis le flux d'entrée
                couple = (Couple) ois.readObject();
            } finally {
                // on ferme les flux
                try {
                    ois.close();
                } finally {
                    fis.close();
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        if(couple != null) {
            System.out.println(couple + " a ete deserialise");
        }
    }
}

