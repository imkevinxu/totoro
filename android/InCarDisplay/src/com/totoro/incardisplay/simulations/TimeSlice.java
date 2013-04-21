package com.totoro.incardisplay.simulations;

import java.util.*;

/**
 * Single instance in time where data is collected.
 */

public class TimeSlice {
	private List<Datum> dataList;
	private Map<String, Double> map;
	private static final double EPSILON_THRESHOLD = 1e-7;
	public TimeSlice(List<Datum> dataListIn) {
		dataList = new ArrayList<Datum>();
		map = new HashMap<String, Double>();
		for(Datum datum: dataListIn)		{
			map.put(datum.getLabel(), datum.getValue());
			dataList.add(datum);
		}
		if(map.size() != dataListIn.size())	{
			throw new IllegalArgumentException("For a single time interval, a given label may only appear once.");
		}
	}

	public Set<String> getAllLabels()	{
		return map.keySet();
	}
	
	public List<Datum> getAllData()	{
		return dataList;
	}
	
	public double getValue(String label)	{
		if(!map.containsKey(label))	{
			throw new IllegalArgumentException("That label does not exist.");
		}
		return map.get(label);
	}
	
	public String toString()	{
		StringBuilder ret = new StringBuilder();
		for(Datum datum: dataList){
			ret.append(datum.toString() + "\n");
		}
		return ret.toString().trim();
	}
	
	public boolean equals(Object o)	{
		if(this == null ^ o == null)	{
			return false;
		}
		if(this == null && o == null)	{
			return true;
		}
		if(!(o instanceof TimeSlice))	{
			return false;
		}
		TimeSlice t = (TimeSlice)o;
		if(map == null ^ t.map == null)	{
			return true;
		}
		if(map == null && t.map == null)	{
			return true;
		}
		if(map.size() != t.map.size())	{
			return false;
		}
		for(String key: map.keySet())	{
			if(!t.map.containsKey(key))	{
				return false;
			}
			if(Math.abs(map.get(key)-t.map.get(key)) > EPSILON_THRESHOLD)		{
				return false;
			}
		}
		return true;
	}
	
}
