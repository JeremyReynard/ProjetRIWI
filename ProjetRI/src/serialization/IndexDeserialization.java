/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serialization;


import index.Index;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public final class IndexDeserialization {
    
    public static Index deserialize(String fileName){
        //System.out.println("Beginning of deserialization");
        Index index = new Index();
        try {
           //Open a InputStream linked "index.serial"
            FileInputStream fis = new FileInputStream(fileName);
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
        
        /*if(index != null) {
            System.out.println("End of deserialization");
            return index;
        }*/
        return index;
    }
    
    static public void main(String ...args) {
        Index index = IndexDeserialization.deserialize("index.serial");
        
        System.out.println(index.toString());
    }
}

