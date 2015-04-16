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
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bibleReader.BibleIO;
import bibleReader.model.Bible;
import bibleReader.model.BibleFactory;
import bibleReader.model.BookOfBible;
import bibleReader.model.Concordance;
import bibleReader.model.Reference;
import bibleReader.model.ReferenceList;
import bibleReader.model.VerseList;

/**
 * Tests for the concordance class. They aren't very precise, but should be good
 * enough.
 * 
 * @author Chuck Cusack, March, 2013
 */
public class Stage12ConcordanceTest {
	// We'll just run the tests on the ESV since it is the hardest version to
	// deal with.
	private static Concordance concordance;

	@BeforeClass
	public static void readFilesAndCreateConcordance() {
		File file = new File("esv.atv");
		VerseList verses = BibleIO.readBible(file);
		Bible bible = BibleFactory.createBible(verses);
		concordance = BibleFactory.createConcordance(bible);
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test(timeout = 500)
	public void testCorrectOrder() {
		ReferenceList refs = concordance.getReferencesContaining("eaten");
		// Are the first 4 correct?
		assertEquals(new Reference(BookOfBible.Genesis, 3, 11), refs.get(0));
		assertEquals(new Reference(BookOfBible.Genesis, 3, 17), refs.get(1));
		assertEquals(new Reference(BookOfBible.Genesis, 6, 21), refs.get(2));
		assertEquals(new Reference(BookOfBible.Genesis, 14, 24), refs.get(3));
		// Are the last 3 correct?
		assertEquals(new Reference(BookOfBible.Acts, 27, 38), refs.get(81));
		assertEquals(new Reference(BookOfBible.James, 5, 2), refs.get(82));
		assertEquals(new Reference(BookOfBible.Revelation, 10, 10), refs.get(83));
		// Good. Chances are the rest are correct.
	}

	@Test(timeout = 250)
	public void testGetReferenceContainingWithMultipleWords_SW() {
		// These don't work because it is searching for a single word and these
		// won't match a single word.
		ReferenceList results = concordance.getReferencesContaining("son of god");
		assertEquals(0, results.size());
		results = concordance.getReferencesContaining("three wise men");
		assertEquals(0, results.size());
	}

	@Test(timeout = 250)
	public void testGetReferenceContainingWithNoResults_SW() {
		ReferenceList results = concordance.getReferencesContaining("trinity");
		assertEquals(0, results.size());
		results = concordance.getReferencesContaining("neo");
		assertEquals(0, results.size());
		results = concordance.getReferencesContaining("cat");
		assertEquals(0, results.size());
		results = concordance.getReferencesContaining("reverend");
		assertEquals(0, results.size());
	}

	@Test(timeout = 250)
	public void testGetReferenceContainingWithOneResult_SW() {
		ReferenceList results = concordance.getReferencesContaining("Christians");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Acts, 11, 26), results.get(0));

		results = concordance.getReferencesContaining("grandmother");
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Timothy2, 1, 5), results.get(0));
	}

	@Test(timeout = 250)
	public void testGetReferenceContainingWithFewResults_SW() {
		ReferenceList results = concordance.getReferencesContaining("Melchizedek");
		assertEquals(10, results.size());
		assertEquals(new Reference(BookOfBible.Genesis, 14, 18), results.get(0));
		assertEquals(new Reference(BookOfBible.Psalms, 110, 4), results.get(1));

		results = concordance.getReferencesContaining("Christian");
		assertEquals(2, results.size());

	}

	@Test(timeout = 250)
	public void testGetReferencesContainingWithManyResults_SW() {
		// One that occurs 47 times, but change the case of the search string
		ReferenceList results = concordance.getReferencesContaining("righteousness");
		assertEquals(265, results.size());

		results = concordance.getReferencesContaining("righteous");
		assertEquals(265, results.size());

		results = concordance.getReferencesContaining("three");
		assertEquals(342, results.size());
	}

	@Test(timeout = 250)
	public void testExtremeSearches_SW() {
		// Empty string should return no results.
		ReferenceList results = concordance.getReferencesContaining("");
		assertEquals(0, results.size());

		results = concordance.getReferencesContaining("the");
		assertEquals(23753, results.size());
		results = concordance.getReferencesContaining("of");
		assertEquals(17161, results.size());
		results = concordance.getReferencesContaining("a");
		assertEquals(6728, results.size());
	}

	@Test(timeout = 500)
	public void testSingleWord() {
		assertEquals(84, concordance.getReferencesContaining("eaten").size());

		assertEquals(23753, concordance.getReferencesContaining("the").size());

		assertEquals(10, concordance.getReferencesContaining("Melchizedek").size());
	}

	@Test(timeout = 500)
	public void testWordsNotThere() {
		assertEquals(0, concordance.getReferencesContaining("monkey").size());

		assertEquals(0, concordance.getReferencesContaining("").size());

		assertEquals(0, concordance.getReferencesContaining(" ").size());

		// You can get some of these if you don't remember about words like
		// "John's" and/or you don't deal with them properly.
		assertEquals(0, concordance.getReferencesContaining("s").size());
	}

	@Test(timeout = 500)
	public void testMultipleWords_All() {
		ArrayList<String> wordList = new ArrayList<String>();
		wordList.add("land");
		wordList.add("of");
		wordList.add("the");
		wordList.add("free");
		assertEquals(2, concordance.getReferencesContainingAll(wordList).size());

		wordList.clear();
		wordList.add("son");
		wordList.add("god");
		wordList.add("man");
		assertEquals(40, concordance.getReferencesContainingAll(wordList).size());
	}

	@Test(timeout = 500)
	public void testSomeWordsNotThere_All() {
		// Some words there, some not.
		ArrayList<String> wordList1 = new ArrayList<String>();
		wordList1.add("god");
		wordList1.add("blah");
		wordList1.add("the");
		assertEquals(0, concordance.getReferencesContainingAll(wordList1).size());

		wordList1.clear();
		wordList1.add("blah");
		wordList1.add("god");
		wordList1.add("the");
		assertEquals(0, concordance.getReferencesContainingAll(wordList1).size());

		wordList1.clear();
		wordList1.add("the");
		wordList1.add("god");
		wordList1.add("blah");
		assertEquals(0, concordance.getReferencesContainingAll(wordList1).size());
	}

	@Test(timeout = 500)
	public void testCommonWords_All() {
		ArrayList<String> wordList = new ArrayList<String>();
		wordList.add("the");
		wordList.add("of");
		wordList.add("and");
		wordList.add("or");
		assertEquals(375, concordance.getReferencesContainingAll(wordList).size());
	}

	@Test(timeout = 500)
	public void testRepeatedWords_All() {
		ArrayList<String> wordList = new ArrayList<String>();
		wordList.add("the");
		wordList.add("THE");
		wordList.add("tHe");
		wordList.add("THe");
		wordList.add("the");
		wordList.add("the");
		wordList.add("the");
		assertEquals(23753, concordance.getReferencesContainingAll(wordList).size());
	}

	@Test(timeout = 500)
	public void testLongLists_All() {
		ArrayList<String> wordList = new ArrayList<String>();
		wordList.add("the");
		wordList.add("god");
		wordList.add("man");
		wordList.add("son");
		wordList.add("of");
		wordList.add("a");
		wordList.add("is");
		assertEquals(3, concordance.getReferencesContainingAll(wordList).size());
	}

	@Test(timeout = 250)
	public void testGetReferenceContainingWithNoResults_All() {
		ReferenceList results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("fig",
				"tree", "blossom", "earth")));
		assertEquals(0, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("earth", "fig", "tree",
				"blossom")));
		assertEquals(0, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("blah")));
		assertEquals(0, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("blah", "god")));
		assertEquals(0, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("god", "blah")));
		assertEquals(0, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("blah", "foo", "ferzle")));
		assertEquals(0, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("peace", "piece")));
		assertEquals(0, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("god", "gods", "godly")));
		assertEquals(0, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("gods", "god", "godly")));
		assertEquals(0, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("godly", "gods", "god")));
		assertEquals(0, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("reverend")));
		assertEquals(0, results.size());
	}

	@Test(timeout = 250)
	public void testGetReferenceContainingWithOneResult_All() {
		ReferenceList results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("trouble",
				"very", "soon")));
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Judges, 11, 35), results.get(0));

		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("within", "month")));
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Ezra, 10, 9), results.get(0));

		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("trust", "in", "the",
				"lord", "with", "all", "your", "heart")));
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Proverbs, 3, 5), results.get(0));

		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("yesterday", "was")));
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Samuel1, 20, 27), results.get(0));

		results = concordance
				.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("pride", "before", "fall")));
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Proverbs, 16, 18), results.get(0));

		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("grandmother")));
		assertEquals(1, results.size());
		assertEquals(new Reference(BookOfBible.Timothy2, 1, 5), results.get(0));
	}

	@Test(timeout = 250)
	public void testGetReferenceContainingWithFewResults_All() {
		ReferenceList results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList(
				"Melchizedek", "king")));
		assertEquals(2, results.size());
		assertEquals(new Reference(BookOfBible.Genesis, 14, 18), results.get(0));
		assertEquals(new Reference(BookOfBible.Hebrews, 7, 1), results.get(1));

		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("king", "Melchizedek")));
		assertEquals(2, results.size());
		assertEquals(new Reference(BookOfBible.Genesis, 14, 18), results.get(0));
		assertEquals(new Reference(BookOfBible.Hebrews, 7, 1), results.get(1));

		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("CHRISTIAN")));
		assertEquals(2, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("pride", "fall")));
		assertEquals(3, results.size());

	}

	@Test(timeout = 250)
	public void testGetReferencesContainingWithManyResults_All() {
		// One that occurs 47 times, but change the case of the search string
		ReferenceList results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays
				.asList("righteousness")));
		assertEquals(265, results.size());

		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("righteous")));
		assertEquals(265, results.size());

		results = concordance
				.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("the", "son", "of", "god")));
		assertEquals(172, results.size());
		results = concordance
				.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("god", "of", "the", "son")));
		assertEquals(172, results.size());
		results = concordance
				.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("the", "god", "of", "son")));
		assertEquals(172, results.size());

	}

	@Test(timeout = 250)
	public void testSearchesDontModifyConcordance_All() {
		ReferenceList results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("king")));
		assertEquals(1936, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("son")));
		assertEquals(1779, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("king", "son")));
		assertEquals(273, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("son", "king")));
		assertEquals(273, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("son")));
		assertEquals(1779, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("king")));
		assertEquals(1936, results.size());
	}

	@Test(timeout = 250)
	public void testEmptyString_All() {
		// Empty string should return no results.
		ReferenceList results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("")));
		assertEquals(0, results.size());
	}

	@Test(timeout = 250)
	public void testExtremeSearches1_All() {
		ReferenceList results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("the")));
		assertEquals(23753, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("of")));
		assertEquals(17161, results.size());
		results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("a")));
		assertEquals(6728, results.size());
	}

	@Test(timeout = 250)
	public void testExtremeSearches2_All() {
		ReferenceList results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("the", "of",
				"and", "or")));
		assertEquals(375, results.size());
	}

	@Test(timeout = 250)
	public void testExtremeSearches3_All() {
		// If you are clever, this one should take no longer than searching for
		// "the".
		// If you are not clever, it will take too long.
		ReferenceList results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("the",
				"tHE", "THE", "ThE", "THe")));
		assertEquals(23753, results.size());
	}

	@Test(timeout = 250)
	public void testExtremeSearches4_All() {
		// This is the toughest one.
		ReferenceList results = concordance.getReferencesContainingAll(new ArrayList<String>(Arrays.asList("the", "of",
				"and")));
		assertEquals(11351, results.size());
	}
}