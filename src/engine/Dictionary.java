package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class Dictionary implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2883912875622922743L;
	private int totalWords;
	private static String INVALID_CHARS = "[!?,.:;\\-_=+\\*\"'\\(\\){}\\[\\]%$]";
	
	// Hashmap words for counting words - for later use (score based on frequency) 
	// Trie is for smart computer
	private HashMap<String, Integer> words;
	private Trie trie;
	
	
	public boolean isValidWord(String word) {
		return words.containsKey(word);
	}
	
	public Dictionary(String filePath) throws FileNotFoundException, IllegalArgumentException {
		words = new HashMap<String, Integer>();
		trie = new Trie();
		try {
			loadDictionary(filePath);
		} catch (IOException e) {
			throw new IllegalArgumentException("problem with dictionary file");
		}
		
		if (totalWords < 1) {
			throw new IllegalArgumentException("Dictionary file doesnt contain any words");
		}
	}
	
	private void loadDictionary(String filePath) throws IllegalArgumentException, IOException {
		BufferedReader br = null;
		FileReader fr = null;
		File file;
		FileInputStream fs = null;;
		
		try {
			file = new File(filePath);
			fs = new FileInputStream(file);
			
			// byte order mark (BOM) is a Unicode character used to signal the endianness 
			// (byte order) of a text file or stream. we don't want to add this as a word.
		
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);
			
			int first = br.read();
			if ((first > 127) || first == 0) {
				// means first 4 letters are BOM
				br.skip(4);	
			}
			
			String currentLine = br.readLine();
			
			while (currentLine != null) {
				currentLine = currentLine.toUpperCase();
				currentLine = currentLine.replaceAll(INVALID_CHARS, "");
				String[] lineWords = currentLine.split(" ");
				
				for (String word : lineWords) {
					if (word.length() > 1) {
						totalWords++;
						trie.add(word);
						if (words.containsKey(word)) {
							words.put(word, words.get(word).intValue() + 1);
						}
						else {
							words.put(word, 1);
						}
					}
				}
				
				currentLine = br.readLine();
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Dictionary file not found");
		}
		finally {
			if (br != null) {
				br.close();
			}
			
			if (fr != null) {
				fr.close();
			}
			
			if (fs != null) {
				fs.close();
			}
		}
	}

	public String GetLongestWord(char[] cs) {
		return trie.search(cs);
	}
	
	public int getPossibleWordCount(char[] cs) {
		return trie.countWords(cs);
	}
	public int getWordCount(String word) {
		return words.get(word);
	}
	
	public int getTotalWords() {
		return totalWords;
	}
}
