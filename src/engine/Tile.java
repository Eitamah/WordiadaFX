package engine;

import java.awt.Point;
import java.io.Serializable;

import gameSettings.Letter;
import javafx.scene.layout.StackPane;

public class Tile extends StackPane implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2001599196092326663L;

	public enum eTileState 	{
		FACE_UP,
		FACE_DOWN,
		EMPTY
	}

	private static final char EMPTY_SIGN = '*';
	
	private Point coord;
	private eTileState state;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coord == null) ? 0 : coord.hashCode());
		result = prime * result + ((letter == null) ? 0 : letter.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (coord == null) {
			if (other.coord != null)
				return false;
		} else if (!coord.equals(other.coord))
			return false;
		if (letter == null) {
			if (other.letter != null)
				return false;
		} else if (!letter.equals(other.letter))
			return false;
		if (state != other.state)
			return false;
		return true;
	}

	private Letter letter;
	
	public int getScore() {
		return letter.getScore();
	}
	
	public eTileState getState() { 
		return state;
	}
	
	public char getSign() {
		if (getState() == eTileState.EMPTY)
			return EMPTY_SIGN;
		else
			return letter.getSign().get(0).charAt(0);
	}
	
	public void setLetter(Letter newLetter) {
		letter = newLetter;
	}
	
	public void setState(eTileState newState) {
		state = newState;
	}
	
	public Tile(Letter newLetter, int x, int y)	{
		state = eTileState.FACE_DOWN;
		letter = newLetter;
		coord = new Point(x, y);
	}
	
	public Point getCoord() {
		return coord;
	}
}