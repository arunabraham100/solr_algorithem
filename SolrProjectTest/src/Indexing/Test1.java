package Indexing;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class Test1 {

	public static void main(String[] args) throws IOException {
		
		   //text file, should be opening in default text editor
        File file = new File("C:\\newFiles\\data\\docs\\task_7_26_2015.xlsx");
        System.out.println(file);
        
        //first check if Desktop is supported by Platform or not
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }
        
        Desktop desktop = Desktop.getDesktop();
        if(file.exists()) desktop.open(file);
        
      
        
        
        /*try{

            if ((new File("C:\\newFiles\\data\\docs\\task_7_26_2015.xlsx")).exists()) {

                Process p = Runtime
                   .getRuntime()
                   .exec("rundll32 url.dll,FileProtocolHandler C:\\newFiles\\data\\docs\\task_7_26_2015.xlsx");
                p.waitFor();

            } else {

                System.out.println("File does not exist");

            }

          } catch (Exception ex) {
            ex.printStackTrace();
          }*/
	}

}
