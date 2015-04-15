package bibleReader.model;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A class that stores a version of the Bible.
 *
 * @author Chuck Cusack (Provided the interface)
 * @author Matt Blessed (Provided the implementation)
 */
public class TreeMapBible implements Bible {
	
	// The Fields
	private String						version;
	private String						title;
	private TreeMap<Reference, Verse>	theVerses;

	/**
	 * Create a new Bible with the given verses.
	 *
	 * @param version the version of the Bible (e.g. ESV, KJV, ASV, NIV).
	 * @param verses All of the verses of this version of the Bible.
	 */
	public TreeMapBible(VerseList verses) {
		this.version = verses.getVersion();
		this.title = verses.getDescription();
		this.theVerses = new TreeMap<Reference, Verse>();
		for (Verse v : verses) {
			this.theVerses.put(v.getReference(), v);
		}
	}

	@Override
	public int getNumberOfVerses() {
		return this.theVerses.size();
	}

	@Override
	public VerseList getAllVerses() {
		return new VerseList(this.version,this.title,this.theVerses.values());
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public boolean isValid(Reference ref) {
		return (this.theVerses.get(ref) != null);
	}

	@Override
	public String getVerseText(Reference r) {
		Verse v = this.theVerses.get(r);
		return (v != null)?v.getText():null;
	}

	@Override
	public Verse getVerse(Reference r) {
		if (r != null && r.getBookOfBible() != null) {
			return this.theVerses.get(r);
		}
		return null;
	}

	@Override
	public Verse getVerse(BookOfBible book, int chapter, int verse) {
		return this.theVerses.get(new Reference(book, chapter, verse));
	}

	@Override
	public VerseList getVersesContaining(String phrase) {
		VerseList vList = new VerseList(version,phrase);
		if (phrase.equals("") || phrase == null) {
			return vList;
		}
		phrase = phrase.toLowerCase();
		for (Map.Entry<Reference, Verse> entry : this.theVerses.entrySet()) {
			if (entry.getValue().getText().toLowerCase().contains(phrase)) {
				vList.add(entry.getValue());
			}
		}
		return vList;
	}

	@Override
	public ReferenceList getReferencesContaining(String phrase) {
		ReferenceList rList = new ReferenceList();
		if (phrase.equals("") || phrase == null) {
			return rList;
		}
		phrase = phrase.toLowerCase();
		for (Map.Entry<Reference, Verse> entry : this.theVerses.entrySet()) {
			if (entry.getValue().getText().toLowerCase().contains(phrase)) {
				rList.add(entry.getKey());
			}
		}
		return rList;
	}

	@Override
	public VerseList getVerses(ReferenceList references) {
		VerseList verses = new VerseList(version, title);
		for(Reference r : references){
			verses.add(getVerse(r));
		}
		return verses;
	}

	@Override
	public int getLastVerseNumber(BookOfBible book, int chapter) {
		Reference r1 = new Reference(book, chapter, 1);
		return getVersesExclusive(r1, new Reference(book, chapter+1, 1)).size();
	}

	@Override
	public int getLastChapterNumber(BookOfBible book) {
		// get all verses from book (could use references also. minor point.)
		VerseList v = getVersesExclusive(new Reference(book, 1, 1), new Reference(BookOfBible.nextBook(book), 1, 1));
		Verse r = v.get(v.size()-1);
		return r.getReference().getChapter();
	}

	@Override
	public ReferenceList getReferencesInclusive(Reference firstVerse, Reference lastVerse) {
		return getReferencesExclusive(firstVerse,new Reference(lastVerse.getBookOfBible(), lastVerse.getChapter(), lastVerse.getVerse()+1));
	}

	@Override
	public ReferenceList getReferencesExclusive(Reference firstVerse, Reference lastVerse) {
		// Implementation copied and changed from Charles Cusack's implementation.
		
		// Make sure the references are in the correct order. If not, return empty reference list.
		if (firstVerse.compareTo(lastVerse) > 0) {
			return new ReferenceList();
		}
		// Return the portion of the TreeMap that contains the verses between
		// the first and the last, not including the last.
		SortedMap<Reference, Verse> s = theVerses.subMap(firstVerse, lastVerse);

		// Get the entries from the map so we can iterate through them.
		Set<Map.Entry<Reference, Verse>> mySet = s.entrySet();

		// We will store the resulting verses here. We copy the version from
		// this Bible and set the description to be the passage that was searched for.
		ReferenceList someReferences = new ReferenceList();

		// Iterate through the set and put the verses in the VerseList.
		for (Map.Entry<Reference, Verse> element : mySet) {
			Reference aRef = element.getKey();
			someReferences.add(aRef);
		}
		return someReferences;
	}

	@Override
	public ReferenceList getReferencesForBook(BookOfBible book) {
		if (book == null) return new ReferenceList();
		Reference r1 = new Reference(book, 1, 1);
		Reference r2 = new Reference(BookOfBible.nextBook(book),1,1);
		return getReferencesExclusive(r1, r2);
	}

	@Override
	public ReferenceList getReferencesForChapter(BookOfBible book, int chapter) {
		if (book == null) return new ReferenceList();
		Reference r1 = new Reference(book,chapter,  1);
		Reference r2 = new Reference(book,chapter+1,1);
		return getReferencesExclusive(r1, r2);
	}

	@Override
	public ReferenceList getReferencesForChapters(BookOfBible book, int chapter1, int chapter2) {
		if (chapter1 > chapter2) return new ReferenceList();
		if (book == null) return new ReferenceList();
		
		Reference r1 = new Reference(book,chapter1,  1);
		Reference r2 = new Reference(book,chapter2+1,1);
		return getReferencesExclusive(r1, r2);
	}

	@Override
	public ReferenceList getReferencesForPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		if (verse1 > verse2) return new ReferenceList();
		if (book == null) return new ReferenceList();
		
		Reference r1 = new Reference(book, chapter, verse1);
		Reference r2 = new Reference(book, chapter, verse2);
		return getReferencesInclusive(r1, r2);
	}

	@Override
	public ReferenceList getReferencesForPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		if (chapter1 > chapter2) return new ReferenceList();
		if (book == null) return new ReferenceList();
		
		Reference r1 = new Reference(book, chapter1, verse1);
		Reference r2 = new Reference(book, chapter2, verse2);
		return getReferencesInclusive(r1, r2);
	}

	@Override
	public VerseList getVersesInclusive(Reference firstVerse, Reference lastVerse) {
		return getVersesExclusive(firstVerse,new Reference(lastVerse.getBookOfBible(), lastVerse.getChapter(), lastVerse.getVerse()+1));
	}

	@Override
	public VerseList getVersesExclusive(Reference firstVerse, Reference lastVerse) {
		// Implementation of this method provided by Chuck Cusack.
		// This is provided so you have an example to help you get started
		// with the other methods.

		// Make sure the references are in the correct order. If not, return empty verse list.
		if (firstVerse.compareTo(lastVerse) > 0) {
			return new VerseList(getVersion(), firstVerse + "-" + lastVerse);
		}
		// Return the portion of the TreeMap that contains the verses between
		// the first and the last, not including the last.
		SortedMap<Reference, Verse> s = theVerses.subMap(firstVerse, lastVerse);

		// Get the entries from the map so we can iterate through them.
		Set<Map.Entry<Reference, Verse>> mySet = s.entrySet();

		// We will store the resulting verses here. We copy the version from
		// this Bible and set the description to be the passage that was searched for.
		VerseList someVerses = new VerseList(getVersion(), firstVerse + "-" + lastVerse);

		// Iterate through the set and put the verses in the VerseList.
		for (Map.Entry<Reference, Verse> element : mySet) {
			Verse aVerse = element.getValue();
			someVerses.add(aVerse);
		}
		return someVerses;
	}

	@Override
	public VerseList getBook(BookOfBible book) {
		if (book == null) return new VerseList(version,"");
		
		Reference r1 = new Reference(book,1,1);
		Reference r2 = new Reference(BookOfBible.nextBook(book),1,1);
		return getVersesExclusive(r1, r2);
	}

	@Override
	public VerseList getChapter(BookOfBible book, int chapter) {
		if (book == null) return new VerseList(version,"");
		
		Reference r1 = new Reference(book, chapter, 1);
		Reference r2 = new Reference(book, chapter+1, 1);
		return getVersesExclusive(r1, r2);
	}

	@Override
	public VerseList getChapters(BookOfBible book, int chapter1, int chapter2) {
		if (chapter1 > chapter2) return new VerseList(version,book+" "+chapter1+"-"+chapter2);
		if (book == null) return new VerseList(version,"");
		
		Reference r1 = new Reference(book, chapter1, 1);
		Reference r2 = new Reference(book, chapter2+1, 1);
		return getVersesExclusive(r1, r2);
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		if (verse1 > verse2) return new VerseList(version, book+" "+chapter+":"+verse1+"-"+verse2);
		if (book == null) return new VerseList(version,"");
		
		Reference r1 = new Reference(book, chapter, verse1);
		Reference r2 = new Reference(book, chapter, verse2);
		return getVersesInclusive(r1, r2);
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		if (chapter1 > chapter2) return new VerseList(version,"");
		if (book == null) return new VerseList(version,"");
		
		Reference r1 = new Reference(book, chapter1, verse1);
		Reference r2 = new Reference(book, chapter2, verse2);
		return getVersesInclusive(r1, r2);
	}
}
