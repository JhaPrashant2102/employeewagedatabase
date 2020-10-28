package com.practice.fileIO;

import java.time.LocalDate;

public class EmployeePayRollData {
	private int id;
	private String name;
	private double salary;
	private LocalDate startDate;

	public EmployeePayRollData(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayRollData(int id, String name, double salary,LocalDate startDate) {
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.startDate = startDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String toString() {
		return "id ="+this.getId()+",name ="+this.getName()+",salary ="+this.getSalary();
	}
}
