package com.dash.resource.endpoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dash.dao.AccountSourceDao;
import com.dash.dao.InstanceDao;
import com.dash.dao.SourceDao;
import com.dash.dao.TaskDao;
import com.dash.jwt.JwtTokenUtil;
import com.dash.model.Account;
import com.dash.model.AccountSource;
import com.dash.model.Instance;
import com.dash.model.Source;
import com.dash.model.Task;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.undefdash.ApplicationFactory;
import com.source.utils.SourceUtils;

@RestController
@RequestMapping("/source")
public class SourceResource {
	private static final Logger logger = LoggerFactory.getLogger(SourceResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@GetMapping("/getListSources")
	public Response<Source> getListSources() {
		Response<Source> response = new Response<Source>();
		try {

			Session session = appFactory.getSession();
			Optional<List<Source>> optListSources = SourceDao.getListSourcesPerAccount(session, 1);
			response.setStatus(Status.SUCCESS);

			if (optListSources.isPresent()) {
				// for each found source , load instances
				List<Source> listSources = optListSources.get();
				for (Source source : listSources) {
					source.setListInstances(new ArrayList<Instance>());
					Optional<List<Instance>> optListInstances = InstanceDao.getListInstancesBySource(session, source);
					if (optListInstances.isPresent())
						source.setListInstances(optListInstances.get());
				}
				response.setData(optListSources.get());
			}
			return response;

		} catch (Exception ex) {
			logger.error("method getListSources error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/addSource")
	public Response<Source> addSource(@RequestParam("title") String title, @RequestParam("website") String website) {
		Response<Source> response = new Response<Source>();
		try {

			Session session = appFactory.getSession();
			
			Source source = new Source(title, website, new Date());
			SourceDao.saveSource(session, source);

			// assign this source to current account
			AccountSourceDao.saveAccountSource(session, new AccountSource(
					(Account) session.load(Account.class, 1), source, new Date()));
			response.setStatus(Status.SUCCESS);
			response.setData(Arrays.asList(source));
			return response;

		} catch (Exception ex) {
			logger.error("method addSource error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/editSource")
	public Response<Source> editSource(@RequestParam("sourceId") long sourceId, @RequestParam("title") String title,
			@RequestParam("website") String website) {
		Response<Source> response = new Response<Source>();
		try {

			Session session = appFactory.getSession();

			Optional<Source> optSource = SourceDao.getSourceById(session, sourceId);
			if (optSource.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("source not found");
				return response;
			} else {
				Source source = optSource.get();
				source.setTitle(title);
				source.setWebsite(website);
				SourceDao.updateSource(session, source);

				response.setStatus(Status.SUCCESS);
				response.setData(Arrays.asList(source));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method editSource error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@GetMapping("/deleteSource")
	public Response<String> deleteSource(@RequestParam("sourceId") long sourceId) {
		Response<String> response = new Response<String>();
		try {

				Session session = appFactory.getSession();

			Optional<Source> optSource = SourceDao.getSourceById(session, sourceId);
			if (optSource.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("source not found");
				return response;
			} else {

				Source source = optSource.get();
				// First we need to unregister all tasks from all instances
				for (Instance instance : source.getListInstances()) {
					if (instance.isDeleted() == false) {
						// delete and unregister all tasks related to this instance
						Optional<List<Task>> optListTasks = TaskDao.getListTasksPerInstance(session,
								instance.getInstanceId());
						if (optListTasks.isPresent()) {
							for (Task task : optListTasks.get()) {
								if (task.getStatus().equals(com.dash.model.Status.active)) {
									// update database deleted attribute
									task.setDeleted(true);
									TaskDao.updateTask(appFactory.getSession(), task);
									SourceUtils.apiUnregister(task);
								}
							}
						}
						// delete the instance
						instance.setDeleted(true);
						InstanceDao.updateInstance(session, instance);
					}
				}
				// now mark the source as deleted
				source.setDeleted(true);
				SourceDao.updateSource(session, source);
				response.setStatus(Status.SUCCESS);
				response.setMessage("deleted successfully");
				return response;

			}

		} catch (Exception ex) {
			logger.error("method deleteSource error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

}
