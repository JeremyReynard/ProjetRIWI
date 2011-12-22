/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testSerialization;


import index.Couple;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class CoupleDeserialization {
    
    static public void main(String ...args) {
        Couple couple = null;
        try {
            //Open a InputStream linked "couple.serial"
            FileInputStream fis = new FileInputStream("couple.serial");
            // Open an objectStream linked to the file
            ObjectInputStream ois= new ObjectInputStream(fis);
            try {
                // deserialization
                couple = (Couple) ois.readObject();
            } finally {
                // Close the stream
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

