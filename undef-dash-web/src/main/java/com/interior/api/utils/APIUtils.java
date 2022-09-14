package com.interior.api.utils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

public class APIUtils {

	final static Logger logger = Logger.getLogger(APIUtils.class);

	public static final String API_URL = "http://localhost:7777";

	// maybe need to be changed once i run the application
	public static final SimpleDateFormat API_DATE_FORMATER = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

	public static Optional<String> doPost(String path, Form form) {
		try {

			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(API_URL).path(path);

			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			javax.ws.rs.core.Response resp = invocationBuilder.post(Entity.form(form));

			if (resp.getStatus() == 200) {
				String body = resp.readEntity(String.class);
				return Optional.of(body);
			} else {
				// API RESPONDED / ERROR THROWN FROM SPRING
				return Optional.empty();
			}

		} catch (Exception ex) {
			logger.error("api connection error : " + ex.getMessage());
			return Optional.empty();
		}

	}

	public static Optional<String> doGet(String path, HashMap<String, Object> mapParameter) {
		try {

			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(API_URL).path(path);
			Iterator<Map.Entry<String, Object>> entrySet = mapParameter.entrySet().iterator();
			while (entrySet.hasNext()) {
				Entry<String, Object> entry = entrySet.next();
				webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
			}
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			javax.ws.rs.core.Response resp = invocationBuilder.get();
			if (resp.getStatus() == 200) {
				String body = resp.readEntity(String.class);
				return Optional.of(body);
			} else {
				// API RESPONDED / ERROR THROWN FROM SPRING
				return Optional.empty();
			}

		} catch (Exception ex) {
			// somehow failed to consume API , returning empty will meaning request fail ?
			logger.error("api connection error : ", ex);
			return Optional.empty();
		}

	}

}
