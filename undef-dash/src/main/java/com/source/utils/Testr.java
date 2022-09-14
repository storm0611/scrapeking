package com.source.utils;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Testr {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Testr().start();
	}

	public void start() {
		try {
			String path = System.getProperty("user.dir") + System.getProperty("file.separator")
					+ "scrapers_authorization.csv";
			Reader reader = Files.newBufferedReader(Paths.get(path));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withSkipHeaderRecord());

			for (CSVRecord csvRecord : csvParser) {
				System.out.println(csvRecord.get(2));
			}
			csvParser.close();
			reader.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
