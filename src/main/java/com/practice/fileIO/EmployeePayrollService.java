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
	private EmployeePayrollDB_IOService employeePayrollDB_IOService;
	private List<EmployeePayRollData> employeePayRollList;

	public EmployeePayrollService() {
		employeePayrollDB_IOService = EmployeePayrollDB_IOService.getInstance();
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
		} else if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().writeData(employeePayRollList);
		}
	}

	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().printData();
		}
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().countEntries();
		}
		return 0;
	}

	public List<EmployeePayRollData> readData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			employeePayRollList = new EmployeePayrollFileIOService().readData();
			return employeePayRollList;
		}
		if (ioService.equals(IOService.DB_IO)) {
			employeePayRollList =employeePayrollDB_IOService.readData();
			return employeePayRollList;
		}
		return null;
	}

	public void updateEmployeeSalary(String name, double salary) {
		//check above
		int result = employeePayrollDB_IOService.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayRollData employeePayRollData = this.getEmployeePayRollData(name);
		if (employeePayRollData != null)
			employeePayRollData.setSalary(salary);
	}

	private EmployeePayRollData getEmployeePayRollData(String name) {
		EmployeePayRollData employeePayRollData;
		employeePayRollData = this.employeePayRollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.getName().equals(name)).findFirst()
				.orElse(null);
		return employeePayRollData;
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<EmployeePayRollData> checkList = employeePayrollDB_IOService.getEmployeePayrollData(name);
		return checkList.get(0).equals(getEmployeePayRollData(name));
	}

}
