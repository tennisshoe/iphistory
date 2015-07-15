package com.mit.achavda.iphistory;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.*;
import java.net.*;
import java.io.*;

import com.domaintoolsapi.*;

public class IPLookup  extends DefaultHandler {

	public enum Host {
		AMAZON, RACKSPACE, MICROSOFT, YAHOO, GOOGLE, AKAMAI, LIMELIGHT, LEVEL3, EDGECAST, CLOUDFLARE, NEPHOSCALE, INTERNAP, SOFTLAYER, GOGRID, DREAMHOST, PEER1, GODADDY, ISPRIME
	}

	private static final String IP_LOOKUP = "http://whois.arin.net/rest/ip/"; // + ".xml"
	private XMLReader xmlReader;
	private File fXML;
	private Host hostToCheck;
	private boolean hostFound;
	private StringBuffer content;
	public boolean useFreeAPI = true;
	private DomainTools domainTools;
	private String method = "whois";	
	
	private static final String PROPERTIES_FILE = "domaintools.properties";
	
	public IPLookup() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(this);
		} catch (Exception e) {
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
	
	public void loadIP(String ip) {
		// first check if the data is already in our local cache
		String filename = Main.CACHE_PATH + method + "_" + ip + ".xml";
		fXML = new File(filename);
		if(!fXML.exists()) {
			//String xml = fetchXML(ip);
			domainTools.setUseFreeAPI(useFreeAPI);
			try {
				DTRequest dtRequest = domainTools.use(method).on(ip).toXML();
				String xml = dtRequest.getXML();
				PrintWriter out = new PrintWriter(filename);
				out.println(xml);
				out.close();			
			} catch  (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean checkHosting(Host hoster){
		hostToCheck = hoster;
		hostFound = false;
		try {
			xmlReader.parse(new InputSource(new FileReader(fXML)));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return hostFound;
	}

	public void startElement(String namespaceURI,
                         String localName,
                         String qName, 
                         Attributes atts) throws SAXException {
		content = new StringBuffer();
	}
	
	public void endElement(String namespaceURI,
                         String localName,
                         String qName) throws SAXException {
		String searchString = hostToCheck.name().toLowerCase();
		String innerText = content.toString().toLowerCase();
		hostFound = hostFound || innerText.contains(searchString);
		content = new StringBuffer();
	}	
	
    public void characters(char ch[], int start, int length)
    throws SAXException {
		content.append(ch, start, length);
    }	
	
	private String fetchXML(String IP){
		try {
			URL url = new URL(IP_LOOKUP + IP + ".xml");
			return fetchXML(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private String fetchXML(URL url) {
		int response_code = 0;
		StringBuilder sbResponse = new StringBuilder();
		String sLine = "";
		String lineSeperator = System.getProperty("line.separator");
		HttpURLConnection httpConnection;
		
		try {
			httpConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e){
			e.printStackTrace();
			return "";
		}

		try{
			httpConnection.setRequestMethod("GET");
			response_code = httpConnection.getResponseCode();

			if(response_code >= 400 ){ 
				System.err.println("Error Response " + response_code); 
				System.err.println(url); 
			}

			//Read the response
			InputStream  response = httpConnection.getInputStream();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(response));
			while ((sLine = bufReader.readLine()) != null){
				sbResponse.append(sLine);
				sbResponse.append(lineSeperator);
			}

		}catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			httpConnection.disconnect();
		}
		return sbResponse.toString();
	}
}