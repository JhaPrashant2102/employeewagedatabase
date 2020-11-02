package com.practice.fileIO;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.practice.fileIO.EmployeePayrollDB_IOService.StatementType;
import com.practice.fileIO.EmployeePayrollService.IOService;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EmployeePayRollServiceTest {

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		List<EmployeePayRollData> employeeList = new ArrayList<>();
		employeeList.add(new EmployeePayRollData(1, "Jeff Bezos", 10000.20));
		employeeList.add(new EmployeePayRollData(2, "Bill gates", 8000.60));
		employeeList.add(new EmployeePayRollData(3, "Mark Zuckerberg", 6000.80));
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.setEmployeePayRollList(employeeList);
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.printData(IOService.FILE_IO);
		long numberOfEntries = employeePayrollService.countEntries(IOService.FILE_IO);
		List<EmployeePayRollData> analysisList = employeePayrollService.readData(IOService.FILE_IO);
		if (analysisList != null) {
			System.out.println(analysisList);
		} else {
			System.out.println("null");
		}
		assertEquals(3, numberOfEntries);
	}

	// UC2
	@Test
	public void givenEmployeePayrollInDbWhenRetrievedShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayRollData> employeePayrollData = employeePayrollService.readData(IOService.DB_IO);
		assertEquals(3, employeePayrollData.size());
	}

	// UC3
	@Test
	public void givenNewSalaryForEmployeeWhenUpdatedShouldSyncWithDBUsingStatement() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayRollData> employeePayRollData = employeePayrollService.readData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Terisa", 4000000.00, StatementType.STATEMENT);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
		assertTrue(result);
	}

	// UC4
	@Test
	public void givenNewSalaryForEmployeeWhenUpdatedShouldSyncWithDBUsingPreparedStatement() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayRollData> employeePayRollData = employeePayrollService.readData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Terisa", 4000000.00, StatementType.PREPARED_STATEMENT);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
		assertTrue(result);
	}

	// UC5
	@Test
	public void givenDateRangeWhenRetrievedShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayRollData> employeePayRollData = employeePayrollService.readData(IOService.DB_IO);
		List<EmployeePayRollData> employeePayRollList = employeePayrollService
				.getEmployeeListInStartDateRange("2010-01-01", "2020-12-12", IOService.DB_IO);
		System.out.println(employeePayRollList.size());
		assertEquals(3, employeePayRollList.size());
	}

	// UC6
	@Test
	public void givenPayrollDataWhenAverageSalaryRetrievedByGenderShouldReturnProperValue() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayRollData> employeePayRollData = employeePayrollService.readData(IOService.DB_IO);
		Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(IOService.DB_IO);
		assertTrue(
				averageSalaryByGender.get("M").equals(2000000.00) && averageSalaryByGender.get("F").equals(4000000.00));
	}

	// UC7
	@Test
	public void givenNewEmployeeWhenAddedShouldSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayRollData> employeePayRollData = employeePayrollService.readData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("Mark", 5000000.00, LocalDate.now(), "M");
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
		assertTrue(result);
	}

	// UC1-multithreading
	@Test
	public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries() {
		EmployeePayRollData[] arrayOfEmps = { new EmployeePayRollData(0, "Jeff Bezos", "M", 100000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Bill Gates", "M", 200000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Mark Zuckerberg", "M", 300000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Sundar", "M", 600000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Mukesh", "M", 100000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Anil", "M", 200000.0, LocalDate.now()), };

		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		System.out.println("Duration without Thread: " + Duration.between(start, end));
		assertEquals(7, employeePayrollService.countEntries(IOService.DB_IO));
	}

	// UC2-multithreading
	@Test
	public void given6Employees_WhenAddedToDBUsingThreads_ShouldMatchEmployeeEntries() {
		EmployeePayRollData[] arrayOfEmps = { new EmployeePayRollData(0, "Jeff Bezos", "M", 100000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Bill Gates", "M", 200000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Mark Zuckerberg", "M", 300000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Sundar", "M", 600000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Mukesh", "M", 100000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Anil", "M", 200000.0, LocalDate.now()), };

		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		System.out.println("Duration without Thread: " + Duration.between(start, end));
		Instant threadStart = Instant.now();
		employeePayrollService.addEmployeesToPayrollWithThreads(Arrays.asList(arrayOfEmps));
		Instant threadend = Instant.now();
		System.out.println("Duration With Thread: " + Duration.between(threadStart, threadend));
		assertEquals(13, employeePayrollService.countEntries(IOService.DB_IO));
	}

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	// JsonServerRestAssured UC4
	@Test
	public void givenEmployeeDataInJsonServer_whenRetrieved_shouldMatchTheCount() {
		EmployeePayRollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		assertEquals(2, entries);

	}

	private EmployeePayRollData[] getEmployeeList() {
		Response response = RestAssured.get("/employee_payroll");
		EmployeePayRollData[] arrayOfEmps = new Gson().fromJson(response.asString(), EmployeePayRollData[].class);
		return arrayOfEmps;
	}

	// JsonServerRestAssured UC1
	@Test
	public void givenNewEmployeeWhenAdded_shouldMatch201ResponseAndCount() {
		EmployeePayRollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		EmployeePayRollData employeePayRollData = new EmployeePayRollData(0, "Mark Zuckerberg", "M", 300000.0,
				LocalDate.now());

		Response response = addEmployeeToJsonServer(employeePayRollData);
		int statusCode = response.getStatusCode();
		assertEquals(201, statusCode);

		employeePayRollData = new Gson().fromJson(response.asString(), EmployeePayRollData.class);
		employeePayrollService.addEmployeeToPayroll(employeePayRollData, IOService.REST_IO);
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		assertEquals(9, entries);
	}

	public Response addEmployeeToJsonServer(EmployeePayRollData employeePayRollData) {
		String empJson = new Gson().toJson(employeePayRollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/employee_payroll");
	}

	// JsonServerRestAssured UC2
	@Test
	public void givenMultipleEmployeeWhenAdded_shouldMatch201ResponseAndCount() {
		EmployeePayRollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));

		EmployeePayRollData[] arrayOfEmps1 = { new EmployeePayRollData(0, "Sunder", "M", 60000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Mukesh", "M", 100000.0, LocalDate.now()),
				new EmployeePayRollData(0, "Anil", "M", 200000.0, LocalDate.now()), };

		for (EmployeePayRollData employeePayRollData : arrayOfEmps1) {
			Response response = addEmployeeToJsonServer(employeePayRollData);
			int statusCode = response.getStatusCode();
			assertEquals(201, statusCode);
			employeePayRollData = new Gson().fromJson(response.asString(), EmployeePayRollData.class);
			employeePayrollService.addEmployeeToPayroll(employeePayRollData, IOService.REST_IO);
		}

		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		assertEquals(6, entries);
	}

	// JsonServerRestAssured UC3
	@Test
	public void givenNewSalaryForEmployeeWhenUpdated_shouldMatch200Response() {
		///populate my memory
		EmployeePayRollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));

		//memory
		employeePayrollService.updateEmployeeSalary("Sunder", 300000.00, IOService.REST_IO);
		EmployeePayRollData employeePayRollData = employeePayrollService.getEmployeePayrollData("Sunder");
		
		//file
		String empJson = new Gson().toJson(employeePayRollData);
		RequestSpecification requestSpecification = RestAssured.given();
		requestSpecification.header("Content-Type","application/json");
		requestSpecification.body(empJson);
		Response response = requestSpecification.put("/employee_payroll/"+employeePayRollData.getId());
		
		//test changes are made at both places or not
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200,statusCode);
	}
	
	@Test
	public void givenEmployeeToDelete_WhenDeleted_shouldMatch200ResponseAndCount() {
		///populate my memory
		EmployeePayRollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));

		//file
		EmployeePayRollData employeePayRollData = employeePayrollService.getEmployeePayrollData("Sunder");
		RequestSpecification requestSpecification = RestAssured.given();
		requestSpecification.header("Content-Type","application/json");
		Response response = requestSpecification.delete("/employee_payroll/"+employeePayRollData.getId());
		
		//test wether deleted from file or not
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200,statusCode);
		
		//delete from memory
		employeePayrollService.deleteEmployeePayroll(employeePayRollData.getName(),IOService.REST_IO);
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(4,entries);
	}

	
}
