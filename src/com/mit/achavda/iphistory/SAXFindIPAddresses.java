package com.mit.achavda.iphistory;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;
import java.io.*;


public class SAXFindIPAddresses 
	extends DefaultHandler 
	implements Iterable<String>
{
	private boolean isIPCharacters;
	private StringBuffer currentIP;
	private TreeSet<String> foundAddresses;
	private XMLReader xmlReader;
	
	public SAXFindIPAddresses() throws ParserConfigurationException, SAXException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser();
		xmlReader = saxParser.getXMLReader();
		xmlReader.setContentHandler(this);
	}

	public Iterator<String> iterator() {
		return foundAddresses.iterator();
	}

	public SAXFindIPAddresses scanFile(String filename) throws IOException, SAXException {
		foundAddresses = new TreeSet<String>();
		currentIP = new StringBuffer();
	
		xmlReader.parse(convertToFileURL(filename));	
		return this;
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
	 
    private static String convertToFileURL(String filename) {
        String path = new File(filename).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
	}
	
}