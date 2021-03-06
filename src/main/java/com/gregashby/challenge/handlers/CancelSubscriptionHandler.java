package com.gregashby.challenge.handlers;

import java.util.Map;

import com.gregashby.challenge.Constants;
import com.gregashby.challenge.MyApp;
import com.gregashby.challenge.accounts.AccountNotFoundException;
import com.gregashby.challenge.accounts.Accounts;
import com.gregashby.challenge.json.AppDirectJsonResponse;

import spark.Request;
import spark.Response;

public class CancelSubscriptionHandler extends SignedFetchHandler implements Constants {

	@Override
	public Map<String, Object> handleSignedFetchResponse(Request request, Response response, AppDirectJsonResponse json)
			throws Exception {
		String userIdToCancel = json.getPayload().getAccount().getAccountIdentifier();

		try {
			MyApp.logger.info("About to delete account {}", userIdToCancel);
			Accounts.deleteAccountById(userIdToCancel);
		} catch (AccountNotFoundException anfe) {
			MyApp.logger.info("ERROR - Unable to cancel account");
			anfe.printStackTrace(System.out);
			return createErrorResultForJson(ERROR_ACCOUNT_NOT_FOUND, "Could not find the account");
		} catch (Exception e) {
			MyApp.logger.info("ERROR - Unable to cancel account");
			e.printStackTrace(System.out);
			return createErrorResultForJson(ERROR_UNKNOWN, "Could not cancel account: " + e.getMessage());
		}

		MyApp.logger.info("SUCCESS - CANCELED SUBSCRIPTION# {}", userIdToCancel);
		Map<String, Object> result = createSuccessResultForJson();
		return result;

	}

}
