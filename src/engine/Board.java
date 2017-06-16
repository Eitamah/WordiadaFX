package engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import engine.Tile.eTileState;
import gameSettings.Letter;

public class Board extends Observable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int MAX_SIZE = 50;
	public static final int MIN_SIZE = 5;
	private Tile[][] board;
	private int size;
	private List<Letter> nextTiles;
	
	public int getSize() {
		return size;
	}
	
	public Tile[][] getBoard() {
		return board;
	}
	
	public Board(int boardSize, List<Letter> nextChars) {
		nextTiles = nextChars;
		size = boardSize;
		board = new Tile[size][size];
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = new Tile(getNextLetter(), i, j);
			}
		}
		setChanged();
		notifyObservers();
	}
	
	/*
	 * List of letters is sorted, so we get a random index and that way get a random letter
	 */
	private Letter getNextLetter() {
		Letter ret;
		
		Random random = new Random();
		int index = random.nextInt(nextTiles.size());
		
		ret = nextTiles.get(index);
		nextTiles.remove(index);
		return ret;
	}

	public void flipTile(int n, int m, eTileState newState) throws IndexOutOfBoundsException {
		if ((n >= size) || (m >= size))
		{
			throw new IndexOutOfBoundsException("Out of board range");
		}
		
		board[n][m].setState(newState);
		setChanged();
		notifyObservers();
	}
	
	public int getTilesLeft() {
		return nextTiles.size();
	}
	
	public List<Tile> getFaceDownTiles() {
		List<Tile> ret = new ArrayList<Tile>();
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j].getState() == eTileState.FACE_DOWN) {
					ret.add(board[i][j]);	
				}
			}
		}
		
		return ret;
	}

	public List<Tile> getFaceUpTiles() {
		List<Tile> ret = new ArrayList<Tile>();
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j].getState() == eTileState.FACE_UP) {
					ret.add(board[i][j]);	
				}
			}
		}
		
		return ret;
	}
	
	public void faceDownAllTiles() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j].getState() != eTileState.EMPTY) {
					board[i][j].setState(eTileState.FACE_DOWN);	
				}
			}
		}
		setChanged();
		notifyObservers();
	}
	
	public List<Character> getFaceUpLetters() {
		List<Character> ret = new ArrayList<Character>();
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j].getState() == eTileState.FACE_UP) {
					ret.add(board[i][j].getSign());	
				}
			}
		}
		
		return ret;
	}

	//word must be legal, all letters are assumed to be available!!!!

	public void selectLetters(String word) {
		for(int i = 0; i < word.length() ; i++) { 
		    Character c = word.charAt(i);
		    
		    Tile select = getUnselectedFaceUpTileWithChar(c);

    		select.setSelected(true);
		    
		}
		setChanged();
		notifyObservers();
	}
	
	//word must be legal, all letters are assumed to be available!!!!
	public void removeLetters(String word) {
		for(int i = 0; i < word.length() ; i++) { 
		    Character c = word.charAt(i);
		    
		    Tile remove = getFaceUpTileWithChar(c);

		    // NOTE: This if is just in case someone uses this function wrong
		    // remove should never be null
		    if (remove != null) {
		    	if (nextTiles.isEmpty()) {
		    		remove.setState(eTileState.EMPTY);
		    	} else {
		    		board[remove.getCoord().x][remove.getCoord().y].setLetter(getNextLetter());
		    		board[remove.getCoord().x][remove.getCoord().y].setState(eTileState.FACE_DOWN);
		    	}
		    }
		    
		}
		setChanged();
		notifyObservers();
	}
	
	private Tile getUnselectedFaceUpTileWithChar(Character c) {
		List<Tile> tiles = getFaceUpTiles();
		Tile ret = null;
		
		for (Tile tile : tiles) {
			if ((tile.getSign() == c.charValue()) &&
					!tile.isSelected()){
				ret = tile;
			}
		}
		
		return ret;
	}
	private Tile getFaceUpTileWithChar(Character c) {
		List<Tile> tiles = getFaceUpTiles();
		Tile ret = null;
		
		for (Tile tile : tiles) {
			if (tile.getSign() == c.charValue()) {
				ret = tile;
			}
		}
		
		return ret;
	}

	public int countSelected() {
		int ret = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j].isSelected()) {
					ret++;	
				}
			}
		}
		
		return ret;
	}

	public void flipAllSelected() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j].isSelected()) {
					board[i][j].setState(eTileState.FACE_UP);
					board[i][j].setSelected(false);
				}
			}
		}		
		setChanged();
		notifyObservers();
	}

	public void removeLetters() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j].isSelected()) {
			    	if (nextTiles.isEmpty()) {
			    		board[i][j].setState(eTileState.EMPTY);
			    		board[i][j].setSelected(false);
			    	} else {
			    		board[i][j].setLetter(getNextLetter());
			    		board[i][j].setState(eTileState.FACE_DOWN);
			    		board[i][j].setSelected(false);
			    	}
				}
			}
		}		
		
		calcFreq();
	}
	
	public void calcFreq() {
		HashMap<Letter, Integer> temp = new HashMap<Letter, Integer>();
		double totalFreq = 0;
		
		for (Letter let : nextTiles) {
			if (!temp.containsKey(let)) {
				int count = 0;
				for (Letter letter : nextTiles) {
					if (let == letter) {
						count ++;
					}
				}
				
				temp.put(let, new Integer(count));
			}
			
		}

		for (Letter letter : nextTiles) {
			double d = temp.get(letter);
			double s = nextTiles.size();
			letter.setFrequency(d / s);
		}
	}
}
