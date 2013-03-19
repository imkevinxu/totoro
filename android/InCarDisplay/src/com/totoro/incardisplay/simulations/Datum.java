package com.totoro.incardisplay.simulations;

/**
 * Immutable class storing a single value for a single label at a single
 * point in time.
 */

public class Datum {
	private String label;
	private double value;
	public Datum(String label, double value) {
		this.label = new String(label);
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public double getValue() {
		return value;
	}
	// TODO (Nick): Add i18n support?
	public String toString()	{
		return String.format("%s, %.10f", label, value);
	}
}
