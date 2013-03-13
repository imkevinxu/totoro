package com.totoro.incardisplay;

public class Car {

	public int id;
	public int year;
	public double mpg;
	public String make, model;
	
	public Car(int id, int year, String make, String model, double mpg) {
		this.id = id;
		this.year = year;
		this.make = make;
		this.model = model;
		this.mpg = mpg;
	}

}
