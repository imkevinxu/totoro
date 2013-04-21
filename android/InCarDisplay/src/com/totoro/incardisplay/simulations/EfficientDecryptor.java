package com.totoro.incardisplay.simulations;

import java.util.ArrayList;

/**
 * Efficient decryptor for compressed files.
 */

public class EfficientDecryptor implements Decryptor {

	@Override
	public TimeSlice[] decrypt(String data) {
		ArrayList<TimeSlice> tempList = new ArrayList<TimeSlice>();
		while(data.length() > 0)	{
			String substring = data.substring(0, 96);
			data = data.substring(96);
			tempList.add(stringToTimeSlice(substring));
		}
		TimeSlice[] ret = new TimeSlice[tempList.size()];
		return tempList.toArray(ret);
	}

	private TimeSlice stringToTimeSlice(String substring) {
		ArrayList<Datum> list = new ArrayList<Datum>();
		list.add(new Datum("currentSpeed", extract(substring.substring(0,8))));
		list.add(new Datum("acceleration", extract(substring.substring(8,16))));
		list.add(new Datum("airConditioning", extract(substring.substring(16,24))));
		list.add(new Datum("steering", extract(substring.substring(24,32))));
		list.add(new Datum("odometer", extract(substring.substring(32,40))));
		list.add(new Datum("brakingPressure", extract(substring.substring(40,48))));
		list.add(new Datum("pedalForce", extract(substring.substring(48,56))));
		list.add(new Datum("altitude", extract(substring.substring(56,64))));
		list.add(new Datum("fuelUsage", extract(substring.substring(64,72))));
		list.add(new Datum("mpg", extract(substring.substring(72,80))));
		list.add(new Datum("tripLength", extract(substring.substring(80,88))));
		list.add(new Datum("ecoScore", extract(substring.substring(88,96))));
		
		return new TimeSlice(list);
	}
	
	private double extract(String str)	{
		long ret = 0;
		for(int i = 0; i < str.length(); i++)	{
			ret |= ((long)str.charAt(i)) << (8*i);
		}
		double ans = Double.longBitsToDouble(ret);
		return ans;
	}
}