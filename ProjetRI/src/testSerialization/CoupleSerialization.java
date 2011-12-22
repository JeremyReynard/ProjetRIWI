package testSerialization;

import index.Couple;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CoupleSerialization{
    
    static public void main(String ...args) {
        try {
            // create a new Couple
            Couple couple = new Couple(100 , 10);
            System.out.println("creation de : " + couple);
            
            // Open an output stream to "couple.serial"
            FileOutputStream fos = new FileOutputStream("couple.serial");
            
            // create an objectStream linked with the file
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            try {
                //Serialization
                oos.writeObject(couple);
                // Trash the temporary variable
                oos.flush();
                System.out.println(couple + " a ete serialise");
            } finally {
                //Close the Stream
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

