import java.io.*;
import java.util.*;
public class DataGenerator {
	private static final Random rng = new Random();

	private static double currentSpeed;
	private static double acceleration;

	private static PrintWriter pw;
	
	public static void main(String[] args) {
		final int NUM_LINES = 100000; // how many lines of data you want
		pw = null;
		try	{
			pw = new PrintWriter(new BufferedWriter(new FileWriter("data.txt")));
			currentSpeed = 0; // kilometers per hour
			acceleration = 0;
			double goalSpeed = 0;
			int numLeft = 0;
			for(int line = 0; line < NUM_LINES; line++, numLeft--)	{
				if(numLeft == 0)	{
					goalSpeed = 150 * rng.nextDouble();
					numLeft = 50+rng.nextInt(100);
				}
				double accelInc = getNextAccel(goalSpeed);
				acceleration += accelInc;
				if(currentSpeed + acceleration < 0)	{
					currentSpeed = 0;
					acceleration = 0;
				}
				else	{
					currentSpeed += acceleration;
					currentSpeed = Math.max(0, currentSpeed);
				}
				print();
			}
		}
		catch(Exception ignored) {
			ignored.printStackTrace();
		}
		finally	{
			pw.close();
		}
	}

	private static void print()	{
		pw.printf("{\"speed\": %.10f, \"accel\": %.10f}\n", currentSpeed, acceleration);
	}
	
	private static final double ACCEL_SCALING_FACTOR = 0.005;

	private static final double ACCEL_CORRECTION = 0.1;
	
	private static double getNextAccel(double goalSpeed) {
		double randomChange = ACCEL_SCALING_FACTOR * Math.abs(rng.nextGaussian()) * (goalSpeed - currentSpeed);
		acceleration -= ACCEL_CORRECTION * Math.abs(rng.nextGaussian()) * acceleration;
		return randomChange;
	}

}
