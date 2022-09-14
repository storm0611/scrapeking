package com.source.utils;

import java.io.Serializable;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

//browser class will be instantiated between all scheduled tasks/threads
public class Browser implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(Browser.class);

	int maxRetries = 30;
	public WebClient client;

	public void initBrowser(int mode) {
		try {

			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
			java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

			client = new WebClient(BrowserVersion.CHROME);
			client.getOptions().setCssEnabled(false);
			client.getOptions().setJavaScriptEnabled(false);
			client.getOptions().setThrowExceptionOnFailingStatusCode(false);
			client.getOptions().setThrowExceptionOnScriptError(false);
			client.getOptions().setUseInsecureSSL(true);
			client.getOptions().setTimeout(190000);
			client.addRequestHeader("user-agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");

			if (mode == 1) {

				// switch proxy
				if (BrowserConfig.listProxies.size() >= 1)
					client.getOptions().setProxyConfig(
							BrowserConfig.listProxies.get(randomRange(0, BrowserConfig.listProxies.size() - 1)));
				// switch user agent
				if (BrowserConfig.listUserAgents.size() >= 1)
					client.addRequestHeader("user-agent",
							BrowserConfig.listUserAgents.get(randomRange(0, BrowserConfig.listUserAgents.size() - 1)));
			}

		} catch (Exception ex) {
			logger.error("method initBrowser error : ", ex);
		}
	}

	public Optional<HtmlPage> getPage(String url) throws InterruptedException {

		int retries = 0;
		boolean keepGoing = true;
		while (keepGoing && retries <= maxRetries) {
			try {

				HtmlPage page = client.getPage(url);
				int responseCode = page.getWebResponse().getStatusCode();

				if (page.getUrl().toString().contains("/visit_captcha") || responseCode != 200 || responseCode == 503
						|| page.getWebResponse().getContentAsString().toLowerCase().contains("you don't smell human")) {
					throw new Exception();
				} else {
					return Optional.of(page);
				}

			} catch (Exception ex) {
				logger.info("  retry ..." + retries);
				retries += 1;
				Thread.sleep(1500);
				initBrowser(1);
			}
		}

		return Optional.empty();
	}

	private static int randomRange(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

}
