package bibleReader.tests;

// If you organize imports, the following import might be removed and you will
// not be able to find certain methods. If you can't find something, copy the
// commented import statement below, paste a copy, and remove the comments.
// Keep this commented one in case you organize imports multiple times.
//
// import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bibleReader.BibleIO;
import bibleReader.model.Bible;
import bibleReader.model.BibleFactory;
import bibleReader.model.BibleReaderModel;
import bibleReader.model.BookOfBible;
import bibleReader.model.MultiBibleModel;
import bibleReader.model.Reference;
import bibleReader.model.ReferenceList;
import bibleReader.model.TreeMapBible;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * Tests for the single-word exact searching.
 * 
 * @author Chuck Cusack, March, 2013
 */
public class Stage12SearchTest {
	private static VerseList[]	verses;
	private static MultiBibleModel		model;
	private static String[]		versions	= new String[] { "kjv.atv", "asv.xmv", "esv.atv" };

	@BeforeClass
	public static void readFiles() {
		
		verses = new VerseList[versions.length];
		for (int i = 0; i < versions.length; i++) {
			File file = new File(versions[i]);
			verses[i] = BibleIO.readBible(file);
		}
		
		// We create the model once for all searches.  That way if the lists in the
		// Concordance class are getting messed up we can catch it.
		// Also because creating the model now takes a few seconds.
		
		model = new BibleReaderModel();
		
		for (int i = 0; i < versions.length; i++) {
			// Make a copy of the VerseList
			VerseList copyOfVerseList = new VerseList(verses[i].getVersion(), verses[i].getDescription(),
					new ArrayList<Verse>(verses[i]));
			Bible testBible = BibleFactory.createBible(copyOfVerseList);
			model.addBible(testBible);
		}
		
		
	}

	@Before
	public void setUp() throws Exception {
	}

	//---------------------------------------------------------------------------------------------------
	// Tests for searching for a single word at a time
	//---------------------------------------------------------------------------------------------------
	@Test(timeout = 250)
	public void testGetReferenceContainingWithMultipleWords_SW() {
		// These don't work because it is searching for a single word and these
		// won't match a single word.
		ReferenceList results = model.getReferencesContainingWord("son of god");
		assertEquals(0, results.size());
		results = model.getReferencesContainingWord("three wise men");
		assertEquals(0, results.size());
	}	
	
	@Test(timeout = 250)
	public void testGetReferenceContainingWithNoResults_SW() {
		ReferenceList results = model.getReferencesContainingWord("trinity");
		assertEquals(0, results.size());
		results = model.getReferencesContainingWord("neo");
		assertEquals(0, results.size());
		results = model.getReferencesContainingWord("cat");
		assertEquals(0, results.size());
	}

	@Test(timeout = 250)
	public void testGetReferenceContainingWithOneResult_SW() {
		ReferenceList results = model.getReferencesContainingWord("Christians");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Acts, 11, 26), results.get(0));

		results = model.getReferencesContainingWord("reverend");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Psalms, 111, 9), results.get(0));

		results = model.getReferencesContainingWord("grandmother");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Timothy2, 1, 5), results.get(0));
	}

	@Test(timeout = 250)
	public void testGetReferenceContainingWithFewResults_SW() {
		ReferenceList results = model.getReferencesContainingWord("Melchizedek");
		assertEquals(10, results.size());
		assertEquals(new Reference(BookOfBible.Genesis, 14, 18), results.get(0));
		assertEquals(new Reference(BookOfBible.Psalms, 110, 4), results.get(1));

		results = model.getReferencesContainingWord("Christian");
		assertEquals(2, results.size());

	}

	@Test(timeout = 250)
	public void testGetReferencesContainingWithManyResults_SW() {
		// One that occurs 47 times, but change the case of the search string
		ReferenceList results = model.getReferencesContainingWord("righteousness");
		assertEquals(317, results.size());

		results = model.getReferencesContainingWord("righteous");
		assertEquals(311, results.size());

		results = model.getReferencesContainingWord("three");
		assertEquals(448, results.size());
	}

	@Test(timeout = 250)
	public void testExtremeSearches_SW() {
		// Empty string should return no results.
		System.out.println("start test");
		ReferenceList results = model.getReferencesContainingWord("");
		assertEquals(0, results.size());

		results = model.getReferencesContainingWord("the");
		assertEquals(25245, results.size());
		results = model.getReferencesContainingWord("of");
		assertEquals(19762, results.size());
		results = model.getReferencesContainingWord("a");
		assertEquals(8194, results.size());
	}
	//---------------------------------------------------------------------------------------------------
	// Tests for searching for more than one word at a time
	//---------------------------------------------------------------------------------------------------
	@Test(timeout = 250)
	public void testGetReferenceContainingWithNoResults_MW() {
		ReferenceList results = model.getReferencesContainingAllWords("fig tree blossom earth");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWords("earth tree fig blossom");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWords("blah");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWords("blah god");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWords("god blah");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWords("blah foo ferzle");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWords("peace piece");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWords("god gods godly");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWords("gods god godly");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWords("godly gods god");
		assertEquals(0, results.size());
	}

	@Test(timeout = 250)
	public void testGetReferenceContainingWithOneResult_MW() {
		ReferenceList results = model.getReferencesContainingAllWords("trouble very soon");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Judges, 11, 35), results.get(0));

		results = model.getReferencesContainingAllWords("within month");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Ezra, 10,9), results.get(0));

		results = model.getReferencesContainingAllWords("trust in the lord with all your heart");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Proverbs, 3,5), results.get(0));
		
		results = model.getReferencesContainingAllWords("yesterday was");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Samuel1, 20,27), results.get(0));
		
		results = model.getReferencesContainingAllWords("pride before fall");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Proverbs, 16,18), results.get(0));

		results = model.getReferencesContainingAllWords("grandmother");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Timothy2, 1, 5), results.get(0));
		
		results = model.getReferencesContainingAllWords("reverend");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Psalms, 111, 9), results.get(0));
	}

	@Test(timeout = 250)
	public void testGetReferenceContainingWithFewResults_MW() {
		ReferenceList results = model.getReferencesContainingAllWords("Melchizedek king");
		assertEquals(2, results.size());
		assertEquals(new Reference(BookOfBible.Genesis, 14, 18), results.get(0));
		assertEquals(new Reference(BookOfBible.Hebrews, 7, 1), results.get(1));

		results = model.getReferencesContainingAllWords("king Melchizedek");
		assertEquals(2, results.size());
		assertEquals(new Reference(BookOfBible.Genesis, 14, 18), results.get(0));
		assertEquals(new Reference(BookOfBible.Hebrews, 7, 1), results.get(1));

		results = model.getReferencesContainingAllWords("CHRISTIAN");
		assertEquals(2, results.size());
		results = model.getReferencesContainingAllWords("pride fall");
		assertEquals(6, results.size());

	}

	@Test(timeout = 250)
	public void testGetReferencesContainingWithManyResults_MW() {
		// One that occurs 47 times, but change the case of the search string
		ReferenceList results = model.getReferencesContainingAllWords("righteousness");
		assertEquals(317, results.size());

		results = model.getReferencesContainingAllWords("righteous");
		assertEquals(311, results.size());

		results = model.getReferencesContainingAllWords("the son of god");
		assertEquals(184, results.size());
		results = model.getReferencesContainingAllWords("god of the son");
		assertEquals(184, results.size());
		results = model.getReferencesContainingAllWords("the god of son");
		assertEquals(184, results.size());

		// make sure the searches aren't messing up the lists in the concordance.
		results = model.getReferencesContainingAllWords("three");
		assertEquals(448, results.size());
		results = model.getReferencesContainingAllWords("king");
		assertEquals(1952, results.size());
		results = model.getReferencesContainingAllWords("son");
		assertEquals(1838, results.size());
		results = model.getReferencesContainingAllWords("king son");
		assertEquals(278, results.size());
		results = model.getReferencesContainingAllWords("son king");
		assertEquals(278, results.size());
		results = model.getReferencesContainingAllWords("son");
		assertEquals(1838, results.size());
		results = model.getReferencesContainingAllWords("king");
		assertEquals(1952, results.size());
	}

	@Test(timeout = 250)
	public void testHardCases_MW() {
		// Can you deal with commas?
		ReferenceList results = model.getReferencesContainingAllWords("and when jesus was baptized, immediately");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Matthew, 3,16), results.get(0));
		// TODO add a few more.
	}

	@Test(timeout = 250)
	public void testExtremeSearches1_MW() {
		// Empty string should return no results.
		ReferenceList results = model.getReferencesContainingAllWords("");
		assertEquals(0, results.size());

		results = model.getReferencesContainingAllWords("the");
		assertEquals(25245, results.size());
		results = model.getReferencesContainingAllWords("of");
		assertEquals(19762, results.size());
		results = model.getReferencesContainingAllWords("a");
		assertEquals(8194, results.size());
	}
	@Test(timeout = 250)
	public void testExtremeSearches2_MW() {
		ReferenceList results = model.getReferencesContainingAllWords("the of and or");
		assertEquals(493, results.size());
	}
	@Test(timeout = 250)
	public void testExtremeSearches3_MW() {
		// If you are clever, this one should take no longer than searching for "the".
		// If you are not clever, it will take too long.
		ReferenceList results = model.getReferencesContainingAllWords("the tHE THE ThE THe");
		assertEquals(25245, results.size());
	}
	@Test(timeout = 250)
	public void testExtremeSearches4_MW() {
		// This is the toughest one.  
		ReferenceList results = model.getReferencesContainingAllWords("the of and");
		assertEquals(14709, results.size());
	}

	//---------------------------------------------------------------------------------------------------
	// Tests for searching for multiple words and phrases (in double quotes).
	//---------------------------------------------------------------------------------------------------
	@Test(timeout = 250)
	public void testWithNoResults_Phrases() {
		ReferenceList results = model.getReferencesContainingAllWordsAndPhrases("\"do unto others\"");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("\"blah\"");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("\"three wise men\"");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("\"pride comes before a fall\"");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("\"the lion shall lie down with the lamb\"");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("\"Jesus slept\"");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("\"john the baptist\" \"kingdom of heaven\" \"greater than he\" \"among those\" \"I say to you\" \"hath not\"");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("godly gods god");
		assertEquals(0, results.size());
	}

	@Test(timeout = 250)
	public void testWithOneResult_Phrases() {
		
		ReferenceList results = model.getReferencesContainingAllWordsAndPhrases("\"though the fig tree should not blossom\"");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Habakkuk, 3, 17), results.get(0));
		
		results = model.getReferencesContainingAllWordsAndPhrases("\"jesus said\" \"to him\"  \"the sons\"");
		System.out.println(results);
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Matthew, 17, 26), results.get(0));
		
		results = model.getReferencesContainingAllWordsAndPhrases("\"though the fig tree should not blossom\"");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Habakkuk, 3, 17), results.get(0));

		// Out of context (the LOVE OF money is the root of all evil).
		results = model.getReferencesContainingAllWordsAndPhrases("\"money is the root of all evil\"");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Timothy1, 6, 10), results.get(0));

		results = model.getReferencesContainingAllWordsAndPhrases("\"trust in the lord with all your heart\"");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Proverbs, 3, 5), results.get(0));
		
		results = model.getReferencesContainingAllWordsAndPhrases("\"jesus wept\"");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.John, 11, 35), results.get(0));

		results = model.getReferencesContainingAllWordsAndPhrases("\"john the baptist\" \"kingdom of heaven\" \"greater than he\" \"among those\" \"I say to you\"");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Matthew, 11, 11), results.get(0));

	}

	@Test(timeout = 250)
	public void testWithFewResults_Phrases() {
		ReferenceList results = model.getReferencesContainingAllWordsAndPhrases("\"son of god\" \"cried out\"");
		assertEquals(4, results.size());
		assertEquals(new Reference(BookOfBible.Matthew, 8, 29), results.get(0));
		assertEquals(new Reference(BookOfBible.Mark, 3, 11), results.get(1));
		assertEquals(new Reference(BookOfBible.Mark, 15, 39), results.get(2));
		assertEquals(new Reference(BookOfBible.Luke, 8, 28), results.get(3));

		// Tricky case--when the quotes are right next to each other.
		results = model.getReferencesContainingAllWordsAndPhrases("\"son of god\"\"cried out\"");
		assertEquals(4, results.size());
		assertEquals(new Reference(BookOfBible.Matthew, 8, 29), results.get(0));
		assertEquals(new Reference(BookOfBible.Mark, 3, 11), results.get(1));
		assertEquals(new Reference(BookOfBible.Mark, 15, 39), results.get(2));
		assertEquals(new Reference(BookOfBible.Luke, 8, 28), results.get(3));

		// Even worse--spaces at the beginning and end
		results = model.getReferencesContainingAllWordsAndPhrases("    \"son of god\"\"cried out\"   ");
		assertEquals(4, results.size());
		assertEquals(new Reference(BookOfBible.Matthew, 8, 29), results.get(0));
		assertEquals(new Reference(BookOfBible.Mark, 3, 11), results.get(1));
		assertEquals(new Reference(BookOfBible.Mark, 15, 39), results.get(2));
		assertEquals(new Reference(BookOfBible.Luke, 8, 28), results.get(3));

		results = model.getReferencesContainingAllWordsAndPhrases("\"son of god\" jesus");
		assertEquals(16, results.size());
	}

	@Test(timeout = 250)
	public void testWithManyResults_Phrases() {
		ReferenceList results = model.getReferencesContainingAllWordsAndPhrases("righteousness");
		assertEquals(317, results.size());

		// without quotes
		results = model.getReferencesContainingAllWordsAndPhrases("righteous one");
		assertEquals(34, results.size());
		// with quotes
		results = model.getReferencesContainingAllWordsAndPhrases("\"righteous one\"");
		assertEquals(9, results.size());

		// These are repeats from the MultipleWord tests, but since this is calling
		// a different method it doesn't hurt to try them again.
		results = model.getReferencesContainingAllWordsAndPhrases("the son of god");
		assertEquals(184, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("god of the son");
		assertEquals(184, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("the god of son");
		assertEquals(184, results.size());

		// make sure the searches aren't messing up the lists in the concordance.
		results = model.getReferencesContainingAllWordsAndPhrases("three");
		assertEquals(448, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("king");
		assertEquals(1952, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("son");
		assertEquals(1838, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("king son");
		assertEquals(278, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("son king");
		assertEquals(278, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("son");
		assertEquals(1838, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("king");
		assertEquals(1952, results.size());
	}

	@Test(timeout = 500)
	public void testExtremeSearches_Phrases() {
		ReferenceList results = model.getReferencesContainingAllWordsAndPhrases("\"and he said\"");
		assertEquals(627, results.size());

		results = model.getReferencesContainingAllWordsAndPhrases("\"the of and or\"");
		assertEquals(0, results.size());
	}

	@Test(timeout = 250)
	public void testTrickySearches_Phrases() {
		// Empty double quotes.
		ReferenceList results = model.getReferencesContainingAllWordsAndPhrases("\"\"");
		assertEquals(0, results.size());
		// space in quotes.
		results = model.getReferencesContainingAllWordsAndPhrases("\" \"");
		assertEquals(0, results.size());
		
		// Can you deal with commas inside quotes?
		// This should find one result
		results = model.getReferencesContainingAllWordsAndPhrases("\"and when jesus was baptized, immediately\"");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Matthew, 3, 16), results.get(0));

		// This should also find one result.
		results = model.getReferencesContainingAllWordsAndPhrases("and when jesus was baptized immediately");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Matthew, 3, 16), results.get(0));

		// But this one should NOT find any.
		results = model.getReferencesContainingAllWordsAndPhrases("\"and when jesus was baptized immediately\"");
		assertEquals(0, results.size());

		// Another one with several variations.
		results = model.getReferencesContainingAllWordsAndPhrases("\"who are you, my son?\"");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Genesis, 27, 18), results.get(0));

		results = model.getReferencesContainingAllWordsAndPhrases("who are you, my son?");
		assertEquals(3, results.size());
		assertEquals(new Reference(BookOfBible.Genesis, 27, 18), results.get(0));
		assertEquals(new Reference(BookOfBible.Jeremiah, 29, 21), results.get(1));
		assertEquals(new Reference(BookOfBible.Hebrews, 5, 5), results.get(2));

		results = model.getReferencesContainingAllWordsAndPhrases("who are you my son");
		assertEquals(3, results.size());
		assertEquals(new Reference(BookOfBible.Genesis, 27, 18), results.get(0));
		assertEquals(new Reference(BookOfBible.Jeremiah, 29, 21), results.get(1));
		assertEquals(new Reference(BookOfBible.Hebrews, 5, 5), results.get(2));
	}

	@Test(timeout = 250)
	public void testOddNumberOfQuotes_Phrases() {
		// Just a single quote.
		ReferenceList results = model.getReferencesContainingAllWordsAndPhrases("\"");
		assertEquals(0, results.size());
		// Three quotes, without and with spaces.
		results = model.getReferencesContainingAllWordsAndPhrases("\"\"\"");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("\" \" \"");
		assertEquals(0, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("\"i am the one\" \"who");
		assertEquals(5, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("\"i am the one\" who");
		assertEquals(5, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("who \"i am the one\"");
		assertEquals(5, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("who am i");
		assertEquals(176, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("who am i\"");
		assertEquals(176, results.size());
		results = model.getReferencesContainingAllWordsAndPhrases("\"who am i");
		assertEquals(176, results.size());
	}
}
