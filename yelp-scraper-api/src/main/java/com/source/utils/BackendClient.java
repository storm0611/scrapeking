package com.source.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backend.model.Record;
import com.google.gson.Gson;

//B2BLead
public class BackendClient {

	private static final Logger logger = LoggerFactory.getLogger(BackendClient.class);

	static String api_key = "46f5c974-8108-11eb-8dcd-0242ac130003";
	static String schema = "B2BLead";

	public static void submitRecord(Record record, String taskId) {
		try {

			Document document = Jsoup.connect("http://localhost:7777/record/saveRecord").data("taskId", taskId)
					.data("schema", schema).data("data", new Gson().toJson(record)).data("apiKey", api_key)
					.ignoreContentType(true).post();

			System.out.println("document : " + document.toString());

		} catch (Exception ex) {
			logger.error("error : ", ex);
		}
	}
}
