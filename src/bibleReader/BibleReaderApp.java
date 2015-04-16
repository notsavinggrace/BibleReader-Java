package bibleReader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import bibleReader.model.ArrayListBible;
import bibleReader.model.Bible;
import bibleReader.model.BibleReaderModel;
import bibleReader.model.ReferenceList;
import bibleReader.model.TreeMapBible;
import bibleReader.model.VerseList;

/**
 * The main class for the Bible Reader Application.
 *
 * @author cusack, Matt Blessed
 * @modified March 26, 2015
 */
public class BibleReaderApp extends JFrame {

	// Change these to suit your needs.
	public static final int	width	= 640;
	public static final int	height	= 420;

	public static void main(String[] args) {
		new BibleReaderApp();
	}

	// Fields
	private final BibleReaderModel	model;
	private final ResultView		resultView;

	// Gui Fields
	private JTextField textField;
	private JButton wordSearchButton;
	private JButton passageSearchButton;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu helpMenu;
	private JMenuItem fileMenuExit;
	private JMenuItem fileMenuOpen;
	private JMenuItem helpMenuAbout;

	/**
	 * Default constructor. We may want to replace this with a different one.
	 */
	public BibleReaderApp() {
		model            = new BibleReaderModel();
		
		File kjvFile     = new File("kjv.atv");
		VerseList kjvVerses = BibleIO.readBible(kjvFile);
		Bible kjv        = new TreeMapBible(kjvVerses);
		model.addBible(kjv);

		File asvFile     = new File("asv.xmv");
		VerseList asvVerses = BibleIO.readBible(asvFile);
		Bible asv        = new TreeMapBible(asvVerses);
		model.addBible(asv);
		
		File esvFile     = new File("esv.atv");
		VerseList esvVerses = BibleIO.readBible(esvFile);
		Bible esv        = new TreeMapBible(esvVerses);
		model.addBible(esv);

		
		

		resultView = new ResultView(model);

		setupGUI();
		pack();
		setSize(width, height);

		// So the application exits when you click the "x".
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(600, 400));
		setVisible(true);
	}

	/**
	 * Set up the main GUI. Make sure you don't forget to put resultView somewhere!
	 */
	private void setupGUI() {
		// Makes everything look nice
		setupLookAndFeel();
		
		this.setLayout(new BorderLayout());
		
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		helpMenu = new JMenu("Help");
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		
		fileMenuOpen = new JMenuItem("Open");
		fileMenuOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(getOwner());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					//File f = new File(chooser.getSelectedFile().getAbsolutePath());
					model.addBible(new ArrayListBible(BibleIO.readBible(chooser.getSelectedFile())));

					JOptionPane.showMessageDialog(getOwner(), "Imported new Bible: " + chooser.getSelectedFile().getName());
				}
			}
		});
		fileMenu.add(fileMenuOpen);
		
		fileMenuExit = new JMenuItem("Exit");
		fileMenuExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(fileMenuExit);

		helpMenuAbout = new JMenuItem("About");
		helpMenuAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(getOwner(), "A bible reader application, used for searching words or passages.\n"
												  + "Authors: cusack + Matt Blessed",
												  	"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpMenu.add(helpMenuAbout);

		Box searchBox = new Box(BoxLayout.X_AXIS);

		textField = new JTextField(25);
		textField.setMaximumSize(textField.getPreferredSize());
		textField.setName("InputTextField");

		wordSearchButton = new JButton("Search");
		wordSearchButton.setMaximumSize(wordSearchButton.getPreferredSize());
		wordSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String searchText = textField.getText().trim();
				ReferenceList list = model.getReferencesContainingAllWordsAndPhrases(searchText);
				resultView.displaySearchResults(list, searchText);
			}
		});
		wordSearchButton.setName("SearchButton");

		passageSearchButton = new JButton("Passage");
		passageSearchButton.setMaximumSize(passageSearchButton.getPreferredSize());
		passageSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String searchText = textField.getText().trim();
				ReferenceList list = model.getReferencesForPassage(searchText);
				resultView.displayPassageResults(list, searchText);
			}
		});
		passageSearchButton.setName("PassageButton");

		searchBox.add(Box.createHorizontalGlue());
		searchBox.add(textField);
		searchBox.add(Box.createHorizontalStrut(10));
		searchBox.add(wordSearchButton);
		searchBox.add(Box.createHorizontalStrut(10));
		searchBox.add(passageSearchButton);
		searchBox.add(Box.createHorizontalGlue());

		this.setJMenuBar(menuBar);
		this.add(searchBox, BorderLayout.NORTH);
		this.add(resultView, BorderLayout.CENTER);
	}
	
	// code snippet taken from cusack's stage 10
	private void setupLookAndFeel() {

		UIManager.put("control", new Color(200,200,200));
		UIManager.put("nimbusLightBackground", new Color(220,220,220));
		UIManager.put("nimbusFocus", new Color(150,150,150));
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// It will use the default look and feel
		}
	}
}
