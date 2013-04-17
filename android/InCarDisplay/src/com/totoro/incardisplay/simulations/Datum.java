package com.totoro.incardisplay.simulations;

/**
 * Immutable class storing a single value for a single label at a single
 * point in time.
 */

public class Datum implements Comparable<Datum> {
	
	private static final double EPSILON_THRESHOLD = 1e-7;
	
	private String label;
	private double value;
	public Datum(String label, double value) {
		if(!validLabel(label))	{
			throw new IllegalArgumentException("Data labels must consist purely of letters.");
		}
		this.label = new String(label);
		this.value = value;
	}
	
	private static boolean validLabel(String str)	{
		for(int i = 0; i < str.length(); i++)	{
			if(!Character.isLetter(str.charAt(i)))	{
				return false;
			}
		}
		return true;
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
	public boolean equals(Object o)	{
		if(this == null ^ o == null)	{
			return false;
		}
		if(this == null && o == null)	{
			return true;
		}
		if(!(o instanceof Datum))	{
			return false;
		}
		Datum d = (Datum)o;
		if(label == null ^ d.label == null)	{ 
			return false;
		}
		if(label != null && d.label != null && !label.equals(d.label)){
			return false;
		}
		return Math.abs(value - d.value) < EPSILON_THRESHOLD;
	}
	public int compareTo(Datum d)	{
		return label.compareTo(d.label);
	}
}
