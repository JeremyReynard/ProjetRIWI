/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package display;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
 */
public class SimpleFilter extends FileFilter{

   //Description et extension acceptée par le filtre
   private String description;
   private String extension;
   //Constructeur à partir de la description et de l'extension acceptée
   public SimpleFilter(String description, String extension){
      if(description == null || extension ==null){
         throw new NullPointerException("Description (or extension) can't be null !");
      }
      this.description = description;
      this.extension = extension;
   }
   //Implémentation de FileFilter
   public boolean accept(File file){
      if(file.isDirectory()) { 
         return true; 
      } 
      String fileName = file.getName().toLowerCase(); 

      return fileName.endsWith(extension);
   }
   
   public String getDescription(){
        return description;
   }
}

