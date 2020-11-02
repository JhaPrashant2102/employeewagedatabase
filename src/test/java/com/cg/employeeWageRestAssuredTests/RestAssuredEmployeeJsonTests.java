package com.cg.employeeWageRestAssuredTests;

import static org.junit.Assert.*;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RestAssuredEmployeeJsonTests {
	private int empId;

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
		empId = 3;
	}

	public Response getEmployeeList() {
		Response response = RestAssured.get("/employees");
		return response;
	}

	@Test
	public void onCallingList_ReturnEmployeeList() {
		Response response = getEmployeeList();
		System.out.println("At first: " + response.asString());
		response.then().body("id", Matchers.hasItems(1, 3, 4, 5));
		response.then().body("name", Matchers.hasItems("Pankaj"));
	}

	@Test
	public void givenEmployee_OnPost_shouldReturnAddedEmployee() {
		Response response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body("{\"name\": \"Ajay\", \"salary\": \"5000\"}").when().post("/employees");
		String responseAsString = response.asString();
		System.out.println(responseAsString);
		JsonObject jsonObject = new Gson().fromJson(responseAsString,JsonObject.class);
		int id = jsonObject.get("id").getAsInt();
		response.then().body("id", Matchers.any(Integer.class));
		response.then().body("name", Matchers.is("Ajay"));
	}
	
	@Test
	public void givenEmployee_OnUpdate_shouldReturnUpdateddEmployee() {
		Response response = RestAssured.given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body("{\"name\": \"Lisa Tamaki\", \"salary\": \"5000\"}").when().put("/employees/"+3);
		String responseAsString = response.asString();
		System.out.println(responseAsString);
		response.then().body("id", Matchers.any(Integer.class));
		response.then().body("name", Matchers.is("Lisa Tamaki"));
		response.then().body("salary",Matchers.is("5000"));
	}
	
	@Test
	public void givenEmployee_OnDelete_shouldReturnSuccessStatus() {
		empId = 8;
		Response response = RestAssured.delete("/employees/"+empId);
		//String responseAsString = response.asString();
		//System.out.println(responseAsString);
		int statusCode =  response.getStatusCode();
		MatcherAssert.assertThat(statusCode,CoreMatchers.is(200));
		response = getEmployeeList();
		System.out.println("At end: "+response.asString());
		response.then().body("id",Matchers.not(empId));
	}

}
