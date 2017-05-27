package engine;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class Trie implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 7484421465296545140L;
	private TrieNode root;
    private int size;

    public Trie() {
        root = new TrieNode();
        size = 0;
    }

    public boolean add(String word) {
        TrieNode trie = root;
        if (trie == null || word == null)
            return false;

        char[] chars = word.toCharArray();
        int counter = 0;
        while (counter < chars.length) {
            Set<Character> childs = trie.getChildren().keySet();
            if (!childs.contains(chars[counter])) {
                insertChar(trie, chars[counter]);
                if (counter == chars.length - 1) {
                    getChild(trie, chars[counter]).setIsWord(true);
                    size++;
                    return true;
                }
            }
            trie = getChild(trie, chars[counter]);
            if (trie.getText().equals(word) && !trie.isWord()) {
                trie.setIsWord(true);
                size++;
                return true;
            }
            counter++;
        }
        return false;
    }

    public boolean find(String str) {
        Map<Character, TrieNode> children = root.getChildren();
        TrieNode t = null;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (children.containsKey(c)) {
                t = children.get(c);
                children = t.getChildren();
            } else return false;
        }

        return true;
    }

    public boolean remove(String str) {

        return findNode(root, str);
    }

    private TrieNode getChild(TrieNode trie, Character c) {
        return trie.getChildren().get(c);
    }

    private TrieNode insertChar(TrieNode trie, Character c) {
        if (trie.getChildren().containsKey(c)) {
            return null;
        }

        TrieNode next = new TrieNode(trie.getText() + c.toString());
        trie.getChildren().put(c, next);
        return next;
    }

    private boolean findNode(TrieNode trie, String s) {

        Map<Character, TrieNode> children = root.getChildren();

        TrieNode parent = null;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (children.containsKey(c)) {
                parent = trie;
                trie = children.get(c);
                children = trie.getChildren();
                if (trie.getText().equals(s)) {

                    parent.getChildren().remove(c);
                    trie = null;
                    return true;
                }
            }
        }
        return false;
    }

    public int getSize() {
        return size;
    }
    
    public String search(char[] cs) {
    	return visit(root, cs);
	}

	public String visit(TrieNode n, char[] allowedCharacters) {
		String bestMatch = null;
    	
		if (n.getChildren().isEmpty()) {
			bestMatch = n.getText();
    	}

    	for (Map.Entry<Character, TrieNode> child : n.getChildren().entrySet()) {
    		if (contains(allowedCharacters, child.getValue().getValue())) {
    			String result = visit(child.getValue(), remove(allowedCharacters, child.getValue().getValue()));
    			// if the result wasn't null, check length and set
    			if (bestMatch == null || result != null
    					&& bestMatch.length() < result.length()) {
    				bestMatch = result;
    				}
    			}
    		}
    	
    	// always return the best known match thus far
    	return bestMatch;
	}
	
	public int countWords(char[] allowedCharacters) {
		return countWords(root, allowedCharacters);
	}
	
	/*
	 * Count children that are words (isWord == true) + this one if its a word
	 */
	private int countWords(TrieNode n, char[] allowedCharacters) {
		if (n.getChildren().isEmpty()) {
			return 0;
    	}
		
		int result = 0;
		
    	for (Map.Entry<Character, TrieNode> child : n.getChildren().entrySet()) {
    		if (contains(allowedCharacters, child.getValue().getValue())) {
    			result += countWords(child.getValue(), remove(allowedCharacters, child.getValue().getValue()));
    			if (child.getValue().isWord()) {
    				result += (child.getValue().isWord() ? 1 : 0);
    			}
			}
		}
    	
    	return result;
	}
	
	private char[] remove(char[] allowedCharacters, char value) {
		char[] newDict = new char[allowedCharacters.length - 1];
		int index = 0;
		for (char x : allowedCharacters) {
			if (x != value) {
				newDict[index++] = x;
				} else {
					// we removed the first hit, now copy the rest
					break;
					}
			}
		System.arraycopy(allowedCharacters, index + 1, newDict, index,
				allowedCharacters.length - (index + 1));
		return newDict;
	}

	 private boolean contains(char[] allowedCharacters, char value) {
		 for (char x : allowedCharacters) {
			 if (value == x) {
				 return true;
				 }
			 }
		 return false;
		 }
	 }