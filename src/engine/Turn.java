package engine;

import java.io.Serializable;
import gameSettings.Player;

public class Turn implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4608988514220019150L;
	private Player player;
	private boolean isValid;
	private String word = "";
//	private List<Point> points;
	
	public Turn(Player p, boolean valid, String text) {
		player = p;
		isValid = valid;
		word = text;
//		points = ps;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean getIsValid() {
		return isValid;
	}
	
	public String getWord() {
		return word;
	}
	
//	public List<Point> getPoints() {
//		return points;
//	}
}
