package com.source.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class EmailExtractor {
	private static final Logger logger = LoggerFactory.getLogger(EmailExtractor.class);

	public WebClient client;
	Pattern patternEmail = Pattern.compile("([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]{2,4})");

	public EmailExtractor(WebClient client) {
		this.client = client;
	}

	public String emailFinder(String url) {
		try {

			ArrayList<String> listRefLinks = relatedURLs(url.trim());
			while (listRefLinks.size() > 10)
				listRefLinks.remove(listRefLinks.size() - 1);

			for (String item : listRefLinks) {
				try {

					if (item.contains("mailto")) {
						Matcher match = patternEmail.matcher(item);
						if (match.find())
							return match.group().trim();
					}

					Page page = client.getPage(item);
					if (page.isHtmlPage()) {

						HtmlPage htmlPage = (HtmlPage) page;

						DomElement mailtoElement = htmlPage.getFirstByXPath(
								"//a[not(contains(@href,'Subject=')) and  starts-with(@href,'mailto')]");
						if (mailtoElement != null) {
							String str = mailtoElement.getAttribute("href");
							str = str.replaceAll("(?i)mailto:", "");
							if (str.indexOf("subject=") == -1)
								return str.trim();
						}

						Matcher matcherEmail = patternEmail.matcher(htmlPage.asText());
						if (matcherEmail.find()) {
							String str = matcherEmail.group(1);
							if (str.indexOf("subject=") == -1)
								return str.trim();
						}
					}
				} catch (Exception ex) {
					logger.error("Error : " + ex.getMessage());
				}
			}
			return "";
		} catch (Exception ex) {
			logger.error("emailFinder method error : " + ex.getMessage());
			return "";
		}
	}

	public ArrayList<String> relatedURLs(String url) {
		try {

			ArrayList<String> listUrls = new ArrayList<String>();
			listUrls.add(url.trim());

			HtmlPage mainPage = client.getPage(url);
			String host = mainPage.getUrl().getHost().replaceAll("www.", "");
			host = host.substring(0, host.indexOf(".")) + ".";

			// CONSTRUCT REFERENCED LINKS LIST

			List<DomElement> listAElements = mainPage.getByXPath("//a[@href]");
			for (DomElement aElement : listAElements) {
				String link = aElement.getAttribute("href").trim();

				String preparedLink = "";
				// FIRST CASE LINK START WITH "/"
				if (link.startsWith("/")) {
					preparedLink = mainPage.getUrl().toURI().getScheme() + "://" + mainPage.getUrl().getHost() + link;
					// FIRST CASE LINK START WITH "HTTP"
				} else if (link.startsWith("http") == false && link.contains("#") == false) {
					preparedLink = mainPage.getUrl().toURI().getScheme() + "://" + mainPage.getUrl().getHost() + "/"
							+ link;
					// NORMAL CASE
				} else if (link.contains(host)) {
					preparedLink = link.trim();
				}

				if (preparedLink.trim().isEmpty() == false && listUrls.contains(preparedLink) == false
						&& preparedLink.contains("#") == false && preparedLink
								.matches("^.*?(.jpeg|.jpg|.tiff|.png|.svg|.pdf|.webp|javascript:).*$") == false) {
					listUrls.add(preparedLink.trim());
				}
			}

			String[] listRef = GetStringArray(listUrls);

			Arrays.sort(listRef, new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					return s1.toLowerCase().indexOf("about") - s2.toLowerCase().indexOf("about");
				}
			});

			Arrays.sort(listRef, new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					return s1.toLowerCase().indexOf("contact") - s2.toLowerCase().indexOf("contact");
				}
			});

			Arrays.sort(listRef, new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					return s1.toLowerCase().indexOf("mailto") - s2.toLowerCase().indexOf("mailto");
				}
			});

			ArrayUtils.reverse(listRef);
			return new ArrayList<String>(Arrays.asList(listRef));

		} catch (Exception ex) {
			// logger.error("relatedURLs method error : " + ex.getMessage());
		}
		return new ArrayList<String>();
	}

	public static String[] GetStringArray(ArrayList<String> arr) {

		// declaration and initialise String Array
		String str[] = new String[arr.size()];

		// ArrayList to Array Conversion
		for (int j = 0; j < arr.size(); j++) {

			// Assign each value to String array
			str[j] = arr.get(j);
		}

		return str;
	}

}
