package com.mit.achavda.iphistory;

import org.xml.sax.*;

public class IPLookup  extends DomainToolsSAX {

	public enum Host {
		AMAZON, RACKSPACE, MICROSOFT, YAHOO, GOOGLE, AKAMAI, LIMELIGHT, LEVEL3, EDGECAST, CLOUDFLARE, NEPHOSCALE, INTERNAP, SOFTLAYER, GOGRID, DREAMHOST, PEER1, GODADDY, ISPRIME
	}

	private Host hostToCheck;
	private boolean hostFound;
	private StringBuffer content;
	
	public IPLookup() {
		super("whois");
	}
	
	public boolean checkHosting(Host hoster){
		hostToCheck = hoster;
		hostFound = false;
		startParsing();
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
}