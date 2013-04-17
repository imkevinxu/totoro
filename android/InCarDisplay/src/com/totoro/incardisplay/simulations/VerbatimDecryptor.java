package com.totoro.incardisplay.simulations;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class takes in a human-readable file of data and decrypts it into
 * data usable by a program. 
 */

public class VerbatimDecryptor implements Decryptor {

	@Override
	public TimeSlice[] decrypt(String data) {
		ArrayList<TimeSlice> tempList = new ArrayList<TimeSlice>();
		while(data.length() > 0)	{
			String substring = data.substring(1, data.indexOf(")"));
			data = data.substring(data.indexOf(")")+1);
			tempList.add(stringToTimeSlice(substring));
		}
		TimeSlice[] ret = new TimeSlice[tempList.size()];
		return tempList.toArray(ret);
	}

	private TimeSlice stringToTimeSlice(String data) {
		ArrayList<Datum> list = new ArrayList<Datum>();
		while(data.length() > 0)	{
			String substring = data.substring(1, data.indexOf("]"));
			data = data.substring(data.indexOf("]")+1);
			list.add(stringToDatum(substring));
		}
		return new TimeSlice(list);
	}

	private Datum stringToDatum(String substring) {
		StringTokenizer st = new StringTokenizer(substring, ",");
		return new Datum(st.nextToken(), Double.parseDouble(st.nextToken()));
	}
	
}
