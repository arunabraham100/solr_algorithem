package Indexing;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



public class Test {

	public static void main(String[] args) {
		
		
		
		 Path source = Paths.get("C:\\newFiles\\html\\Environment Setup.pdf");
		    try {
				System.out.println(Files.probeContentType(source));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    final File file = new File("C:\\newFiles\\html\\Environment Setup.pdf");  
		    try  
		    {  
		      String fileType = Files.probeContentType(file.toPath());  
		      System.out.println(fileType);
		    }  
		    catch (IOException ioException)  
		    {  
		      
		    } 
		   

	}

}
