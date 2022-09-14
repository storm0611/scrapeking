package com.source.utils;

import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class BrowserConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(BrowserConfig.class);

	public static List<String> listUserAgents;
	public static List<ProxyConfig> listProxies;

	static {
		try {

			listProxies = new ArrayList<ProxyConfig>();
			listUserAgents = new ArrayList<String>();

			String content = new String(
					Files.readAllBytes(Paths.get(
							System.getProperty("user.dir") + System.getProperty("file.separator") + "config.json")),
					StandardCharsets.UTF_8);

			JsonObject jsonPayload = new JsonParser().parse(new JsonReader(new StringReader(content)))
					.getAsJsonObject();
			JsonArray proxies = jsonPayload.get("proxies").getAsJsonArray();
			for (JsonElement proxyElement : proxies) {
				JsonObject proxyObject = proxyElement.getAsJsonObject();
				listProxies.add(
						new ProxyConfig(proxyObject.get("host").getAsString(), proxyObject.get("port").getAsInt()));
			}

			JsonArray userAgents = jsonPayload.get("user-agents").getAsJsonArray();
			for (JsonElement uaElement : userAgents)
				listUserAgents.add(uaElement.getAsString());

			Collections.shuffle(listUserAgents);
			Collections.shuffle(listUserAgents);
			Collections.shuffle(listUserAgents);

		} catch (Exception ex) {
			logger.error("error : ", ex);
		}
	}

}
