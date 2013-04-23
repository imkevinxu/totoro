package com.totoro.incardisplay.simulations;

import java.io.*;
public class JSONOutput {
	// Change these flags for number of files and number of seconds.
	private static final int MIN_SECONDS = 60*5;
	private static final int MAX_SECONDS = 60*15;
	private static final int NUM_FILES = 100;
	public static void main(String[] args) throws IOException {
		for(int i = 1; i <= NUM_FILES; i++)	{
			System.out.println("Generating file " + i);
			DataGenerator gen = new DataGenerator();
			int num = MIN_SECONDS + (int)((MAX_SECONDS-MIN_SECONDS+1)*Math.random());
			gen.generateData(num);
			TimeSlice[] list = gen.getAllData();
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("data" + i + ".txt")));
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

