package com.interior.api.dao;

import java.util.Arrays;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.Account;

public class AccountDao {

	final static Logger logger = Logger.getLogger(AccountDao.class);

	public static Response<Account> authentication(String username, String password) {
		Response<Account> response = new Response<Account>();
		try {

			response.setStatus(Status.SUCCESS);
			response.setData(null);
			if (username.equals("admin") && password.equals("1234")) {
				response.setData(Arrays.asList(new Account("user", "generated_token")));
				return response;
			}
			return response;

		} catch (Exception ex) {
			logger.error("method authentication error : ", ex);
			response.setMessage("internal error : " + ex.getMessage());
			response.setStatus(Status.ERROR);
			return response;
		}
	}

}
