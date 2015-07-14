package com.mit.achavda.iphistory;

import java.io.*;
import java.util.*;

import com.opencsv.*;


public class Main {
	
	public static final String CACHE_PATH = ".\\cache\\";
	private static final String FILE_NAME = ".\\data\\top_websites_US.csv"; 	
	//private static final String FILE_NAME = ".\\data\\free.csv"; 
	private static final String OUTPUT_FILE = ".\\out\\out.csv";
	
	public static void main(String[] args) throws Exception {	
		System.out.println("Starting");	
				
		FindIPAddresses addressFinder = new FindIPAddresses();
		addressFinder.useFreeAPI = false;
		IPLookup ipLookup = new IPLookup();
		ipLookup.useFreeAPI = false;
		
		CSVWriter writer = new CSVWriter(new FileWriter(OUTPUT_FILE));
		String [] headers = {"Domain", "Uses Amazon", "Uses CloudFlare", "Uses Akamai"};
		writer.writeNext(headers);
		
		CSVReader reader = new CSVReader(new FileReader(FILE_NAME));
		String [] nextLine;
		// first line is author information
		nextLine = reader.readNext();
		// second line is headers		
		nextLine = reader.readNext();		
        while ((nextLine = reader.readNext()) != null) {
			// second element of file should be the domain
			String domain = nextLine[1];
			boolean isAmazon = false;
			boolean isCloudFlare = false;
			boolean isAkamai = false;
			System.out.println(domain);
			
			addressFinder.loadDomain(domain);
			Iterator<String> i = addressFinder.iterator();
			while(i.hasNext()) {
				String ip = i.next();
				ipLookup.loadIP(ip);
				boolean isAmazonIP = ipLookup.checkHosting(IPLookup.Host.AMAZON);
				boolean isCloudIP = ipLookup.checkHosting(IPLookup.Host.CLOUDFLARE);
				boolean isAkamaiIP = ipLookup.checkHosting(IPLookup.Host.AKAMAI);
				System.out.println("Found address " + ip + (isAmazonIP ? " uses Amazon" : "") + (isCloudIP ? " uses CloudFlare" : "") + (isAkamaiIP ? " uses Akamai" : ""));
				isAmazon = isAmazon || isAmazonIP;
				isCloudFlare = isCloudFlare || isCloudIP;
				isAkamai = isAkamai || isAkamaiIP;
			}				
	
			String[] entries = {domain, String.valueOf(isAmazon), String.valueOf(isCloudFlare), String.valueOf(isAkamai) };
			writer.writeNext(entries);
			
		}		
		
		writer.close();
		reader.close();
		
		/*
		String ip = "174.129.2.58";
		boolean isAmazon = ipLookup.loadIP(ip).checkHosting(IPLookup.Host.AMAZON);
		System.out.println("Netflix " + ip + (isAmazon ? " uses Amazon" : ""));
		*/
		
		System.out.println("Done");	

	}
		
}