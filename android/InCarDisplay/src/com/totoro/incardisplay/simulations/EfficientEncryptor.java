package com.totoro.incardisplay.simulations;

/**
 * An efficient encryptor which only stores values and assumes that only certain data
 * values exist. 
 */
public class EfficientEncryptor implements Encryptor {

	@Override
	public String encrypt(TimeSlice[] data) {
		StringBuilder sb = new StringBuilder();
		for(TimeSlice t: data)	{
			long mask = 0xff;
			for(Datum d: t.getAllData())	{
				long val = Double.doubleToLongBits(d.getValue());
				for(int i = 0; i < 8; i++)	{
					long temp = val & (mask << (8*i));
					temp >>= 8 * i;
					sb.append((char)temp);
				}
			}
		}
		return sb.toString();
	}

}
