package com.api.stepdefinition;

import static org.junit.Assert.*;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.api.utils.TestContext;

import io.cucumber.java.en.*;
import java.util.concurrent.TimeUnit;

public class ViewRateDetailsStepdefinition {
	private TestContext context;
	private static final Logger LOG = LogManager.getLogger(ViewRateDetailsStepdefinition.class);
	
	public ViewRateDetailsStepdefinition(TestContext context) {
		this.context = context;
	}

	@Given("user has access to endpoint {string}")
	public void userHasAccessToEndpoint(String endpoint) {		
		context.session.put("endpoint", endpoint);
	}

	@When("user makes a request to view the rates")
	public void userMakesARequestToViewRates() {
		context.response = context.requestSetup().when().get(context.session.get("endpoint").toString());
		String resultstatus = context.response.getBody().jsonPath().get("result");
		LOG.info("Result for the API: "+resultstatus);
		assertEquals("success",resultstatus);
		context.session.put("result", resultstatus);
		System.out.println(context.response.getBody().prettyPrint());
	}

	@Then("user should get the response code {int}")
	public void userShpuldGetTheResponseCode(Integer statusCode) {
		assertEquals(Long.valueOf(statusCode), Long.valueOf(context.response.getStatusCode()));
	}

	@And("user validates the response with JSON schema {string}")
	public void userValidatesResponseWithJSONSchema(String schemaFileName) {
		context.response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/"+schemaFileName));
		LOG.info("Successfully Validated schema from "+schemaFileName);
	}
	
	@And("user validates that AED is in the range of {float} and {float} against {string}")
	public void userValidatesThatAEDIsInTheRangeofAnd(float arg0, float arg1, String rate) {
		JsonPath jsonpath = context.response.jsonPath();
		assertEquals(rate,jsonpath.getString("base_code"));
		assertEquals(jsonpath.getFloat("rates.AED")>=arg0,true);
		assertEquals(jsonpath.getFloat("rates.AED")<=arg1,true);
		LOG.info("Successsfully Validates rates for AED is in the expected range, current AED rate is:"+jsonpath.getFloat("rates.AED"));
	}

	@And("user validates that the {int} currency pairs are returned")
	public void userValidatesThatTheCurrencyPairsAreReturned(int arg0) {
		int size = context.response.body().path("rates.size()");
		assertEquals("Number of currency pairs returned",arg0,size);
		LOG.info("Successfully Validatd the number of currency pairs returned:"+size);
	}

	@And("response time should be less than {int} seconds")
	public void responseTimeShouldBeLessThanSeconds(int arg0) {
		assertEquals(context.response.getTimeIn(TimeUnit.MILLISECONDS)<=arg0*1000,true);
		LOG.info("Successfully Validates the response time is:"+context.response.getTimeIn(TimeUnit.MILLISECONDS)+" which is within the expected time");
	}
}
