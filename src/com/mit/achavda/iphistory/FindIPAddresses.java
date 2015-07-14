package com.mit.achavda.iphistory;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.*;
import java.io.*;

import com.domaintoolsapi.*;

public class FindIPAddresses 
	extends DefaultHandler 
	implements Iterable<String>
{
	private boolean isIPCharacters;
	private StringBuffer currentIP;
	private TreeSet<String> foundAddresses;
	private XMLReader xmlReader;
	public boolean useFreeAPI = true;
	private DomainTools domainTools;
	private String method = "hosting-history";	
	
	private static final String PROPERTIES_FILE = "domaintools.properties";
	
	public FindIPAddresses() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(this);
		} catch (Exception e){
			e.printStackTrace();
		}

		Properties p=new Properties();  
		try {
			FileReader reader=new FileReader(PROPERTIES_FILE);  
			p.load(reader);  
			reader.close();		
		} catch (FileNotFoundException e) {
			System.err.println("Missing properties file " + PROPERTIES_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String username = p.getProperty("username", "my_username");  
		String key = p.getProperty("key", "my_key");  
		domainTools = new DomainTools(username,key);

	}

	public Iterator<String> iterator() {
		return foundAddresses.iterator();
	}
	
	public void loadDomain(String domain) {

		String filename = Main.CACHE_PATH + method + "_" + domain + ".xml";
		File fXML = new File(filename);
		if(!fXML.exists()) {		
			domainTools.setUseFreeAPI(useFreeAPI);
			try {
				DTRequest dtRequest = domainTools.use(method).on(domain).toXML();
				String response = dtRequest.getXML();
				
				PrintWriter out = new PrintWriter(fXML);
				out.println(response);
				out.close();									
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
			
		foundAddresses = new TreeSet<String>();
		currentIP = new StringBuffer();
	
		try {
			xmlReader.parse(new InputSource(new FileReader(fXML)));				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public void characters(char ch[], int start, int length)
    throws SAXException {
		if (isIPCharacters) currentIP.append(ch, start, length);
    }
    
	public void startElement(String namespaceURI,
                         String localName,
                         String qName, 
                         Attributes atts) throws SAXException {
		isIPCharacters = qName.equals("pre_ip") || qName.equals("post_ip");
	}
	
	public void endElement(String namespaceURI,
                         String localName,
                         String qName) throws SAXException {
		if (isIPCharacters) {
			String IPAddress = currentIP.toString().trim();
			if (IPAddress.length() > 0) foundAddresses.add(IPAddress);
			currentIP = new StringBuffer();
		}
	}
	
}