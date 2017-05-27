package views.console.MenuItems;

import java.util.Collections;
import java.util.List;

import engine.Board;
import engine.GameManager;
import engine.Tile.eTileState;
import gameSettings.Letter;
import gameSettings.Player;

public class Helpers {
	
	public static final char FACE_DOWN_CHAR = '-';
	
	public static void printGameStatus(GameManager gameManager) {
		printBoard(gameManager);
		System.out.println("Tiles left :" + gameManager.getCurrentGame().getBoard().getTilesLeft());
		System.out.println("Next player : " + gameManager.getCurrentGame().getCurrentPlayer().getName().get(0));
	}
	
	public static void printBoard(GameManager gameManager) {
		Board board = gameManager.getCurrentGame().getBoard();

    	System.out.printf("\t");
		for (int col = 0; col < board.getSize(); col++) {
			System.out.printf("%4d", col + 1);
		}
		System.out.println();
		System.out.println();

	    for (int row = 0; row < board.getSize(); row++) {
	    	System.out.printf("%4d", row + 1);
	    	System.out.printf("\t");

	        for (int col = 0; col < board.getSize(); col++) {
	        	if (board.getBoard()[row][col].getState() != eTileState.FACE_DOWN) {	        		
	        		System.out.printf("%4c", board.getBoard()[row][col].getSign());
	        	} else {
	        		System.out.printf("%4c", FACE_DOWN_CHAR);
	        	}
	        }
	        
	        System.out.println();
	    }
	}

	public static void printGameStats(GameManager gameManager) {
		System.out.println("================ Game Stats ===============");
		System.out.println("Number of turns played - " + gameManager.getCurrentGame().getTurns().size());

		long now = System.currentTimeMillis();
		long duration = now - gameManager.getCurrentGame().getTimeStarted();
		
		// change to seconds
		duration = duration / 1000;
		
		int minutes = (int) (duration / 60);
		int seconds = (int) (duration % 60);
		String time = String.format("%02d:%02d", minutes, seconds);
		System.out.println("Time since game started " + time);
		
		List<Letter> list = gameManager.getCurrentGame().getSettings().getDescriptor().
				getStructure().getLetters().getLetter();
		
		for (Letter letter : list) {
			int count = Collections.frequency(gameManager.getCurrentGame().getSettings().getLetters(), letter);
			System.out.println(letter.getSign().get(0).toUpperCase() + ": " + count + 
					"/" + gameManager.getCurrentGame().getSettings().getLetters().size());
		}
		
		for (Player player : gameManager.getCurrentGame().getPlayers()) {
			System.out.println(player.getName() + " Score = " + gameManager.getCurrentGame().getScore(player));
		}
		
		for (Player player : gameManager.getCurrentGame().getPlayers()) {
			System.out.println(player.getName() + " words:");
			
			for (String word : player.getWordsPlayed()) {
				System.out.println(word + " : " + gameManager.getCurrentGame().getSettings().getDictionary()
						.getWordCount(word) + " / " + gameManager.getCurrentGame().getSettings().getDictionary()
						.getTotalWords());
			}
		}
	}
}
