package views.console.MenuItems;

import java.util.Scanner;
import engine.GameManager;
import engine.Game.eGameState;

public class LoadGameItem extends MenuItem {

	public LoadGameItem(Scanner scanner) {
		super(scanner);
		// TODO Auto-generated constructor stub
	}

	private final String MENU_STRING = "Load Game";

	@Override
	public String getString() {
		return MENU_STRING;
	}

	@Override
	public void Execute(GameManager gameManager) {
		if ((gameManager.getCurrentGame() == null) ||
			(gameManager.getCurrentGame().getStatus() != eGameState.RUNNING)) {
			System.out.println("Please enter xml path");
			scanner.nextLine();
			String input = scanner.nextLine();
	
			try {
				// TODO: Remove this!!!!
//				input = "C:\\temp\\basic_1.xml";
				gameManager.loadGame(input);
				
				System.out.println("Would you like a computer player?");
				System.out.println("0 for none");
				System.out.println("1 for player 1");
				System.out.println("2 for player 2");
				System.out.println("3 for both");
				System.out.println("Anything invalid will be considered as none");
				int computerPlayers = 0;
				try {
					int val = scanner.nextInt();
					if (val >= 0 && val <= 3) {
						computerPlayers = val;
					}
					scanner.nextLine();
				} catch (Exception e){
					// do nothing
				}
				
				switch (computerPlayers) {
					case 1:
					{
						gameManager.getCurrentGame().getPlayers().get(0).setType("Computer");
						break;
					}
					case 2:
					{
						gameManager.getCurrentGame().getPlayers().get(1).setType("Computer");
						break;
					}
					case 3:
					{
						gameManager.getCurrentGame().getPlayers().get(0).setType("Computer");
						gameManager.getCurrentGame().getPlayers().get(1).setType("Computer");
					}
	
					default:
						break;
				}
				
				Helpers.printGameStatus(gameManager);
			} catch (IllegalStateException e) {
				System.out.println("Can't load settings " + e.getMessage());
			} catch (Exception e) {
				String message = "Problem with xml file:" + e.getMessage();
				System.out.println(message);
			}
		} else {
			System.out.println("Can't load a new game while current game is running");
		}
	}
}
