package com.practice.fileIO;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayRollData {
	private int id;
	private String name;
	private String gender;
	private double salary;
	private LocalDate startDate;

	public EmployeePayRollData(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayRollData(int id, String name, double salary, LocalDate startDate) {
		this(id, name, salary);
		this.startDate = startDate;
	}

	public EmployeePayRollData(int id, String name, String gender, double salary, LocalDate startDate) {
		this(id, name, salary, startDate);
		this.setGender(gender);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
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
		return "id =" + this.getId() + ",name =" + this.getName() + ",salary =" + this.getSalary();
	}

	@Override
	public int hashCode() {
		return Objects.hash(name,gender,salary,startDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayRollData that = (EmployeePayRollData) obj;
		return id == that.id && Double.compare(that.salary, salary) == 0 && name.equals(that.name);
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}
