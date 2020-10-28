package com.practice.fileIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollFileIOService {
	public String PAYROLL_FILE_NAME = "payroll-file.txt";

	public void writeData(List<EmployeePayRollData> employeePayRollList) {
		StringBuffer empBuffer = new StringBuffer();
		employeePayRollList.forEach(employee -> {
			String employeeData = employee.toString().concat("\n");
			empBuffer.append(employeeData);
		});

		try {
			Files.write(Paths.get(PAYROLL_FILE_NAME), empBuffer.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printData() {
		try {
			Files.lines(new File("payroll-file.txt").toPath()).forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public long countEntries() {
		long entries = 0;
		try {
			entries = Files.lines(new File("payroll-file.txt").toPath()).count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entries;
	}

	public List<EmployeePayRollData> readData() {
		List<EmployeePayRollData> returnList = new ArrayList<>();
		try {
			Files.lines(new File(PAYROLL_FILE_NAME).toPath()).map(line -> line.trim()).forEach(line -> {
				int check = 1;
				int id = 0;
				double salary = 0;
				String name = "";
				String data = line.toString();
				String dataList[] = data.split(",");
				for (String string : dataList) {
					if (check == 1) {
						id = Integer.parseInt(string.replaceAll("id =", ""));
						check++;
						continue;
					}
					if(check==2) {
						name = string.replaceAll("name =", "");
						check++;
						continue;
					}
					if(check==3) {
						salary = Double.parseDouble(string.replaceAll("salary =", ""));
						break;
					}
				}
				returnList.add(new EmployeePayRollData(id, name, salary));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnList;
	}

}
