package views.javaFX;


import engine.GameManager;
import engine.Tile;
import engine.Tile.eTileState;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TileController extends StackPane {
	private GameController game;
	private int x;
	private int y;
	private boolean isSelected = false;
    private Rectangle border;
    private Tile tile;
	private Text text = new Text();

	public TileController(GameController gmc, int nX, int nY, Tile i_tile, double height, double width) {
		game = gmc;
		x = nX;
		y = nY;
		this.setVisible(true);
		border = new Rectangle(width, height);
        border.setStroke(Color.GREEN);
        border.setFill(null);
        setTranslateX(x * width);
        setTranslateY(y * height);
		text.setFont(Font.font(18));
		tile = i_tile;
		text.setVisible(true);
		getChildren().addAll(border, text);
		this.setOnMouseClicked(e -> Clicked());
		Refresh();
	}
	
	private Object Clicked() {
//		game.TilePressed(this);
		return null;
	}

	public void Refresh()
	{
/*		if (tile.getState() == eTileState.FACE_UP) */{
			int letterPoints = game.gameManager.getCurrentGame().getSettings().getLetterScore(tile.getSign());
			text.setText(Character.toString(tile.getSign()) + "(" + Integer.toString(letterPoints) + ")"); // TODO: Add points
		}
	}
	
}
