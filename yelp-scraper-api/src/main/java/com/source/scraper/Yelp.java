package com.source.scraper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backend.model.Record;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.source.cache.CacheUtils;
import com.source.cache.Entry;
import com.source.utils.BackendClient;
import com.source.utils.Browser;
import com.source.utils.EmailExtractor;

//B2BLead
@DisallowConcurrentExecution
public class Yelp implements InterruptableJob, Serializable {
	private static final long serialVersionUID = 1L;

	// this is job for yelp , browser/technical_scraping details are configured
	// locally by programmer during scraper deployment

	private boolean keepFlag = true;
	private static final Logger logger = LoggerFactory.getLogger(Yelp.class);

	// this is scraper configuration exposed to the outer world
	public static List<String> referentialParameters;
	static {
		referentialParameters = new ArrayList<String>();
		referentialParameters.add("locations");
		referentialParameters.add("keywords");
	}

	String taskId;
	HashMap<String, String> parameters;
	EmailExtractor extractor;
	Browser browser;
	CacheUtils cacheUtils;

	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {

			JobDataMap dataMap = context.getJobDetail().getJobDataMap();

			taskId = context.getJobDetail().getKey().getName();
			parameters = (HashMap<String, String>) dataMap.get("parameters");

			// construct browser
			browser = new Browser();
			browser.initBrowser(0);
			// initialize email extractor
			extractor = new EmailExtractor(browser.client);

			try {
				cacheUtils = new CacheUtils("task-" + taskId);
				cacheUtils.mode = "update";
				logger.info("cache connection state : " + cacheUtils.getSession().isConnected());
			} catch (Exception ex) {
				logger.warn("cache issue : ", ex);
			}

			// System.out.println("status : " +
			// hibernateUtil.getSessionFactory().openSession().isConnected());
			// do your magic now
			ArrayList<String> locations = new ArrayList<String>();
			if (parameters.containsKey("locations")) {
				for (String loc : parameters.get("locations").toLowerCase().split(",")) {
					if (loc.equals("all")) {
						String line = "";
						BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")
								+ System.getProperty("file.separator") + "all_us_locations.txt"));
						while ((line = br.readLine()) != null)
							locations.add(line);
						br.close();
					} else if (loc.equalsIgnoreCase("ny")) {

						String line = "";
						BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")
								+ System.getProperty("file.separator") + "ny cities.txt"));
						while ((line = br.readLine()) != null)
							locations.add(line + " NY, united states");
						br.close();

					} else {
						locations.add(loc);
					}
				}
			}

			List<String> keywords = new ArrayList<String>();
			if (parameters.containsKey("keywords"))
				keywords = Arrays.asList(parameters.get("keywords").toLowerCase().split(","));

			for (String location : locations) {
				for (String keyword : keywords) {
					if (keepFlag) {
						try {
							getListings(keyword, location);
						} catch (Exception ex) {
							logger.warn("ex : " + ex.getMessage());
						}
					}
				}
			}

			logger.info("completed task : " + taskId);

		} catch (Exception ex) {
			logger.error("method execute error : ", ex);
		}
	}

	public void getListings(String keyword, String location) {
		try {
			logger.info("keyword : " + keyword + " , location : " + location);

			String baseURL = "https://www.yelp.com/search?find_desc=" + keyword + "&find_loc=" + location;

			int pageCounter = -10;
			boolean keepGoing = true;
			while (keepGoing) {
				try {

					if (keepFlag == false)
						return;
					pageCounter += 10;

					Optional<HtmlPage> optPage = browser.getPage(baseURL + "&start=" + pageCounter);

					if (optPage.isPresent() == false) {
						keepGoing = false;
					} else {
						logger.info(optPage.get().getUrl().toString());
						List<DomElement> listLinks = ((HtmlPage) optPage.get())
								.getByXPath("//div/a[starts-with(@href,'/biz/') and not(contains(@href,'hrid'))]");

						if (listLinks.size() == 0)
							keepGoing = false;

						for (DomElement aElement : listLinks) {
							String postURL = "https://www.yelp.com" + aElement.getAttribute("href");
							Optional<Record> optRecord = scrapePost(postURL);
							if (optRecord.isPresent())
								BackendClient.submitRecord(optRecord.get(), taskId);

						}

					}
				} catch (Exception ex) {
					logger.error(ex.getMessage());
				}

			}

		} catch (Exception ex) {
			logger.error("start getListingsURL error : ", ex);
		}
	}

	public Optional<Record> scrapePost(String postURL) {
		try {

			logger.info("     " + postURL);

			if (cacheUtils.checkIfEntryExist(postURL) == false) {
				cacheUtils.saveEntry(new Entry(postURL, new Date()));

				Optional<HtmlPage> optPostPage = browser.getPage(postURL);
				if (optPostPage.isPresent()) {

					DomElement scriptElement = optPostPage.get()
							.getFirstByXPath("//div[@class='main-content-wrap main-content-wrap--full']");
					if (scriptElement != null) {

						Record record = new Record();

						DomElement addressElement = optPostPage.get()
								.getFirstByXPath("//a[starts-with(@href,'/map')]/../following-sibling::p");
						if (addressElement != null)
							record.setFullAddress(StringUtils.normalizeSpace(addressElement.getTextContent()).trim());

						DomElement nameElement = ((HtmlPage) optPostPage.get())
								.getFirstByXPath("//meta[@property='og:title']");
						if (nameElement != null) {
							String content = nameElement.getAttribute("content");
							if (content.contains("-")) {
								record.setName(
										StringUtils.normalizeSpace(content.substring(0, content.indexOf("-"))).trim());
							} else {
								record.setName(StringUtils.normalizeSpace(content));
							}
						}

						Pattern patternPhone = Pattern.compile("\"phoneNumber\": \"(.*?)\",");
						Matcher matcherPhone = patternPhone
								.matcher(StringUtils.normalizeSpace(scriptElement.asXml()).trim());
						if (matcherPhone.find())
							record.setPhone(StringUtils.normalizeSpace(matcherPhone.group(1)));

						Pattern patternEmail = Pattern.compile("/biz_redir\\?url=(.*?);");
						Matcher matcherEmail = patternEmail
								.matcher(StringUtils.normalizeSpace(scriptElement.asXml()).trim());
						if (matcherEmail.find()) {

							String url = java.net.URLDecoder.decode(matcherEmail.group(1).replace("&amp", ""),
									StandardCharsets.UTF_8.name());
							String email = extractor.emailFinder(url);

							if (email != null && email.isEmpty() == false && email.trim().length() <= 180) {
								record.setEmail(email);
								return Optional.of(record);
							}

						}
					}
				}

			}

			return Optional.empty();
		} catch (Exception ex) {
			logger.error("start scrapePost error : ", ex);
			return Optional.empty();
		}
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		// TODO Auto-generated method stub
		try {
			keepFlag = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
