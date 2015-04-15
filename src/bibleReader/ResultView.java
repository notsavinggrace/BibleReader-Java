package bibleReader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import bibleReader.model.BibleReaderModel;
import bibleReader.model.Reference;
import bibleReader.model.ReferenceList;

/**
 * The display panel for the Bible Reader.
 *
 * @author cusack, Matt Blessed
 * @modified March 26, 2015
 */
public class ResultView extends JPanel {
	JScrollPane scrollPane;
	JEditorPane editorPane;
	JTextField resultsField;
	BibleReaderModel bibleModel = new BibleReaderModel();

	private JButton nextButton;
	private JButton previousButton;
	private JTextField pagingTextField;
	private int currentPage;
	private boolean isWordSearch;
	private boolean isPassageSearch;

	private ReferenceList searchResults; // store all of the current results
	private String searchPhrase;

	private final class NextButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			incrementPage();
		}
	}

	private final class PreviousButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			decrementPage();
		}
	}

	/**
	 * Construct a new ResultView and set its model to myModel. It needs to
	 * model to look things up.
	 *
	 * @param myModel
	 *            The model this view will access to get information.
	 */
	public ResultView(BibleReaderModel myModel) {

		bibleModel = myModel;
		setUpGui();
	}

	/**
	 * Construct the GUI for the Layout
	 */
	@SuppressWarnings("serial")
	private void setUpGui() {
		// setLayout(new BorderLayout(10, 10));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.setBorder(new EmptyBorder(8, 8, 8, 8));

		editorPane = new JEditorPane() {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return true;
			}
		};
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);
		editorPane.setName("OutputEditorPane");

		scrollPane = new JScrollPane(editorPane);
		scrollPane.setPreferredSize(new Dimension(600, 300));

		resultsField = new JTextField(50);
		resultsField.setPreferredSize(new Dimension(200, 22));
		resultsField.setMinimumSize(new Dimension(200, 22));
		resultsField.setMaximumSize(new Dimension(200, 22));
		resultsField.setEditable(false);
		resultsField.setBackground(new Color((float)0.0, (float)0.0, (float)0.0, (float)0.0));

		pagingTextField = new JTextField(50);
		pagingTextField.setPreferredSize(new Dimension(200, 22));
		pagingTextField.setMinimumSize(new Dimension(200, 22));
		pagingTextField.setMaximumSize(new Dimension(200, 22));
		pagingTextField.setEditable(false);
		pagingTextField.setHorizontalAlignment(JTextField.RIGHT);
		pagingTextField.setBackground(new Color((float)0.0, (float)0.0, (float)0.0, (float)0.0));

		nextButton = new JButton("Next");
		nextButton.setMaximumSize(nextButton.getPreferredSize());
		nextButton.addActionListener(new NextButtonActionListener());
		nextButton.setName("NextButton");

		previousButton = new JButton("Previous");
		previousButton.setMaximumSize(previousButton.getPreferredSize());
		previousButton.addActionListener(new PreviousButtonActionListener());
		previousButton.setName("PreviousButton");

		this.add(scrollPane);
		this.add(Box.createVerticalStrut(10));
		Box controls = new Box(BoxLayout.X_AXIS);
		controls.add(resultsField);
		controls.add(Box.createHorizontalGlue());
		controls.add(pagingTextField);
		controls.add(Box.createHorizontalGlue());
		controls.add(previousButton);
		controls.add(Box.createHorizontalGlue());
		controls.add(nextButton);
		this.add(controls);
		
		searchPhrase = "";
		currentPage = 0;
		isWordSearch = false;
		isPassageSearch = false;
		
		nextButton.setEnabled(false);
		previousButton.setEnabled(false);
	}

	/**
	 * Display the results from a word search.
	 */
	public void displaySearchResults(ReferenceList references, String searchText) {
		searchResults = references; // set current results, start at page 1
		searchPhrase = searchText;
		currentPage = 1;
		isPassageSearch = false;
		isWordSearch = true;
		setCurrentPageText(1);
		
		ReferenceList pageOneReferences;
		// If more than one page, use a sublist of the first twenty
		if (references.size() > 20) {
			pageOneReferences = new ReferenceList(references.subList(0, 20)); // 1 - 20
			nextButton.setEnabled(true);
		}
		else {
			pageOneReferences = references;
			nextButton.setEnabled(false);
		}
		previousButton.setEnabled(false);
		
		if (searchText.isEmpty()) {
			resultsField.setText("Nothing was inputted.");
			return; // don't render results
		} else {
			resultsField.setText("The bible contains \"" + searchText + "\" "
					+ references.size() + " times.");
		}

		renderSearchResults(pageOneReferences);
	}

	/**
	 * Display the results from a passage search.
	 */
	public void displayPassageResults(ReferenceList references, String searchText) {
		searchResults = references; // set current results, start at page 1
		searchPhrase = searchText;
		currentPage = 1;
		isPassageSearch = true;
		isWordSearch = false;
		setCurrentPageText(1);
		
		ReferenceList pageOneReferences;
		// If more than one page, use a sublist of the first twenty
		if (references.size() > 20) {
			pageOneReferences = new ReferenceList(references.subList(0, 20));
			nextButton.setEnabled(true);
		}
		else {
			pageOneReferences = references;
			nextButton.setEnabled(false);
		}
		previousButton.setEnabled(false);
		
		if (searchText.isEmpty()) {
			resultsField.setText("Nothing was inputted.");
			return; // don't render results
		} else {
			resultsField.setText("Displaying results for \"" + searchText
					+ "\" (" + references.size() + " verses).");
		}
		
		// render the results
		renderPassageResults(pageOneReferences);
	}
	
	private void renderSearchResults(ReferenceList ref) {
		StringBuffer buffer = new StringBuffer();
		if (ref.isEmpty()) {
			buffer.append("No results found.");
		} else {
			buffer.append("<table>");
			buffer.append("<tr>");
			buffer.append("<th>Verse</th>");
			for (String version : bibleModel.getVersions()) {
				buffer.append("<th>");
				buffer.append(version);
				buffer.append("</th>");
			}
			// boolean firstLine = true;
			for (Reference r : ref) {
				buffer.append("<tr>");
				buffer.append("<td valign=\"top\" width=\"10%\">" + r + "</td>");
				for (String version : bibleModel.getVersions()) {
					buffer.append("<td>");
					// Replace every occurrence of foo with <b>foo</b> in the String
					// phrase regardless of the case.
					buffer.append(bibleModel.getText(version, r).replaceAll("(?i)"+searchPhrase, "<b>$0</b>"));
					buffer.append("</td>");
				}
				buffer.append("</tr>");
			}
		}

		editorPane.setText(buffer.toString());
		editorPane.setCaretPosition(0);
	}
	
	private void renderPassageResults(ReferenceList ref) {
		StringBuffer buffer = new StringBuffer();
		
		if (ref.isEmpty()) {
			buffer.append("No results found.");
		} else {
			Reference firstRef = ref.get(0);
			Reference lastRef = ref.get(ref.size()-1);
			
			String passagesTitle;
			
			if (firstRef.getBookOfBible().equals(lastRef.getBookOfBible())) {
				if (firstRef.getChapter() == lastRef.getChapter()) {
					// Same chapter
					passagesTitle = firstRef.toString()+"-"+lastRef.getVerse();
				}
				else {
					// Same book
					passagesTitle = firstRef.toString()+"-"+lastRef.getChapter()+":"+lastRef.getVerse();
				}
			}
			else {
				// Different book
				passagesTitle = firstRef.toString()+"-"+lastRef.toString();
			}
			
			buffer.append("<center><b>"+passagesTitle+"</b></center>");
			buffer.append("<br>");
			buffer.append("<table>");
			buffer.append("<tr>");
			for (String version : bibleModel.getVersions()) {
				buffer.append("<th>");
				buffer.append(version);
				buffer.append("</th>");
			}
			buffer.append("</tr>");
			buffer.append("<tr>");
			for (String version : bibleModel.getVersions()) {
				boolean firstLine = true;

				buffer.append("<td valign=\"top\">");

				for (Reference r : ref) {
					String verseText = bibleModel.getText(version, r);
					if (verseText != null && verseText != "") {
						buffer.append("<sup>");
						if (r.getVerse() == 1) {
							buffer.append("<font size=\"5\">");
							if (firstLine) {
								firstLine = false;
							} else {
								buffer.append("<br><br>");
							}
							buffer.append(r.getChapter());
						} else {
							buffer.append(r.getVerse());
						}
						buffer.append("</sup></font>");
						buffer.append(verseText);
						
					}
					
				}

				buffer.append("</td>");
			}
			buffer.append("</tr>");
			buffer.append("</table>");
		}

		editorPane.setText(buffer.toString());
		editorPane.setCaretPosition(0);
	}
	
	private int getNumberOfPagesForCurrentResult() {
		return (int)Math.ceil(searchResults.size()/20.0);
	}
	
	// Set 0 to void text
	private void setCurrentPageText(int page) {
		currentPage = page;
		if (page != 0) {
			pagingTextField.setText("Displaying page "+page+" of "+getNumberOfPagesForCurrentResult());
		}
		else {
			pagingTextField.setText("");
		}
	}
	
	private void incrementPage() {
		if ((currentPage+1) > getNumberOfPagesForCurrentResult()) return;
		previousButton.setEnabled(true);
		
		currentPage++;
		
		setCurrentPageText(currentPage);
		
		int fromIndex = ((currentPage-1)*20);
		int toIndex =   (currentPage*20);
		
		if (toIndex > (searchResults.size()-1)) {
			// use what's left
			toIndex = searchResults.size();//(searchResults.size()%(currentPage*20))-1;
			//System.out.println("Shortened: fromIndex:"+fromIndex+" toIndex: "+toIndex);
		}
		
		// sublist of the next page of results
		ReferenceList nextPageRef = new ReferenceList(searchResults.subList(fromIndex, toIndex));
		
		if (isWordSearch) {
			renderSearchResults(nextPageRef);
		}
		else if (isPassageSearch) {
			renderPassageResults(nextPageRef);
		}
		
		// If on last page, cannot next.
		if (currentPage == getNumberOfPagesForCurrentResult()) {
			nextButton.setEnabled(false);
		}
	}
	
	private void decrementPage() {
		if ((currentPage-1) == 0) return;
		nextButton.setEnabled(true);
		
		currentPage--;
		setCurrentPageText(currentPage);
		
		// sublist of the next page of results
		ReferenceList nextPageRef = new ReferenceList(searchResults.subList(((currentPage-1)*20), (currentPage*20)));
		
		if (isWordSearch) {
			renderSearchResults(nextPageRef);
		}
		else if (isPassageSearch) {
			renderPassageResults(nextPageRef);
		}
		
		// If on first page, cannot previous.
		if (currentPage == 1) {
			previousButton.setEnabled(false);
		}
	}
}
