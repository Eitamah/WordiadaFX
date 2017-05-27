package views.console.MenuItems;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import engine.GameManager;
import views.console.ConsoleView;

public class LoadFileItem extends MenuItem {

	private final String MENU_STRING = "Load saved game";
	private ConsoleView console;
	
	public LoadFileItem(ConsoleView cm, Scanner scanner) {
		super(scanner);
		console = cm;
	}

	@Override
	public String getString() {
		return MENU_STRING;
	}

	@Override
	public void Execute(GameManager gameManager) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream("game.dat");
			ois = new ObjectInputStream(fis);
			gameManager = (GameManager)ois.readObject();
			console.changeManager(gameManager);
		} catch (IOException e) {
			System.out.println("Unable to read from file");
		} catch (ClassNotFoundException e) {
			System.out.println("Unable to read from file");
		} finally {
			try {
				if (ois != null)
					ois.close();
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				
			}
		}

	}

}
