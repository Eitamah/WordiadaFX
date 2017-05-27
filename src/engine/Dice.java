package engine;

import java.io.Serializable;
import java.util.Random;

public class Dice implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1916363675135133122L;
	private static final int MIN_VALUE = 2;
	public static final int MIN_SIDES = 2;
	private int sides;
	private Random random;
	
	public int getSides() {
		return sides;
	}
	
	public Dice(int numOfSides) {
		sides = numOfSides;
		random = new Random();
	}
	
	public int rollDice() {
		return random.nextInt(sides - MIN_VALUE + 1) + MIN_VALUE;
	}
}
