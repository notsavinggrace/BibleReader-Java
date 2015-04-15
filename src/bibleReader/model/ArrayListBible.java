package bibleReader.model;

import java.util.ArrayList;

/**
 * A class that stores a version of the Bible.
 *
 * @author Chuck Cusack (Provided the interface). Modified February 9, 2015.
 * @author Nathan Gingrich (provided the implementation). Modified February 11, 2015.
 * @author Matt Blessed (Provided the implementation). Modified March 2, 2015.
 */
public class ArrayListBible implements Bible {
	// The Fields
	private final String version;
	private final String title;
	private final ArrayList<Verse> theVerses;

	/**
	 * Create a new Bible with the given verses.
	 *
	 * @param verses
	 *            All of the verses of this version of the Bible.
	 */
	public ArrayListBible(VerseList verses) {
		theVerses = new ArrayList<Verse>();
		for (Verse v : verses) {
			theVerses.add(v);
		}
		version = verses.getVersion();
		title = verses.getDescription();
	}

	@Override
	public int getNumberOfVerses() {
		return theVerses.size();
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean isValid(Reference ref) {
		for (Verse v : theVerses) {
			if (ref.equals(v.getReference())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getVerseText(Reference r) {
		for (Verse v : theVerses) {
			if (r.equals(v.getReference())) {
				return v.getText();
			}
		}
		return "";
	}

	@Override
	public Verse getVerse(Reference r) {
		for (Verse v : theVerses) {
			if (r.equals(v.getReference())) {
				return v;
			}
		}
		return null;
	}

	@Override
	public Verse getVerse(BookOfBible book, int chapter, int verse) {
		Reference r = new Reference(book, chapter, verse);
		for (Verse v : theVerses) {
			if (v.getReference().equals(r)) {
				return v;
			}
		}
		return null;
	}

	@Override
	public VerseList getAllVerses() {
		return new VerseList(version, title, theVerses);
	}

	@Override
	public VerseList getVersesContaining(String phrase) {
		VerseList vl = new VerseList(version, phrase);
		if (phrase.equals("") || phrase == null) {
			return vl;
		}

		phrase = phrase.toLowerCase();
		for (Verse v : theVerses) {
			if (v.getText().toLowerCase().contains(phrase)) {
				vl.add(v);
			}
		}
		return vl;
	}

	@Override
	public ReferenceList getReferencesContaining(String phrase) {
		ReferenceList rl = new ReferenceList();
		if (phrase.equals("") || phrase == null) {
			return rl;
		}
		phrase = phrase.toLowerCase();
		for (Verse v : theVerses) {
			if (v.getText().toLowerCase().contains(phrase)) {
				rl.add(v.getReference());
			}
		}
		return rl;
	}

	// ---------------------------------------------------------------------------------------------
	// The following part of this class should be implemented for Stage 7.
	//
	// HINT: Do not reinvent the wheel. Some of these methods can be implemented
	// by looking up
	// one or two things and calling another method to do the bulk of the work.
	// ---------------------------------------------------------------------------------------------

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
		// Set previous verse, iterate through the verses until the chapter of the verse is greater than
		// the chapter we are looking for. Once we have found the verse chapter that is one greater than the 
		// one we are looking for, return the previous verse's verse number.
		
		if (book == null) {
			return 0;
		}
		
		Verse pV = null;
		boolean inBook = false;
		for (int i = 0; i < theVerses.size(); i++) {
			Verse v = theVerses.get(i);
			if (v.getReference().getBookOfBible().equals(book)) {
				inBook = true;
				if (v.getReference().getChapter() > chapter) {
					return pV.getReference().getVerse();
				}
			}
			else {
				// If just passed the book, use the last verse in the book
				if (inBook) {
					return pV.getReference().getVerse();
				}
			}
			pV = v;
		}
		// Just in case for last book of the bible
		return pV.getReference().getVerse();
	}

	@Override
	public int getLastChapterNumber(BookOfBible book) {
		// Gets last verse, returns its chapter number
		if (book == null) {
			return 0;
		}
		
		Verse pV = null;
		boolean inBook = false;
		for (int i = 0; i < theVerses.size(); i++) {
			Verse v = theVerses.get(i);
			if (v.getReference().getBookOfBible().equals(book)) {
				inBook = true;
			}
			else {
				if (inBook) {
					// Just passed book.
					// Use last verse's chapter number
					return pV.getReference().getChapter();
				}
			}
			pV = v;
		}
		// Just in case for last book of the bible
		return pV.getReference().getChapter();
	}

	@Override
	public ReferenceList getReferencesInclusive(Reference firstVerse, Reference lastVerse) {
		// Homework 8 solution
		
		ReferenceList results = new ReferenceList();
		//return results;
		
		if (firstVerse.compareTo(lastVerse) > 0) {
			return results;
		}
		
		int index1 = 0;
		// If the first condition is true it will not read the second condition, thus not resulting in a null pointer exception
		while (index1 <= theVerses.size()-1 && !theVerses.get(index1).getReference().equals(firstVerse)) {
			index1++;
		}
		if (index1 == theVerses.size()) return results;
		
		int index2 = index1;
		// If the first condition is true it will not read the second condition, thus not resulting in a null pointer exception
		while (index2 <= theVerses.size()-1 && !theVerses.get(index2).getReference().equals(lastVerse) ) {
			index2++;
		}
		if (index2 == theVerses.size()) return results;
		
		for (int i = index1; i < theVerses.size(); i++) {
			if (i <= index2) {
				results.add(theVerses.get(i).getReference());
			}
		}
		return results;
	}
		/*
		ReferenceList refList = new ReferenceList();
		
		if (firstVerse.compareTo(lastVerse) > 0) {
			return refList;
		}
		
		// Not sure if we should check if the references are both valid.
		// Seems a bit complex of an operation.
		if (!isValid(firstVerse) || !isValid(lastVerse)) {
			return refList;
		}
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().equals(firstVerse)) {
				insideRange = true;
			}
			else if (v.getReference().equals(lastVerse)) {
				insideRange = false;
				refList.add(v.getReference()); // Inclusive of last verse
				return refList;
			}
			
			if (insideRange) {
				refList.add(v.getReference());
			}
		}
		
		return refList;
		*/
	//}

	@Override
	public ReferenceList getReferencesExclusive(Reference firstVerse, Reference lastVerse) {
		ReferenceList refList = new ReferenceList();
		if (firstVerse.compareTo(lastVerse) > 0) {
			return refList;
		}
		
		// Not sure if we should check if the references are both valid.
		// Seems a bit complex of an operation.
		if (!isValid(firstVerse) || !isValid(lastVerse)) {
			return refList;
		}
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().equals(firstVerse)) {
				insideRange = true;
			}
			else if (v.getReference().equals(lastVerse)) {
				insideRange = false;
				return refList;
			}
			
			if (insideRange) {
				refList.add(v.getReference());
			}
		}
		return refList;
	}

	@Override
	public ReferenceList getReferencesForBook(BookOfBible book) {
		ReferenceList refList = new ReferenceList();
		
		if (book == null) {
			return refList;
		}
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().getBookOfBible().equals(book)) {
				insideRange = true;
			}
			else {
				if (insideRange) {
					insideRange = false;
					return refList;
				}
			}
			if (insideRange) {
				refList.add(v.getReference());
			}
		}
		return refList;
	}

	@Override
	public ReferenceList getReferencesForChapter(BookOfBible book, int chapter) {
		ReferenceList refList = new ReferenceList();
		
		if (book == null) {
			return refList;
		}
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().getBookOfBible().equals(book) && v.getReference().getChapter() == chapter) {
				insideRange = true;
			}
			else if (v.getReference().getChapter() != chapter && insideRange) {
				insideRange = false;
				return refList;
			}
			
			if (insideRange) {
				refList.add(v.getReference());
			}
		}
		
		return refList;
	}

	@Override
	public ReferenceList getReferencesForChapters(BookOfBible book, int chapter1, int chapter2) {
		ReferenceList refList = new ReferenceList();
		
		if (chapter1 > chapter2) {
			return refList;
		}
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().getBookOfBible().equals(book) && v.getReference().getChapter() >= chapter1 && v.getReference().getChapter() <= chapter2) {
				insideRange = true;
			}
			else if ((v.getReference().getChapter() < chapter1 || v.getReference().getChapter() > chapter2) && insideRange) {
				insideRange = false;
				return refList;
			}
			
			if (insideRange) {
				refList.add(v.getReference());
			}
		}
		
		return refList;
	}

	@Override
	public ReferenceList getReferencesForPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		Reference firstRef = new Reference(book, chapter, verse1);
		Reference secondRef = new Reference(book, chapter, verse2);
		
		ReferenceList refList = new ReferenceList();
		
		if (verse1 > verse2) {
			return refList;
		}
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			
			if (v.getReference().equals(firstRef)) {
				insideRange = true;
			}
			else if (v.getReference().equals(secondRef)) {
				insideRange = false;
				refList.add(v.getReference()); // inclusive of last reference
				return refList;
			}
			
			if (insideRange) {
				refList.add(v.getReference());
			}
		}
		
		return refList;
	}

	@Override
	public ReferenceList getReferencesForPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		Reference firstRef = new Reference(book, chapter1, verse1);
		Reference secondRef = new Reference(book, chapter2, verse2);
		
		ReferenceList refList = new ReferenceList();
		
		if (firstRef.compareTo(secondRef) > 0) {
			return refList;
		}
		
		if (!isValid(firstRef) || !isValid(secondRef)) {
			return refList;
		}
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().equals(firstRef)) {
				insideRange = true;
			}
			else if (v.getReference().equals(secondRef)) {
				insideRange = false;
				refList.add(v.getReference());
				return refList;
			}
			
			if (insideRange) {
				refList.add(v.getReference());
			}
		}
		
		return refList;
	}

	@Override
	public VerseList getVersesInclusive(Reference firstVerse, Reference lastVerse) {
        VerseList vList = new VerseList(this.version,firstVerse+" to "+lastVerse);
        
        if (firstVerse.compareTo(lastVerse) > 0) {
            return vList;
        }
        
        // Not sure if we should check if the references are both valid.
        // Seems a bit complex of an operation.
        if (!isValid(firstVerse) || !isValid(lastVerse)) {
            return vList;
        }
        
        boolean insideRange = false;
        for (Verse v : theVerses) {
            if (v.getReference().equals(firstVerse)) {
                insideRange = true;
            }
            else if (v.getReference().equals(lastVerse)) {
                insideRange = false;
                vList.add(v); // Inclusive of last verse
                return vList;
            }
            
            if (insideRange) {
            	vList.add(v);
            }
        }
        
        return vList;
	}

	@Override
	public VerseList getVersesExclusive(Reference firstVerse, Reference lastVerse) {
		VerseList vList = new VerseList(this.version,firstVerse+" to "+lastVerse);
		
		if (firstVerse.compareTo(lastVerse) > 0) {
			return vList;
		}
		
		// Not sure if we should check if the references are both valid.
		// Seems a bit complex of an operation.
		if (!isValid(firstVerse) || !isValid(lastVerse)) {
			return vList;
		}
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().equals(firstVerse)) {
				insideRange = true;
			}
			else if (v.getReference().equals(lastVerse)) {
				insideRange = false;
				return vList;
			}
			
			if (insideRange) {
				vList.add(v);
			}
		}
		return vList;
	}

	@Override
	public VerseList getBook(BookOfBible book) {
		if (book == null) {
			return new VerseList(this.version,"");
		}
		
		VerseList vList = new VerseList(this.version,book.toString());
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().getBookOfBible().equals(book)) {
				insideRange = true;
			}
			else {
				if (insideRange) {
					insideRange = false;
					return vList;
				}
			}
			if (insideRange) {
				vList.add(v);
			}
		}
		
		return vList;
	}

	@Override
	public VerseList getChapter(BookOfBible book, int chapter) {
		if (book == null) {
			return new VerseList(this.version,"");
		}
		
		VerseList vList = new VerseList(this.version,book+" "+chapter);
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().getBookOfBible().equals(book) && v.getReference().getChapter() == chapter) {
				insideRange = true;
			}
			else if (v.getReference().getChapter() != chapter && insideRange) {
				insideRange = false;
				return vList;
			}
			
			if (insideRange) {
				vList.add(v);
			}
		}
		
		return vList;
	}

	@Override
	public VerseList getChapters(BookOfBible book, int chapter1, int chapter2) {
		if (book == null) {
			return new VerseList(this.version,"");
		}
		
		VerseList vList = new VerseList(this.version,book+" "+chapter1+"-"+chapter2);
		
		if (chapter1 > chapter2) {
			return vList;
		}
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().getBookOfBible().equals(book) && v.getReference().getChapter() >= chapter1 && v.getReference().getChapter() <= chapter2) {
				insideRange = true;
			}
			else if ((v.getReference().getChapter() < chapter1 || v.getReference().getChapter() > chapter2) && insideRange) {
				insideRange = false;
				return vList;
			}
			
			if (insideRange) {
				vList.add(v);
			}
		}
		
		return vList;
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		Reference firstRef = new Reference(book, chapter, verse1);
		Reference secondRef = new Reference(book, chapter, verse2);
		
		VerseList vList = new VerseList(this.version,book+" "+chapter+":"+verse1+"-"+verse2);
		
		if (firstRef.compareTo(secondRef) > 0) {
			return vList;
		}
		
		if (!isValid(firstRef) || !isValid(secondRef)) {
			return vList;
		}
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().equals(firstRef)) {
				insideRange = true;
			}
			else if (v.getReference().equals(secondRef)) {
				insideRange = false;
				vList.add(v); // inclusive of last reference
				return vList;
			}
			
			if (insideRange) {
				vList.add(v);
			}
		}
		
		return vList;
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		Reference firstRef = new Reference(book, chapter1, verse1);
		Reference secondRef = new Reference(book, chapter2, verse2);
		
		VerseList vList = new VerseList(this.version,book+" "+chapter1+":"+verse1+" - "+chapter2+":"+verse2);
		
		if (firstRef.compareTo(secondRef) > 0) {
			return vList;
		}
		
		if (!isValid(firstRef) || !isValid(secondRef)) {
			return vList;
		}
		
		boolean insideRange = false;
		for (Verse v : theVerses) {
			if (v.getReference().equals(firstRef)) {
				insideRange = true;
			}
			else if (v.getReference().equals(secondRef)) {
				insideRange = false;
				vList.add(v);
				return vList;
			}
			
			if (insideRange) {
				vList.add(v);
			}
		}
		
		return vList;
	}
}
