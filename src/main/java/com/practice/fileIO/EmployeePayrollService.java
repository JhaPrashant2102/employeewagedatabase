package com.practice.fileIO;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
	// to specify which IO we'll be using
	// we'll be implementing function in other class associated with these IO stream
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayRollData> employeePayRollList;

	public EmployeePayrollService() {
		this.employeePayRollList = new ArrayList<>();
	}

	public List<EmployeePayRollData> getEmployeePayRollList() {
		return employeePayRollList;
	}

	public void setEmployeePayRollList(List<EmployeePayRollData> employeePayRollList) {
		this.employeePayRollList = employeePayRollList;
	}

	public static void main(String args[]) {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		Scanner consoleInputReader = new Scanner(System.in);
		employeePayrollService.readEmployeePayrollData(consoleInputReader);
		employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
	}

	private void readEmployeePayrollData(Scanner consoleInputReader) {
		System.out.println("Enter Employee ID: ");
		int id = consoleInputReader.nextInt();
		System.out.println("Enter Employee Name: ");
		String name = consoleInputReader.next();
		System.out.println("Enter Employee Salary: ");
		Double salary = consoleInputReader.nextDouble();
		employeePayRollList.add(new EmployeePayRollData(id, name, salary));
	}

	public void writeEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO)) {
			System.out.println("Employee PayRoll Data :" + employeePayRollList);
		}
		else if(ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().writeData(employeePayRollList);
		}
	}

	public void printData(IOService ioService) {
		if(ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().printData();
		}
	}

	public long countEntries(IOService ioService) {
		if(ioService.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().countEntries();
		}
		return 0;
	}

	public List<EmployeePayRollData> readData(IOService ioService) {
		if(ioService.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().readData();
		}
		if(ioService.equals(IOService.DB_IO)) {
			return new EmployeePayrollDB_IOService().readData();
		}
		return null;
	}

}
