package engine;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;
import java.util.Observable;


import engine.GameSettings.eGameType;
import engine.Tile.eTileState;
import gameSettings.Player;
import gameSettings.Player.ePlayerType;
import gameSettings.Players;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;

public class Game extends Observable implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum eGameState {
		UNINITIALIZED,
		LOADED,
		RUNNING,
		FINISHED
	}
	
	private eGameState state;
	private GameSettings settings;
	private Board board;
	private Dice dice;
	private int currentPlayer;
	private List<Turn> turns;
	long timeStarted;
	private Random random;
	private HashMap<Player, Number> score; 
	
	public Game() {
		state = eGameState.UNINITIALIZED;
		settings = null;
		board = null;
		random = new Random();
	}
	
	public void LoadSettings(String filePath) throws FileNotFoundException, IllegalArgumentException, 
												JAXBException, IllegalStateException {
		settings = new GameSettings(filePath);
		checkPlayers();
		initGame();
	}

	/*
	 * Check if there are players in the file (if not, default is 2 - player1 and player2
	 * if there are players in the file - check for duplicates ID or invalid types
	 */
	private void checkPlayers() {
		//TODO: Put back for assignment2.
		if ((settings.getDescriptor().getPlayers() == null) ||
			(settings.getDescriptor().getPlayers().getPlayer().size() < 2)) {
			throw new IllegalArgumentException("Not enough players - must have atleast 2");
		}
		
		for (Player player : settings.getDescriptor().getPlayers().getPlayer()) {
			if (Collections.frequency(settings.getDescriptor().getPlayers().getPlayer(), player) > 1) {
				throw new IllegalArgumentException("Player with id " + player.getId() + " included more"
						+ "than once in xml");
			}
			
			if (player.getType() == ePlayerType.INVALID) {
				throw new IllegalArgumentException("Player with id " + player.getId() + " has an "
						+ "invalid type");
			}
			
			player.setName(player.getName().get(0));
		}		
	}

	public void startGame() {
		switch (state) {
			case UNINITIALIZED: {
				throw new IllegalStateException("Game hasn't been loaded yet");
	//			break;
			} case LOADED: {
				state = eGameState.RUNNING;
				break;
			} case RUNNING: {
				throw new IllegalStateException("Game already running");
	//			break;
			} case FINISHED: {
				throw new IllegalStateException("Game finished");
	//			break;
			}
			default: {
				break;
			}
		}		
		
		timeStarted = System.currentTimeMillis();
		Thread thread = new Thread() {
		    public void run() {
				while (state == eGameState.RUNNING) {
					playComputerTurnsTask();
				}
		  }
		};
		thread.start();
	}

	int counter =0;
	public void playComputerTurnsTask() {
		while ((getCurrentPlayer().getType().compareTo(ePlayerType.COMPUTER) == 0) &&
				(state != eGameState.FINISHED)) {
			int diceRoll = getDiceRoll();
			int tilesToUncover = diceRoll;
			List<Tile> uncoveredTiles = board.getFaceDownTiles();
			
			while (tilesToUncover > 0 &&
					uncoveredTiles.size() > 0) {
				int tileToUncover = random.nextInt(uncoveredTiles.size());
				Tile uncover = uncoveredTiles.get(tileToUncover);
				board.flipTile(uncover.getCoord().x, uncover.getCoord().y, eTileState.FACE_UP);
				uncoveredTiles = board.getFaceDownTiles();
				tilesToUncover--;
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}
			
			List<Character> availableChars = board.getFaceUpLetters();
			char[] cs = new char[availableChars.size()];
			
			for (int i = 0; i < availableChars.size(); i++) {
				cs[i] = availableChars.get(i).charValue();				
			}
			
			String word = settings.getDictionary().GetLongestWord(cs);
			if (word != null) {
				try {
					playWord(word);
					try {
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}

					Turn turn = new Turn(getCurrentPlayer(), true, word);
					saveTurn(turn);
				} catch (IllegalLettersException | InvalidWordException e) {
					// Should never happen during computer turn
				}
			} else {
			// To avoid an endless game (no words available and no humans playing) 
				counter++;
				
				if (counter > 10000) {
					state = eGameState.FINISHED;
				}
			}
			
			endTurn();
		}
	}
	
	/*
	 * 1. Check that all the letters chosen are face up
	 * 2. Check if the word is valid
	 * 3. Change letters from chosen tiles
	 * 4. update score
	 * 5. add word to player history
	 * 6. increment next player
	 */
	public void playWord(String word) throws IllegalLettersException, InvalidWordException {
		List<Character> availableChars = board.getFaceUpLetters();
		word = word.toUpperCase();

		// First check that all the letters chosen are face up
		for(int i = 0; i < word.length() ; i++) { 
		    Character c = word.charAt(i);
		    
		    if (availableChars.contains(c)) {
		    	availableChars.remove(c);
		    }
		    else {
		    	throw new IllegalLettersException();
		    }
		}
		
		// Check if the word exists
		if (!settings.getDictionary().isValidWord(word)) {
			throw new InvalidWordException();
		}
		
		// word is fine, letters are fine - remove the letters from the board
		board.removeLetters(word);

		// First assignment (console) the score was in a list in this class
		score.replace(getCurrentPlayer(), getScore(getCurrentPlayer()) + settings.getScore(word));
		// Second assignment - for binding issues, score had to be moved to player (for table)
		// i didnt remove the previous score just incase... 
		getCurrentPlayer().UpdateScore(settings.getScore(word));
		getCurrentPlayer().addWordPlayed(word);		
	}

	private void initGame() {
		currentPlayer = 0;
		board = new Board(settings.getBoardSize(), settings.getLetters());
		dice = new Dice(settings.getDiceFacets());
		turns = new ArrayList<Turn>();
		state = eGameState.LOADED;
		
	}

	public eGameState getStatus() {
		return state;
	}
	
	public void endGame() {
		state = eGameState.FINISHED;		
	}
	
	public boolean isSettingsValid() {
		return settings.isValid();
	}
	
	public Player getCurrentPlayer() {
		return settings.getDescriptor().getPlayers().getPlayer().get(currentPlayer);
	}
	
	public Board getBoard() {
		return board;
	}
	
	/*
	 * Gets character for tile - if tile isn't visible, return -
	 */
	public Tile getTile(int row, int col) {
		return board.getBoard()[row][col];
	}
	
	public GameSettings getSettings() {
		return settings;
	}

	public Player getWinner() {
		currentPlayer = (currentPlayer + 1) % settings.getDescriptor().getPlayers().getPlayer().size();
		return settings.getDescriptor().getPlayers().getPlayer().get(currentPlayer);
	}
	
	public int getDiceRoll() {
		return dice.rollDice();
	}

	public List<Turn> getTurns() {
		return turns;
	}

	public long getTimeStarted() {
		return timeStarted;
	}

	public List<Player> getPlayers() {
		return settings.getDescriptor().getPlayers().getPlayer();
	}

	public void endTurn() {
		if (settings.getDescriptor().getGameType().isGoldFishMode()) {
			board.faceDownAllTiles();
		}
		
		incrementCurrentPlayer();

		if ((board.getFaceDownTiles().size() == 0) && 
				(board.getTilesLeft() == 0)) {
			state = eGameState.FINISHED;			
		}
	}

	private void incrementCurrentPlayer() {
		currentPlayer = (currentPlayer + 1) % settings.getDescriptor().getPlayers().getPlayer().size();
	}

	public int getScore(Player player) {
		return score.get(player).intValue();
	}

	public void saveTurn(Turn turn) {
		turns.add(turn);		
	}
}