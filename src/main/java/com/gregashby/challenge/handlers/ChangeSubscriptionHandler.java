package com.gregashby.challenge.handlers;

import java.util.Map;

import com.gregashby.challenge.Constants;
import com.gregashby.challenge.MyApp;
import com.gregashby.challenge.accounts.Account;
import com.gregashby.challenge.accounts.AccountNotFoundException;
import com.gregashby.challenge.accounts.Accounts;
import com.gregashby.challenge.json.AppDirectJsonResponse;

import spark.Request;
import spark.Response;

public class ChangeSubscriptionHandler extends SignedFetchHandler implements Constants {

	@Override
	public Map<String, Object> handleSignedFetchResponse(Request request, Response response, AppDirectJsonResponse json)
			throws Exception {
		String userIdToChange = json.getPayload().getAccount().getAccountIdentifier();

		try {
			MyApp.logger.info("About to change account {}", userIdToChange);
			Account account = Accounts.fetchAccount(userIdToChange);
			account.setEditionCode(json.getPayload().getOrder().getEditionCode());
			account.setStatus(json.getPayload().getAccount().getStatus());
			Accounts.update(account);
		} catch (AccountNotFoundException anfe) {
			MyApp.logger.info("ERROR - Unable to change account");
			anfe.printStackTrace(System.out);
			return createErrorResultForJson(ERROR_ACCOUNT_NOT_FOUND, "Could not find the account");
		} catch (Exception e) {
			MyApp.logger.info("ERROR - Unable to cancel account");
			e.printStackTrace(System.out);
			return createErrorResultForJson(ERROR_UNKNOWN, "Could not change account: " + e.getMessage());
		}

		MyApp.logger.info("SUCCESS - CHANGED SUBSCRIPTION# {}", userIdToChange);
		Map<String, Object> result = createSuccessResultForJson();
		return result;

	}

}
