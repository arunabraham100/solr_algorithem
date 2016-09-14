package Indexing;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


public class SolrIndex {

	private static HttpSolrServer solr;
	static UpdateRequest req = new UpdateRequest(); 
	static List<String> myFilesToIndex = new ArrayList<String>();

	public static void main(String[] args) throws IOException, SAXException, TikaException {
		//SolarQuery.queryMySolr("solr");
		try {
			 solr = new HttpSolrServer("http://localhost:8983/solr/star");

			
			solr.deleteByQuery( "*:*" );
			
			String[] folderPath={"C:\\newFiles\\"};
			
			for (int j = 0; j < folderPath.length; j++) {
				
				System.out.println("list of folder :  "+folderPath[j]);
				j++;
				
			}
			
			File folder = new File("C:\\newFiles\\");
			
			
			File[] listOfFiles = folder.listFiles();
			
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					String myFilePath = listOfFiles[i].getAbsolutePath();
					String myFileName = listOfFiles[i].getName();
					
		
					
					processDocument(myFilePath, myFileName);  
				} else if (listOfFiles[i].isDirectory()) {		    	  
					myFilesToIndex.add(listOfFiles[i].getAbsolutePath());
				}
			}		

			while(!myFilesToIndex.isEmpty()){
				//System.out.println(listOfFiles.length);
				folder = new File(myFilesToIndex.get(0));
				getFileNames(folder.listFiles());
				myFilesToIndex.remove(0);
				
			}

			
			@SuppressWarnings("unused")
			UpdateResponse rsp = req.process( solr );
			
		}
		catch  (Exception ex) {
			System.out.println(ex.getMessage());
	
		}		
	}
 


	private static void getFileNames(File[] listFiles) {
		for (int i = 0; i < listFiles.length; i++) {
			if (listFiles[i].isFile()) {
				String myFilePath = listFiles[i].getAbsolutePath();
				String myFileName = listFiles[i].getName();
				if(myFileName.contains(myFileName))
				{
					//System.out.println(myFileName);
				}
				processDocument(myFilePath,myFileName);  
			} else if (listFiles[i].isDirectory()) {		    	  
				myFilesToIndex.add(listFiles[i].getAbsolutePath());
			}
		}

	}



	private static void processDocument(String pathfilename, String myFileName)  {

		try {
			
			InputStream input = new FileInputStream(new File(pathfilename));			
			ContentHandler textHandler = new BodyContentHandler(10*1024*1024);
			Metadata meta = new Metadata();
			Parser parser = new AutoDetectParser(); 
			ParseContext context = new ParseContext();	 
			parser.parse(input, textHandler, meta, context);
			
			
			UUID guid = java.util.UUID.randomUUID();
			String docid = guid.toString();
			System.out.println(docid);
			String doctitle = myFileName;
			System.out.println(doctitle);
			String docurl = pathfilename;
			System.out.println(docurl);
			String doccontent = textHandler.toString();
			//String doccontent=content.replaceAll ("\\s","").trim();
			System.out.println(doccontent);
			//System.out.println(doccontent);
			String author=meta.get(TikaCoreProperties.CREATOR);
			Date date=meta.getDate(TikaCoreProperties.MODIFIED);
			String dateFormat=new SimpleDateFormat("dd-MMM-yyyy").format(date);
			System.out.println(date);
			System.out.println(dateFormat);
			
			System.out.println(author);
			if(doccontent==null){		
				doccontent="no content";	
			}
			//String author = "no author";
			String size = "size";
			
			

			//call to index
			indexDocument(docid, doctitle, author, docurl, doccontent, size,dateFormat);
		}
		catch  (Exception ex) {	
			System.out.println(ex.getMessage());
		}
	}

	private static void indexDocument(String docid, String doctitle, String doccreator, String docurl, String doccontent, String size,String dateFormat) 
	{

		try {
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("id", docid);
			doc.addField("title", doctitle);
			doc.addField("author", doccreator);		
			doc.addField("url", docurl);		
			doc.addField("text", doccontent);
			doc.addField("date", dateFormat);
			req.setAction( UpdateRequest.ACTION.COMMIT, false, false );
			req.add( doc ); 
			 UpdateResponse resp  = solr.add(doc);	
			 solr.commit();
			 System.out.println("STATUS  :  "+resp.getStatus());
		} 
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}	
}
