package views.console.MenuItems;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import engine.GameManager;
import engine.Game.eGameState;
import gameSettings.Letter;
import gameSettings.Player;

public class GetStatsItem extends MenuItem {

	public GetStatsItem(Scanner scanner) {
		super(scanner);
	}

	private final String MENU_STRING = "Get game statistics";

	@Override
	public String getString() {
		return MENU_STRING;
	}

	@Override
	public void Execute(GameManager gameManager) {
		if ((gameManager.getCurrentGame() != null) &&
				((gameManager.getCurrentGame().getStatus() == eGameState.RUNNING) ||
				(gameManager.getCurrentGame().getStatus() == eGameState.FINISHED))){
			Helpers.printGameStats(gameManager);
		} else {
			System.out.println("Must have a game running");
		}
	}

}
