package Indexing;


import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;


public class SolarQuery {

	public static List<QueryResultBean> queryMySolr(String keyword) throws MalformedURLException {
		QueryResultBean myResultBean = null;
		List<QueryResultBean> myResultBeanList = new ArrayList<QueryResultBean>();

		SolrServer solr = new HttpSolrServer("http://localhost:8983/solr/star/"); 
		SolrQuery sq = new SolrQuery();
		
		String key = keyword;
		sq.setQuery(key);
		List<String> myLabelList = new ArrayList<String>();
		myLabelList.add("title");
		myLabelList.add("url");
		myLabelList.add("content");
		try {
			
			QueryResponse rsp = solr.query(sq);
			SolrDocumentList docs = rsp.getResults();
			//System.out.println(docs);
			ListIterator<SolrDocument> iter = docs.listIterator();
			while (iter.hasNext()) {

				myResultBean = new QueryResultBean();
				String title = "";
				String url = "";
				String index = "";
				SolrDocument doc = iter.next();
				Map<String, Collection<Object>> values = doc
						.getFieldValuesMap();
				Iterator<String> labels = doc.getFieldNames().iterator();
				while (labels.hasNext()) {
					String Labelkey = labels.next();
					if (myLabelList.contains(Labelkey)) {
						Collection<Object> myValueObject = values.get(Labelkey);

						Iterator myIter = myValueObject.iterator();
						while (myIter.hasNext()) {
							Object obj = myIter.next();
							String myContent = obj.toString();
							//System.out.println("myContent: " +myContent);

							if ("title".equalsIgnoreCase(Labelkey)) {
								title = myContent;
								myResultBean.setTitle(myContent);
							} else if ("url".equalsIgnoreCase(Labelkey)) {
								url = myContent;
								myResultBean.setUrl(myContent);

							} else if ("content".equalsIgnoreCase(Labelkey)
									&& myContent.contains(key)
									) {
									System.out.println("URL:" +url);
								
								if(!key.contains("*"))
								{
								Pattern p= Pattern.compile(key);
								Matcher m = p.matcher(myContent);
								int count =0;
								while(m.find()){
									count++;
								}
								if( count >0) {
									myResultBean.setMessage( "Number of occurances: " + count );
								}
								}
								if (myResultBeanList.isEmpty()) {
									List<String> myContentList = new ArrayList<String>();
									myContentList.add(formatContent(key,myContent,0));
									myResultBean.setContent(myContentList);
									myResultBean.setIndexerCount(1);
									myResultBeanList.add(myResultBean);
								} else {
									int myInd = -1;
									for (QueryResultBean myBean : myResultBeanList) {
										System.out.println(myBean.getUrl());

										if (myBean.getUrl().equalsIgnoreCase(
												url)) {
											myInd = myResultBeanList.indexOf(myBean);
											System.out.print(myInd +" : ");
										}
									}

									if(myInd == -1){
										List<String> myContentList = new ArrayList<String>();											
										myContentList.add(formatContent(key,myContent,0));
										myResultBean
										.setContent(myContentList);
										myResultBean.setIndexerCount(1);
										myResultBeanList.add(myResultBean);
									}else{										
										String s = formatContent(key,myContent,myResultBeanList.get(myInd).getContent().size());
										myResultBeanList.get(myInd).getContent().add(formatContent(key,s,0));
										myResultBeanList.get(myInd).getContent().add(s);
										myResultBeanList.get(myInd).getContent().add(myContent);
									}

								}
							}

						}
					}

				}
			}
			
		} catch (SolrServerException e) {
			e.printStackTrace();
			
		}
		return myResultBeanList;
	}

	private static String formatContent(String myKey, String myContent, int myCount) {

		if(myCount==0){
			int myIndex = myContent.indexOf(myKey);
			int offset = 300;
			if(myIndex>300){

				if(myIndex+offset>myContent.length()){
					myContent = myContent.substring(myIndex-300,myContent.length());
				}else{
					myContent = myContent.substring(myIndex-300,myIndex+offset );
				}
			}else{
				if(myIndex+offset>myContent.length()){
					myContent = myContent.substring(0,myContent.length());
				}else{
					myContent = myContent.substring(0,myIndex+offset );
				}
			}
		}else{

			int i = myCount;
			while(i>0){
				int myIndex = myContent.indexOf(myKey);
				int myLength = myContent.length();	
				int offset = 0;
				if(myIndex-300<0){
					offset = myIndex+myKey.length();
				}else{
					offset = myIndex+myKey.length()-300;
				}
				if(myIndex+300>myContent.length()){
					myContent = myContent.substring(offset,myContent.length());
				}else{
					myContent = myContent.substring(offset,(myIndex+300) );
				}
				myContent = myContent.substring(offset, myLength);
				i--;
			}
		}
		System.out.println(myContent);
		myContent = myContent.replaceAll(myKey, "<b>"+myKey+"</b>");
		return myContent;



	}
}
