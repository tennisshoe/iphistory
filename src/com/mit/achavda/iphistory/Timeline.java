package com.mit.achavda.iphistory;

import org.xml.sax.*;
import java.io.*;

public class Timeline  extends DomainToolsSAX {
	private StringBuffer content;
	
	private final static String HISTORY_TAG = "ip_history";
	private final static String DATE_TAG = "actiondate";
	private final static String IP_TAG = "post_ip";
	private String ip;
	private String date;
	private boolean inDate = false;
	private boolean inIP = false;
	private StringBuffer contents;
	private PrintWriter writer;
	private IPLookup iplookup;
	
	public Timeline() {
		super("hosting-history");
		iplookup = new IPLookup();
		iplookup.useFreeAPI = false;
		try {
			writer = new PrintWriter(Main.OUTPUT_PATH + "timeline.csv");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		writer.println("Domain,Start Date,IP Address, Host");
	}
	
	public void startElement(String namespaceURI,
					 String localName,
					 String qName, 
					 Attributes atts) throws SAXException {
		if(qName.equals(DATE_TAG)) {
			inDate = true;
			contents = new StringBuffer();
			return;
		}
		if(qName.equals(IP_TAG)) {
			inIP = true;
			contents = new StringBuffer();
		}		
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		if(inDate || inIP) {
			contents.append(ch, start, length);
		}
    }	
	
	public void endElement(String namespaceURI,
                         String localName,
                         String qName) throws SAXException {
		if(qName.equals(DATE_TAG)) {
			inDate = false;
			if(contents.length() > 0) {
				date = contents.toString();
			}
			return;
		}
		if(qName.equals(IP_TAG)) {
			inIP = false;
			if(contents.length() > 0) {
				ip = contents.toString();
			}
		}
		if(qName.equals(HISTORY_TAG)) {
			if(date != null && ip != null) {
				iplookup.loadResult(ip);
				String hostname = "Unknown";
				for(IPLookup.Host host: IPLookup.Host.values()) {
					if(iplookup.checkHosting(host)) hostname = host.name();
				}
				writer.print(target);
				writer.print(",");
				writer.print(date);
				writer.print(",");
				writer.print(ip);
				writer.print(",");
				writer.println(hostname);
			}
			date = null;
			ip = null;
		}
	}

	public void createTimeline() {
		startParsing();
	}
	
	public void close(){
		writer.close();
	}
	
}