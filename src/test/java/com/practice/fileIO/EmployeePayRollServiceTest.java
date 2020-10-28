package com.practice.fileIO;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import com.practice.fileIO.EmployeePayrollService.IOService;


public class EmployeePayRollServiceTest {

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		List<EmployeePayRollData> employeeList = new ArrayList<>();
		employeeList.add(new EmployeePayRollData(1,"Jeff Bezos",10000.20));
		employeeList.add(new EmployeePayRollData(2,"Bill gates",8000.60));
		employeeList.add(new EmployeePayRollData(3,"Mark Zuckerberg",6000.80));
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.setEmployeePayRollList(employeeList);
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.printData(IOService.FILE_IO);
		long numberOfEntries = employeePayrollService.countEntries(IOService.FILE_IO);
		List<EmployeePayRollData> analysisList = employeePayrollService.readData(IOService.FILE_IO);
		if(analysisList!=null) {
		System.out.println(analysisList);
		}
		else {
			System.out.println("null");
		}
		assertEquals(3,numberOfEntries);
	}
	
	//UC2
	@Test
	public void givenEmployeePayrollInDbWhenRetrievedShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayRollData> employeePayrollData = employeePayrollService.readData(IOService.DB_IO);
		assertEquals(3,employeePayrollData.size());
	}
	
	

}
