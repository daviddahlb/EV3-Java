/*
 * Runs a large EV3 Motor based on the light sensor. The light sensor needs to be
 * connected to sensor port 3, and the motor must be connected to motor port A.
 * It will activate the motor based on the brightness of the color sensed.
 */

package org.lejos.ev3.sample.tarkanstest;

import lejos.hardware.BrickFinder;
import lejos.hardware.device.tetrix.TetrixControllerFactory;
import lejos.hardware.device.tetrix.TetrixMotorController;
import lejos.hardware.device.tetrix.TetrixServo;
import lejos.hardware.device.tetrix.TetrixServoController;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.DCMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class LightSensorTest {

	static final Port LIGHT_SENSOR_PORT = SensorPort.S3;
	static final Port ULTRASONIC_SENSOR_PORT = SensorPort.S2;
	static final Port TOUCH_SENSOR_PORT = SensorPort.S4;
	static DCMotor motor1;
	static DCMotor motor2;
	static TetrixMotorController motorController;
	static TetrixServoController servoController;
	static TetrixControllerFactory factory;
	static final boolean LEFT = true;
	static final boolean RIGHT = false;
	static SensorModes buttonSensor;
	static SampleProvider pressed;
	
	static SensorModes ultrasonicSensor;
	static SampleProvider distance;
	
	static SensorModes lightSensor;
	static SampleProvider red;
	
	static float[] sample;
	
	static final int MICROSECLOW_DEFAULT = 750;
	static final int MICROSECHIGH_DEFAULT = 2250;
	static final int TRAVELRANGE_DEFAULT = 200;
	
	public static void main(String[] args) {
		lightSensor = new EV3ColorSensor(LIGHT_SENSOR_PORT);
		red = lightSensor.getMode("Red");
		sample = new float[red.sampleSize()];
		initializeControllers();
		RegulatedMotor m = new EV3LargeRegulatedMotor(MotorPort.A);
		while (true) {
			red.fetchSample(sample, 0);
			if (sample[0] <= .2) {
				m.forward();
			} else {
				m.stop();
			}
		}
	}
	
	public static void turn(boolean direction){
		if(LEFT==direction){
			motor1.forward();
			motor2.forward();
		}else{
			motor1.backward();
			motor2.backward();
		}
	}
	/**
	 * Always run this first, before initializing anything else
	 */
	public static void initializeControllers() {
		factory = new TetrixControllerFactory(SensorPort.S1);
		motorController = factory.newMotorController();
		servoController = factory.newServoController();
	}
	
	public static void moveForward() {
		motor1.backward();
		motor2.forward();
	}
	
	public static void moveBackward() {
		motor1.forward();
		motor2.backward();
	}
	
	/**
	 * Run this before using the tetrix motors
	 */
	public static void initializeMotors() {
		if (motorController != null) {
			motor1 = motorController.getBasicMotor(TetrixMotorController.MOTOR_1);
			motor2 = motorController.getBasicMotor(TetrixMotorController.MOTOR_2);
		}
	}
	/**
	 * Run this to get a new TetrixServo object
	 * @param servoNumber the port the servo is connected to
	 * @return the servo object
	 */
	public static TetrixServo initializeServo(int servoNumber) {
		if (servoController != null) {
			return servoController.getServo(servoNumber);
		} else {
			return null;
		}
	}
	
	public static void servoDefaults(TetrixServo servo) {
		servo.setRange(MICROSECLOW_DEFAULT, MICROSECHIGH_DEFAULT, TRAVELRANGE_DEFAULT);
	}
	
	public static void setPower(int power) {
		motor1.setPower(power-10);
		motor2.setPower(power);
	}

	public static void stop() {
		motor1.stop();
		motor2.stop();
	}

	public static void whileButtonPressed() {
		buttonSensor = new EV3TouchSensor(TOUCH_SENSOR_PORT);
		pressed = buttonSensor.getMode("Touch");
		sample = new float[pressed.sampleSize()];
		RegulatedMotor m = new EV3LargeRegulatedMotor(MotorPort.A);
		while (true) {
			if (buttonPressed()) {
				m.forward();
			} else {
				m.stop();
			}
			Delay.msDelay(100);
		}
	}
	
	public static void initializeUltrasonicSensor() {
		ultrasonicSensor = new EV3UltrasonicSensor(ULTRASONIC_SENSOR_PORT);
		distance = ultrasonicSensor.getMode("Distance");
		sample = new float[distance.sampleSize()];
	}
	
	public static void whileDistanceAway(double dist) {
		ultrasonicSensor = new EV3UltrasonicSensor(ULTRASONIC_SENSOR_PORT);
		distance = ultrasonicSensor.getMode("Distance");
		sample = new float[distance.sampleSize()];
		RegulatedMotor m = new EV3LargeRegulatedMotor(MotorPort.A);
		while (true) {
			if (distanceRead() <= dist) {
				m.forward();
			} else {
				m.stop();
			}
			Delay.msDelay(10);
		}
	}
	
	public static double distanceRead() {
		if (distance != null) {
			distance.fetchSample(sample, 0);
		}
		return sample[0];
	}
	
	public static boolean buttonPressed() {
		if (pressed != null) {
			pressed.fetchSample(sample, 0);
		}
		return (sample[0] == 1);
	}
}
