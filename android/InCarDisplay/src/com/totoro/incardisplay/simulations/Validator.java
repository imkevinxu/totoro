package com.totoro.incardisplay.simulations;

/**
 * This class serves as a framework for validating whether encryptors and decryptors can work together.
 * If a pair cannot be validated, then a RuntimeException will be thrown with information regarding
 * the failure.
 */
public class Validator {
	public static void validate(Encryptor encrypt, Decryptor decrypt)	{
		DataGenerator generator = new DataGenerator();
		generator.generateData(1000);
		TimeSlice[] originalData = generator.getAllData();
		TimeSlice[] modifiedData = decrypt.decrypt(encrypt.encrypt(originalData));
		if(originalData.length != modifiedData.length)	{
			throw new RuntimeException(String.format("Claimed to decrypt %d data elements, original had %d data elements.", modifiedData.length, originalData.length));
		}
		for(int i = 0; i < originalData.length; i++)	{
			if(!originalData[i].equals(modifiedData[i]))	{
				throw new RuntimeException(String.format("Decrypted datum %s, should have had datum %s, index %d", modifiedData[i], originalData[i], i));
			}
		}
		System.out.println(encrypt.getClass().getSimpleName() + " can be decrypted by " + decrypt.getClass().getSimpleName());
	}

	public static void main(String[] args) {
		validate(new VerbatimEncryptor(), new VerbatimDecryptor());
		validate(new EfficientEncryptor(), new EfficientDecryptor());
	}
	
}
