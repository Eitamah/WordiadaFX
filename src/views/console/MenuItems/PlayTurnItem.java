package views.console.MenuItems;

import java.awt.Point;
import java.util.Scanner;

import engine.Game.eGameState;
import engine.GameManager;
import engine.IllegalLettersException;
import engine.InvalidWordException;
import engine.Tile.eTileState;
import engine.Turn;
import gameSettings.Player;

public class PlayTurnItem extends MenuItem {

	public PlayTurnItem(Scanner scanner) {
		super(scanner);
	}

	private final String MENU_STRING = "Play turn";

	@Override
	public String getString() {
		return MENU_STRING;
	}

	@Override
	public void Execute(GameManager gameManager) {
		if ((gameManager.getCurrentGame() != null) &&
				(gameManager.getCurrentGame().getStatus() == eGameState.RUNNING)) {
			playHumanTurn(gameManager);
			playComputerTurns(gameManager);
			Helpers.printGameStatus(gameManager);
			
			if (gameManager.getCurrentGame().getStatus() == eGameState.FINISHED) {
				System.out.println("Game OVER");
				System.out.println("Winner is " + gameManager.getCurrentGame().getWinner());
				Helpers.printGameStats(gameManager);
			}
		} else {
			System.out.println("Game isn't running");
		}
	}

	private void playComputerTurns(GameManager gameManager) {
		gameManager.getCurrentGame().playComputerTurns();
	}

	private void playHumanTurn(GameManager gameManager) {
		int diceRoll = gameManager.getCurrentGame().getDiceRoll();
		int tilesToUncover = diceRoll;
		int triesLeft = gameManager.getCurrentGame().getSettings().getDescriptor().getStructure().getRetriesNumber() + 1;
		System.out.println("Current player - " + gameManager.getCurrentGame().getCurrentPlayer().getName());
		System.out.println("Dice roll - " + diceRoll);

		while ((tilesToUncover > 0) && 
				(gameManager.getCurrentGame().getBoard().getFaceDownTiles().size() > 0)) {
			Point coord = getCoordinatesFromUser(gameManager);
			
			if (gameManager.getCurrentGame().getBoard().getBoard()[coord.x][coord.y].getState() 
					== eTileState.FACE_UP) {
				System.out.println("Already face up - choose a face down tile");
			} else if (gameManager.getCurrentGame().getBoard().getBoard()[coord.x][coord.y].getState()
					== eTileState.EMPTY) {
				System.out.println("Empty tile, please choose another one");
			} else {
				gameManager.getCurrentGame().getBoard().flipTile(coord.x, coord.y, eTileState.FACE_UP);
				tilesToUncover--;
			}
		}
		
		Helpers.printBoard(gameManager);
		Player player = gameManager.getCurrentGame().getCurrentPlayer();
		String input = "";
		boolean isValid = false;
		
		while ((triesLeft > 0) && (!isValid)) {
			System.out.println("Enter word [text]");
			input = scanner.nextLine();
			
			try {
				gameManager.getCurrentGame().playWord(input);
				isValid = true;
			} catch (IllegalLettersException e) {
				System.out.println("Not all letters are available. Please choose again");
			} catch (InvalidWordException e) {
				triesLeft--;
				System.out.println("Word doesnt exist. " + triesLeft + " tries left");
			}
		}
		Turn turn = new Turn(player,  isValid,  input);
		gameManager.getCurrentGame().saveTurn(turn);
		gameManager.getCurrentGame().endTurn();
	}
	
	private Point getCoordinatesFromUser(GameManager gameManager) {
		boolean isValid = false;
		Point returnPoint = new Point();
		
		while (!isValid) {
			System.out.println("Please choose coordinates for next tile [row] [col]");
			try {
				int row = scanner.nextInt();
				int col = scanner.nextInt();
				scanner.nextLine();
				
				if ((((row - 1) < 0) || ((row - 1)> gameManager.getCurrentGame().getBoard().getSize())) ||
					(((col - 1) < 0) || ((col - 1)> gameManager.getCurrentGame().getBoard().getSize()))) {
					throw new Exception();
				}
				
				isValid = true;
				returnPoint.x = row - 1;
				returnPoint.y = col - 1;
			} catch (Exception e) {
				System.out.println("Invalid coordinates. Enter two numbers between 1 and " + 
							gameManager.getCurrentGame().getBoard().getSize() + 1);
//				scanner.nextLine();
			}
		}
		
		return returnPoint;
	}
}
