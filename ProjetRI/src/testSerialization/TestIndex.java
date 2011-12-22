package testSerialization;

import index.Couple;
import index.Index;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestIndex{
    
    static public void main(String ...args) {
        
        Map<String, List<Couple>> collection = new HashMap<>();
        
        try {
            
            List<Couple> list = new ArrayList<>();
            list.add(new Couple(100, 10));
            collection.put("test", list);
            
            // création d'une personne
            Index index = new Index(collection);
            System.out.println("creation de : " + index);
            
            // ouverture d'un flux de sortie vers le fichier "personne.serial"
            FileOutputStream fos = new FileOutputStream("index.serial");
            
            // création d'un "flux objet" avec le flux fichier
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            try {
                // sérialisation : écriture de l'objet dans le flux de sortie
                oos.writeObject(index);
                // on vide le tampon
                oos.flush();
                System.out.println(index + " a ete serialise");
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

