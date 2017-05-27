package views.console.MenuItems;

import java.util.Scanner;

import engine.GameManager;

public abstract class MenuItem {
	Scanner scanner;
	
	public MenuItem(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public abstract String getString();
	public abstract void Execute(GameManager gameManager);
}