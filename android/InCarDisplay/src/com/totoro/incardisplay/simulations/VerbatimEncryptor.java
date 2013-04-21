package com.totoro.incardisplay.simulations;

/**
 * This encryptor outputs data in a human-readable format. 
 */
public class VerbatimEncryptor implements Encryptor {

	@Override
	public String encrypt(TimeSlice[] data) {
		StringBuilder sb = new StringBuilder();
		for(TimeSlice t: data)	{
			sb.append(String.format("(%s)", timeSliceToString(t)));
		}
		return sb.toString();
	}
	
	private String timeSliceToString(TimeSlice t)	{
		StringBuilder sb = new StringBuilder();
		for(Datum d: t.getAllData())	{
			sb.append(datumToString(d));
		}
		return sb.toString();
	}
	
	private String datumToString(Datum d)	{
		return String.format("[%s,%.10f]", d.getLabel(), d.getValue());
	}
	
}
