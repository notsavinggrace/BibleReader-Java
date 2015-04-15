package bibleReader.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
//import static org.junit.Assert.*;


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
import bibleReader.model.Reference;
import bibleReader.model.ReferenceList;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * Test the getReferenceForPassageTest method in MultiBibleModel.
 * Assume BibleIO works and KJV.atv is correct.
 * The KJV bible is used for all the tests.
 *
 * @author Nathan Gingrich, Matt Blessed
 * @modified February 25, 2013
 *
 */
public class ReferenceForPassageTest {
	private static VerseList	versesFromFile;
	private BibleReaderModel	model;
	private ReferenceList results;

	@BeforeClass
	public static void readFile() {
		// Our tests will be based on the KJV version for now.
		File file = new File("kjv.atv");
		// We read the file here so it isn't done before every test.
		versesFromFile = BibleIO.readBible(file);
	}

	@Before
	public void setUp() throws Exception {
		// Make a shallow copy of the verses.
		ArrayList<Verse> copyOfList = new ArrayList<Verse>(versesFromFile);
		// Now make a copy of the VerseList
		VerseList copyOfVerseList = new VerseList(versesFromFile.getVersion(), versesFromFile.getDescription(),
				copyOfList);

		Bible testBible = BibleFactory.createBible(copyOfVerseList);
		model = new BibleReaderModel();
		model.addBible(testBible);
	}

	/**
	 * These tests contain chapters/verses out of order or
	 * with invalid syntax, and should all have invalid results.
	 *
	 * This test assumes getReferencesForPassage() returns an
	 * empty list if no valid results are found.
	 */
	@Test
	public void testInvalidResultsInvalidSyntax() {
		results = model.getReferencesForPassage("1Tim 3-2");
		assertEquals(0, results.size());

		results = model.getReferencesForPassage("Deut :2-3");
		assertEquals(0, results.size());

		results = model.getReferencesForPassage("Josh 6:4- :6");
		assertEquals(0, results.size());

		results = model.getReferencesForPassage("Ruth : - :");
		assertEquals(0, results.size());

		results = model.getReferencesForPassage("2 Sam : 4-7 :");
		assertEquals(0, results.size());

		results = model.getReferencesForPassage("Ephesians 5:2,4");
		assertEquals(0, results.size());

		results = model.getReferencesForPassage("John 3;16");
		assertEquals(0, results.size());
	}

	/**
	 * These tests contain invalid books, chapters, or verses.
	 *
	 * This test assumes getReferencesForPassage() returns an
	 * empty list if no valid results are found.
	 */
	@Test
	public void testInvalidResultsInvalidParams() {
		results = model.getReferencesForPassage("Jude 2");
		assertEquals(0, results.size());

		results = model.getReferencesForPassage("Herman 2");
		assertEquals(0, results.size());

		results = model.getReferencesForPassage("John 3:163");
		assertEquals(0, results.size());

		results = model.getReferencesForPassage("Mal 3:6-24:7");
		assertEquals(0, results.size());
	}

	/**
	 * These tests test multiple verses from multiple chapters
	 * that use very strange syntax, but still give valid results.
	 */
	@Test
	public void testValidResultsOddSyntax() {
		VerseList verseResults;
		VerseList actualResults;
		verseResults = getVersesForReference("Ephesians 5-6:9");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(29305, 29346));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());

		verseResults = getVersesForReference("Hebrews 11-12:2");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(30173, 30214));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());
	}

	/**
	 * These tests specify a whole book.
	 */
	@Test
	public void testValidResultsWholeBook() {
		VerseList verseResults;
		VerseList actualResults;
		verseResults = getVersesForReference("1 Kings");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(8718, 9533));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());

		verseResults = getVersesForReference("Philemon");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(29362, 29465));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());
	}

	/**
	 * These tests specify verses from multiple chapters.
	 */
	@Test
	public void testValidResultsMultipleVersesAndChapters() {
		VerseList verseResults;
		VerseList actualResults;
		verseResults = getVersesForReference("Isa 52:13 - 53:12");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(18709, 18723));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());

		verseResults = getVersesForReference("Mal 3:6-4:6");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(23126, 23144));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());
	}

	/**
	 * These tests specify either one or multiple chapters.
	 */
	@Test
	public void testValidResultsMultipleChapters() {
		VerseList verseResults;
		VerseList actualResults;
		verseResults = getVersesForReference("Song of Solomon 3");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(17572, 17582));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());

		verseResults = getVersesForReference("Revelation 22");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(31081, 31101));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());

		verseResults = getVersesForReference("1 Tim 2-4");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(29717, 29763));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());

		verseResults = getVersesForReference("1 John 2-3");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(30551, 30603));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());
	}

	/**
	 * These tests test multiple verses from a single chapter.
	 */
	@Test
	public void testValidResultsSingleChapter() {
		VerseList verseResults = getVersesForReference("Ecclesiastes 3 : 1 - 8");
		VerseList actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(17360, 17367));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());

		verseResults = getVersesForReference("Joshua 24:28-33");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(6504, 6509));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());

		verseResults = getVersesForReference("Psalm 23:1-6");
		actualResults = new VerseList("KJV", "KJV", versesFromFile.subList(14236, 14231));
		assertArrayEquals(actualResults.toArray(), verseResults.toArray());
	}

	/**
	 * These tests test a single verse from a single chapter.
	 */
	@Test
	public void testValidResultsSingleVerse() {
		results = model.getReferencesForPassage("John 3 : 16");
		assertEquals(new Reference(BookOfBible.John, 3, 16), results.get(0));

		results = model.getReferencesForPassage("Gen 1:1");
		assertEquals(new Reference(BookOfBible.Genesis, 1, 1), results.get(0));

		results = model.getReferencesForPassage("Revelation 22:21");
		assertEquals(new Reference(BookOfBible.Revelation, 22, 21), results.get(0));
	}

	/**
	 * Helper method to get a VerseList from the getReferencesForPassage() method.
	 *
	 * @param reference The reference to pass into getReferencesForPassage
	 * @return A VerseList from the results ReferenceList.
	 */
	public VerseList getVersesForReference(String reference) {
		ReferenceList list = model.getReferencesForPassage(reference);
		VerseList results = model.getVerses("KJV", list);
		return results;
	}

}
