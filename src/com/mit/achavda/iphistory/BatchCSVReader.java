package com.mit.achavda.iphistory;

import java.io.*;
import java.util.*;

import com.opencsv.*;


public class BatchCSVReader {

	String[] csvFiles;
	int currentFile = -1;
	CSVReader currentReader = null;

	BatchCSVReader(String... files) {
		csvFiles = files;
	}
	
	private CSVReader nextReader() {
		currentFile++;
		if (csvFiles.length >= currentFile)
			return null;
		currentReader = null;
		try {
			currentReader = new CSVReader(new FileReader(csvFiles[currentFile]));
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return currentReader;
	} 
	
	public String[] readNext() {
		String[] next;
		do { 
			if (currentReader == null) {
				currentReader = nextReader();
				if (currentReader == null) return null;
			}
			next = null;
			try {
				next = currentReader.readNext();
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}
			if(next == null) currentReader = null;
		} while(next == null);
		return next;
	}

}