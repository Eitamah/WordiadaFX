package views.console.MenuItems;

import java.util.Scanner;

import engine.Game.eGameState;
import engine.GameManager;

public class EndCurrentGameItem extends MenuItem {

	public EndCurrentGameItem(Scanner scanner) {
		super(scanner);
	}

	private final String MENU_STRING = "End current game";

	@Override
	public String getString() {
		return MENU_STRING;
	}

	@Override
	public void Execute(GameManager gameManager) {
		if ((gameManager.getCurrentGame() != null) && (gameManager.getCurrentGame().getStatus() == eGameState.RUNNING)) {
			gameManager.getCurrentGame().endGame();
			System.out.println("=======================Game ended==============");
			System.out.println("Winner - " + gameManager.getCurrentGame().getWinner().getName().get(0));
			Helpers.printGameStatus(gameManager);
			Helpers.printGameStats(gameManager);
		} else {
			System.out.println("Game not running");	
		}
		
	}

}
