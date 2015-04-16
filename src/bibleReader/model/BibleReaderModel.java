package bibleReader.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The model of the Bible Reader. It stores the Bibles and has methods for
 * searching for verses based on words or references.
 *
 * @author cusack, Cameron Van Eyl, Nathan Gingrich, Matt Blessed, Karl Heusinkveld
 */
public class BibleReaderModel implements MultiBibleModel {
	private final TreeMap<String, Bible> bibles;
	private final HashMap<String, Concordance> concordances;

	/**
	 * Default constructor. You probably need to instantiate objects and do
	 * other assorted things to set up the model.
	 */
	public BibleReaderModel() {
		bibles = new TreeMap<String, Bible>();
		concordances = new HashMap<String, Concordance>();
	}

	@Override
	public String[] getVersions() {
		return bibles.keySet().toArray(new String[bibles.size()]);
	}

	@Override
	public int getNumberOfVersions() {
		return bibles.size();
	}

	@Override
	public void addBible(Bible bible) {
		bibles.put(bible.getVersion(), bible);
		
		Concordance c = BibleFactory.createConcordance(bible);
		
		concordances.put(bible.getVersion(), c);
	}

	@Override
	public Bible getBible(String version) {
		return bibles.get(version);
	}

	@Override
	public VerseList getVerses(String version, ReferenceList references) {
		return bibles.get(version).getVerses(references);
	}

	@Override
	public String getText(String version, Reference reference) {
		Bible bible = bibles.get(version);
		if (bible == null) return "";
	    String text = bible.getVerseText(reference);;
		return text;
	}

	@Override
	public ReferenceList getReferencesContaining(String words) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (String version : bibles.keySet()) {
			references.addAll(bibles.get(version).getReferencesContaining(words));
		}
		return new ReferenceList(references);
	}
	
	// stage 12
	@Override
	public ReferenceList getReferencesContainingWord(String word) {
		// single word
		if (word.split(" ").length > 1) return new ReferenceList();
		
		TreeSet<Reference> results = new TreeSet<Reference>();
		for (String version : bibles.keySet()) {
			results.addAll(concordances.get(version).getReferencesContaining(word));
		}
		
		return new ReferenceList(results);
	}

	@Override
	public ReferenceList getReferencesContainingAllWords(String words) {
		TreeSet<Reference> results = new TreeSet<Reference>();
		
		ArrayList<String> wordsList = Concordance.extractWords(words);
		for (String version : bibles.keySet()) {
			results.addAll(concordances.get(version).getReferencesContainingAll(wordsList));
		}
		
		return new ReferenceList(results);
	}

	@Override
	public ReferenceList getReferencesContainingAllWordsAndPhrases(String words) {
		TreeSet<Reference> result = new TreeSet<Reference>();
		
		//System.out.println("Phrase:              "+words);
		//words = words.trim().toLowerCase();
		//System.out.println("ToLowerCase+Trimmed: "+words);
		if (words.split("\"").length > 1) {
			// has quotes
			System.out.println("quoted search:"+words);
			
			ArrayList<TreeSet<Reference>> results = new ArrayList<TreeSet<Reference>>();
			
			for (String phrase : words.split("\"")) {
				phrase.trim(); // doesnt catch some only whitespace strings
				if (!phrase.matches("([^a-zA-Z]|\\s*)")) {
					System.out.println("Searching for quote \""+phrase+"\"");
					ReferenceList r = getReferencesContainingAllWords(phrase.toLowerCase());
					
					TreeSet<Reference> singleResult = new TreeSet<Reference>();
					phrase = phrase.replaceAll("\"", "");
					for (String version : bibles.keySet()) {
						for (Verse v : getVerses(version, r)) {
							if (v != null) {
								String text = v.getText().toLowerCase();
								if (text.contains(phrase) || text.contains(phrase.toLowerCase())) {
									singleResult.add(v.getReference());
								}
							}
						}
					}
					System.out.println("Results: "+singleResult.size());
					
					results.add(singleResult);
				}
			}
			
			System.out.println("Ended up with "+results.size()+" sets of results.");
			
			if (results.size() == 0) return new ReferenceList();
			if (results.size() == 1) return new ReferenceList(results.get(0));
			
			TreeSet<Reference> firstResult = results.get(0);
			for (TreeSet<Reference> aResult : results) {
				if (aResult != firstResult) {
					
					firstResult.retainAll(aResult);
					System.out.println("After retainAll: "+firstResult.size());
					
				}
			}
			
			result = firstResult;
			
				/*
				if (phraseResults.size() == 1) {
					results.add(phraseResults.get(0));
					System.out.println(phrase+" "+phraseResults.get(0)+" <- was only one.");
				}
				else if (phraseResults.size() > 1) {
					TreeSet<Reference> firstResult = phraseResults.get(0);
					for (TreeSet<Reference> aResult : phraseResults) {
						if (aResult != firstResult) {
							firstResult.retainAll(aResult);
						}
					}
					
					ReferenceList finalResults = new ReferenceList();
					for (String version : bibles.keySet()) {
						for (Verse v : getVerses(version, new ReferenceList(firstResult))) {
							if (v.getText().contains(phrase.replaceAll("\"", ""))) {
								finalResults.add(v.getReference());
							}
						}
					}
					
					results.add(new TreeSet<Reference>(finalResults));
				}
			}
			
			
			
			*/
		}
		else if (words.split(" ").length == 1) {
			System.out.println("single word search:"+words);
			// single word
			result.addAll(getReferencesContainingWord(words));
		}
		else {
			System.out.println("multi word search:"+words);
			// multi worded
			result.addAll(getReferencesContainingAllWords(words));
		}
		return new ReferenceList(result);
	}

	public static Pattern bookPattern = Pattern
			.compile("\\s*((?:1|2|3|I|II|III)\\s*\\w+|(?:\\s*[a-zA-Z]+)+)\\s*(.*)");
	public static Pattern cvPattern = Pattern
			.compile("(\\d*)\\s*:\\s*(\\d*)\\s*");
	public static Pattern cversesPattern = Pattern
			.compile("(\\d*)\\s*:\\s*(\\d*)\\s*-\\s*(\\d*)\\s*");
	public static Pattern chapterPattern = Pattern
			.compile("(\\d*)(?:\\s*)");
	public static Pattern chaptersPattern = Pattern
			.compile("(\\d*)\\s*(?:-)\\s*(\\d*)\\s*");
	public static Pattern chaptersVersesPattern = Pattern
			.compile("(\\d*)\\s*:\\s*(\\d*)\\s*-\\s*(\\d*)\\s*:\\s*(\\d*)\\s*");
	public static Pattern multipleVersesWithOddPattern = Pattern
			.compile("(\\d*)\\s*-\\s*(\\d*)\\s*:\\s*(\\d*)\\s*");
	
	@Override
	public ReferenceList getReferencesForPassage(String reference) {
		ReferenceList refList = new ReferenceList();
		
		String theRest = null;
		BookOfBible book;
		int chapter1, chapter2, verse1, verse2;

		Matcher m = bookPattern.matcher(reference);
		if (m.matches()) {
			book = BookOfBible.getBookOfBible(m.group(1));
			if (book == null) {
				return refList;
			}
			theRest = m.group(2);
			try {
				if (theRest.length() == 0) {
					refList = getBookReferences(book);
				} else if ((m = cvPattern.matcher(theRest)).matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					verse1 = Integer.parseInt(m.group(2));
					refList = getVerseReferences(book, chapter1, verse1);
					
				} else if ((m = cversesPattern.matcher(theRest)).matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					verse1 = Integer.parseInt(m.group(2));
					verse2 = Integer.parseInt(m.group(3));
					refList = getPassageReferences(book, chapter1, verse1, verse2);
					
				} else if ((m = chapterPattern.matcher(theRest)).matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					refList = getChapterReferences(book, chapter1);
					
				} else if ((m = chaptersPattern.matcher(theRest)).matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					chapter2 = Integer.parseInt(m.group(2));
					refList = getChapterReferences(book, chapter1, chapter2);
					
				} else if ((m = chaptersVersesPattern.matcher(theRest)).matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					verse1 = Integer.parseInt(m.group(2));
					chapter2 = Integer.parseInt(m.group(3));
					verse2 = Integer.parseInt(m.group(4));
					refList = getPassageReferences(book, chapter1, verse1, chapter2, verse2);
					
				} else if ((m = multipleVersesWithOddPattern.matcher(theRest))
						.matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					verse1 = 1;
					chapter2 = Integer.parseInt(m.group(2));
					verse2 = Integer.parseInt(m.group(3));
					refList = getPassageReferences(book, chapter1, verse1, chapter2, verse2);
					
				} else {
					// Invalid format for chapters and verses
				}
			} catch (NumberFormatException e) {
				return refList;
			}
		} else {
			// Invalid format for book of bible
		}
		
		return refList;
	}

	// -----------------------------------------------------------------------------
	// The methods below are for use by the getReferencesForPassage method
	// above.
	// After it parses the input string it will call one of these.
	//
	// These methods should be somewhat easy to implement. They are kind of
	// delegate
	// methods in that they call a method on the Bible class to do most of the
	// work.
	// However, they need to do so for every version of the Bible stored in the
	// model.
	// and combine the results.
	//
	// Once you implement one of these, the rest of them should be fairly
	// straightforward.
	// Think before you code, get one to work, and then implement the rest based
	// on
	// that one.
	//
	// These methods should not notify the observers. There may be times when we
	// want to call them and not notify. Since they are private methods, we
	// should
	// have the right to do that.
	//
	// These methods also shouldn't set the query/result type since we presume
	// one
	// of the public methods was called and hopefully that method did so.
	// -----------------------------------------------------------------------------

	@Override
	public ReferenceList getVerseReferences(BookOfBible book, int chapter,
			int verse) {
		Reference r = new Reference(book, chapter, verse);
		
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (String version : bibles.keySet()) {
			Verse v = bibles.get(version).getVerse(r);
			if (v != null) {
				references.add(v.getReference());
			}
		}
		return new ReferenceList(references);
	}

	@Override
	public ReferenceList getPassageReferences(Reference startVerse,
			Reference endVerse) {
		
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (String version : bibles.keySet()) {
			references.addAll(bibles.get(version).getReferencesInclusive(startVerse, endVerse));
		}
		return new ReferenceList(references);
	}

	@Override
	public ReferenceList getBookReferences(BookOfBible book) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (String version : bibles.keySet()) {
			references.addAll(bibles.get(version).getReferencesForBook(book));
		}
		return new ReferenceList(references);
	}

	@Override
	public ReferenceList getChapterReferences(BookOfBible book, int chapter) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (String version : bibles.keySet()) {
			references.addAll(bibles.get(version).getReferencesForChapter(book, chapter));
		}
		return new ReferenceList(references);
	}

	@Override
	public ReferenceList getChapterReferences(BookOfBible book, int chapter1,
			int chapter2) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (String version : bibles.keySet()) {
			references.addAll(bibles.get(version).getReferencesForChapters(book, chapter1, chapter2));
		}
		return new ReferenceList(references);
	}

	@Override
	public ReferenceList getPassageReferences(BookOfBible book, int chapter,
			int verse1, int verse2) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (String version : bibles.keySet()) {
			references.addAll(bibles.get(version).getReferencesForPassage(book, chapter, verse1, verse2));
		}
		return new ReferenceList(references);
	}

	@Override
	public ReferenceList getPassageReferences(BookOfBible book, int chapter1,
			int verse1, int chapter2, int verse2) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (String version : bibles.keySet()) {
			references.addAll(bibles.get(version).getReferencesForPassage(book, chapter1, verse1, chapter2, verse2));
		}
		return new ReferenceList(references);
	}
}
