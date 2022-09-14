package com.dash.resource.endpoints;

import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dash.dao.AccountDao;
import com.dash.jwt.JwtTokenUtil;
import com.dash.jwt.JwtUserDetailsService;
import com.dash.model.Account;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.undefdash.ApplicationFactory;
import com.dash.undefdash.Main;

@RestController
@RequestMapping("/account")
public class AccountResource {

	private static final Logger logger = LoggerFactory.getLogger(AccountResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@PostMapping("/authenticate")
	public Response<String> authentication(@RequestParam("login") String login,
			@RequestParam("password") String password, HttpSession httpSession, HttpServletRequest request) {
		Response<String> response = new Response<String>();
		try {
			try {
				authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
				UserDetails userDetails = userDetailsService.loadUserByUsername(login);

				Account account = AccountDao.authenticate(appFactory.getSession(), login, password);
				HashMap<String, Object> mapClaims = new HashMap<String, Object>();
				mapClaims.put("accountId", account.getAccountId());
				String token = jwtTokenUtil.generateToken(userDetails, mapClaims);

				response.setData(Arrays.asList(token));
				response.setStatus(Status.SUCCESS);

			} catch (Exception e) {
				logger.info("", e);
				response.setMessage(e.getMessage());
				response.setStatus(Status.ERROR);
			}

			return response;

		} catch (Exception ex) {

			logger.error("method authentication error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());

			return response;
		}

	}

}
