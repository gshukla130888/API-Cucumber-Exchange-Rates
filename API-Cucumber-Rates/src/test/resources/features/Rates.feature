Feature: To view the exchange rate details

  @viewRateDetails
  Scenario: To view the AED to USD exchange rate details
    Given user has access to endpoint "/v6/latest/USD"
    When user makes a request to view the rates
    Then user should get the response code 200
    And user validates that AED is in the range of 3.6 and 3.7 against "USD"
    And user validates that the 162 currency pairs are returned
    And response time should be less than 3 seconds
    And user validates the response with JSON schema "ratesDetailsSchema.json"
