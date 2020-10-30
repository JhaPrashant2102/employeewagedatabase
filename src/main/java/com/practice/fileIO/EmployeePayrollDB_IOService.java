package com.practice.fileIO;

import java.sql.Connection;
import java.sql.Date;
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
	private int connectionCounter = 0;
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
		connectionCounter++;
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?SSL=false";
		String userName = "root";
		String password = "root";
		Connection connection;
		System.out.println("Processing Thread: "+Thread.currentThread().getName()+"Connecting to database with Id:" + connectionCounter);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Processing Thread: "+Thread.currentThread().getName()+" Id: "+connectionCounter+"Connection is successful!!!   " + connection);
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
		Map<String, Double> avgSalaryMap = new HashMap<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String gender = resultSet.getString("gender");
				Double salary = resultSet.getDouble("avg_salary");
				avgSalaryMap.put(gender, salary);
			}
		} catch (SQLException e) {
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

	public EmployeePayRollData addEmployeeToPayrollUC7(String name, double salary, LocalDate startDate, String gender) {
		int employeeId = -1;
		EmployeePayRollData employeePayRollData = null;
		String sql = String.format(
				"insert into employee_payroll_2 (name,gender,salary,start) values ('%s','%s','%s','%s')", name, gender,
				salary, Date.valueOf(startDate));
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employeePayRollData = new EmployeePayRollData(employeeId, name, salary, startDate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayRollData;
	}

	public EmployeePayRollData addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
		int employeeId = -1;
		Connection connection = null;
		EmployeePayRollData employeePayRollData = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"insert into employee_payroll_2 (name,gender,salary,start) values ('%s','%s','%s','%s')", name,
					gender, salary, Date.valueOf(startDate));
			int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayRollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		try (Statement statement = connection.createStatement()) {
			double deduction = salary * 0.2;
			double taxablePay = salary - deduction;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format(
					"insert into payroll_details (employee_id,basic_pay,deductions,taxable_pay,tax,net_pay) values ('%s','%s','%s','%s','%s','%s')",
					employeeId, salary, deduction, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				employeePayRollData = new EmployeePayRollData(employeeId, name, salary, startDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return employeePayRollData;
	}

}
