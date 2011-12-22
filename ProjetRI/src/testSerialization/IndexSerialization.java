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

public class IndexSerialization{
    
    static public void main(String ...args) {
        
        Map<String, List<Couple>> collection = new HashMap<>();
        
        try {
            
            List<Couple> list = new ArrayList<>();
            list.add(new Couple(100, 10));
            collection.put("test", list);
            
            // create a new Index
            Index index = new Index(collection);
            System.out.println("creation de : " + index);
            
            // Open an output stream to "index.serial"
            FileOutputStream fos = new FileOutputStream("index.serial");
            
            // create an objectStream linked with the file
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            try {
                 //Serialization
                oos.writeObject(index);
               // Trash the temporary variable
                oos.flush();
                System.out.println(index + " a ete serialise");
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

