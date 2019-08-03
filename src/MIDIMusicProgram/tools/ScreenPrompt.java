package tools;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Transmitter;
import javax.speech.AudioException;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import keyboard.Note;
import keyboard.VirtualKeyboard;
import midi.Chord;
import midi.ChordProgression;
import midi.ChordProgressionActions;
import midi.Genre;
import midi.ListOfChords;
import midi.ListOfScales;
import midi.MidiMessageTypes;
import midi.Scale;
import midi.TypesOfArpeggios;
import midi.Scale.ascendingSolfege;
import midi.Scale.descendingSolfege;
import midi.Scale.heptTonicDegree;
import midi.Scale.hexaTonicDegrees;
import midi.Scale.octaTonicDegree;
import midi.Scale.pentatonicDegrees;
import midiDevices.PlayBackDevices;

/**
 * This class contains the applications screen prompt and displays the feature
 * sets to the user.
 */

public class ScreenPrompt implements MouseListener, ActionListener {

	// GUI Swing components
	private JPanel basePanel = null;
	private JTextArea contentTextArea;
	private JScrollPane scrollPane;
	private DefaultListModel<String> conditionalModel = null;
	private JComboBox<String> arpeggiosList;
	private JList<String> jListInput = null;
	private int jListTableWidth;
	private int jListYPos = SwingComponents.getJListYPos();
	private int jListTableHeight;
	private int jYAndHeight;

	// Dimensions
	private int screenWidth = SwingComponents.getInstance().getScreenWidth();
	private int screenHeight = SwingComponents.getInstance().getScreenHeight();
	private int x = screenWidth / 2 + 20;

	// State conditions
	private int featureState = 0;
	private int pageState = 0;
	private boolean chordFeature = false;
	private boolean inversionFeature = false;
	private boolean scalesFeature = false;
	private boolean relativePitchFeature = false;
	private boolean chordProgressionFeature = false;
	private boolean createSong = false;
	private boolean quizEnabled = false;
	private boolean chordQuizEnabled = false;
	private boolean scaleQuizEnabled = false;
	private boolean progressionQuizEnabled = false;
	private boolean resetReference = false;
	private boolean randomTriggered = false;
	private boolean randomTriggeredReference = false;
	private boolean scaleDisplayIsReset = false;
	private boolean highlighted = false;
	private boolean previousInversion = false;
	private boolean reverseOnce = false;
	private int inversionCounter = 1;
	private int intervalCounter = 0;

	// Music components
	private String scale = "";
	private Chord foundChord = null;
	private Note foundInterval = null;
	private Scale foundScale = null;

	// Action command buttons
	private JButton prevState = null;
	private JButton playChordState = null;
	private JButton homeState = null;
	private JButton nextInversionState = null;
	private JButton prevInversionState = null;
	private JButton playScaleState = null;
	private JButton playProgression = null;
	private JButton choiceButton = null;

	// Relative Pitch buttons
	private JButton playRandomIntervalState = null;
	private JButton playNextIntervalState = null;
	private JButton playPrevIntervalState = null;
	private JToggleButton colorModeState = null;
	private JToggleButton scaleRangeColorModeState = null;
	private JPanel actionHolder = new JPanel(new GridBagLayout());
	private JPanel innerContent;
	private JPanel quizHolderContent;

	// Used for instances
	private GridBagConstraints actionBarConstraints = new GridBagConstraints();
	private ListOfChords chordInstance = ListOfChords.getInstance();
	private MidiMessageTypes messages = MidiMessageTypes.getInstance();
	private TypesOfArpeggios preg = TypesOfArpeggios.getInstance();
	private SwingComponents components = SwingComponents.getInstance();
	private ChordProgressionActions prog = ChordProgressionActions.getInstance();
	private ChordProgression progressionChord;
	private ArrayList<Chord> inversionChords = new ArrayList<Chord>();
	private String scaleOrder = "";
	private int progressionType;

	private static volatile boolean stopPlayback = false;
	private static volatile boolean changeApreggio = false;
	private static volatile ScreenPrompt instance = null;

	private ScreenPrompt() {
	}

	public static ScreenPrompt getInstance() {
		if (instance == null) {
			synchronized (ScreenPrompt.class) {
				if (instance == null) {
					instance = new ScreenPrompt();
					instance.setJListDimensions();
				}
			}
		}
		return instance;
	}

	public void setJListDimensions() {
		jListTableWidth = screenWidth / 2 - 20;
		jListTableHeight = screenHeight / 2 - 50;
		jYAndHeight = jListYPos + jListTableHeight;
	}

	public JPanel editPanel() {
		return basePanel;
	}

	public void addToPanel(JPanel aPanel) {
		basePanel.add(aPanel);
	}

	public JComboBox<String> addApreggiosBox() {
		arpeggiosList = components.customJComboBox(70, 40, "Apreggios", this, 24);
		return arpeggiosList;
	}

	// INTERACT WITH FEATURE SETS FROM WELCOME SCREEN
	///////////////////////////////////////////////////////////////
	public void changePageState(int pageNumber) {
		pageState = pageNumber;
	}

	public JPanel createCurrentPromptState() throws InvalidMidiDataException {
		basePanel = new JPanel();
		basePanel.setPreferredSize(new Dimension(screenWidth / 2, screenHeight / 2));
		basePanel.setMinimumSize(new Dimension(screenWidth / 2, screenHeight / 2));
		switch (featureState) {
		case 0:
			imagesWelcomePrompt();
			break;
		case 1:
			if (pageState == 1) {
				if (quizEnabled) {
					// Internal boolean values change implementation
					imagesWelcomePrompt();
				} else {
					displayKeysPrompt();
				}
			} else if (pageState == 2) {

				if (quizEnabled) {
					basePanel.setLayout(new GridBagLayout());
					if (chordQuizEnabled) {
						Quizes.getInstance().assignFeatureName("Chords");
					}

					else if (scaleQuizEnabled) {
						Quizes.getInstance().assignFeatureName("Scales");
					}

					else if (progressionQuizEnabled) {
						Quizes.getInstance().assignFeatureName("Progressions");
					}

					createCommandButtons("Quiz", Color.decode("#303030"));
					baseSummary("Quiz Test", Color.decode("#303030"), Color.decode("#505050"));
					displayConditionalPrompts("Quiz Answers", "Major", "Minor", Color.decode("#303030"),
							Color.decode("#505050"));

					if (chordQuizEnabled) {
						if (components.getColorToggleStatus()) {
							components.changeColorToggle(false);
							colorModeState.setSelected(false);
						}
						PlaybackFunctions.resetChordsColor();
						PlaybackFunctions.emptyNotes();
						Quizes.getInstance().selectQuiz("Chords", contentTextArea);
					}

					else if (scaleQuizEnabled) {
						if (components.getColorToggleStatus()) {
							components.changeColorToggle(false);
							colorModeState.setSelected(false);
						}
						PlaybackFunctions.resetChordsColor();
						PlaybackFunctions.emptyNotes();
						Quizes.getInstance().selectQuiz("Scales", contentTextArea);
					}

					else if (progressionQuizEnabled) {
						if (components.getColorToggleStatus()) {
							components.changeColorToggle(false);
							colorModeState.setSelected(false);
						}
						PlaybackFunctions.resetChordsColor();
						PlaybackFunctions.emptyNotes();
						Quizes.getInstance().selectQuiz("Progressions", contentTextArea);
					}
					
				} else if (chordProgressionFeature) {
					createCommandButtons("stages", Color.decode("#303030"));
					displayConditionalPrompts("Progressions", "Common chords in Major Scale",
							"Common Chords in Minor Scale", Color.decode("#303030"), Color.decode("#505050"));
					basePanel.setBackground(Color.decode("#303030"));
				}

				else if (createSong) {
					createCommandButtons("genre", Color.decode("#303030"));
					displayConditionalPrompts("Genres", "Classical", "Blues", Color.decode("#303030"),
							Color.decode("#505050"));
					basePanel.setBackground(Color.decode("#303030"));
				} else {
					displayChordOrScalesNamesPrompt();
				}
			}

			else if (pageState == 3) {
				basePanel.setLayout(new GridBagLayout());
				if (scalesFeature == true) {
					scalesChoiceSummary();
				}

				else if (relativePitchFeature == true) {
					createCommandButtons("stages", Color.decode("#303030"));
					basePanel.setBackground(Color.decode("#303030"));
					// Diverge from scale names from feature 3
					displayConditionalPrompts("Scale Order", "Ascending Order", null, Color.decode("#303030"),
							Color.decode("#505050"));
				}

				else if (chordProgressionFeature == true) {
					createCommandButtons("stages", Color.decode("#303030"));
					basePanel.setBackground(Color.decode("#303030"));
					displayProgressions();
				} else {
					findSpecificChord();
				}
			}

			else if (pageState == 4) {
				// Added command actions in below method as for ascending or
				// descending scale
				if (relativePitchFeature == true) {
					scalesOrder();
				} else if (chordProgressionFeature == true) {
					createCommandButtons("feature 5", Color.decode("#303030"));
					basePanel.setBackground(Color.decode("#303030"));
					progressionsList();
				}
			}
			break;
		case 3:
			break;
		}
		return basePanel;
	}

	public JPanel createWelcomeGridPanel(MouseListener mouse, Color back, String name) {
		JPanel temp = new JPanel();
		temp.setBackground(back);
		temp.setName(name);
		temp.addMouseListener(mouse);
		temp.setPreferredSize(
				new Dimension(basePanel.getPreferredSize().width / 3, basePanel.getPreferredSize().height / 3));
		return temp;
	}

	// Initial state with list model values added
	public void imagesWelcomePrompt() throws InvalidMidiDataException {
		basePanel.setLayout(new GridBagLayout());
		actionBarConstraints = components.conditionalConstraints(1, 1, 0, 0, GridBagConstraints.NONE);

		// Inner panel background colour
		basePanel.setBackground(Color.decode("#202020"));
		if (quizEnabled) {
			createCommandButtons("stages", Color.decode("#303030"));
			basePanel.setBackground(Color.decode("#303030"));
		}
		int width = basePanel.getPreferredSize().width / 3;
		int height = basePanel.getPreferredSize().height / 3;
		for (int i = 1; i < 8; i++) {
			if (i == 1 || i == 2 || i == 3) {
				actionBarConstraints = components.conditionalConstraints(1, 1, 0, 0, GridBagConstraints.NONE);
			}

			else if (i == 4 || i == 5 || i == 6) {
				if (quizEnabled) {
					actionBarConstraints = components.conditionalConstraints(1, 1, 0, 0, GridBagConstraints.NONE);
				} else {
					actionBarConstraints = components.conditionalConstraints(1, 1, 0, 2, GridBagConstraints.NONE);
				}
			} else if (i == 7) {
				actionBarConstraints = components.conditionalConstraints(1, 1, 0, 1, GridBagConstraints.NONE);
			}
			try {
				switch (i) {

				case 1:
					JPanel chord = null;
					if (quizEnabled) {
						BufferedImage chordBuff = ImageIO.read(new File("src/Images/Chord quiz tile image.png"));
						chord = components.customizeFeaturePanel(width, height, this, chordBuff, "Chords Quiz");
					} else {
						BufferedImage chordBuff = ImageIO.read(new File("src/Images/Chord tile image.png"));
						chord = components.customizeFeaturePanel(width, height, this, chordBuff, "Chords");
					}
					chord.setBackground(Color.white);
					actionBarConstraints.anchor = GridBagConstraints.WEST;
					actionBarConstraints.insets = new Insets(0, 2, 0, 0);
					basePanel.add(chord, actionBarConstraints);
					break;

				case 2:
					if (!quizEnabled) {
						BufferedImage inversionBuff = ImageIO
								.read(new File("src/Images/Chord inversion tile image.png"));
						JPanel inversion = components.customizeFeaturePanel(width, height, this, inversionBuff,
								"Inversions");
						inversion.setBackground(Color.white);
						actionBarConstraints.anchor = GridBagConstraints.CENTER;
						basePanel.add(inversion, actionBarConstraints);
					}
					break;

				case 3:
					JPanel scales = null;
					if (quizEnabled) {
						BufferedImage scalesBuff = ImageIO.read(new File("src/Images/Scale quiz tile image.png"));
						scales = components.customizeFeaturePanel(width, height, this, scalesBuff, "Scales Quiz");
					} else {
						BufferedImage scalesBuff = ImageIO.read(new File("src/Images/Scale tile image.png"));
						scales = components.customizeFeaturePanel(width, height, this, scalesBuff, "Scales");
					}
					scales.setBackground(Color.white);
					actionBarConstraints.anchor = GridBagConstraints.EAST;
					actionBarConstraints.insets = new Insets(0, 0, 0, 2);
					basePanel.add(scales, actionBarConstraints);
					break;

				case 4:
					if (!quizEnabled) {
						BufferedImage relativeBuff = ImageIO.read(new File("src/Images/Reletive pitch tile image.png"));
						JPanel relative = components.customizeFeaturePanel(width, height, this, relativeBuff,
								"Relative");
						relative.setBackground(Color.white);
						actionBarConstraints.anchor = GridBagConstraints.WEST;
						basePanel.add(relative, actionBarConstraints);
					}
					break;

				case 5:
					JPanel progression = null;
					if (quizEnabled) {
						BufferedImage progressionBuff = ImageIO
								.read(new File("src/Images/Chord progression quiz tile image.png"));
						progression = components.customizeFeaturePanel(width, height, this, progressionBuff,
								"Progressions Quiz");
					} else {
						BufferedImage progressionBuff = ImageIO
								.read(new File("src/Images/Chord Progression tile image.png"));
						progression = components.customizeFeaturePanel(width, height, this, progressionBuff,
								"Progressions");
					}
					progression.setBackground(Color.white);
					actionBarConstraints.anchor = GridBagConstraints.CENTER;
					basePanel.add(progression, actionBarConstraints);
					break;

				case 6:
					if (!quizEnabled) {
						BufferedImage genresBuff = ImageIO.read(new File("src/Images/Genres tile image.png"));
						JPanel genres = components.customizeFeaturePanel(width, height, this, genresBuff, "Genres");
						genres.setBackground(Color.white);
						actionBarConstraints.anchor = GridBagConstraints.EAST;
						basePanel.add(genres, actionBarConstraints);
					}
					break;
				case 7:
					if (!quizEnabled) {
						BufferedImage quizBuff = ImageIO.read(new File("src/Images/Quizes tile image.png"));
						JPanel quiz = components.customizeFeaturePanel(width, height, this, quizBuff, "Quizes");
						quiz.setBackground(Color.white);
						actionBarConstraints.anchor = GridBagConstraints.SOUTH;
						basePanel.add(quiz, actionBarConstraints);
					}
					break;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Add current state model to be created for each prompt page
	public void changePanelState() throws InvalidMidiDataException {
		scrollPane = retrieveStatePane();
		if (relativePitchFeature == true && pageState == 3 || chordProgressionFeature == true && pageState == 3) {
			actionBarConstraints = components.conditionalConstraints(1, 1, 0, 1, GridBagConstraints.NONE);
			actionBarConstraints.gridwidth = 0;
			basePanel.add(scrollPane, actionBarConstraints);
		}

		else if (pageState == 2 && chordQuizEnabled || pageState == 2 && scaleQuizEnabled
				|| pageState == 2 && progressionQuizEnabled) {
			// Place adjacent to text area
			actionBarConstraints = components.conditionalConstraints(1, 1, 1, 0, GridBagConstraints.NONE);
			actionBarConstraints.anchor = GridBagConstraints.LINE_START;
			actionBarConstraints.insets = new Insets(0, 10, 0, 0);
			quizHolderContent.add(scrollPane, actionBarConstraints);
		} else {
			basePanel.add(scrollPane);
		}
	}

	// Re-used each time anew Panel is create through user interactions
	public JScrollPane retrieveStatePane() throws InvalidMidiDataException {
		jListInput.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jListInput.setVisibleRowCount(-1);
		jListInput.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE));
		jListInput.addMouseListener(this);
		scrollPane = new JScrollPane(jListInput);

		if (pageState == 2 && chordQuizEnabled || pageState == 2 && scaleQuizEnabled
				|| pageState == 2 && progressionQuizEnabled) {
			scrollPane.setPreferredSize(new Dimension(jListTableWidth / 6, jListTableHeight / 3));
			scrollPane.setMinimumSize(new Dimension(jListTableWidth / 6, jListTableHeight / 3));
		} else if (pageState == 1) {
			scrollPane.setPreferredSize(new Dimension(jListTableWidth - jListTableWidth / 7, jListTableHeight / 2));
			scrollPane.setMinimumSize(new Dimension(jListTableWidth - jListTableWidth / 7, jListTableHeight / 2));
		}

		else {
			scrollPane.setPreferredSize(new Dimension(jListTableWidth, jListTableHeight / 2 + jListTableHeight / 3));
			scrollPane.setMinimumSize(new Dimension(jListTableWidth, jListTableHeight / 2 + jListTableHeight / 3));
		}
		return scrollPane;
	}

	// Create key names model list
	public void displayKeysPrompt() throws InvalidMidiDataException {
		conditionalModel = new DefaultListModel<String>();
		jListInput = new JList<String>(conditionalModel);
		createCommandButtons("stages", Color.decode("#303030"));

		JLabel title = null;
		if (createSong == true) {
			title = new JLabel("Genre key");
		}
		// Feature set 2 - inversion of chords
		else if (inversionFeature == true) {
			title = new JLabel("Inversion root");
		}
		// Feature set 1 - Create chords
		else if (chordFeature == true) {
			title = new JLabel("Chord root");
		}
		// Feature set 3 - play scales based on key
		else if (scalesFeature == true) {
			title = new JLabel("Scale key");
		} else if (relativePitchFeature == true) {
			title = new JLabel("Referrence note");
		} else if (chordProgressionFeature == true) {
			title = new JLabel("Chord progression root");
		}
		title.setFont(new Font("Serif", Font.BOLD, 30));
		title.setForeground(Color.white);
		basePanel.add(title);
		components.colourMenuPanels(jListInput, Color.decode("#505050"), Color.decode("#505050"));
		basePanel.setBackground(Color.decode("#303030"));
		jListInput.setName("Key Names");
		jListInput.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		jListInput.setForeground(Color.WHITE);

		ArrayList<String> stringKeys = chordInstance.getAllKeyNotesStrings();
		for (String noteString : stringKeys) {
			noteString = noteString.substring(0, noteString.length() - 1);
			conditionalModel.addElement(noteString);
		}
		changePanelState();
	}

	// Feature 4
	public void displayConditionalPrompts(String listName, String text1, String text2, Color one, Color two)
			throws InvalidMidiDataException {
		conditionalModel = new DefaultListModel<String>();
		jListInput = new JList<String>(conditionalModel);
		components.colourMenuPanels(jListInput, one, two);
		jListInput.setName(listName);
		jListInput.setFixedCellWidth(jListTableWidth - 20);
		conditionalModel.addElement(text1);
		if (text2 != null) {
			conditionalModel.addElement(text2);
		}
		jListInput.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		jListInput.setFont(new Font("Tahoma", Font.BOLD, 24));
		jListInput.setForeground(Color.white);
		changePanelState();
	}

	public void displayProgressions() throws InvalidMidiDataException {
		if (progressionType == 0) {
			conditionalModel = prog.getMajorChordProgressions();
		}

		else if (progressionType == 5) {
			conditionalModel = prog.getMinorChordProgressions();
		}
		jListInput = new JList<String>(conditionalModel);
		components.colourMenuPanels(jListInput, Color.decode("#303030"), Color.decode("#505050"));
		jListInput.setName("Sequences");
		jListInput.setForeground(Color.WHITE);
		jListInput.setFixedCellWidth(jListTableWidth - 20);
		jListInput.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		jListInput.setFont(new Font("Tahoma", Font.BOLD, 24));
		jListInput.setForeground(Color.WHITE);
		changePanelState();
	}

	// Create key names model list
	public void displayChordOrScalesNamesPrompt() throws InvalidMidiDataException {
		// Feature 3 and 4
		if (scalesFeature == true || relativePitchFeature == true) {
			conditionalModel = Scale.getScales();
			jListInput = new JList<String>(conditionalModel);
			jListInput.setName("Scale Names");
			jListInput.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		}
		// Feature 1 and 2
		else {
			conditionalModel = new DefaultListModel<String>();
			jListInput = new JList<String>(conditionalModel);
			jListInput.setName("Chord Names");
			jListInput.setLayoutOrientation(JList.HORIZONTAL_WRAP);

			ArrayList<String> stringChords = Chord.getAllChordEnums();
			for (String noteString : stringChords) {
				conditionalModel.addElement(noteString);
			}
		}
		// Shared settings
		createCommandButtons("stages", Color.decode("#303030"));
		components.colourMenuPanels(jListInput, Color.decode("#505050"), Color.decode("#505050"));
		jListInput.setForeground(Color.WHITE);
		basePanel.setBackground(Color.decode("#303030"));
		jListInput.setFixedCellWidth(jListTableWidth - 20);
		jListInput.setFont(new Font("Arial", Font.BOLD, 24));
		changePanelState();
	}

	public boolean checkIfEndInversion() {
		// Get root of current inversion
		Chord aChord = chordInstance.getCurrentInversion();
		if (aChord == null) {
			return false;
		}
		Note invertedRoot = aChord.getChordNotes().get(0);
		// Compare to original to see if it has return to original chord in new
		// octave
		if (foundChord.getChordNotes().get(foundChord.getChordNotes().size() - 1).equals(invertedRoot)) {
			return true;
		}
		return false;
	}

	public void findSpecificChord() throws InvalidMidiDataException {
		String completeChord = Chord.getStoredChord();

		// e.g. cmajTetra
		if (completeChord.length() == 9 && !completeChord.contains("#")
				|| completeChord.length() == 10 && completeChord.contains("#")) {
			scale = completeChord.contains("#") ? completeChord.substring(2, 10) : completeChord.substring(1, 9);
		}

		else if (completeChord.length() == 8 && !completeChord.contains("#")
				|| completeChord.length() == 9 && completeChord.contains("#")) {
			scale = completeChord.contains("#") ? completeChord.substring(2, 9) : completeChord.substring(1, 8);
		}

		// e.g. cseven or c#seven
		else if (completeChord.length() == 7 && !completeChord.contains("#")
				|| completeChord.length() == 8 && completeChord.contains("#")) {
			scale = completeChord.contains("#") ? completeChord.substring(2, 8) : completeChord.substring(1, 7);
		}

		// e.g chord is Ctetra or C#min11
		else if (completeChord.length() == 6 && !completeChord.contains("#")
				|| completeChord.length() == 7 && completeChord.contains("#")) {
			scale = completeChord.contains("#") ? completeChord.substring(2, 7) : completeChord.substring(1, 6);
		}

		// e.g chord is Cmaj6 or C#maj6
		else if (completeChord.length() == 5 && !completeChord.contains("#")
				|| completeChord.length() == 6 && completeChord.contains("#")) {
			scale = completeChord.contains("#") ? completeChord.substring(2, 6) : completeChord.substring(1, 5);
		}

		// e.g chord is Cmaj or C#maj
		else if (completeChord.length() == 4 && !completeChord.contains("#")
				|| completeChord.length() == 5 && completeChord.contains("#")) {
			scale = completeChord.contains("#") ? completeChord.substring(2, 5) : completeChord.substring(1, 4);
		}

		// e.g chord is C11 or C#11
		else if (completeChord.length() == 3 && !completeChord.contains("#")
				|| completeChord.length() == 4 && completeChord.contains("#")) {
			scale = completeChord.contains("#") ? completeChord.substring(2, 4) : completeChord.substring(1, 3);
		}

		// e.g chord is C9 or C#9
		else if (completeChord.length() == 2 && !completeChord.contains("#")
				|| completeChord.length() == 3 && completeChord.contains("#")) {
			scale = completeChord.contains("#") ? completeChord.substring(2, 3) : completeChord.substring(1, 2);
		}
		String key = completeChord.contains("#") ? completeChord.substring(0, 2) : completeChord.substring(0, 1);
		key = key.toUpperCase();

		// Current chord to play in button
		foundChord = chordInstance.getChordFromKeyScale(key, scale);

		if (foundChord == null) {
			// Debugging details
			System.out.println("Chord not found");
		} else {

			// Used when feature 2 has been triggered
			if (inversionFeature == true) {
				// Create for instance of feature 2
				nextInversionState = components.customJButton(70, 40, "Next Inversion", this, 24,
						Color.decode("#B8B8B8"));
				prevInversionState = components.customJButton(70, 40, "Prev Inversion", this, 24,
						Color.decode("#B8B8B8"));

				// Create inversion action commands in main bar
				createCommandButtons("feature 2", Color.decode("#303030"));

				if (checkIfEndInversion()) {

					// Keep button pressed down only when color mode is on and
					// user reached last inversion when its final next or
					// previous inversion.
					if (components.getColorToggleStatus()) {
						colorModeState.setSelected(true);
					}

					inversionCounter = 1;
					Chord getFirstInversion = chordInstance.getFirstInversion();
					chordInstance.storeCurrentInversion(getFirstInversion);
					foundChord = chordInstance.getCurrentInversion();

					// Constrain on y axis puts button on next column but at 0 x
					// axis
					actionBarConstraints = components.conditionalConstraints(1, 1, 0, 1, GridBagConstraints.HORIZONTAL);
					actionBarConstraints.gridwidth = 1;

					actionHolder.add(nextInversionState, actionBarConstraints);
				}

				else {
					// When previous pressed, this triggers resets until
					// previous pressed again. Other code prevents reverse
					// inversion
					if (previousInversion == true) {

						// Keep button pressed down only when color mode is on.
						// Previous inversion creates new panel layout.
						// Function still works without it, but missing
						// aesthetic is confusing
						if (components.getColorToggleStatus()) {
							colorModeState.setSelected(true);
						}

						previousInversion = false;
						foundChord = chordInstance.getCurrentInversion();
					}

					else {
						chordInstance.chordInversion(foundChord);
						foundChord = chordInstance.getCurrentInversion();
					}

					// Store first inversion when reach the end inversion to
					// reset it
					if (inversionCounter == 1) {
						chordInstance.storeFirstInversion(foundChord);
					}

					actionBarConstraints = components.conditionalConstraints(1, 1, 0, 1, GridBagConstraints.HORIZONTAL);
					actionBarConstraints.gridwidth = 1;
					actionHolder.add(nextInversionState, actionBarConstraints);

					if (inversionCounter > 1) {
						// Keep button pressed down only when color mode is on.
						// Next inversion creates new panel layout.
						// Function still works without it, but missing
						// aesthetic is confusing
						if (components.getColorToggleStatus()) {
							colorModeState.setSelected(true);
						}
						actionBarConstraints = components.conditionalConstraints(1, 1, 1, 1,
								GridBagConstraints.HORIZONTAL);
						actionBarConstraints.gridwidth = 1;
						actionHolder.add(prevInversionState, actionBarConstraints);
					}
				}
				baseSummary("Chord Inversions", Color.decode("#303030"), Color.decode("#505050"));
			} else {
				createCommandButtons("feature 1", Color.decode("#303030"));
				baseSummary("Chord Notes", Color.decode("#303030"), Color.decode("#505050"));
			}
			chordSummary();
		}
	}

	public void chordSummary() {
		String removedOctave = foundChord.getChordNotes().get(0).getName();
		removedOctave = removedOctave.substring(0, removedOctave.length() - 1);

		contentTextArea.append("\nChord Root: " + removedOctave + "\n");
		contentTextArea.append("Chord Name: " + removedOctave + foundChord.getChordName() + "\n\n");
		contentTextArea.append("Chord Notes: ");

		for (Note aNote : foundChord.getChordNotes()) {
			String noteNoOctave = aNote.getName();
			noteNoOctave = noteNoOctave.substring(0, noteNoOctave.length() - 1);
			contentTextArea.append(noteNoOctave + " | ");
		}
		contentTextArea.append("\n");
		if (inversionFeature == true) {
			contentTextArea.append("Number of inversions: " + inversionCounter + "\n");
		} else {
			for (Chord.chordNoteNames n : Chord.chordNoteNames.values()) {
				if (n.toString().equals("seven") || n.toString().equals("nine") || n.toString().equals("eleven")
						|| n.toString().equals("thirteen")) {
					String[] getNotes = n.getChord();

					for (String aString : getNotes) {
						getNotes = aString.split(",");

						contentTextArea.append(Arrays.toString(getNotes) + "\n");
					}
					break;
				} else if (n.toString().equals(foundChord.getChordName())) {
					String[] getNotes = n.getChord();

					for (String aString : getNotes) {
						getNotes = aString.split(",");

						contentTextArea.append(Arrays.toString(getNotes) + "\n");
					}
					break;
				}
			}
		}
		contentTextArea.append("\n");
	}

	public void baseSummary(String areaName, Color innerColor, Color areaColor) {
		innerContent = new JPanel(new GridBagLayout());
		innerContent.setBackground(innerColor);

		contentTextArea = new JTextArea();
		if (pageState == 3 && chordFeature || pageState == 3 && inversionFeature) {
			contentTextArea.setFont(new Font("Tahoma", Font.BOLD, 22));
		}

		else if (pageState == 3 && scalesFeature || pageState == 4 && relativePitchFeature
				|| pageState == 4 && chordProgressionFeature) {
			contentTextArea.setFont(new Font("Tahoma", Font.BOLD, 21));
		}

		contentTextArea.setName(areaName);
		contentTextArea.setBackground(areaColor);
		contentTextArea.setForeground(Color.WHITE);
		contentTextArea.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE));
		innerContent.setPreferredSize(new Dimension(screenWidth / 2, screenHeight / 2));
		innerContent.setMinimumSize(new Dimension(screenWidth / 2, screenHeight / 2));

		actionBarConstraints = components.conditionalConstraints(1, 1, 0, 0, GridBagConstraints.BOTH);
		actionBarConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		actionBarConstraints.insets = new Insets(10, 10, 0, 0);
		innerContent.add(contentTextArea, actionBarConstraints);

		if (pageState == 2 && chordQuizEnabled || pageState == 2 && scaleQuizEnabled
				|| pageState == 2 && progressionQuizEnabled) {

			// Store quiz text on left side of screen prompt
			actionBarConstraints = components.conditionalConstraints(1, 1, 0, 0, GridBagConstraints.NONE);
			actionBarConstraints.anchor = GridBagConstraints.LINE_START;
			quizHolderContent = new JPanel(new GridBagLayout());

			// Resize text area
			innerContent.setPreferredSize(new Dimension(screenWidth / 3, screenHeight / 2));
			innerContent.setMinimumSize(new Dimension(screenWidth / 3, screenHeight / 2));

			// This colours the background in chord and scale quizzes
			basePanel.setBackground(Color.decode("#303030"));
			quizHolderContent.setBackground(Color.decode("#303030"));
			actionBarConstraints.insets = new Insets(0, 0, 0, 0);
			quizHolderContent.add(innerContent, actionBarConstraints);

			// Store button on right side of screen prompt
			actionBarConstraints = components.conditionalConstraints(1, 1, 1, 0, GridBagConstraints.NONE);
			actionBarConstraints.anchor = GridBagConstraints.PAGE_START;
			actionBarConstraints.insets = new Insets(50, 10, 0, 0);
			quizHolderContent.add(Quizes.getInstance().addPlayQuiz(), actionBarConstraints);

			// Add everything to the base panel screen prompt
			actionBarConstraints = components.conditionalConstraints(1, 1, 0, 1, GridBagConstraints.NONE);
			basePanel.add(quizHolderContent, actionBarConstraints);
			contentTextArea.setFont(new Font("Tahoma", Font.BOLD, 24));
		}

		else {
			actionBarConstraints = components.conditionalConstraints(1, 1, 0, 1, GridBagConstraints.NONE);
			actionBarConstraints.gridwidth = 1;
			if (pageState == 4 && relativePitchFeature) {
				actionBarConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
			} else {
				actionBarConstraints.anchor = GridBagConstraints.LINE_START;

			}
			basePanel.add(innerContent, actionBarConstraints);
		}

	}

	public void scaleSummary() {
		String removedOctave = foundScale.getTonic().getName();
		removedOctave = removedOctave.substring(0, removedOctave.length() - 1);
		contentTextArea.append("Scale Key: " + removedOctave + "\n");
		contentTextArea.append("Scale name: " + foundScale.getScaleName() + "\n\n");
		contentTextArea.append("Scale Notes: ");
		for (Note aNote : foundScale.getScaleNotesList()) {
			String noteInDegreeMinusOctave = aNote.getName();
			noteInDegreeMinusOctave = noteInDegreeMinusOctave.substring(0, noteInDegreeMinusOctave.length() - 1);
			contentTextArea.append(noteInDegreeMinusOctave + " | ");
		}
		contentTextArea.append("\n");
	}

	public void progressionsList() {
		baseSummary("Ordered Chord Progressions", Color.decode("#303030"), Color.decode("#505050"));
		int i = 0;

		contentTextArea.append("Sequence: " + progressionChord.getProgressionName() + " \n\n");

		// Best practice says chord progression should end on root
		for (Chord aChord : progressionChord.getProgressionChords()) {
			contentTextArea.append("Chord Name: " + aChord.getChordName() + " | ");
			String[] chordNames = new String[aChord.getChordNotes().size()];
			for (Note aNote : aChord.getChordNotes()) {
				chordNames[i++] = aNote.getName();
			}
			String chordNotesString = Arrays.toString(chordNames);
			chordNotesString = chordNotesString.substring(1, chordNotesString.length() - 2);
			contentTextArea.append("Chord Notes: " + chordNotesString + "\n");
			i = 0;
		}
	}

	public void scalesOrder() throws InvalidMidiDataException {
		basePanel.setBackground(Color.decode("#303030"));
		if (relativePitchFeature == true && pageState == 4) {
			components.colourMenuPanels(jListInput, Color.decode("#303030"), Color.decode("#505050"));
			if (scaleOrder.contains("Ascending")) {
				createCommandButtons("Ascending feature 4", Color.decode("#303030"));
			} else if (scaleOrder.contains("Descending")) {
				createCommandButtons("Descending feature 4", Color.decode("#303030"));
			}
			baseSummary("Ordered Notes", Color.decode("#303030"), Color.decode("#505050"));
			relativePitch();
		}
	}

	public void scalesChoiceSummary() throws InvalidMidiDataException {
		if (scalesFeature == true) {
			createCommandButtons("feature 3", Color.decode("#303030"));
			basePanel.setBackground(Color.decode("#303030"));
			baseSummary("Scales Notes", Color.decode("#303030"), Color.decode("#505050"));
			scaleSummary();
			generateDegreesText();
		}
	}

	/**
	 * Prepares an element to be returned from an argument collection.
	 * Uses include retrieving a given quiz's target answer to be assigned 
	 * to its play button. This use works by returning a value that has been 
	 * randomly chosen between a random major and relative minor inputs.
	 */
	public static <T> T random(Collection<T> coll) {
		int num = (int) (Math.random() * coll.size());
		for (T t : coll)
			if (--num < 0)
				return t;
		throw new AssertionError();
	}

	public void generateRandomPitch() {
		Collection<Integer> currentScaleIntervels = ListOfScales.getInstance().getScalePitchValues();
		Integer guestInterval = random(currentScaleIntervels);
		for (Entry<String, Note> entry : Note.getNotesMap().entrySet()) {
			if (entry.getValue().getPitch() == guestInterval) {
				foundInterval = Note.getNotesMap().get(entry.getKey());
				break;
			}
		}
	}

	public void playNextScaleInterval(int index) throws InvalidMidiDataException {
		Note aNote = foundScale.getScaleNotesList().get(index);
		PlaybackFunctions.playIntervalNote(aNote);
	}

	public void relativePitch() throws InvalidMidiDataException {
		String removedOctave = foundScale.getTonic().getName();
		removedOctave = removedOctave.substring(0, removedOctave.length() - 1);
		contentTextArea.append("Scale Tonic: " + removedOctave + "\n");
		contentTextArea.append("Scale name: " + foundScale.getScaleName() + "\n\n");
		String removeOctave = foundScale.getScaleNotesList().get(0).getName();
		contentTextArea.append("Reference pitch: " + removeOctave);

		ArrayList<Integer> givenScaleNotePitches = new ArrayList<Integer>();
		for (Note aNote : foundScale.getScaleNotesList()) {
			givenScaleNotePitches.add(aNote.getPitch());

		}
		PlaybackFunctions.setIndexCounter(givenScaleNotePitches.size() - 1);
		Collection<Integer> givenScaleNotes = givenScaleNotePitches;

		// Store for later individual traversal to aid interval recognition
		ListOfScales.getInstance().currentScalePitchValues(givenScaleNotes);

		// Save initial random interval
		generateRandomPitch();

	}

	public void createCommandButtons(String state, Color buttonsBacking) {
		actionHolder = new JPanel(new GridBagLayout());
		actionHolder.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		actionHolder.setBackground(buttonsBacking);
		actionHolder.setPreferredSize(new Dimension((int) basePanel.getPreferredSize().getWidth(), screenHeight / 10));
		actionHolder.setMinimumSize(new Dimension((int) basePanel.getMinimumSize().getWidth(), screenHeight / 10));

		// Create Previous button in action bar
		actionBarConstraints = components.conditionalConstraints(1, 1, 0, 0, GridBagConstraints.HORIZONTAL);
		actionBarConstraints.gridwidth = 1;
		actionBarConstraints.insets = new Insets(0, 0, 0, 0);
		prevState = components.customJButton(70, 40, "Previous", this, 24, Color.decode("#B8B8B8"));
		actionHolder.add(prevState, actionBarConstraints);

		// Create Home button in action bar
		actionBarConstraints = components.conditionalConstraints(1, 1, 1, 0, GridBagConstraints.HORIZONTAL);
		actionBarConstraints.gridwidth = 1;
		homeState = components.customJButton(70, 40, "Home", this, 24, Color.decode("#B8B8B8"));
		actionHolder.add(homeState, actionBarConstraints);

		// Create Color button in action bar
		if (!state.equals("stages") && !state.equals("Quiz")) {
			actionBarConstraints = components.conditionalConstraints(1, 1, 2, 0, GridBagConstraints.HORIZONTAL);
			actionBarConstraints.gridwidth = 1;
			colorModeState = components.customActionJToggleButton(70, 40, "Color Mode", this, 24,
					Color.decode("#B8B8B8"));
			actionHolder.add(colorModeState, actionBarConstraints);
		}

		// Create play chord button in action bar
		if (state.equals("feature 1") || state.equals("feature 2")) {
			actionBarConstraints = components.conditionalConstraints(1, 1, 5, 0, GridBagConstraints.HORIZONTAL);
			actionBarConstraints.gridwidth = 1;
			playChordState = components.customJButton(70, 40, "Play Chord", this, 24, Color.decode("#B8B8B8"));
			actionHolder.add(playChordState, actionBarConstraints);
		}

		else if (state.equals("feature 3")) {
			actionBarConstraints = components.conditionalConstraints(1, 1, 5, 0, GridBagConstraints.HORIZONTAL);
			actionBarConstraints.gridwidth = 1;
			playScaleState = components.customJButton(70, 40, "Play Scale", this, 24, Color.decode("#B8B8B8"));
			actionHolder.add(playScaleState, actionBarConstraints);
		}

		// Create random interval button in action commands
		else if (state.contains("feature 4")) {
			actionBarConstraints = components.conditionalConstraints(1, 1, 5, 0, GridBagConstraints.HORIZONTAL);
			actionBarConstraints.gridwidth = 1;
			playRandomIntervalState = components.customJButton(70, 40, "Random Interval", this, 24,
					Color.decode("#B8B8B8"));
			actionHolder.add(playRandomIntervalState, actionBarConstraints);

			if (state.contains("Ascending")) {
				actionBarConstraints = components.conditionalConstraints(1, 1, 6, 0, GridBagConstraints.HORIZONTAL);
				actionBarConstraints.gridwidth = 1;
				playNextIntervalState = components.customJButton(70, 40, "Next Interval", this, 24,
						Color.decode("#B8B8B8"));
				actionHolder.add(playNextIntervalState, actionBarConstraints);
			} else if (state.contains("Descending")) {
				actionBarConstraints = components.conditionalConstraints(1, 1, 6, 0, GridBagConstraints.HORIZONTAL);
				actionBarConstraints.gridwidth = 1;
				playPrevIntervalState = components.customJButton(70, 40, "Prev Interval", this, 24,
						Color.decode("#B8B8B8"));
				actionHolder.add(playPrevIntervalState, actionBarConstraints);
			}

			actionBarConstraints = components.conditionalConstraints(1, 1, 7, 0, GridBagConstraints.HORIZONTAL);
			actionBarConstraints.gridwidth = 1;
			scaleRangeColorModeState = components.customActionJToggleButton(70, 40, "Range color mode", this, 24,
					Color.decode("#B8B8B8"));
			actionHolder.add(scaleRangeColorModeState, actionBarConstraints);
		}

		// Create play progression button in action command
		if (state.equals("feature 5")) {
			actionBarConstraints = components.conditionalConstraints(1, 1, 5, 0, GridBagConstraints.HORIZONTAL);
			actionBarConstraints.gridwidth = 1;
			playProgression = components.customJButton(70, 40, "Play Progression", this, 24, Color.decode("#B8B8B8"));
			actionHolder.add(playProgression, actionBarConstraints);

			// Create arpeggios drop box
			actionBarConstraints = components.conditionalConstraints(1, 1, 6, 0, GridBagConstraints.HORIZONTAL);
			actionBarConstraints.gridwidth = 1;
			actionHolder.add(addApreggiosBox(), actionBarConstraints);
		}

		actionBarConstraints = components.conditionalConstraints(1, 1, 0, 0, GridBagConstraints.NONE);
		actionBarConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		basePanel.add(actionHolder, actionBarConstraints);

	}

	public void generateDegreesText() {
		pentatonicDegrees[] penDegrees = pentatonicDegrees.values();
		hexaTonicDegrees[] hexDegrees = hexaTonicDegrees.values();
		heptTonicDegree[] heptDegrees = heptTonicDegree.values();
		octaTonicDegree[] octDegrees = octaTonicDegree.values();
		ascendingSolfege[] ascSolfege = ascendingSolfege.values();
		descendingSolfege[] descSolfege = descendingSolfege.values();

		int i = 0;
		int scaleDegree = 0;
		for (Note aNote : foundScale.getScaleNotesList()) {
			String noteInDegreeMinusOctave = aNote.getName();
			noteInDegreeMinusOctave = noteInDegreeMinusOctave.substring(0, noteInDegreeMinusOctave.length() - 1);
			switch (foundScale.getScaleNotesList().size()) {

			case 6:
				contentTextArea
						.append("\nDegree " + ++scaleDegree + ": " + penDegrees[i] + " | " + noteInDegreeMinusOctave);
				break;
			case 7:
				contentTextArea
						.append("\nDegree " + ++scaleDegree + ":" + hexDegrees[i] + " | " + noteInDegreeMinusOctave);
				break;
			case 8:
				contentTextArea
						.append("\nDegree " + ++scaleDegree + ":" + heptDegrees[i] + " | " + noteInDegreeMinusOctave);
				break;
			case 9:
				contentTextArea
						.append("\nDegree " + ++scaleDegree + ":" + octDegrees[i] + " | " + noteInDegreeMinusOctave);
				break;
			case 12:
				if (foundScale.getScaleName().contains("Ascending")) {
					contentTextArea.append("\nSolfege: " + ascSolfege[i] + " | " + noteInDegreeMinusOctave);
				} else if (foundScale.getScaleName().contains("Descending")) {
					contentTextArea.append("\nSolfege: " + descSolfege[i] + " | " + noteInDegreeMinusOctave);
					break;
				}
			}
			i++;
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	public ArrayList<Component> listOfPanels() {
		Component[] panels = basePanel.getComponents();
		ArrayList<Component> tolist = new ArrayList<Component>(Arrays.asList(panels));
		return tolist;
	}

	public void updateDisplay(String name, BufferedImage aBuff, int index, int xPos, int yPos, int anchor)
			throws IOException {
		int width = basePanel.getPreferredSize().width / 3;
		int height = basePanel.getPreferredSize().height / 3;
		JPanel replacePanel = components.customizeFeaturePanel(width, height, this, aBuff, name);
		replacePanel.setBackground(Color.decode("#FFFFFF"));
		basePanel.remove(basePanel.getComponent(index));
		actionBarConstraints = components.conditionalConstraints(1, 1, xPos, yPos, GridBagConstraints.NONE);
		actionBarConstraints.anchor = anchor;
		basePanel.add(replacePanel, actionBarConstraints, index);
		basePanel.revalidate();
		basePanel.repaint();
	}

	public void conditionalImages(String featureChosen, String file, String panelName) throws IOException {
		try {
			switch (featureChosen) {
			case "Chords":
				BufferedImage chordAltBuff = ImageIO.read(new File(file));
				updateDisplay(panelName, chordAltBuff, 0, 0, 0, GridBagConstraints.WEST);
				break;
			case "Inversions":
				BufferedImage inversionAltBuff = ImageIO.read(new File(file));
				updateDisplay(panelName, inversionAltBuff, 1, 0, 0, GridBagConstraints.NORTH);
				break;
			case "Scales":
				BufferedImage scalesAltBuff = ImageIO.read(new File(file));
				updateDisplay(panelName, scalesAltBuff, 2, 0, 0, GridBagConstraints.EAST);
				break;
			case "Relative":
				BufferedImage relativeAltBuff = ImageIO.read(new File(file));
				updateDisplay(panelName, relativeAltBuff, 3, 0, 2, GridBagConstraints.WEST);
				break;
			case "Progressions":
				BufferedImage progressionAltBuff = ImageIO.read(new File(file));
				updateDisplay(panelName, progressionAltBuff, 4, 0, 2, GridBagConstraints.SOUTH);
				break;
			case "Genres":
				BufferedImage genreAltBuff = ImageIO.read(new File(file));
				updateDisplay(panelName, genreAltBuff, 5, 0, 2, GridBagConstraints.EAST);
				break;
			case "Quizes":
				BufferedImage quizAltBuff = ImageIO.read(new File(file));
				updateDisplay(panelName, quizAltBuff, 6, 0, 1, GridBagConstraints.CENTER);
				break;
			case "Chords Quiz":
				BufferedImage chordQuizAltBuff = ImageIO.read(new File(file));
				updateDisplay(panelName, chordQuizAltBuff, 1, 0, 0, GridBagConstraints.WEST);
				break;
			case "Scales Quiz":
				BufferedImage scaleQuizAltBuff = ImageIO.read(new File(file));
				updateDisplay(panelName, scaleQuizAltBuff, 2, 0, 0, GridBagConstraints.EAST);
				break;
			case "Progressions Quiz":
				BufferedImage progressionQuizAltBuff = ImageIO.read(new File(file));
				updateDisplay(panelName, progressionQuizAltBuff, 3, 0, 0, GridBagConstraints.CENTER);
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		Object obj = arg0.getSource();

		if (VirtualKeyboard.getInstance().getLearnFrame() != null) {
			if (obj.getClass().equals(JPanel.class)) {
				if (listOfPanels().contains(obj)) {
					if (!highlighted) {
						JPanel check = (JPanel) obj;
						String featureChosen = check.getName();
						try {
							switch (featureChosen) {
							case "Chords":
								conditionalImages(featureChosen, "src/Images/Chord selected tile image.png", "Chords");
								break;
							case "Inversions":
								conditionalImages(featureChosen, "src/Images/Chord inversion selected tile image.png",
										"Inversions");
								break;
							case "Scales":
								conditionalImages(featureChosen, "src/Images/Scale selected tile image.png", "Scales");
								break;
							case "Relative":
								conditionalImages(featureChosen, "src/Images/Relative selected tile image.png",
										"Relative");
								break;
							case "Progressions":
								conditionalImages(featureChosen, "src/Images/Progression selected tile image.png",
										"Progressions");
								break;
							case "Genres":
								conditionalImages(featureChosen, "src/Images/Genres selected tile image.png", "Genres");
								break;
							case "Quizes":
								conditionalImages(featureChosen, "src/Images/Quizes selected tile image.png", "Quizes");
								break;
							case "Chords Quiz":
								conditionalImages(featureChosen, "src/Images/Quizes 1 selected tile image.png",
										"Chords Quiz");
								break;
							case "Scales Quiz":
								conditionalImages(featureChosen, "src/Images/Quizes 3 selected tile image.png",
										"Scales Quiz");
								break;
							case "Progressions Quiz":
								conditionalImages(featureChosen, "src/Images/Quizes 2 selected tile image.png",
										"Progressions Quiz");
								break;
							}
							highlighted = true;

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// if(obj.getClass().equals(JList.class)){
		// JList <String> temp = (JList)obj;
		// components.colourMenuPanels(temp, Color.green, Color.red);
		// }
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		Object obj = arg0.getSource();
		if (VirtualKeyboard.getInstance().getLearnFrame() != null) {
			if (obj.getClass().equals(JPanel.class)) {

				if (listOfPanels().contains(obj)) {
					if (highlighted) {
						JPanel check = (JPanel) obj;
						String featureChosen = check.getName();
						try {
							switch (featureChosen) {
							case "Chords":
								conditionalImages(featureChosen, "src/Images/Chord tile image.png", "Chords");
								break;
							case "Inversions":
								conditionalImages(featureChosen, "src/Images/Chord inversion tile image.png",
										"Inversions");
								break;
							case "Scales":
								conditionalImages(featureChosen, "src/Images/Scale tile image.png", "Scales");
								break;
							case "Relative":
								conditionalImages(featureChosen, "src/Images/Reletive pitch tile image.png",
										"Relative");
								break;
							case "Progressions":
								conditionalImages(featureChosen, "src/Images/Chord Progression tile image.png",
										"Progressions");
								break;
							case "Genres":
								conditionalImages(featureChosen, "src/Images/Genres tile image.png", "Genres");
								break;
							case "Quizes":
								conditionalImages(featureChosen, "src/Images/Quizes tile image.png", "Quizes");
								break;
							case "Chords Quiz":
								conditionalImages(featureChosen, "src/Images/Chord quiz tile image.png", "Chords Quiz");
								break;
							case "Scales Quiz":
								conditionalImages(featureChosen, "src/Images/Scale quiz tile image.png", "Scales Quiz");
								break;
							case "Progressions Quiz":
								conditionalImages(featureChosen, "src/Images/Chord progression quiz tile image.png",
										"Progressions Quiz");
								break;
							}
							highlighted = false;

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void recordInversions(Chord saveInversion) {
		if (!inversionChords.contains(saveInversion)) {
			inversionChords.add(saveInversion);
		}
	}

	public Chord getLastInversionFromList(int index) {
		return inversionChords.get(index);
	}

	public void accessModelFeatures(String panelChoice) {

		try {

			switch (panelChoice) {
			case "Chords":
				featureState = 1;
				pageState = 1;
				chordFeature = true;
				VirtualKeyboard.getInstance().updateScreenPrompt();
				break;
			case "Inversions":
				// Change to feature 1 to re use it
				featureState = 1;
				pageState = 1;
				inversionFeature = true;
				VirtualKeyboard.getInstance().updateScreenPrompt();

				break;
			case "Scales":
				featureState = 1;
				scalesFeature = true;
				pageState = 1;
				VirtualKeyboard.getInstance().updateScreenPrompt();
				break;

			// Play relative pitch
			case "Relative":
				featureState = 1;
				relativePitchFeature = true;
				pageState = 1;
				SwingComponents.getInstance().displayScalesOnlyState(true);
				VirtualKeyboard.getInstance().updateScreenPrompt();
				break;

			// Play Chord Progressions
			case "Progressions":
				featureState = 1;
				chordProgressionFeature = true;
				pageState = 1;
				VirtualKeyboard.getInstance().updateScreenPrompt();
				break;

			case "Genres":
				featureState = 1;
				createSong = true;
				pageState = 1;
				VirtualKeyboard.getInstance().updateScreenPrompt();
				break;

			case "Quizes":
				quizEnabled = true;
				featureState = 1;
				pageState = 1;
				VirtualKeyboard.getInstance().updateScreenPrompt();
				break;

			case "Chords Quiz":
				chordQuizEnabled = true;
				featureState = 1;
				pageState = 2;
				VirtualKeyboard.getInstance().updateScreenPrompt();
				break;

			case "Scales Quiz":
				scaleQuizEnabled = true;
				featureState = 1;
				pageState = 2;
				VirtualKeyboard.getInstance().updateScreenPrompt();
				break;

			case "Progressions Quiz":
				progressionQuizEnabled = true;
				featureState = 1;
				pageState = 2;
				VirtualKeyboard.getInstance().updateScreenPrompt();
				break;
			}
			// After user has clicked on selected feature panel
			highlighted = false;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	public void handleHomeStateButtonActions() throws InvalidMidiDataException {
		// If the user returns to the home screen while the color mode is still
		// enabled

		// ADDED relativePitchFeature addition
		if (relativePitchFeature == true && components.getColorToggleStatus()
				|| inversionFeature == true && components.getColorToggleStatus()
				|| scalesFeature == true && components.getColorToggleStatus()
				|| chordFeature == true && components.getColorToggleStatus()
				|| chordProgressionFeature == true && components.getColorToggleStatus()
				|| relativePitchFeature == true && components.getRangeColorToggleStatus()
				|| createSong == true && components.getColorToggleStatus()) {

			PlaybackFunctions.resetChordsColor();
			// Still do below conditions for other conditions below
			inversionFeature = false;
			scalesFeature = false;
			chordFeature = false;
			relativePitchFeature = false;
			components.changeColorToggle(false); // Turn off color mode
			components.changeRangeColorToggle(false); // Turn off color mode
			reverseOnce = false;
			chordProgressionFeature = false;
			createSong = false;
			stopPlayback = true;
			// Reset choice
			preg.storeCurrentChoice("Clear");
			PlaybackFunctions.setIndexCounter(0);
			PlaybackFunctions.setRandomIntervalCounter(0);
			PlaybackFunctions.setMelodicIndexCounter(0);
			PlaybackFunctions.emptyNotes();
		}

		else { // Carry on as if the color mode has been disabled in each
				// feature
			PlaybackFunctions.setIndexCounter(0);
			PlaybackFunctions.setRandomIntervalCounter(0);
			PlaybackFunctions.setMelodicIndexCounter(0);
			PlaybackFunctions.emptyNotes();
			// Will apply to all features

			if (quizEnabled) {
				quizEnabled = false;
				chordQuizEnabled = false;
				scaleQuizEnabled = false;
				progressionQuizEnabled = false;
			}

			if (inversionFeature == true) {
				inversionFeature = false; // turn off inversion feature on home
											// return
				PlaybackFunctions.resetChordsColor();
				stopPlayback = true;
			}
			// ADDED relativePitchFeature addition - might cause problems
			else if (scalesFeature == true || relativePitchFeature == true) {
				scalesFeature = false;
				relativePitchFeature = false;
				// Rest scales store when going straight home from 3rd and 2nd
				// page
				Scale.resetScalesLists();
				PlaybackFunctions.resetChordsColor();
				// Stop playback thread
				stopPlayback = true;

			} else if (chordFeature == true) {
				chordFeature = false;
				PlaybackFunctions.resetChordsColor();
				stopPlayback = true;
			}
			// Might need to tamper with this
			else if (chordProgressionFeature == true) {
				chordProgressionFeature = false;
				PlaybackFunctions.resetChordsColor();

				progressionChord.getProgressionChords().clear();
				stopPlayback = true;
				// Reset choice
				preg.storeCurrentChoice("Clear");
			}

			else if (createSong) {
				createSong = false;
				PlaybackFunctions.resetChordsColor();
				stopPlayback = true;

			}

		}
		pageState = 0;
		featureState = 0;
		VirtualKeyboard.getInstance().updateScreenPrompt();
	}

	@Override
	public void mousePressed(MouseEvent optionPressed) {
		Object obj = optionPressed.getSource();

		try {


			if (obj.getClass().equals(JPanel.class)) {
				JPanel check = (JPanel) obj;
				String featureChosen = check.getName();
				switch (featureChosen) {

				case "Chords":
					accessModelFeatures("Chords");
					break;
				case "Inversions":
					accessModelFeatures("Inversions");
					break;
				case "Scales":
					accessModelFeatures("Scales");
					break;
				case "Relative":
					accessModelFeatures("Relative");
					break;
				case "Progressions":
					accessModelFeatures("Progressions");
					break;
				case "Genres":
					accessModelFeatures("Genres");
					break;
				case "Quizes":
					accessModelFeatures("Quizes");
					break;
				case "Chords Quiz":
					accessModelFeatures("Chords Quiz");
					break;
				case "Scales Quiz":
					accessModelFeatures("Scales Quiz");
					break;
				case "Progressions Quiz":
					accessModelFeatures("Progressions Quiz");
					break;
				}
			}

			else if (obj.equals(jListInput)) {
				// 1st page actions
				///////////////////////////////////////////////////////////////////////////////
				if (jListInput.getName().equals("Key Names")) {
					pageState = 2;
					int index = jListInput.locationToIndex(optionPressed.getPoint());

					// Current key from list model in feature state 1's first
					// page
					String note = conditionalModel.getElementAt(index);

					// Only load scales from key on the first page. If the user
					// later clicks previous from the 2nd page,
					// or the home button in page 2 or 3, the list will be
					// deleted and remade.
					/** ADDED 3RD OPTION TO LOAD SUPPORT FOR THE FEATURE */
					if (scalesFeature == true || relativePitchFeature == true || chordProgressionFeature == true) {
						ListOfScales.getInstance().generateScalesNames(note);
					}

					// Used with feature set 1 an 2
					else {
						Chord.storeRoot(note); // concatenate with soon to store
												// chord name to create complete
												// chord
					}
					VirtualKeyboard.getInstance().updateScreenPrompt();
				}

				// 2nd page actions
				///////////////////////////////////////////////////////////////////////////////
				///////////////////////////////////////////////////////////////////////////////
				else if (jListInput.getName().equals("Chord Names")) {
					pageState = 3;
					int index = jListInput.locationToIndex(optionPressed.getPoint());

					// Current chord name from list model in feature state 1's
					// second page
					String chord = conditionalModel.getElementAt(index);

					Chord.storeChordName(chord); // Store to later concatenate
													// with previous stored root
													// note
					// Enable chord and chord inversion playback
					stopPlayback = false;
					VirtualKeyboard.getInstance().updateScreenPrompt();
				}

				else if (jListInput.getName().equals("Scale Names")) {
					pageState = 3;
					int index = jListInput.locationToIndex(optionPressed.getPoint());

					foundScale = Scale.getScaleFromList(index);
					// Store key for quiz options
					Scale.storeScaleKey(foundScale.getTonic().getName());
					Scale.storeCurrentScaleName(foundScale.getScaleName());
					ListOfScales.getInstance().displayedScaleNotes(foundScale);

					// Enable scale start
					stopPlayback = false;
					VirtualKeyboard.getInstance().updateScreenPrompt();
				}

				else if (jListInput.getName().equals("Progressions")) {
					pageState = 3;
					progressionType = jListInput.locationToIndex(optionPressed.getPoint());
					if (progressionType == 1) {
						progressionType = 5;
						// Used in quiz
						ChordProgressionActions.progressionScale(progressionType);
					}
					foundScale = Scale.getScaleFromList(progressionType);
					VirtualKeyboard.getInstance().updateScreenPrompt();
				}

				// EXPERIMENTAL
				else if (jListInput.getName().equals("Genres")) {
					pageState = 3;
					String choice = jListInput.getSelectedValue();
					String key = Chord.getStoredRoot();
					stopPlayback = false;
					Genre.getInstance().createSong(key, choice);

				}

				// 3rd page actions - 4 page features
				///////////////////////////////////////////////////////////////////////////////
				///////////////////////////////////////////////////////////////////////////////

				else if (jListInput.getName().equals("Scale Order")) {
					pageState = 4;
					int index = jListInput.locationToIndex(optionPressed.getPoint());

					// If descending order is chosen
					if (index == 1 && reverseOnce == false) {
						Collections.reverse(foundScale.getScaleNotesList());
						reverseOnce = true;
					}

					else if (index == 0 && reverseOnce == true) {
						Collections.reverse(foundScale.getScaleNotesList());
						reverseOnce = false;
					}
					// Enable Relative Pitch start
					stopPlayback = false;
					scaleOrder = conditionalModel.getElementAt(index);
					VirtualKeyboard.getInstance().updateScreenPrompt();
				}

				else if (jListInput.getName().equals("Sequences")) {
					pageState = 4;
					int index = jListInput.locationToIndex(optionPressed.getPoint());
					String selectedProgression = conditionalModel.getElementAt(index);

					// Enable Chord progression start
					stopPlayback = false;

					// Store chosen progression to use in quiz
					prog.storeCurrentProgressionString(selectedProgression);

					String[] getBits = selectedProgression.split("\\s+");
					progressionChord = prog.makeChordProgression(selectedProgression, foundScale, getBits);

					// Store minor or major based progression to use in quiz
					prog.storeCurrentProgression(progressionChord);
					VirtualKeyboard.getInstance().updateScreenPrompt();
				}

				else if (jListInput.getName().equals("Quiz Answers")) {
					int index = jListInput.locationToIndex(optionPressed.getPoint());
					String selectedProgression = conditionalModel.getElementAt(index);
					Quizes.getInstance().doesAnswerMatch(selectedProgression);
				}

			}

			////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////

			else if (obj.equals(prevState)) {
				--pageState;

				if (pageState == 0) {
					featureState = 0;

					// reset all feature conditions when on welcome screen
					// prompt
					inversionFeature = false;
					scalesFeature = false;
					chordFeature = false;
					relativePitchFeature = false;
					chordProgressionFeature = false;
					createSong = false;
					if (quizEnabled) {
						quizEnabled = false;
					}

					PlaybackFunctions.setIndexCounter(0);
					PlaybackFunctions.setRandomIntervalCounter(0);
					PlaybackFunctions.setMelodicIndexCounter(0);
					PlaybackFunctions.emptyNotes();

				}

				// Used with feature set
				else if (pageState == 1) {
					if (quizEnabled) {
						// Quiz only conditions reset
						chordQuizEnabled = false;
						scaleQuizEnabled = false;
						progressionQuizEnabled = false;
					}
					// ADDING SECOND BOOLEAN MIGHT CAUSE PROBLEM
					if (scalesFeature == true || relativePitchFeature == true) {
						// feature 3 = For each new key selected, prevents over
						// filling list and model
						Scale.resetScalesLists();
					}
				}

				else if (pageState == 2) {

					if (scalesFeature && components.getColorToggleStatus()
							|| scalesFeature && components.getColorToggleStatus() == false) {

						components.changeColorToggle(false);
						// Stop play back of thread
						stopPlayback = true;
						PlaybackFunctions.resetChordsColor();
					}

					else {
						if (inversionFeature == true) {

							// Reset stored inversion for each new root/chord
							// name combination
							inversionChords = new ArrayList<Chord>();
							inversionCounter = 1;
							chordInstance.resetInversion();
							stopPlayback = true;
						}

						// DONT ADD RESET SCALES AT THIS POINT BECAUSE THE
						// LOADED SCALES FROM THE KEY CAN BE
						// USED TO ACCESS THE OTHER SCALES. The scales are
						// loaded on page 1, not page 2, which is this stage

						// If colour mode is on while pressing previous
						if (inversionFeature == true && components.getColorToggleStatus()
								|| chordFeature == true && components.getColorToggleStatus()
								|| scalesFeature == true && components.getColorToggleStatus()) {
							/*** TEST FOR COLOR */
							components.changeColorToggle(false);
							Chord.resetChordsLists(); // Stops list over filling
							PlaybackFunctions.resetChordsColor();
							stopPlayback = true;
						}

						else if (createSong || createSong && components.getColorToggleStatus()) {
							pageState = 1;
							PlaybackFunctions.resetChordsColor();
							components.changeColorToggle(false); 
							// Turn off color mode
						}

						// Whether chordFeature or inversion feature is true
						String temp = Chord.getStoredChord();
						temp = temp.replace(Chord.getStoredChordName(), "");
						Chord.storeRoot(temp);
						stopPlayback = true;
					}
				}

				else if (pageState == 3) {
					if (relativePitchFeature == true
							|| relativePitchFeature == true && components.getColorToggleStatus()) {
						components.changeColorToggle(false);
						if (relativePitchFeature == true && components.getRangeColorToggleStatus()) {
							components.changeRangeColorToggle(false);
						}
						PlaybackFunctions.resetChordsColor();
						reverseOnce = false;
						// Disable relative pitch back
						stopPlayback = true;
					}
					// Either its own, turn it off, clear list and trigger stop
					// thread, or make false again, clear list and trigger stop
					// thread
					else if (chordProgressionFeature && components.getColorToggleStatus()
							|| chordProgressionFeature && components.getColorToggleStatus() == false) {
						progressionChord.getProgressionChords().clear();
						components.changeColorToggle(false);
						stopPlayback = true;
						// Reset choice
						preg.storeCurrentChoice("Clear");
						

						/** Possible problem with progression notes colour */
						// This remove color chord when user changes apreggio,
						// has color mode one, plays button,
						// and the presses previous
						PlaybackFunctions.resetChordsColor();
					}

				}

				VirtualKeyboard.getInstance().updateScreenPrompt();
			}

			else if (obj.equals(homeState)) {
				handleHomeStateButtonActions();
			}

			else if (obj.equals(playChordState)) {

				new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							if (!stopPlayback) {
								// Feature 2 play back
								if (inversionFeature == true) {
									Chord playCurrentInversion = chordInstance.getCurrentInversion();
									PlaybackFunctions.playAnyChordLength(playCurrentInversion);
									String editedNote = TTS.getInstance().theoryToTTS(playCurrentInversion);
									TTS.getInstance().prepareFunction("Chord", editedNote);
								}

								else {
									// Feature 1 play back
									PlaybackFunctions.playAnyChordLength(foundChord);
									String editedNote = TTS.getInstance().theoryToTTS(foundChord);
									TTS.getInstance().prepareFunction("Chord", editedNote);
								}
							}
						} catch (EngineException | EngineStateError | IllegalArgumentException | AudioException
								| InvalidMidiDataException | InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}

			else if (obj.equals(playScaleState)) {
				// Feature 3 play back
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							// int i = 0;
							for (Note aNote : foundScale.getScaleNotesList()) {
								// End thread if user leaves progressions page
								if (!stopPlayback) {
									PlaybackFunctions.playIntervalNote(aNote);
									String editedNote = aNote.getName();
									editedNote = editedNote.substring(0, editedNote.length() - 1);
									editedNote = editedNote.contains("#") ? editedNote.replace("#", " SHARP")
											: editedNote;

									PlaybackFunctions.timeDelay(500);
									TTS.getInstance().prepareFunction("Scale", editedNote);
									PlaybackFunctions.timeDelay(1000);
								}
								// When scale has finished played, the last note
								// in the scale has its colour reset.If the user presses the home
								// or return button while its playing, code has been assigned to
								// reset all notes in a chord, so this method does not need to be called.
								if(PlaybackFunctions.getStoredPreNotes().size() >0){
								PlaybackFunctions.resetLastNoteColor();
								}
							}
						} catch (InvalidMidiDataException | EngineException | EngineStateError
								| IllegalArgumentException | AudioException | InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}

			////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////
			else if (obj.equals(playRandomIntervalState)) {
				generateRandomPitch();

				//////////////////////////////////////
				if (components.getRangeColorToggleStatus()) {
					components.changeColorToggle(false);
					components.changeRangeColorToggle(false);
					PlaybackFunctions.resetScaleDisplayColor();
					scaleRangeColorModeState.setSelected(false);
					if (PlaybackFunctions.getStoredPreNotes().size() > 0) {
						PlaybackFunctions.emptyNotes();
					}
					PlaybackFunctions.setIndexCounter(0);
					PlaybackFunctions.setRandomIntervalCounter(0);
					PlaybackFunctions.setMelodicIndexCounter(0);
				}
				// First instance of random button
				if (messages.getRandomState() == false && PlaybackFunctions.getMelodicIndexCounter() == 0
						&& !scaleDisplayIsReset) {
					messages.storeRandomState(true);
				}

				else if (messages.getRandomState() == false && PlaybackFunctions.getMelodicIndexCounter() > 0) {
					PlaybackFunctions.resetLastNoteColor();
					PlaybackFunctions.setRandomIntervalCounter(PlaybackFunctions.getMelodicIndexCounter());
					messages.storeRandomState(true);
				}

				if (messages.getMelodyInterval()) {
					randomTriggered = true;
					messages.storeMelodyInterval(false);
				}

				if (randomTriggeredReference == true
						&& !foundInterval.getName().equals(foundScale.getTonic().getName())) {
					int lastIndex = foundScale.getTonic().getName().length() > 2 ? 66 : 64;
					contentTextArea.replaceRange("", 45, lastIndex);
					randomTriggeredReference = false;
				}

				/** THIS ADDES THE TEXT BUT MIGHT CAUSE PROBLEMS */
				if (foundInterval.getName().equals(foundScale.getTonic().getName())) {
					if (!randomTriggeredReference) {
						String removeOctave = foundScale.getScaleNotesList().get(0).getName();
						contentTextArea.append("Reference pitch: " + removeOctave);
					}
					randomTriggeredReference = true;
				}

				else if (foundInterval.getName().equals(foundScale.getTonic().getName()) && intervalCounter == 0) {
					int lastIndex = foundScale.getTonic().getName().length() > 2 ? 66 : 64;
					// contentTextArea.replaceRange("", 45, lastIndex);
					randomTriggeredReference = false;
				}

				PlaybackFunctions.playIntervalNote(foundInterval);
			}
			////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////

			else if (obj.equals(playNextIntervalState)) {

				if (components.getRangeColorToggleStatus()) {
					components.changeColorToggle(false);
					components.changeRangeColorToggle(false);
					PlaybackFunctions.resetScaleDisplayColor();
					scaleRangeColorModeState.setSelected(false);
					if (PlaybackFunctions.getStoredPreNotes().size() > 0) {
						PlaybackFunctions.emptyNotes();
					}
					PlaybackFunctions.setIndexCounter(0);
					PlaybackFunctions.setRandomIntervalCounter(0);
					PlaybackFunctions.setMelodicIndexCounter(0);
					intervalCounter = 0;
				}

				else if (messages.getRandomState()) {
					PlaybackFunctions.resetLastNoteColor();
					PlaybackFunctions.setMelodicIndexCounter(PlaybackFunctions.currentRandomIntervalCounter());
					intervalCounter = 0;
					randomTriggered = true;
					messages.storeRandomState(false);
					messages.storeMelodyInterval(true);
				}

				// Partially Solve colour timings
				if (intervalCounter == foundScale.getScaleNotesList().size() && randomTriggered == true) {
					intervalCounter = 0;
					resetReference = true;
				} else if (intervalCounter == foundScale.getScaleNotesList().size() - 1 && randomTriggered == false) {
					PlaybackFunctions.resetLastNoteColor();
					PlaybackFunctions.setMelodicIndexCounter(0);
					intervalCounter = PlaybackFunctions.getMelodicIndexCounter();
					resetReference = true;
				}

				// Might involve random triggered
				if (randomTriggered == false) {
					intervalCounter = PlaybackFunctions.getMelodicIndexCounter();
				}

				// Handle reference note text
				if (intervalCounter == 0 || intervalCounter == 1) {
					// Done on full cycle and is the note after the key
					if (intervalCounter == 1 && resetReference == true) {
						resetReference = false;
					}
				}
				// Used to differentiate progressive interval and random
				// singular note
				MidiMessageTypes.getInstance().storeIntervalStateID(2);
				if (messages.getMelodyInterval() == false) {
					messages.storeMelodyInterval(true);
				}

				playNextScaleInterval(intervalCounter);
				if (randomTriggered) {
					intervalCounter++;
				}
			}
			////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////

			else if (obj.equals(nextInversionState)) {
				// Stored current inversion as last each next inversion
				if (components.getColorToggleStatus()) {
					PlaybackFunctions.resetChordsColor();
				}
				++inversionCounter;
				recordInversions(foundChord);
				VirtualKeyboard.getInstance().updateScreenPrompt();
			}

			// Previous Inversion button action
			///////////////////////////////////////////////////////////////////////////
			else if (obj.equals(prevInversionState)) {

				if (components.getColorToggleStatus()) {
					PlaybackFunctions.resetChordsColor();
				}

				// Get the last stored inversion
				previousInversion = true;
				--inversionCounter;
				Chord lastInversion = getLastInversionFromList(inversionCounter - 1);
				chordInstance.storeCurrentInversion(lastInversion);
				VirtualKeyboard.getInstance().updateScreenPrompt();
			}

			// Colour button actions
			///////////////////////////////////////////////////////////////////////////
			else if (obj.equals(colorModeState)) {

				// Turn on colour mode
				if (!components.getColorToggleStatus()) {
					components.changeColorToggle(true);

					// Change 3 - Might cause problems
					//////////////////////////////////////
					if (components.getRangeColorToggleStatus()) {
						components.changeColorToggle(true);
						components.changeRangeColorToggle(false);
						PlaybackFunctions.resetScaleDisplayColor();
						scaleRangeColorModeState.setSelected(false);
						if (PlaybackFunctions.getStoredPreNotes().size() > 0) {
							PlaybackFunctions.emptyNotes();
						}
						PlaybackFunctions.setIndexCounter(0);
						PlaybackFunctions.setRandomIntervalCounter(0);
						PlaybackFunctions.setMelodicIndexCounter(0);
					}
				}
				// Turn off colour mode
				else if (components.getColorToggleStatus()) {
					components.changeColorToggle(false);

					// Might need to remove
					if (relativePitchFeature == true) {
						if (PlaybackFunctions.currentRandomIntervalCounter() >= 1) {
							PlaybackFunctions.resetLastNoteColor();
							intervalCounter = 0;
						} else if (PlaybackFunctions.getMelodicIndexCounter() >= 1) {
							intervalCounter = 0;
							PlaybackFunctions.resetLastNoteColor();
						}
					}

					// Chord base features
					else {
						PlaybackFunctions.resetChordsColor();
					}
				}
			}

			else if (obj.equals(scaleRangeColorModeState)) {
				PlaybackFunctions.playOrDisplay(false);
				// Turn on color mode
				if (!components.getRangeColorToggleStatus()) {

					if (components.getColorToggleStatus()) {
						if (PlaybackFunctions.getStoredPreNotes().size() > 0) {
							PlaybackFunctions.resetLastNoteColor();
						}
						components.changeColorToggle(false);
						colorModeState.setSelected(false);
					}

					components.changeRangeColorToggle(true);

					Scale displayOnly = ListOfScales.getInstance().getDisplayedScaleNotes();
					PlaybackFunctions.displayOrPlayScale(displayOnly);

					// Turn off color mode
				} else if (components.getRangeColorToggleStatus()) {

					// Re color original keys colour
					components.changeRangeColorToggle(false);
					PlaybackFunctions.resetScaleDisplayColor();

					// Change 4 - Might cause problems
					PlaybackFunctions.setIndexCounter(0);
					PlaybackFunctions.setRandomIntervalCounter(0);
					PlaybackFunctions.setMelodicIndexCounter(0);
					PlaybackFunctions.emptyNotes();
					scaleDisplayIsReset = true;
				}
			}

			else if (obj.equals(playProgression)) {
				// This dummy list fixes a concurrency issue when traversing the
				// chords in the thread's for each loop. This occurs if
				// the user leaves the page while the progression is playing
				ArrayList<Chord> dummy = new ArrayList<Chord>();
				dummy.addAll(progressionChord.getProgressionChords());

				String[] getBits = new String[progressionChord.getProgressionChords().size()];
				int k = 0;
				String strongRoot = "";
				for (String aString : progressionChord.getProgressionName().split("\\s+")) {
					getBits[k++] = aString;
					if (strongRoot.equals("")) {
						strongRoot = aString;
					}
				}
				getBits[k] = strongRoot;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							int i = 0;
							int textIndex = 2;
							for (Chord aChord : dummy) {
								// End thread if user leaves progressions page
								if (!stopPlayback) {

									if (!changeApreggio || preg.getCurrentChoice().equals("Clear")) {
										PlaybackFunctions.playAnyChordLength(aChord);
										TTS.getInstance().prepareFunction("Progression", getBits[i]);
										components.colourJText(contentTextArea, textIndex);
										PlaybackFunctions.timeDelay(1000);
										PlaybackFunctions.resetChordsColor();
										i++;
										textIndex++;
									}

									else if (changeApreggio) {
										PlaybackFunctions.playAnyChordLength(aChord);
										components.colourJText(contentTextArea, textIndex);
										String retrieved = preg.getCurrentChoice();
										preg.createApreggio(retrieved, aChord);

										if (components.getColorToggleStatus()) {
											PlaybackFunctions.resetChordsColor();
										}
										i++;
									}
								}
							}
						} catch (InvalidMidiDataException | InterruptedException | BadLocationException
								| EngineException | EngineStateError | IllegalArgumentException | AudioException e) {
							e.printStackTrace();
						}
					}
				}).start();

			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	public boolean getShared() {
		return stopPlayback;
	}

	public void changeSharedState(boolean change) {
		stopPlayback = change;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object obj = arg0.getSource();
		if (obj.equals(arpeggiosList)) {
			String apreggioName = arpeggiosList.getSelectedItem().toString();
			preg.storeCurrentChoice(apreggioName);
			changeApreggio = true;
		}
	}

	/**
	 * Experimental: This code is used to allow the user to select instruments
	 * that have not be recognised on system start up.
	 */
	public void changeInput() {
		JFrame connectionsPanel = SwingComponents.getInstance().floatingDebugFrame(true, false, null, "Connections",
				screenWidth / 2, screenHeight / 2, screenWidth / 2, screenHeight / 2);
		connectionsPanel.getContentPane().setBackground(Color.decode("#00BFFF"));

		//int width = connectionsPanel.getPreferredSize().width;
		//int height = connectionsPanel.getPreferredSize().height;

		conditionalModel = new DefaultListModel<String>();
		if (PlayBackDevices.getInstance().getConnections().isEmpty()) {
			conditionalModel.addElement("No input connecions detected");
			conditionalModel.addElement("No input connecions detected");
			conditionalModel.addElement("No input connecions detected");
		} else {
			for (Transmitter aTransmitter : PlayBackDevices.getInstance().getConnections()) {
				conditionalModel.addElement(aTransmitter.toString());
			}
		}
		jListInput = new JList<String>(conditionalModel);
		components.colourMenuPanels(jListInput, Color.GREEN, Color.LIGHT_GRAY);
		jListInput.setName("Input");
		jListInput.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		jListInput.setFixedCellWidth(connectionsPanel.getPreferredSize().width - 20);

		jListInput.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jListInput.setVisibleRowCount(-1);
		jListInput.setBorder(new LineBorder(Color.BLUE));
		jListInput.addMouseListener(this);
		jListInput.setFont(new Font("Arial", Font.BOLD, 14));

		scrollPane = new JScrollPane(jListInput);
		connectionsPanel.setLayout(new GridLayout(2, 1));
		connectionsPanel.getContentPane().add(scrollPane);
	}
}