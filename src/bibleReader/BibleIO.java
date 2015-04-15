package bibleReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import bibleReader.model.Bible;
import bibleReader.model.BibleFactory;
import bibleReader.model.BookOfBible;
import bibleReader.model.Reference;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * A utility class that has useful methods to read/write Bibles and Verses.
 *
 * @author cusack, blessed
 */
public class BibleIO {

	/**
	 * Read in a file and create a Bible object from it and return it.
	 *
	 * @param bibleFile
	 * @return A VerseList containing all of the verses read from the file given, or null if error.
	 */
	
	// This method is complete
	public static VerseList readBible(File bibleFile) { // Get the extension of the file
		String name = bibleFile.getName();
		String extension = name.substring(name.lastIndexOf('.') + 1, name.length());

		// Call the read method based on the file type.
		if ("atv".equals(extension.toLowerCase())) {
			return readATV(bibleFile);
		} else if ("xmv".equals(extension.toLowerCase())) {
			return readXMV(bibleFile);
		} else {
			return null;
		}
	}

	/**
	 * Read in a Bible that is saved in the "ATV" format. The format is described below.
	 *
	 * @param bibleFile The file containing a Bible with .atv extension.
	 * @return A Bible object constructed from the file bibleFile, or null if there was an error reading the file.
	 */
	private static VerseList readATV(File bibleFile) {
		try {
			FileReader inStream   = new FileReader(bibleFile);
			BufferedReader inData = new BufferedReader(inStream);

			// Get the first line, containing information about the bible (version + description).
			String in      = inData.readLine();
			String[] split = in.split(": ");
			VerseList list;
			if (split.length == 2) {
				System.out.println("Split 1");
				list = new VerseList(split[0], split[1]);
			}
			else {
				System.out.println("Split 2 "+split.length);
				list = new VerseList(in, "");
			}
			
			// Parse the file into verses.
			while((in = inData.readLine()) != null) {
				// Data is in format BOOK@CH:V@VERSE_TEXT
				split = in.split("[@]");
				if (split.length != 3) {
					inData.close();
					return null;
				}
				String[] ref = split[1].split(":");
				if (ref.length != 2) {
					inData.close();
					return null;
				}
				BookOfBible book = BookOfBible.getBookOfBible(split[0]);
				if (book == null) {
					inData.close();
					return null;
				}
				
				Reference r = new Reference(book,
											Integer.parseInt(ref[0]),
											Integer.parseInt(ref[1]));
				Verse v = new Verse(r, split[2]);
				list.add(v);
			}
			inData.close();
			return list;
	    }
	    catch(IOException e) {
	    	 System.out.println("Error opening file.");
	    }
		catch(NumberFormatException e) {
			System.out.println("Error parsing int.");
		}
		return null;
	}

	/**
	 * Read in the Bible that is stored in the XMV format.
	 * The format reads as follows: 
	 * <Book Genesis, description>
	 * <Chapter 1>
	 * <Verse 1>I am a verse.  
	 * <Verse 2>I am another verse.  
	 *
	 * @param bibleFile The file containing a Bible with .xmv extension.
	 * @return A Bible object constructed from the file bibleFile, or null if there was an error reading the file.
	 */
	private static VerseList readXMV(File bibleFile) {
		try {
			FileReader inStream   = new FileReader(bibleFile);
			BufferedReader inData = new BufferedReader(inStream);

			// Get the first line, containing information about the bible (version + description).
			String in      = inData.readLine();
			String[] split = in.split(": ");
			
			if (split.length != 2) {
				inData.close();
				return null;
			}
			
			String version = split[0].replaceAll("<Version ", "");
			String description = split[1];//.replaceAll(">", ""); // Actual in tests shows that the > stays, but it shouldnt be there.
			
			VerseList list = new VerseList(version, description);
			
			BookOfBible currentBook = null;
			int currentChapter = 0;
			
			// Parse the file into verses.
			while((in = inData.readLine()) != null) {
				split = in.split(">");
				
				if (split.length < 1 || split.length > 2) {
					inData.close();
					return null;
				}
				
				if (split[0].startsWith("<Verse")) {
					// Is verse
					Reference r = new Reference(currentBook,
												currentChapter,
												Integer.parseInt(split[0].replaceAll("<Verse ", "")));
					
					Verse v = new Verse(r, split[1].trim());
					
					list.add(v);
				}
				else if (split[0].startsWith("<Chapter")) {
					// Start new chapter
					split[0] = split[0].replaceAll("<", "");
					currentChapter = Integer.parseInt(split[0].replaceAll("Chapter ", ""));
				}
				else if (split[0].startsWith("<Book")) {
					// Start new book
					split[0] = split[0].replaceAll(", .*", "");
					currentBook = BookOfBible.getBookOfBible(split[0].replaceAll("<Book ", ""));
				}
			}
			inData.close();
			return list;
	    }
	    catch(IOException e) {
	    	 System.out.println("Error opening file.");
	    }
		catch(NumberFormatException e) {
			System.out.println("Error parsing int.");
		}
		
		return null;
	}

	/**
	 * Write out the Bible in the ATV format.
	 *
	 * @param file The file that the Bible should be written to.
	 * @param bible The Bible that will be written to the file.
	 */
	public static void writeBibleATV(File file, Bible bible) {
		try {
			FileWriter outStream = new FileWriter(file);
			PrintWriter outData = new PrintWriter(outStream);
			
			VerseList vList = bible.getAllVerses();
			// Prints first line with version and description eg KJV: I am a bible...
			outData.println(vList.getVersion()+": "+vList.getDescription());
			
			// Prints each verse in 'Book@1:1@Verse Text' format
			for (Verse v : vList) {
				Reference r = v.getReference();
				outData.println(r.getBook()+"@"+r.getChapter()+":"+r.getVerse()+"@"+v.getText());
			}
			
			outData.close();
		} catch (IOException e) {
			System.out.println("I/O Error");
		}
	}

	/**
	 * Write out the given verses in the ATV format, using the description as the first line of the file.
	 *
	 * @param file The file that the Bible should be written to.
	 * @param description The contents that will be placed on the first line of the file, formatted appropriately.
	 * @param verses The verses that will be written to the file.
	 */
	public static void writeVersesATV(File file, String description, VerseList verses) {
		VerseList vList = new VerseList(verses.getVersion(), description);
		vList.addAll(verses);
		BibleIO.writeBibleATV(file, BibleFactory.createBible(verses));
	}

	/**
	 * Write the string out to the given file. It is presumed that the string is an HTML rendering of some verses, but
	 * really it can be anything.
	 *
	 * @param file The file that the text should be written to.
	 * @param text The contents that will be written into the file.
	 */
	public static void writeText(File file, String text) {
		try {
			FileWriter outStream = new FileWriter(file);
			PrintWriter outData = new PrintWriter(outStream);
			outData.println(text);
			outData.close();
		} catch (IOException e) {
			System.out.println("File write error");
		}
	}
}
