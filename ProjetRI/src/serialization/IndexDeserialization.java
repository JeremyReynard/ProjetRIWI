/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serialization;


import index.Index;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
 */
public final class IndexDeserialization {
    
    public static Index deserialize(String fileName){
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
        } catch(IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        } 

        return index;
    }

}

