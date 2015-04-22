package bibleReader.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;

/**
 * Concordance is a class which implements a concordance for a Bible. In other words, it allows the easy lookup of all
 * references which contain a given word.
 * 
 * @author Chuck Cusack, March 2013 (Provided the interface)
 * @author Matt, Karl, March 2013 (Provided the implementation details)
 */
public class Concordance {
	private Hashtable<String, HashSet<Reference>> index;
	
	/**
	 * Construct a concordance for the given Bible.
	 */
	public Concordance(Bible bible) {
		double debugTime = System.currentTimeMillis();
		
		System.out.println((System.currentTimeMillis()-debugTime)+"started index on "+bible.getVersion());
		index = new Hashtable<String, HashSet<Reference>>();
		for (Verse v : bible.getAllVerses()) {
			//System.out.println((System.currentTimeMillis()-debugTime)+""+v);
			for (String word : extractWords(v.getText())) {
				if (!word.equals("")) {
					word = word.toLowerCase();
					HashSet<Reference> sortedSet = index.get(word);
					if (sortedSet != null) {
						sortedSet.add(v.getReference());
					}
					else {
						sortedSet = new HashSet<Reference>();
						sortedSet.add(v.getReference());
					}
					index.put(word, sortedSet);
				}
			}
		}
		System.out.println((System.currentTimeMillis()-debugTime)+"finished index on "+bible.getVersion());
	}

	/**
	 * Return the list of references to verses that contain the word 'word' (ignoring case) in the version of the Bible
	 * that this concordance was created with.
	 * 
	 * @param word a single word (no spaces, etc.)
	 * @return the list of References of verses from this version that contain the word, or an empty list if no verses
	 *         contain the word.
	 */
	public ReferenceList getReferencesContaining(String word) {
		word = word.toLowerCase();
		if (!index.containsKey(word)) {
			return new ReferenceList();
		}
		HashSet<Reference> h = index.get(word);
		return new ReferenceList(h);
	}

	/**
	 * Given an array of Strings, where each element of the array is expected to be a single word (with no spaces, etc.,
	 * but ignoring case), return a ReferenceList containing all of the verses that contain <i>all of the words</i>.
	 * 
	 * @param words A list of words.
	 * @return An ReferenceList containing references to all of the verses that contain all of the given words, or an
	 *         empty list if
	 */
	public ReferenceList getReferencesContainingAll(ArrayList<String> words) {
		ArrayList<TreeSet<Reference>> results = new ArrayList<TreeSet<Reference>>();
		for (String word : words) {
			word = word.toLowerCase();
			
			TreeSet<Reference> result = new TreeSet<Reference>();	
			
			if (!index.containsKey(word)) return new ReferenceList();
			
			result.addAll(index.get(word));
			results.add(result);
		}
		
		if (results.size() == 0) return new ReferenceList();
		if (results.size() == 1) return new ReferenceList(results.get(0));
		
		TreeSet<Reference> firstResult = results.get(0);
		for (TreeSet<Reference> result : results) {
			if (result != results.get(0)) {
				firstResult.retainAll(result);
			}
		}
		
		return new ReferenceList(firstResult);
	}
	
	public static ArrayList<String> extractWords(String text) {
		text = text.toLowerCase();
		// Removes...
		text = text.replaceAll("(<sup>[,\\w]*?</sup>|'s|`s|&#\\w*;)", "");
		// Removes...
		text = text.replaceAll(",","");
		String[] words = text.split("\\W+");
		ArrayList<String> toReturn = new ArrayList<String>(Arrays.asList(words));
		toReturn.remove("");
		return toReturn;
	}
}
