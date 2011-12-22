package testSerialization;

import index.Couple;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TestCouple{
    
    static public void main(String ...args) {
        try {
            // création d'une personne
            Couple couple = new Couple(100 , 10);
            System.out.println("creation de : " + couple);
            
            // ouverture d'un flux de sortie vers le fichier "personne.serial"
            FileOutputStream fos = new FileOutputStream("couple.serial");
            
            // création d'un "flux objet" avec le flux fichier
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            try {
                // sérialisation : écriture de l'objet dans le flux de sortie
                oos.writeObject(couple);
                // on vide le tampon
                oos.flush();
                System.out.println(couple + " a ete serialise");
            } finally {
                //fermeture des flux
                try {
                    oos.close();
                } finally {
                    fos.close();
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

