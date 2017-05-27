package views.console.MenuItems;

import java.util.Scanner;

import engine.GameManager;
import engine.Game.eGameState;

public class ShowGameStatusItem extends MenuItem {
	
	public ShowGameStatusItem(Scanner scanner) {
		super(scanner);
		// TODO Auto-generated constructor stub
	}

	private final String MENU_STRING = "Show game status";

	@Override
	public String getString() {
		// TODO Auto-generated method stub
		return MENU_STRING;
	}

	@Override
	public void Execute(GameManager gameManager) {
		if ((gameManager.getCurrentGame() != null) && (gameManager.getCurrentGame().getStatus() != eGameState.UNINITIALIZED)) {
			Helpers.printGameStatus(gameManager);
		} else {
			System.out.println("Game must be loaded first");
		}
	}

	
}