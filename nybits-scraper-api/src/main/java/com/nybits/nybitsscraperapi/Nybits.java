package com.nybits.nybitsscraperapi;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import com.backend.model.Record;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.html.Option;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.source.cache.CacheUtils;
import com.source.cache.Entry;
import com.source.utils.BackendClient;
import com.source.utils.Browser;
import com.source.utils.EmailExtractor;

//B2BLead
@DisallowConcurrentExecution
public class Nybits implements InterruptableJob, Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(Nybits.class);

	// this is job for yelp , browser/technical_scraping details are configured
	// locally by programmer during scraper deployment

	private boolean keepScrapingFlag = true;

	// this is scraper configuration exposed to the outer world
	public static List<String> listRequiredParameters;
	static {
		listRequiredParameters = new ArrayList<String>();
		listRequiredParameters.add("listings urls(separated with #)");
	}

	int number_pages = 100;

	String taskId;
	HashMap<String, String> parameters;
	EmailExtractor extractor;
	Browser browser;
	CacheUtils cacheUtils;

	public static void main(String[] args) {
		Nybits job = new Nybits();
		job.browser = new Browser();
		job.extractor = new EmailExtractor(job.browser.getClient());

		job.taskId = "testId";
		try {
			job.cacheUtils = new CacheUtils("task-" + job.taskId);
			job.cacheUtils.mode = "update";
			logger.info("cache connection state : " + job.cacheUtils.getSession().isConnected());
		} catch (Exception ex) {
			logger.warn("cache issue : ", ex);
		}

		job.extractListing(
				"https://www.nybits.com/search/?_rid_=24&_ust_todo_=65733&_xid_=fSahpdr698WaLg-1570723264&_a%21process=y&all=all&%21%21rmin=&%21%21rmax=&%21%21fee=any&%21%21orderby=dateposted&submit=+SEARCH+NYC+RENTAL+APARTMENTS");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {

			keepScrapingFlag = true;

			// task relative data
			this.taskId = context.getJobDetail().getKey().getName();
			this.parameters = (HashMap<String, String>) context.getJobDetail().getJobDataMap().get("parameters");

			// construct browser
			this.browser = new Browser();
			// initialize email extractor
			this.extractor = new EmailExtractor(browser.getClient());

			try {
				cacheUtils = new CacheUtils("task-" + taskId);
				cacheUtils.mode = "update";
				logger.info("cache connection state : " + cacheUtils.getSession().isConnected());
			} catch (Exception ex) {
				logger.warn("cache issue : ", ex);
			}

			List<String> listings = new ArrayList<String>();
			if (parameters.containsKey("listings urls(separated with #)"))
				listings = Arrays.asList(parameters.get("listings urls(separated with #)").toLowerCase().split(","));

			for (String listingURL : listings) {
				if (keepScrapingFlag) {
					extractListing(listingURL);
				}
			}

			logger.info("completed task : " + taskId);

		} catch (Exception ex) {
			logger.error("method execute error : ", ex);
		}
	}

	public void extractListing(String listing) {
		try {
			logger.info(listing);
			int maxPageIndex = 100;
			int currentPageIndex = 0;

			HtmlPage previousPage = null;
			while (currentPageIndex <= maxPageIndex) {

				if (keepScrapingFlag == false)
					return;

				currentPageIndex += 1;
				Optional<HtmlPage> optDataPage = Optional.empty();
				logger.info(" page : " + currentPageIndex);
				// the first page handled differently
				if (currentPageIndex == 1) {

					optDataPage = browser.getPage(new WebRequest(new URL(listing)));
					if (optDataPage.isPresent())
						previousPage = optDataPage.get();

				} else {
					DomElement navFormElement = previousPage
							.getFirstByXPath("//input[@type='submit' and @disabled]/..");
					if (navFormElement == null) {
						return;

					} else {

						String action = navFormElement.getAttribute("action");

						if (action.indexOf(";jsessionid") > action.indexOf("?_ust_todo"))
							action = action.replaceAll(
									action.substring(action.indexOf(";jsessionid"), action.indexOf("?_ust_todo")), "");

						DomElement pageElement = previousPage
								.getFirstByXPath("//input[@type='submit' and @disabled]/following-sibling::*");

						WebRequest request = new WebRequest(new URL(action), HttpMethod.POST);

						request.setAdditionalHeader("Accept",
								"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
						request.setAdditionalHeader("Host", "www.nybits.com");
						request.setAdditionalHeader("Origin", "https://www.nybits.com");
						request.setAdditionalHeader("Referer", previousPage.getBaseURI());
						request.setAdditionalHeader("Accept-Language", "en,fr-FR;q=0.9,fr;q=0.8,en-US;q=0.7,ar;q=0.6");
						request.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");
						request.setAdditionalHeader("Accept-Encoding", "gzip, deflate, br");

						request.setRequestParameters(new ArrayList<NameValuePair>());

						request.getRequestParameters().add(
								new NameValuePair(pageElement.getAttribute("name"), String.valueOf(currentPageIndex)));
						request.getRequestParameters().add(new NameValuePair("!!rmax", ""));
						request.getRequestParameters().add(new NameValuePair("!!fee", "any"));
						request.getRequestParameters().add(new NameValuePair("_a!process", "y"));
						request.getRequestParameters().add(new NameValuePair("fne", "y"));
						request.getRequestParameters().add(new NameValuePair("submit", "SEARCH NYC RENTAL APARTMENTS"));
						request.getRequestParameters().add(new NameValuePair("!!rmin", ""));
						request.getRequestParameters().add(new NameValuePair("all", "all"));
						request.getRequestParameters().add(new NameValuePair("!!orderby", "dateposted"));

						optDataPage = browser.getPage(request);
						if (optDataPage.isPresent())
							previousPage = optDataPage.get();

					}
				}

				if (optDataPage.isPresent()) {
					List<DomElement> listPostsElements = optDataPage.get().getByXPath("//a[@class='card__title']");
					for (DomElement postElement : listPostsElements) {
						extractProperty(postElement.getAttribute("href"));
						if (keepScrapingFlag == false)
							return;
					}
				}

			}
		} catch (Exception ex) {
			logger.error("method extractListing error : ", ex);
		}

	}

	public void extractProperty(String url) {
		try {

			logger.info("     - " + url);

			if (cacheUtils.checkIfEntryExist(url)) {
				keepScrapingFlag = false;

			} else {
				cacheUtils.saveEntry(new Entry(url, new Date()));
				Optional<HtmlPage> optPropertyPage = browser.getPage(new WebRequest(new URL(url)));
				if (optPropertyPage.isPresent()) {

					Record record = new Record();
					DomElement nameElement = optPropertyPage.get()
							.getFirstByXPath("//th[text()='Manager:']/following-sibling::*/a");
					if (nameElement != null) {
						record.setName(StringUtils.normalizeSpace(nameElement.getTextContent()).trim());
						Optional<HtmlPage> optManagerPage = browser
								.getPage(new WebRequest(new URL(nameElement.getAttribute("href"))));
						if (optManagerPage.isPresent()) {
							DomElement phoneElement = optManagerPage.get()
									.getFirstByXPath("//td[text()='Phone']/following-sibling::td");
							if (phoneElement != null) {
								record.setPhone(StringUtils.normalizeSpace(phoneElement.getTextContent()).trim());
							}

							DomElement websiteElement = optManagerPage.get()
									.getFirstByXPath("//td[text()='Web']/following-sibling::td/a");
							if (websiteElement != null) {
								String website = websiteElement.getAttribute("href");
								String email = extractor.emailFinder(website);
								if (email != null && email.trim().isEmpty() == false) {
									record.setEmail(email);
									BackendClient.submitRecord(record, taskId);
								}
							}

						}
					}

				}
			}

		} catch (Exception ex) {
			logger.error("method extractProperty error : ", ex);
		}
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		// TODO Auto-generated method stub
		try {
			keepScrapingFlag = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
