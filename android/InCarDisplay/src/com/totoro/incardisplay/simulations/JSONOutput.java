package com.totoro.incardisplay.simulations;

import java.io.*;
public class JSONOutput {
	public static void main(String[] args) throws IOException {
		DataGenerator gen = new DataGenerator();
		gen.generateData(100);
		TimeSlice[] list = gen.getAllData();
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("jsonoutput")));
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(TimeSlice t: list)	{
			sb.append(get(t, ++count));
		}
		sb.deleteCharAt(sb.length()-1);
		sb.deleteCharAt(sb.length()-1);
		pw.println("{");
		pw.println("\t\"trips\":[");
		pw.println(sb);
		pw.println("\t]");
		pw.println("}");
		pw.close();
	}
	
	public static String get(TimeSlice t, int num)	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("\t\t{\n", num));
		for(Datum d: t.getAllData())		{
			sb.append(format(d));
		}
		sb.deleteCharAt(sb.length()-1);
		sb.deleteCharAt(sb.length()-1);
		sb.append("\n\t\t},\n");
		return sb.toString();
	}

	private static Object format(Datum d) {
		return String.format("\t\t\t\"%s\": %.10f,\n", d.getLabel(), d.getValue());
	}
	
}

