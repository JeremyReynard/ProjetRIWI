/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testSerialization;


import index.Index;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class IndexDeserialization {
    
    static public void main(String ...args) {
        Index index = null;
        try {
           //Open a InputStream linked "index.serial"
            FileInputStream fis = new FileInputStream("index.serial");
            // Open an objectStream linked to the file
            ObjectInputStream ois= new ObjectInputStream(fis);
            try {
                // deserialization
                index = (Index) ois.readObject();
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
        if(index != null) {
            System.out.println(index + " a ete deserialise");
        }
    }
}

