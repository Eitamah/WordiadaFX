package engine;

public class WordScore implements Comparable<WordScore>{
	private int segment;
	private int wordCount;
	
	public WordScore(int seg, int count) {
		segment = seg;
		wordCount = count;
	}
	
	public void setSegment(int seg) {
		segment = seg;
	}
	
	public void incrementWordCount() {
		wordCount++;
	}
	
	public int getSegment() {
		return segment;
	}
	
	public int getWordCount() {
		return wordCount;
	}

	@Override
	public int compareTo(WordScore o) {
		Integer a = this.wordCount;
		Integer b = o.getWordCount();
		
		return a.compareTo(b);
	}
}
