package com.gregashby.challenge.handlers;

import java.util.Map;

import com.gregashby.challenge.Constants;
import com.gregashby.challenge.MyApp;
import com.gregashby.challenge.accounts.Account;
import com.gregashby.challenge.accounts.Accounts;
import com.gregashby.challenge.json.AppDirectJsonResponse;
import com.gregashby.challenge.json.JsonTransformer;

import spark.Request;
import spark.Response;

public class CreateSubscriptionHandler extends SignedFetchHandler implements Constants {

	public Map<String, Object> handleSignedFetchResponse(Request request, Response response, AppDirectJsonResponse json) throws Exception {
		Account account = new Account();
		account.setEmail(json.getCreator().getEmail());
		account.setCompanyId(json.getPayload().getCompany().getUuid());
		account.setEditionCode(json.getPayload().getOrder().getEditionCode());
		account.setStatus("FREE_TRIAL"); // TODO confirm if this is the correct
											// initial status
		try {
			Accounts.createAccount(account);
			if (account.getId() == null) {
				throw new Exception("Did not get an error but could not create an account.");
			}
		} catch (Exception e) {
			MyApp.logger.info("ERROR - Unable to create account");
			e.printStackTrace(System.out);
			return createErrorResultForJson(ERROR_UNKNOWN, "Could not create account: " + e.getMessage());
		}

		MyApp.logger.info("SUCCESS - Created account# {}", account.getId());
		Map<String, Object> result = createSuccessResultForJson();
		result.put("accountIdentifier", account.getId());
		MyApp.logger.info(new JsonTransformer().render(result));
		return result;

	}

}
