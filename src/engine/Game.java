package engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	long timeStarted;
	private Random random;
	private HashMap<Player, Number> score; 
	private int numOfTurns;
	private String lastWordPlayed;
	
	
	public Game() {
		state = eGameState.UNINITIALIZED;
		settings = null;
		board = null;
		random = new Random();
		score = new HashMap<Player, Number>();
		settings = new GameSettings();
		savedTurns = new ArrayList<ByteArrayOutputStream>();
	}
	
	public void LoadSettings(String filePath) throws FileNotFoundException, IllegalArgumentException, 
												JAXBException, IllegalStateException {
		
		settings.Load(filePath);
		
		checkPlayers();
		initGame();
		setChanged();
		notifyObservers(1);
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
		}		
	}

	public void startGame() {
		switch (state) {
			case UNINITIALIZED: {
				throw new IllegalStateException("Game hasn't been loaded yet");
	//			break;
			} case LOADED: {
				state = eGameState.RUNNING;
				numOfTurns = 0;
				saveTurn();
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
					try {
						TimeUnit.MILLISECONDS.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
					}
				}
		  }
		};
		thread.start();
		setChanged();
		notifyObservers();
	}

	int counter = 0;
	public void playComputerTurnsTask() {
		Player current = getCurrentPlayer();
		if ((current.getType() == ePlayerType.COMPUTER) &&
				(state != eGameState.FINISHED)) {
			lastWordPlayed = "";
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
					TimeUnit.MILLISECONDS.sleep(50);
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
				/*try {
					playWord(word);


				} catch (IllegalLettersException | InvalidWordException e) {
					// Should never happen during computer turn
				}*/
			} else {
				// To avoid an endless game (no words available and no humans playing) 
				counter++;
				
				if (counter > 1000) {
					state = eGameState.FINISHED;
				}
			}
			
			try {
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			
			endTurn(word);
			setChanged();
			notifyObservers();
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
	// this is for computer play word
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
//		board.removeLetters(word);
		board.removeLetters();
		
//		if (board.getTilesLeft() == 0) {
//			state = eGameState.FINISHED; 
//		}

		// First assignment (console) the score was in a list in this class
		score.put(getCurrentPlayer(), getScore(getCurrentPlayer()) + settings.getScore(word));
		
		getCurrentPlayer().addWordPlayed(word);	
		setChanged();
		notifyObservers();
	}
	
	private void initGame() {
		currentPlayer = 0;
		board = new Board(settings.getBoardSize(), settings.getLetters());
		dice = new Dice(settings.getDiceFacets());
		state = eGameState.LOADED;
		setChanged();
		notifyObservers();
		
	}

	public eGameState getStatus() {
		return state;
	}
	
	public void endGame() {
		state = eGameState.FINISHED;		
		
		setChanged();
		notifyObservers();
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
		ArrayList<Player> playersList = (ArrayList<Player>) settings.getDescriptor().getPlayers().getPlayer();
		ArrayList<Player> playingPlayers = new ArrayList<Player>();
		
		for (Player player : playersList) {
			if (player.isPlaying())
				playingPlayers.add(player);
		}
		
		int maxScore = 0;
		Player winner = null;
		
		for (Player player : playingPlayers) {
			if (getScore(player) > maxScore) {
				winner = player;
				maxScore = getScore(player);
			}
		}
		
		return winner;
	}
	
	public int getDiceRoll() {
		return dice.rollDice();
	}

	public long getTimeStarted() {
		return timeStarted;
	}

	public List<Player> getPlayers() {
		return settings.getDescriptor().getPlayers().getPlayer();
	}

	public void endTurn(ArrayList<Tile> word) {
		numOfTurns++;
		setChanged();
		notifyObservers();
		
		try {
			TimeUnit.MILLISECONDS.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		String tempWord = "";
		if (word.size() == 0) {
			tempWord = new String("");
		} else {
			for (Tile tile : word) {
				tempWord = tempWord + tile.getSign();
			}
		}
		
		getCurrentPlayer().setLastWord(tempWord);

		saveTurn();

		if (settings.isGoldFishMode) {
			board.faceDownAllTiles();
		}
		board.removeLetters();
		
		if (tempWord.length() > 1)
		{
			getCurrentPlayer().addWordPlayed(tempWord);	
			score.put(getCurrentPlayer(), getScore(getCurrentPlayer()) + settings.getScore(tempWord));
		}
		setChanged();
		notifyObservers();
		
		if ((board.getFaceDownTiles().size() == 0) && 
				(board.getTilesLeft() == 0)) {
			state = eGameState.FINISHED;			
		}
		
		incrementCurrentPlayer();
		checkForEndGame();
		setChanged();
		notifyObservers();
	}
	
	public void endTurn(String word) {
		numOfTurns++;
		setChanged();
		notifyObservers();
		
		try {
			TimeUnit.MILLISECONDS.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		if (word == null) {
			word = new String("");
		}
		
		getCurrentPlayer().setLastWord(word);
		board.selectLetters(word);
		saveTurn();

		if (settings.isGoldFishMode) {
			board.faceDownAllTiles();
		}

		if (word != "") {
			try {
				
				playWord(word);
			} catch (IllegalLettersException e) {
				// TODO Auto-generated catch block
		//			e.printStackTrace();
			} catch (InvalidWordException e) {
				// TODO Auto-generated catch block
		//			e.printStackTrace();
			}
		}		
		
		if ((board.getFaceDownTiles().size() == 0) && 
				(board.getTilesLeft() == 0)) {
			state = eGameState.FINISHED;			
		}
		
		incrementCurrentPlayer();
		checkForEndGame();
		setChanged();
		notifyObservers();
	}

	private void checkForEndGame() {
		int playersPlaying = 0;
		for (Player player : getPlayers()) {
			if (player.isPlaying())
				playersPlaying++;
		}
		
		if (playersPlaying < 2)
			state = eGameState.FINISHED;
		
		if (board.getTilesLeft() == 0) {
			state = eGameState.FINISHED; 
		}

		if (state == eGameState.FINISHED) {
			saveTurn();
		}
	}

	public void Forfiet() {
		getPlayers().get(currentPlayer).setIsPlaying(false);
		endTurn("");
//		state = eGameState.FINISHED;
	}
	private void incrementCurrentPlayer() {
		currentPlayer = (currentPlayer + 1) % settings.getDescriptor().getPlayers().getPlayer().size();
		
		while (!settings.getDescriptor().getPlayers().getPlayer().get(currentPlayer).isPlaying()) {
			currentPlayer = (currentPlayer + 1) % settings.getDescriptor().getPlayers().getPlayer().size();
		}
	}

	public int getScore(Player player) {
		if (score.containsKey(player)) {
			return score.get(player).intValue();
		} else {
			return 0;
		}
	}

	public int numberOfTurns() {
		return numOfTurns;
	}
	
	// transient - don't serialize this
	transient List<ByteArrayOutputStream> savedTurns;

	public int numOfSavedTurns() {
		return savedTurns.size();
	}
	public void saveTurn() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			savedTurns.add(baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
	public Game getTurn(int index) {
		ByteArrayInputStream bais = new ByteArrayInputStream(savedTurns.get(index).toByteArray());
		ObjectInputStream ois;
		Game savedGame = null;
		
		try {
			ois = new ObjectInputStream(bais);
			savedGame = (Game)ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		return savedGame;
	}
}