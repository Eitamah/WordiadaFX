package engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import gameSettings.GameDescriptor;
import gameSettings.Letter;
import javafx.beans.InvalidationListener;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class GameSettings extends Observable implements ValidationEventHandler, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5822399758574401295L;

	public enum eWinnerBy {
		WordCount,
		WordScore
	}
	
	public enum eGameType {
		Basic,
		Multiplayer
	}
	public boolean isGoldFishMode = false;
	private String dictFilePath;
	private int deckSize;
	private Dictionary dictionary;
	private int boardSize;
	private eGameType gameType;
	private boolean valid;
	private boolean xmlValid;
	private eWinnerBy winnerBy;
	private List<Letter> letters;
	private int numOfPlayers = 2;
	private int numOfDiceFacets;
	private GameDescriptor gd;
	
	public enum eLoadSteps {
		FILE(1),
		UNMARSHAL(2),
		DICTIONARY(3),
		VALIDATION(4),
		FINISHED(5);
		
		private int value;
		public int getValue() {
			return value;
		}
		private eLoadSteps(int num) {
			value = num;
		}
	}
	
	public void Load(String filePath) throws FileNotFoundException, IllegalArgumentException, JAXBException {
		valid = false;
		JAXBContext jaxbContext;
		Unmarshaller jaxbUnmarshaller;
		jaxbContext = JAXBContext.newInstance(GameDescriptor.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        jaxbUnmarshaller.setEventHandler(this);
		String path = "";	
		try {
			setChanged();
			notifyObservers(eLoadSteps.FILE);
			TimeUnit.MILLISECONDS.sleep(350);
			File XMLfile = new File(filePath);
			path = XMLfile.getAbsolutePath();
			path = path.substring(0, path.lastIndexOf(File.separator));
			xmlValid = true;
			setChanged();
			notifyObservers(eLoadSteps.UNMARSHAL);
			TimeUnit.MILLISECONDS.sleep(350);
			gd = (GameDescriptor) jaxbUnmarshaller.unmarshal(XMLfile);
		} catch (JAXBException e) {
			xmlValid = false;
			throw new FileNotFoundException("XML File not found");
		} catch (Exception e) {
		}
		
		// If the xml was invalid, the event would have set xmlValid to false
		if (xmlValid) {
			dictFilePath =  path + "\\dictionary\\" + gd.getStructure().getDictionaryFileName();
			setChanged();
			notifyObservers(eLoadSteps.DICTIONARY);
			try {
				TimeUnit.MILLISECONDS.sleep(350);
			} catch (Exception e) {
			}
			dictionary = new Dictionary(dictFilePath);
		
			
			setChanged();
			notifyObservers(eLoadSteps.VALIDATION);
			try {
				TimeUnit.MILLISECONDS.sleep(350);
			} catch (Exception e) {
			}
			
			deckSize = gd.getStructure().getLetters().getTargetDeckSize();
			boardSize = gd.getStructure().getBoardSize();
		
			if ((boardSize > Board.MAX_SIZE) || (boardSize < Board.MIN_SIZE)) {
				throw new IllegalArgumentException("Board size must be between " + Board.MAX_SIZE + " and "
						+ Board.MIN_SIZE);
			}
			
			if (deckSize < (boardSize * boardSize)) {
				throw new IllegalArgumentException("Deck size not enough for board");
			}
			
			if (!gd.getGameType().getValue().equals("MultiPlayer")) {
				throw new IllegalArgumentException("Game type must be MultiPlayer ");
			} else {
				gameType = eGameType.Multiplayer;
			}
			
			if (gd.getGameType().getWinnerAccordingTo().equals("WordScore")) {
				winnerBy = eWinnerBy.WordScore;
			} else {
				winnerBy = eWinnerBy.WordCount;
			}
			
			if (gd.getGameType().isGoldFishMode()) {
				isGoldFishMode = true;
			}
			
			if (gd.getStructure().getCubeFacets() >= Dice.MIN_SIDES) {
				numOfDiceFacets = gd.getStructure().getCubeFacets();
			} else {
				throw new IllegalArgumentException("Dice facets must be at least " + Dice.MIN_SIDES);
			}
			
			loadLetters(gd);
			valid = true;
			
			setChanged();
			notifyObservers(eLoadSteps.FINISHED);
		}
	}

	private void loadLetters(GameDescriptor gd) throws IllegalArgumentException {
		letters = new ArrayList<Letter>();
		List<Letter> list = gd.getStructure().getLetters().getLetter();
		double totalFreq = 0;
		
		for (Letter let : list) {
			totalFreq += let.getFrequency();
		}
		
		for (Letter let : list) {
			let.setFrequency(Math.ceil( (let.getFrequency() * 100) / totalFreq) );
		}
		
		for (Letter let : list) {
			let.getSign().get(0).toUpperCase();
			
			if (letters.contains(let)) {
				throw new IllegalArgumentException("the letter " + let.getSign() + " apears more than once");
			}
			
			int numOfLetter = (int)Math.ceil((let.getFrequency() / 100) * gd.getStructure().getLetters().getTargetDeckSize());
			
			for (int i = 0; i < numOfLetter; ++i) {
				letters.add(let);
			}
		}
	}
	
	public void recalculateFreq() {
		
	}

	public boolean isValid() {
		return valid;
	}
	
	public eGameType getGameType() {
		return gameType;
	}
	
	public int getDeckSize() {
		return deckSize;
	}
	
	public Dictionary getDictionary() {
		return dictionary;
	}
	
	public int getNumOfPlayers() {
		return numOfPlayers;
	}
	
	public int getBoardSize() {
		return boardSize;
	}

	public List<Letter> getLetters() {
		return letters;
	}
	@Override
	public boolean handleEvent(ValidationEvent event) {
		xmlValid = false;
		// TODO : Maybe get rid of getMessage()
		throw new IllegalArgumentException("Xml invalid " + event.getMessage() + " line " 
				+ event.getLocator().getLineNumber() + " column" + event.getLocator().getColumnNumber());
	}
	
	public int getDiceFacets() {
		return numOfDiceFacets;
	}
	
	public GameDescriptor getDescriptor() {
		return gd;
	}

	public int getScore(String word) {
		int score = 0;
		
		if (winnerBy == eWinnerBy.WordCount) {
			score = 1;
		} else {
			for (char letter : word.toCharArray()) {
				score += getLetterScore(letter);
			}
			
			score *= dictionary.getWordSegment(word);
		}
		
		return score;
	}
	
	public int getLetterScore(String word)
	{
		int score = 0;
		for (char letter : word.toCharArray()) {
			score += getLetterScore(letter);
		}
		return score;
	}
	
	public int getLetterScore(char c)
	{
		List<Letter> list = gd.getStructure().getLetters().getLetter();

		for (Letter letter : list) {
			if (letter.getSign().get(0).charAt(0) == c) {
				return letter.getScore();
			}
		}
		
		return 0;
	}
	
	public eWinnerBy getWinnerBy() {
		return winnerBy;
	}
}
