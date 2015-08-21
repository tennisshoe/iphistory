package com.mit.achavda.iphistory;

import java.io.*;
import java.util.*;

import com.opencsv.*;


public class Main {
	
	public static final String CACHE_PATH = ".\\cache\\";
	public static final String OUTPUT_PATH = ".\\out\\";
	//private static final String TIMELINE_INPUT = ".\\data\\amazon_customers.csv";
	//private static final String TIMELINE_INPUT = ".\\data\\online_backup.csv"; 	 	
	private static final String TIMELINE_INPUT = ".\\data\\online_photos.csv"; 	
	//private static final String TIMELINE_INPUT = ".\\data\\top_100_US.csv"; 	
	//private static final String TIMELINE_INPUT = ".\\data\\top_100_DE.csv"; 	
	//private static final String TIMELINE_INPUT = ".\\data\\top_100_FR.csv"; 	
	//private static final String TIMELINE_INPUT = ".\\data\\top_100_UK.csv"; 	
	//private static final String FILE_NAME = ".\\data\\top_websites_US.csv"; 	
	private static final String FILE_NAME = ".\\data\\aws_case_studies.csv"; 	
	//private static final String FILE_NAME = ".\\data\\free.csv"; 
	private static final String OUTPUT_FILE = ".\\out\\out.csv";
	
	
	public static void main(String[] args) throws Exception {	
		System.out.println("Starting");	

		Timeline timeline = new Timeline();
		timeline.useFreeAPI = false;
		CSVReader reader = new CSVReader(new FileReader(TIMELINE_INPUT));
		String nextLine[];
		//header line
		reader.readNext();
        while ((nextLine = reader.readNext()) != null) {
			String domain = nextLine[1];
			timeline.loadResult(domain);
			timeline.createTimeline();			
		}
		reader.close();
		timeline.close();
		
		/*
				
		FindIPAddresses addressFinder = new FindIPAddresses();
		addressFinder.useFreeAPI = false;
		IPLookup ipLookup = new IPLookup();
		ipLookup.useFreeAPI = false;
		
		CSVWriter writer = new CSVWriter(new FileWriter(OUTPUT_FILE));
		StringBuffer sbHeaders = new StringBuffer("Domain");
		for(IPLookup.Host host: IPLookup.Host.values()) {
			sbHeaders.append("#");
			sbHeaders.append(host.name());
		}
		String [] headers = sbHeaders.toString().split("#");
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
			Map<IPLookup.Host, Boolean> mHosts = new HashMap<IPLookup.Host, Boolean>();
			for(IPLookup.Host host: IPLookup.Host.values()) {
				mHosts.put(host, false);
			}
			System.out.println(domain);
			
			addressFinder.loadDomain(domain);
			Iterator<String> i = addressFinder.iterator();
			while(i.hasNext()) {
				String ip = i.next();
				ipLookup.loadResult(ip);
				System.out.print("Address check " + ip); 
				for(IPLookup.Host host: IPLookup.Host.values()) {
					boolean found = ipLookup.checkHosting(host);
					mHosts.put(host, mHosts.get(host) || found);
					if(found) System.out.print(" uses " + host.name());
				}
				System.out.println("");
			}

			StringBuffer sbEntries = new StringBuffer(domain);
			for(IPLookup.Host host: IPLookup.Host.values()) {
				sbEntries.append("#");
				sbEntries.append(String.valueOf(mHosts.get(host)));
			}
			String [] entries = sbEntries.toString().split("#");
			writer.writeNext(entries);
			
		}		
		
		writer.close();
		reader.close();
		*/
		
		// String ip = "174.129.2.58";
		// boolean isAmazon = ipLookup.loadIP(ip).checkHosting(IPLookup.Host.AMAZON);
		// System.out.println("Netflix " + ip + (isAmazon ? " uses Amazon" : ""));
		
		System.out.println("Done");	

	}
}