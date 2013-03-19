package com.totoro.incardisplay.simulations;

import java.util.*;

public class TimeSlice {
	private List<Datum> dataList;
	private Map<String, Double> map;
	public TimeSlice(List<Datum> dataListIn) {
		dataList = new ArrayList<Datum>();
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
	
}
