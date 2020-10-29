package com.practice.fileIO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDB_IOService {
	enum StatementType {
		PREPARED_STATEMENT, STATEMENT;
	}

	private PreparedStatement employeePayrollDataStatement;
	private static EmployeePayrollDB_IOService employeePayrollDB_IOService;

	private EmployeePayrollDB_IOService() {
	}

	public static EmployeePayrollDB_IOService getInstance() {
		if (employeePayrollDB_IOService == null) {
			employeePayrollDB_IOService = new EmployeePayrollDB_IOService();
		}
		return employeePayrollDB_IOService;
	}

	public List<EmployeePayRollData> readData() {
		String sql = "select * from employee_payroll_2;";
		List<EmployeePayRollData> employeePayrollList = new ArrayList<>();
		try {
			Connection connection = this.getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(result);
			connection.close();
			// connection needs to be closed - notes
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?SSL=false";
		String userName = "root";
		String password = "root";
		Connection connection;
		System.out.println("Connecting to database:" + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Connection is successful!!!   " + connection);
		return connection;

	}

	public int updateEmployeeData(String name, double salary, StatementType type) {
		switch (type) {
		case PREPARED_STATEMENT:
			return this.updateEmployeeDataUsingPreparedStatement(name, salary);
		case STATEMENT:
			return this.updateEmployeeDataUsingStatement(name, salary);
		default:
			return 0;
		}

	}

	private int updateEmployeeDataUsingStatement(String name, double salary) {
		String sql = String.format("update employee_payroll_2 set salary = %.2f where name = '%s';", salary, name);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
		try (Connection connection = this.getConnection()) {
			String sql = "update employee_payroll_2 set salary = ? where name = ?;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDouble(1, salary);
			preparedStatement.setString(2, name);
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public Map<String, Double> getAverageSalaryByGender() {
		String sql = "select gender, avg(salary) as avg_salary from employee_payroll_2 group by gender;";
		Map<String,Double> avgSalaryMap = new HashMap<>();
		try(Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				String gender = resultSet.getString("gender");
				Double salary = resultSet.getDouble("avg_salary");
				avgSalaryMap.put(gender, salary);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return avgSalaryMap;
	}
	public List<EmployeePayRollData> getEmployeePayrollData(String name) {
		List<EmployeePayRollData> employeePayRollList = null;
		if (this.employeePayrollDataStatement == null) {
			this.prepareStatementForEmployeeData();
		}
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayRollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayRollList;
	}

	private List<EmployeePayRollData> getEmployeePayrollData(ResultSet resultSet) {
		List<EmployeePayRollData> employeePayrollList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayRollData(id, name, salary, startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "Select * from employee_payroll_2 where name = ?;";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<EmployeePayRollData> getEmployeeListInRange(String date1, String date2) {
		String sql = String.format("select * from employee_payroll_2 where start between '%s' and '%s';", date1, date2);
		List<EmployeePayRollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return employeePayrollList;
	}

}
