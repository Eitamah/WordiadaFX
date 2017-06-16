package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.sun.javafx.collections.MappingChange.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

public class Dictionary implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2883912875622922743L;
	private int totalWords;
	private final int NUM_SEGMENTS = 3;
	private static String INVALID_CHARS = "[!?,.:;\\-_=+\\*\"'\\(\\){}\\[\\]%$]";
	
	// Hashmap words for counting words - for later use (score based on frequency) 
	// Trie is for smart computer
	// Pair<Integer, Integer>
	private HashMap<String, WordScore> words;
	private Trie trie;
	
	
	public boolean isValidWord(String word) {
		return words.containsKey(word);
	}
	
	public Dictionary(String filePath) throws FileNotFoundException, IllegalArgumentException {
		words = new HashMap<String, WordScore>();
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
//							words.put(word, words.get(word).intValue() + 1);
							words.get(word).incrementWordCount();
						}
						else {
							WordScore ws = new WordScore(0, 1);
							words.put(word, ws);
						}
					}
				}
				
				currentLine = br.readLine();
			}
			
			setSegments();
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
		return words.get(word).getWordCount();
	}
	
	public int getTotalWords() {
		return totalWords;
	}
	
	public List<String>	getLeastPopularWords() {
		HashMap<String, WordScore> temp = (HashMap<String, WordScore>) words.clone();
		sortByValue(temp);
		
		List<String> ret = FXCollections.observableArrayList(temp.keySet());
		
		temp = sortMapByValues(temp);
		
		ret = FXCollections.observableArrayList(temp.keySet());

		if (ret.size() < 10)
			return ret;
		else
			return ret.subList(0, 10);		
	}
	
	public static <K, V extends Comparable<? super WordScore>> HashMap<String, WordScore> sortByValue(HashMap<String, WordScore> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(HashMap.Entry.comparingByValue(Collections.reverseOrder()))
	              .collect(Collectors.toMap(
        		    HashMap.Entry::getKey, 
        		    HashMap.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}
	
	private static HashMap<String, WordScore> sortMapByValues(HashMap<String, WordScore> aMap) {
        
        Set<Entry<String, WordScore>> mapEntries = aMap.entrySet();
        
        // used linked list to sort, because insertion of elements in linked list is faster than an array list. 
        List<Entry<String,WordScore>> aList = new LinkedList<Entry<String, WordScore>>(mapEntries);

        // sorting the List
        Collections.sort(aList, new Comparator<Entry<String,WordScore>>() {

            @Override
            public int compare(Entry<String, WordScore> ele1,
                    Entry<String, WordScore> ele2) {
                
                return ele1.getValue().compareTo(ele2.getValue());
            }
        });
        
        // Storing the list into Linked HashMap to preserve the order of insertion. 
        HashMap<String,WordScore> aMap2 = new LinkedHashMap<String, WordScore>();
        for(Entry<String,WordScore> entry: aList) {
            aMap2.put(entry.getKey(), entry.getValue());
        }
        
        return aMap2;
    }

	public int getWordSegment(String word) {
		return words.get(word).getSegment();
	}
	
	private void setSegments() {
		int totalDifferentWords = words.size();
		int segmentSize = totalDifferentWords / NUM_SEGMENTS;
		
		HashMap<String, WordScore> temp = (HashMap<String, WordScore>) words.clone();
		sortByValue(temp);
		
		List<String> ret = FXCollections.observableArrayList(temp.keySet());
		
		temp = sortMapByValues(temp);
		int currentSegment = NUM_SEGMENTS;
		int nCounter = 0;
		for (String word : temp.keySet()) {
			words.get(word).setSegment(currentSegment);
			nCounter++;
			
			if (nCounter > segmentSize) {
				nCounter = 0;
				currentSegment--;
			}
		}
	}
}
