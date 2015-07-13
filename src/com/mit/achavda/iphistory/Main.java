package com.mit.achavda.iphistory;

import java.io.*;
import java.util.*;

import com.opencsv.*;
import com.domaintoolsapi.*;


public class Main {
	
	private static final String FILE_NAME = ".\\data\\free.csv"; 
	public static final String CACHE_PATH = ".\\cache\\";
	//private static final String FILE_NAME = ".\\data\\top_50_websites_US.csv"; 	
	
	public static void main(String[] args) throws Exception {
		String method = "hosting-history";		
		
		System.out.println("Starting");	
				
		DomainTools domainTools = new DomainTools("your_username", "your_key");
		domainTools.setUseFreeAPI(true);
		
		FindIPAddresses addressFinder = new FindIPAddresses();
		
		CSVReader reader = new CSVReader(new FileReader(FILE_NAME));
		String [] nextLine;
		// first line is author information
		nextLine = reader.readNext();
		// second line is headers		
		nextLine = reader.readNext();		
        while ((nextLine = reader.readNext()) != null) {
			// second element of file should be the domain
			String domain = nextLine[1];
			System.out.println(domain);

			DTRequest dtRequest = domainTools.use(method).on(domain).toXML();
			String response = dtRequest.getXML();
			PrintWriter out = new PrintWriter(CACHE_PATH + method + "_" + domain + ".xml");
			out.println(response);
			out.close();									
			
			addressFinder.scanFile(CACHE_PATH + method + "_" + domain + ".xml");
			Iterator<String> i = addressFinder.iterator();
			while(i.hasNext()) {
				System.out.println("Found address " + i.next());
			}				
			
		}		

		System.out.println("Done");	

	}
		
}