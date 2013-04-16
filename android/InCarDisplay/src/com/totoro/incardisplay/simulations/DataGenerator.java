package com.totoro.incardisplay.simulations;

import java.util.*;

/**
 * Random data generator.
 */

public class DataGenerator {

	private Random rng;

	private TimeSlice[] data;

	public DataGenerator()	{
		rng = new Random();
	}

	public TimeSlice getTimeSlice(int time)	{
		if(time < 0)	{
			throw new IllegalArgumentException("Time cannot be negative.");
		}
		else if(time >= data.length)	{
			throw new IllegalArgumentException("Time is out-of-bounds.");
		}
		return data[time];
	}
	
	public TimeSlice[] getAllData()	{
		return data;
	}
	
	// TODO(Nick): Latitude/longitude
	// TODO(Nick): Eco-score
	// TODO(Nick): Tell KX what units are
	// TODO(Nick): Make output JSON
	public void generateData(final int NUM_LINES) {
		currentSpeed = 0;
		acceleration = 0;
		airConditioning = 0;
		steering = 0;
		odometer = 0;
		brakingPressure = 0;
		pedalForce = 0;
		altitude = 0;
		fuelUsage = 0;
		mpg = 0;
		tripLength = 0;
		double goalSpeed = 30 + 50 * rng.nextDouble();
		int numSpeedTurnsLeft = 50+rng.nextInt(100);
		int numStoppingTurns = 0;
		data = new TimeSlice[NUM_LINES];
		for(int line = 0; line < NUM_LINES; line++, numSpeedTurnsLeft--)	{
			if(numSpeedTurnsLeft == 0)	{
				numStoppingTurns = 2 + rng.nextInt(8);
				goalSpeed = 30 + 50 * rng.nextDouble();
				numSpeedTurnsLeft = 50+rng.nextInt(100);
			}
			boolean isStopped = false;
			if(numStoppingTurns-- > 0)	{
				isStopped = true;
				numSpeedTurnsLeft++;
			}
			double accelInc = getNextAccel(isStopped, goalSpeed, acceleration, currentSpeed);
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
			airConditioning += getNextAC(airConditioning);
			steering += getNextSteering(steering);
			odometer += currentSpeed / 3600;
			altitude = 20 * rng.nextGaussian();
			fuelUsage += getFuelUsage(currentSpeed);
			tripLength++;
			mpg = odometer / fuelUsage;
			data[line] = generateDatum();
		}
	}

	private static final double FUEL_USAGE_SCALE = 0.0000001;
	
	/*
	 * Amount of energy needed to travel per unit distance scales with
	 * the square of speed.
	 */
	private double getFuelUsage(double currentSpeed)	{
		return FUEL_USAGE_SCALE * currentSpeed * currentSpeed;
	}
	
	private double currentSpeed;
	private double acceleration;
	private double airConditioning;
	private double steering;
	private double odometer;
	private double brakingPressure;
	private double pedalForce;
	private double altitude;
	private double fuelUsage;
	private double mpg;
	private long tripLength;
	
	private TimeSlice generateDatum()	{
		List<Datum> list = new ArrayList<Datum>();
		list.add(new Datum("currentSpeed", currentSpeed));
		list.add(new Datum("acceleration", acceleration));
		list.add(new Datum("airConditioning", airConditioning));
		list.add(new Datum("steering", steering));
		list.add(new Datum("odometer", odometer));
		list.add(new Datum("brakingPressure", brakingPressure));
		list.add(new Datum("pedalForce", pedalForce));
		list.add(new Datum("altitude", altitude));
		list.add(new Datum("fuelUsage", fuelUsage));
		list.add(new Datum("mpg", mpg));
		list.add(new Datum("tripLength", tripLength));
		return new TimeSlice(list);
	}
	
	private static final double STEERING_SCALE = 0.01;

	private double getNextSteering(double steering) {
		return 10 * rng.nextGaussian() - STEERING_SCALE * steering;
	}

	private static final double AC_SCALING = 0.01;

	private double getNextAC(double airConditioning)	{
		return Math.max(-airConditioning, rng.nextGaussian() - AC_SCALING * airConditioning);
	}

	private static final double ACCEL_SCALING_FACTOR = 0.005;

	private static final double ACCEL_CORRECTION = 0.1;

	private double getNextAccel(boolean mustStop, double goalSpeed, double acceleration, double currentSpeed) {
		double randomChange = ACCEL_SCALING_FACTOR * Math.abs(rng.nextGaussian()) * (goalSpeed - currentSpeed);
		if(mustStop)	{
			randomChange = Math.min(0, randomChange - 25);
		}
		acceleration -= ACCEL_CORRECTION * Math.abs(rng.nextGaussian()) * acceleration;
		return randomChange;
	}

}
