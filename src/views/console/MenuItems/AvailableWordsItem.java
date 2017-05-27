package views.console.MenuItems;

import java.util.List;
import java.util.Scanner;

import engine.GameManager;
import engine.Tile;
import engine.Game.eGameState;

public class AvailableWordsItem extends MenuItem{

	private final String MENU_STRING = "Get number of available words";
	
	public AvailableWordsItem(Scanner scanner) {
		super(scanner);
	}

	@Override
	public String getString() {
		return MENU_STRING;
	}

	@Override
	public void Execute(GameManager gameManager) {
		if ((gameManager.getCurrentGame() != null) &&
			(gameManager.getCurrentGame().getStatus() == eGameState.RUNNING)) {
			List<Tile> tiles = gameManager.getCurrentGame().getBoard().getFaceUpTiles();
			char[] letters = getLettersFromList(tiles);
			System.out.println("Number of available words -  " +  
					gameManager.getCurrentGame().getSettings().getDictionary().getPossibleWordCount(letters));
		} else {
			System.out.println("Game must be running");
		}
		
	}

	private char[] getLettersFromList(List<Tile> tiles) {
		String ret = "";
		for (Tile tile : tiles) {
			ret = ret + tile.getSign();
		}
		
		return ret.toCharArray();
	}

}
