package com.source.utils;

import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Instance;
import com.dash.model.Task;
import com.dash.model.TaskParameter;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.gson.Gson;

public class SourceUtils implements Serializable {

	// this is a utility class serve to address any registered instance/source that
	// respect the sources specs
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(SourceUtils.class);

	private static final String PING_URI = "/ping";
	private static final String UNREGISTER_URI = "/unregister";
	private static final String REGISTER_URI = "/register";
	private static final String PARAMETERS_URI = "/parameters";
	private static final String LIST_REGISTERED_TASKS = "/list-registered-tasks";

	public static WebClient client;
	static {
		logger.info("initialize virtual browser ...");
		client = new WebClient(BrowserVersion.CHROME);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		client.getOptions().setTimeout(20 * 1000);
		client.addRequestHeader("user-agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
	}

	public static Optional<String> getListParameters(Instance instance) {
		try {
			String configurationURL = instance.getUrl() + PARAMETERS_URI;
			Page page = client.getPage(configurationURL);
			return Optional.of(page.getWebResponse().getContentAsString());
		} catch (Exception ex) {
			logger.error("method getListParameters error : " + ex.getMessage());
			return Optional.empty();
		}

	}

	public static boolean instanceStatus(Instance instance) {
		try {
			String pingURL = instance.getUrl() + PING_URI;
			Page page = client.getPage(pingURL);
			if (page.getWebResponse().getStatusCode() == 200)
				return true;
			else
				return false;
		} catch (Exception ex) {
			return false;
		}
	}

	public static Response<String> registerTask(Task task) {
		Response<String> response = new Response<String>();
		try {

			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

			String registerURL = task.getInstance().getUrl() + REGISTER_URI;

			WebRequest request = new WebRequest(new URL(registerURL), HttpMethod.POST);

			List<NameValuePair> parameters = new ArrayList<NameValuePair>();

			parameters.add(new NameValuePair("taskId", task.getTaskId()));
//			parameters.add(
//					new NameValuePair("databaseURL", String.valueOf(task.getCampaign().getRecordsDBConnectionURL())));
//			parameters.add(new NameValuePair("username", String.valueOf(task.getCampaign().getUsername())));
//			parameters.add(new NameValuePair("password", String.valueOf(task.getCampaign().getPassword())));
			parameters.add(new NameValuePair("cronUnit", String.valueOf(task.getCronUnit())));
			parameters.add(new NameValuePair("cronValue", String.valueOf(task.getCronValue())));
			parameters.add(new NameValuePair("repeatForever", String.valueOf(task.isRepeatForever())));

			if (task.getExpireDate() != null)
				parameters.add(new NameValuePair("expireDate", formater.format(task.getExpireDate())));

			// set parameters
			Map<String, String> map = new HashMap<String, String>();
			for (TaskParameter param : task.getListTaskParameters())
				map.put(param.getKey(), param.getValue());

			parameters.add(new NameValuePair("parameters", new Gson().toJson(map)));
			request.setRequestParameters(parameters);

			Page apiResponse = client.getPage(request);

			if (apiResponse.getWebResponse().getStatusCode() == 200) {
				response.setStatus(Status.SUCCESS);
				response.setMessage("schduled");
			} else if (apiResponse.getWebResponse().getStatusCode() == 409) {
				response.setStatus(Status.WARN);
				response.setMessage("already exist");
			} else {
				response.setStatus(Status.ERROR);
				response.setMessage(apiResponse.getWebResponse().getContentAsString());
			}

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("method registerTask error : " + ex.getMessage());
			response.setStatus(Status.ERROR);
			response.setMessage("backend internal error : " + ex.getMessage());
			return response;
		}

	}

	public static boolean apiUnregister(Task task) {
		try {

			String unregisterURL = task.getInstance().getUrl() + UNREGISTER_URI;
			WebRequest request = new WebRequest(new URL(unregisterURL), HttpMethod.POST);

			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new NameValuePair("taskId", String.valueOf(task.getTaskId())));

			request.setRequestParameters(parameters);

			Page page = client.getPage(request);
			if (page.getWebResponse().getStatusCode() == 200)
				return true;
			else
				return false;

		} catch (Exception ex) {
			logger.error("method apiUnregister error : " + ex.getMessage());
			return false;
		}
	}

	public static Optional<List<String>> listRegisteredTasks(Instance instance) {
		try {

			String url = instance.getUrl() + LIST_REGISTERED_TASKS;
			Page page = client.getPage(url);

			ObjectMapper mapper = new ObjectMapper();

			String[] myIds = mapper.readValue(page.getWebResponse().getContentAsString(), String[].class);
			return Optional.of(Arrays.asList(myIds));

		} catch (Exception ex) {
			logger.error("method listRegisteredTasks error : " + ex.getMessage());
			return Optional.empty();
		}
	}

}
