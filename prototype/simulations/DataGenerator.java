import java.io.*;
import java.util.*;
public class DataGenerator {
	private static final Random rng = new Random();

	// current speed
	private static double currentSpeed;
	// current acceleration
	private static double acceleration;
	// current pressure on brake pedal
	private static double brakingPressure;
	// current pressure on accelerator
	private static double pedalForce;
	// current A/C
	private static double airConditioning;

	private static PrintWriter pw;

	public static void main(String[] args) {
		final int NUM_LINES = 100000; // how many lines of data you want
		pw = null;
		try	{
			pw = new PrintWriter(new BufferedWriter(new FileWriter("data.txt")));
			currentSpeed = 0; // kilometers per hour
			acceleration = 0;
			airConditioning = 0;
			double goalSpeed = 0;
			int numSpeedTurnsLeft = 0;
			for(int line = 0; line < NUM_LINES; line++, numSpeedTurnsLeft--)	{
				if(numSpeedTurnsLeft == 0)	{
					goalSpeed = 150 * rng.nextDouble();
					numSpeedTurnsLeft = 50+rng.nextInt(100);
				}
				double accelInc = getNextAccel(goalSpeed);
				acceleration += accelInc;
				if(currentSpeed + acceleration < 0)	{
					currentSpeed = 0;
					acceleration = 0;
					brakingPressure = 0;
					pedalForce = 0;
				}
				else	{
					currentSpeed += acceleration;
					currentSpeed = Math.max(0, currentSpeed);
					brakingPressure = Math.max(0, -accelInc);
					pedalForce = Math.max(0, accelInc);
				}
				airConditioning += getNextAC();
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
		pw.printf("{");
		pw.printf("\"speed\": %.10f, ", currentSpeed);
		pw.printf("\"accel\": %.10f, ", acceleration);
		pw.printf("\"brake\": %.10f, ", brakingPressure);
		pw.printf("\"pedal\": %.10f, ", pedalForce);
		pw.printf("\"ac\": %.10f", airConditioning);
		pw.println("}");
	}

	private static final double AC_SCALING = 0.01;

	private static double getNextAC()	{
		return Math.max(-airConditioning, rng.nextGaussian() - AC_SCALING * airConditioning);
	}

	private static final double ACCEL_SCALING_FACTOR = 0.005;

	private static final double ACCEL_CORRECTION = 0.1;




	private static double getNextAccel(double goalSpeed) {
		double randomChange = ACCEL_SCALING_FACTOR * Math.abs(rng.nextGaussian()) * (goalSpeed - currentSpeed);
		acceleration -= ACCEL_CORRECTION * Math.abs(rng.nextGaussian()) * acceleration;
		return randomChange;
	}

}
