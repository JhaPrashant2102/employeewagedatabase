package com.cg.employeewagedatabase;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class DBdemo {
	public static void main(String args[]) {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?SSL=false";
		String userName = "root";
		String password = "root";

		//sql connection that i am establishing
		Connection connection;

		// check whether driver is registered
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver Loaded!");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find driver in class path in classpath!", e);
		}

		// listing the drivers
		listDrivers();

		// Connecting to database
		try {
			System.out.println("Connecting to database:      " + jdbcURL);
			connection = DriverManager.getConnection(jdbcURL,userName,password);
			System.out.println("Connection is successful!!!  "+connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while (driverList.hasMoreElements()) {
			Driver driverClass = driverList.nextElement();
			System.out.println("Drivers are :                " + driverClass.getClass().getName());
		}
	}
}
