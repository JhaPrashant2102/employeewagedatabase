package com.practice.fileIO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.practice.fileIO.EmployeePayrollDB_IOService.StatementType;

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

	public EmployeePayrollService(List<EmployeePayRollData> employeeList) {
		this();
		this.employeePayRollList = employeeList;
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
		if (ioService.equals(IOService.DB_IO)) {
			System.out.println(this.employeePayRollList);
		}
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().countEntries();
		}
		if (ioService.equals(IOService.DB_IO)) {
			return this.employeePayRollList.size();
		}
		if (ioService.equals(IOService.REST_IO)) {
			return this.employeePayRollList.size();
		}
		return this.employeePayRollList.size();
	}

	public List<EmployeePayRollData> readData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			employeePayRollList = new EmployeePayrollFileIOService().readData();
			return employeePayRollList;
		}
		if (ioService.equals(IOService.DB_IO)) {
			employeePayRollList = employeePayrollDB_IOService.readData();
			return employeePayRollList;
		}
		return null;
	}

	public void addEmployeesToPayroll(List<EmployeePayRollData> payrollList) {
		payrollList.forEach(employeePayrollData -> {
			System.out.println("Employee Being Added: " + employeePayrollData.getName());
			this.addEmployeeToPayroll(employeePayrollData.getName(), employeePayrollData.getSalary(),
					employeePayrollData.getStartDate(), employeePayrollData.getGender());
			System.out.println("Employee Added: " + employeePayrollData.getName());
		});
		System.out.println(this.employeePayRollList);
	}

	public void addEmployeeToPayroll(EmployeePayRollData employeePayRollData, IOService ioService) {
		if (ioService.equals(IOService.DB_IO)) {
			this.addEmployeeToPayroll(employeePayRollData.getName(), employeePayRollData.getSalary(),
					employeePayRollData.getStartDate(), employeePayRollData.getGender());
		}

		else if (ioService.equals(IOService.REST_IO)) {
			employeePayRollList.add(employeePayRollData);
		} else
			employeePayRollList.add(employeePayRollData);
	}

	public void addEmployeesToPayrollWithThreads(List<EmployeePayRollData> payrollList) {
		// needed for thread complete execution check
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
		payrollList.forEach(employeePayrollData -> {
			Runnable task = () -> {
				employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
				System.out.println("Employee being added : " + Thread.currentThread().getName());
				this.addEmployeeToPayroll(employeePayrollData.getName(), employeePayrollData.getSalary(),
						employeePayrollData.getStartDate(), employeePayrollData.getGender());
				employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
				System.out.println("Employee Added: " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, employeePayrollData.getName());
			thread.start();
		});
		// while loop is needed to check whether the thread gets completed or not
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(payrollList);
	}

	public List<EmployeePayRollData> getEmployeeListInStartDateRange(String date1, String date2, IOService ioService) {
		if (ioService.equals(IOService.DB_IO)) {
			return employeePayrollDB_IOService.getEmployeeListInRange(date1, date2);
		}
		return null;
	}

	public void updateEmployeeSalary(String name, double salary, StatementType type) {
		// check above
		int result = employeePayrollDB_IOService.updateEmployeeData(name, salary, type);
		if (result == 0)
			return;
		EmployeePayRollData employeePayRollData = this.getEmployeePayRollData(name);
		if (employeePayRollData != null)
			employeePayRollData.setSalary(salary);
	}

	public void updateEmployeeSalary(String name, double salary, IOService ioService) {
		if (ioService.equals(IOService.REST_IO)) {
			EmployeePayRollData employeePayRollData = this.getEmployeePayRollData(name);
			if (employeePayRollData != null)
				employeePayRollData.setSalary(salary);
		} else {
			EmployeePayRollData employeePayRollData = this.getEmployeePayRollData(name);
			if (employeePayRollData != null)
				employeePayRollData.setSalary(salary);
		}
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

	public Map<String, Double> readAverageSalaryByGender(IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDB_IOService.getAverageSalaryByGender();
		return null;
	}

	public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
		this.employeePayRollList.add(employeePayrollDB_IOService.addEmployeeToPayroll(name, salary, startDate, gender));
	}

	public EmployeePayRollData getEmployeePayrollData(String name) {
		return this.employeePayRollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.getName().equals(name)).findFirst()
				.orElse(null);
	}

}
