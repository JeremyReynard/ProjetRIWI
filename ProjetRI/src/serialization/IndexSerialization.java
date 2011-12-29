package serialization;

import index.Index;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JProgressBar;

public final class IndexSerialization {

    public static void serialize(Index index, String filePath){
        
        IndexSerialization.serialize(index, filePath, null, null);
    }
    
    public static void serialize(Index index, String filePath, JProgressBar jpBarFile, JProgressBar jpBarGlobal) {
        
        boolean pBars = true;
        int initialGlobalBarValue = 0;
        if (jpBarFile == null || jpBarGlobal == null){
            pBars = false;           
        }
        else {
             initialGlobalBarValue = jpBarGlobal.getValue();
        }
        
        try {

            if (!pBars){
                System.out.println("Beginning of serialization");
            }
            else {
                jpBarFile.setString("Serialization");
                jpBarFile.setIndeterminate(true); 
                jpBarGlobal.setValue(initialGlobalBarValue + (int) (0.15 * initialGlobalBarValue) );
            }

            // Open an output stream to "index.serial"
            FileOutputStream fos = new FileOutputStream(filePath);
            
            if (pBars){
                jpBarGlobal.setValue(initialGlobalBarValue + (int) (0.25 * initialGlobalBarValue) );
            }

            // create an objectStream linked with the file
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            
            if (pBars){
                jpBarGlobal.setValue(initialGlobalBarValue + (int) (0.40 * initialGlobalBarValue) );
            }
            
            try {
                //Serialization
                oos.writeObject(index);
                if (pBars){
                    jpBarGlobal.setValue(initialGlobalBarValue + (int) (0.80 * initialGlobalBarValue) );
                }
                // Trash the temporary variable
                oos.flush();
                if (pBars){
                    jpBarGlobal.setValue(initialGlobalBarValue + (int) (0.90 * initialGlobalBarValue) );
                }
                if (!pBars){
                    System.out.println("End of serialization");
                }
                else {
                    jpBarGlobal.setValue(100);
                    jpBarFile.setIndeterminate(false);
                    jpBarFile.setValue(100);
                }
            } finally {
                //Close the Stream
                try {
                    oos.close();
                } finally {
                    fos.close();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    static public void main(String... args) {

        Map<String, Map<String,Integer>> collection = new HashMap<>();

            Map<String, Integer> valueMap = new HashMap<>();
            
            valueMap.put("100", 10);
            collection.put("test", valueMap);

            // create a new Index
            Index index = new Index(collection);
            IndexSerialization.serialize(index, "index.serial");
    }
}
