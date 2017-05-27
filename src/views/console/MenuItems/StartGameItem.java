package views.console.MenuItems;

import java.util.Scanner;

import engine.GameManager;

public class StartGameItem extends MenuItem {

	public StartGameItem(Scanner scanner) {
		super(scanner);
		// TODO Auto-generated constructor stub
	}

	private final String MENU_STRING = "Start game";

	@Override
	public String getString() {
		return MENU_STRING;
	}

	@Override
	public void Execute(GameManager gameManager) {
		if ((gameManager.getCurrentGame() != null) && (gameManager.getCurrentGame().isSettingsValid())) {
			try {
				gameManager.startGame();
				System.out.println("Game started!");
			} catch (IllegalStateException e) {
				System.out.println(e.getMessage());
			}
		}
		else {
			System.out.println("Settings aren't valid. Please load settings");			
		}
	}

}
