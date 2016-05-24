package com.gregashby.challenge.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.Gson;

public class AppDirectResponseTest {

	@Test
	public void testParse() {

		String json = "{'type':'SUBSCRIPTION_ORDER','marketplace':{'partner':'ashbygreg-test','baseUrl':'https://ashbygreg-test.byappdirect.com'},"
				+ "'applicationUuid':null,'flag':'DEVELOPMENT',"
				+ "'creator':{'uuid':'fb22ebdb-aa0e-4d21-98e7-342ef037f4a2',"
				+ "				'openId':'https://ashbygreg-test.byappdirect.com/openid/id/fb22ebdb-aa0e-4d21-98e7-342ef037f4a2',"
				+ "				'email':'ashbygreg@gmail.com','firstName':'Greg','lastName':'Ashby','language':'en','address':null,'attributes':null},"
				+ "'payload':{'user':null,'company':{'uuid':'30c741dd-2ce1-469b-86cf-ea8bf715556f','externalId':null,'name':'Greg\\'s Company',"
				+ "									'email':null,'phoneNumber':null,'website':null,'country':'US'},"
				+ "			'account':null,'addonInstance':null,'addonBinding':null,"
				+ "			'order':{'editionCode':'BASIC','addonOfferingCode':null,'pricingDuration':'MONTHLY',"
				+ "						'items':[{'unit':'USER','quantity':1}]},'notice':null,'configuration':{}},"
				+ "'returnUrl':null,'links':[]}";

		Gson gson = new Gson();
		AppDirectResponse result = gson.fromJson(json, AppDirectResponse.class);

		assertEquals("SUBSCRIPTION_ORDER", result.getType());
		assertEquals("ashbygreg-test", result.getMarketplace().getPartner());
		assertEquals("30c741dd-2ce1-469b-86cf-ea8bf715556f", result.getPayload().getCompany().getUuid());
		assertEquals("ashbygreg@gmail.com", result.getCreator().getEmail());
	}
	

}
