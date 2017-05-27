package views.console.MenuItems;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import engine.GameManager;

public class SaveGameItem extends MenuItem{

	private final String MENU_STRING = "Save game";
	
	public SaveGameItem(Scanner scanner) {
		super(scanner);
	}

	@Override
	public String getString() {
		return MENU_STRING;
	}

	@Override
	public void Execute(GameManager gameManager) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream("game.dat");
			oos = new ObjectOutputStream(fos);
			oos.writeObject(gameManager);
		} catch (IOException e) {
			System.out.println("Problem saving file");
		} finally {
			try {
				if (oos != null)
					oos.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				
			}
		}
	}
}
