/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testSerialization;


import indexes.Index;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class IndexDeserialization {
    
    static public void main(String ...args) {
        Index index = null;
        try {
            // ouverture d'un flux d'entrée depuis le fichier "personne.serial"
            FileInputStream fis = new FileInputStream("index.serial");
            // création d'un "flux objet" avec le flux fichier
            ObjectInputStream ois= new ObjectInputStream(fis);
            try {
                // désérialisation : lecture de l'objet depuis le flux d'entrée
                index = (Index) ois.readObject();
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
        if(index != null) {
            System.out.println(index + " a ete deserialise");
        }
    }
}
