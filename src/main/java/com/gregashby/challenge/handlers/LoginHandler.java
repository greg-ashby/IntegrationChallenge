package com.gregashby.challenge.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openid4java.association.AssociationException;
import org.openid4java.association.AssociationSessionType;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;

import com.gregashby.challenge.Constants;
import com.gregashby.challenge.MyApp;
import com.gregashby.challenge.accounts.Accounts;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class LoginHandler extends RequestHandlerForFreeMarker implements Constants {

	private static ConsumerManager openIdManager = null;
	
	private static ConsumerManager getManager(){
		if(openIdManager == null){
			openIdManager = new ConsumerManager();
			openIdManager.setAssociations(new InMemoryConsumerAssociationStore());
			openIdManager.setNonceVerifier(new InMemoryNonceVerifier(5000));
			openIdManager.setMinAssocSessEnc(AssociationSessionType.DH_SHA256);
		}
		return openIdManager;
	}

	@Override
	public ModelAndView handle(Request request, Response response) throws Exception {
		
		MyApp.logger.info("authenticating with openid");

		if ("true".equals(request.queryParams("is_return"))) {
			MyApp.logger.info("authenticating the return");
			processOpenIdAuthenticationReturn(request, response);
			if (request.session().attribute(SESSION_ATTRIBUTE_IDENTIFIER) == null) {
				MyApp.logger.info("sorry, login failed");
				Map<String, Object> attributes = createAttributesForFreeMarker(request, "justMessage.ftl",
						"sorry, login failed");
				attributes.put("accounts", Accounts.getAll());
				return new ModelAndView(attributes, LAYOUT_TEMPLATE);
			} else {
				Map<String, Object> attributes = createAttributesForFreeMarker(request, "justMessage.ftl",
						"login success, enjoy the elevated access!");
				attributes.put("accounts", Accounts.getAll());
				return new ModelAndView(attributes, LAYOUT_TEMPLATE);
				// TODO get some info from the provider and display it
			}
		} else {
			MyApp.logger.info("confirming openid identifier has been provided");
			String identifier = request.queryParams("openid_identifier");
			if (identifier != null) {
				MyApp.logger.info("making authentication request");
				makeOpenIdAuthenticationRequest(identifier, request, response);
				Map<String, Object> attributes = createAttributesForFreeMarker(request, "justMessage.ftl",
						"redirecting to login provider");
				attributes.put("accounts", Accounts.getAll());
				return new ModelAndView(attributes, LAYOUT_TEMPLATE);
				// return new ModelAndView(null, null);
			} else {
				Map<String, Object> attributes = createAttributesForFreeMarker(request, "justMessage.ftl",
						"no login provider supplied");
				attributes.put("accounts", Accounts.getAll());
				return new ModelAndView(attributes, LAYOUT_TEMPLATE);
			}
		}

	}

	private void makeOpenIdAuthenticationRequest(String identifier, Request request, Response response)
			throws DiscoveryException, MessageException, ConsumerException {
		String returnUrl = request.url() + "?is_return=true";
		List discoveries = getManager().discover(identifier);
		DiscoveryInformation discovered = getManager().associate(discoveries);
		request.session().attribute("openid-disc", discovered);
		AuthRequest authenticationRequest = getManager().authenticate(discovered, returnUrl);
		response.redirect(authenticationRequest.getDestinationUrl(true));
	}
	
	private void processOpenIdAuthenticationReturn(Request request, Response response)
			throws MessageException, DiscoveryException, AssociationException {
		Identifier identifier = verifyOpenIdResponse(request);
		if (identifier != null) {
			request.session().attribute(SESSION_ATTRIBUTE_IDENTIFIER, identifier);
		}
	}
	
	private Identifier verifyOpenIdResponse(Request request)
			throws MessageException, DiscoveryException, AssociationException {

		ParameterList response = new ParameterList(getQueryParamsAsMap(request));
		DiscoveryInformation discovered = (DiscoveryInformation) request.session().attribute("openid-disc");
		String receivingURL = request.url();
		String queryString = request.queryString();
		if (queryString != null && queryString.length() > 0) {
			receivingURL += "?" + queryString;
		}
		VerificationResult verification = getManager().verify(receivingURL, response, discovered);
		Identifier verified = verification.getVerifiedId();
		if (verified != null) {
			AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
			// receiveSimpleRegistration(request, authSuccess);
			// receiveAttributeExchange(request, authSuccess);
			return verified; // success
		}

		return null;

	}
	
	/**
	 * ParameterList constructor expects a Map, and Spark's
	 * request.queryParamsMap doesn't implement a Map interface for some reason
	 * so we have to do this the hard map
	 * 
	 * @param request
	 * @return
	 */
	private Map<String, String> getQueryParamsAsMap(Request request) {

		Set<String> paramNames = request.queryParams();
		Map<String, String> params = new HashMap<>();
		paramNames.stream().forEach((param) -> {
			params.put(param, request.queryParams(param));
		});
		return params;
	}


}
