package com.mit.achavda.iphistory;

import java.net.*;
import java.io.*;

public class IPLookup  extends DefaultHandler {

	public enum Host {
		AMAZON
	}

	private static final String IP_LOOKUP = "http://whois.arin.net/rest/ip/"; // + ".xml"
	private XMLReader xmlReader;
	private File fXML;
	private Host hostToCheck;
	private boolean hostFound;
	private StringBuffer content;
	
	public IPLookup() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser();
		xmlReader = saxParser.getXMLReader();
		xmlReader.setContentHandler(this);
	}
	
	public IPLookup loadIP(String IP) {
		// first check if the data is already in our local cache
		String filename = Main.CACHE_PATH + IP + ".xml";
		fXML = new File(filename);
		if(!fXML.exists()) {
			System.err.println("File not found " + filename);
			String xml = fetchXML(IP);
			PrintWriter out = new PrintWriter(filename);
			out.println(xml);
			out.close();
		}
		
		return this;
	}

	public boolean checkHosting(Host hoster){
		hostToCheck = hoster;
		hostFound = false;
		xmlReader.parse(new FileReader(fXML));
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
		String searchString;
		switch(hostToCheck) {
			case AMAZON: 
				searchString = "Amazon";
			
		}
		return content.toString().toLowercase().contains(searchString);
	}	
	
    public void characters(char ch[], int start, int length)
    throws SAXException {
		content.append(ch, start, length);
    }	
	
	private String fetchXML(String IP){
		URL url = new URL(IP_LOOKUP + IP + ".xml");
		return fetchXML(url);
	}
	
	private String fetchXML(URL url) {
		int response_code = 0;
		StringBuilder sbResponse = new StringBuilder();
		String sLine = "";
		String lineSeperator = System.getProperty("line.separator");

		try{
			httpConnection = (HttpURLConnection) url.openConnection();
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