package com.totoro.incardisplay.simulations;

import java.io.*;

/**
 * Benchmarker for comparing different methods of encryption. 
 */

public class EncryptorComparison {
	public static void main(String[] args) throws IOException {
		DataGenerator gen = new DataGenerator();
		gen.generateData(1000);
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("verbatim.out")));
		pw.println(new VerbatimEncryptor().encrypt(gen.getAllData()));
		pw.close();
		//pw = new PrintWriter(new BufferedWriter(new FileWriter("efficient.out")));
		//pw.println(new EfficientEncryptor().encrypt(gen.getAllData()));
		//pw.close();
	}
}
