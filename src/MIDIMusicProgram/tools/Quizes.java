package tools;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.sound.midi.InvalidMidiDataException;
import javax.speech.EngineStateError;
import javax.swing.JButton;
import javax.swing.JTextArea;
import keyboard.Note;
import midi.Chord;
import midi.ChordProgression;
import midi.ChordProgressionActions;
import midi.ListOfChords;
import midi.ListOfScales;
import midi.Scale;

/**
 * This class defines the quizzes supported in the program.
 */

public class Quizes implements MouseListener {

	private ArrayList<Chord> allMajorChords = ListOfChords.getInstance().getKeptMajors();
	private ArrayList<Chord> allMinorChords = ListOfChords.getInstance().getKeptMinors();
	private ArrayList<String> tempStoreScore = new ArrayList<String>();

	// Text and text based conditional variables
	private JTextArea text;
	private String handlefeature = "";
	private JButton userChoice = null;

	// Numerical and conditional variables
	private int successCounter = 0;

	// Chord Quiz components
	private Chord majorChordChoice = null;
	private Chord minorChordChoice = null;
	private Chord randomChordChoice = null;

	// Progression quiz components
	private Scale majorProgressionScale;
	private Scale minorProgressionScale;
	private String randomMajorProgressionString = "";
	private String relativeMinorProgression = "";
	private ChordProgression minorProgression;
	private ChordProgression majorProgression;
	private ChordProgression randomProgressionName;

	// Scale quiz components
	private Scale quizMajorScale;
	private Scale quizMinorScale;
	private Scale randomScaleName;

	// Singleton Instances
	private ChordProgressionActions prog = ChordProgressionActions.getInstance();
	private SwingComponents components = SwingComponents.getInstance();
	private ListOfScales scales = ListOfScales.getInstance();

	private static volatile Quizes instance = null;

	public static Quizes getInstance() {
		if (instance == null) {
			synchronized (Quizes.class) {
				if (instance == null) {
					instance = new Quizes();
				}
			}
		}
		return instance;
	}

	private Quizes() {
	}

	public void assignFeatureName(String name) {
		handlefeature = name;
	}

	/**
	 * Defines the variable button and target type name data for a given quiz.
	 */
	public JButton addPlayQuiz() {
		if (handlefeature.equals("Chords")) {
			userChoice = components.customJButton(270, 40, "Play chord", this, 20, Color.decode("#505050"));
		} else if (handlefeature.equals("Scales")) {
			userChoice = components.customJButton(270, 40, "Play scale", this, 20, Color.decode("#505050"));
		} else if (handlefeature.equals("Progressions")) {
			userChoice = components.customJButton(270, 40, "Play progression", this, 20, Color.decode("#505050"));
		}
		return userChoice;
	}

	/**
	 * This is the central function where the arguments affect what type of quiz
	 * is loaded.
	 * 
	 * @param feature
	 *            - The type of quiz to attempt.
	 * @param contentTextArea
	 *            - The JTextArea component to display the quiz data.
	 */
	public void selectQuiz(String feature, JTextArea contentTextArea) {
		text = contentTextArea;
		handlefeature = feature;
		switch (feature) {

		case "Chords":
			minOrMajChordQuiz();
			break;

		case "Scales":
			minOrMajScaleQuiz();
			break;

		case "Progressions":
			minOrMajProgressionsQuiz();
			break;

		}
	}

	/**
	 * This creates the relative minor version of the randomly retrieved major
	 * progression in the quiz attempt. It works by converting each individual
	 * string in the argument sequence, and converting it to the opposite. E.G.
	 * As "I" from I v VI iv is in upper case (major), its converted to "i" in
	 * lower case (minor).
	 * 
	 * @param bits
	 *            - The array that contains the major progression as individual
	 *            strings.
	 */
	public void adjustProgression(String[] bits) {
		relativeMinorProgression = "";
		for (String aString : bits) {
			char[] charArray = aString.toCharArray();
			if (Character.isLowerCase(charArray[0])) {
				relativeMinorProgression += aString.toUpperCase() + " ";
			} else if (Character.isUpperCase(charArray[0])) {
				relativeMinorProgression += aString.toLowerCase() + " ";
			}
		}
	}

	/** Prepares quiz input data to be grouped to later be randomised */
	public <T> ArrayList<T> mixedValues(T one, T two) {
		ArrayList<T> temp = new ArrayList<T>();
		temp.add(one);
		temp.add(two);
		return temp;
	}

	/**
	 * Retrieves a major version of the quiz's target progression, along with
	 * its relative minor progression.It then assigns either progression
	 * randomly to the play button and target answer. It also defines the text
	 * in the quiz.
	 */
	public void minOrMajProgressionsQuiz() {
		String[] getBits = null;
		ArrayList<String> coll = Note.randomNotesForScaleKeys();
		String baseKey = ScreenPrompt.random(coll);
		text.append("Progression Quiz \n\nThe purpose of this quiz is to find\nout if this "
				+ "progression is a major \nor minor progression\n\n");

		randomMajorProgressionString = ScreenPrompt.random(prog.getMajorProgsNames());
		getBits = randomMajorProgressionString.split("\\s+");

		int i = 0;
		for (Scale aScale : scales.getDiatonicMajorScales()) {
			if (aScale.getTonic().getName().equals(baseKey + "3")) {
				majorProgressionScale = aScale;
				minorProgressionScale = scales.getDiatonicMinorScales().get(i);
				break;
			}
			i++;
		}
		adjustProgression(getBits);

		// Created both progressions
		majorProgression = prog.makeChordProgression(randomMajorProgressionString, majorProgressionScale, getBits);
		String[] relativeBits = relativeMinorProgression.split("\\s+");
		minorProgression = prog.makeChordProgression(relativeMinorProgression, minorProgressionScale, relativeBits);

		// Randomise progression played
		ArrayList<ChordProgression> mixProgressions = mixedValues(majorProgression, minorProgression);
		randomProgressionName = ScreenPrompt.random(mixProgressions);
	}

	/**
	 * Retrieves a major version of the quiz's target scale, along with its
	 * relative minor scale.It then assigns either scale randomly to the play
	 * button and target answer. It also defines the text in the quiz.
	 */
	public void minOrMajScaleQuiz() {
		ArrayList<String> coll = Note.randomNotesForScaleKeys();
		String baseKey = ScreenPrompt.random(coll);
		int i = 0;
		for (Scale aScale : scales.getDiatonicMajorScales()) {
			if (aScale.getTonic().getName().equals(baseKey + "3")) {
				quizMajorScale = aScale;
				quizMinorScale = scales.getDiatonicMinorScales().get(i);
				break;
			}
			i++;
		}
		ArrayList<Scale> mixScales = mixedValues(quizMajorScale, quizMinorScale);
		randomScaleName = ScreenPrompt.random(mixScales);
		text.append("Scale Quiz \n\nThe purpose of this quiz is to find\nout if this scale is a major or minor\n\n");
	}

	/**
	 * Retrieves a major version of the quiz's target chord, along with its
	 * relative minor chord.It then assigns either chord randomly to the play
	 * button and target answer. It also defines the text in the quiz.
	 */
	public void minOrMajChordQuiz() {
		ArrayList<String> allMajorChordNames = Chord.getPureMajorEnums();
		String randomChordName = "";
		ArrayList<String> temp = Note.randomNotesForScaleKeys();
		String baseRoot = ScreenPrompt.random(temp);
		randomChordName = ScreenPrompt.random(allMajorChordNames);
		text.append("Chords Quiz \n\nThe purpose of this quiz is to find\nout if this chord is a major or minor\n\n");

		int i = 0;
		for (Chord aChord : allMajorChords) {
			if (aChord.getChordName().equals(randomChordName)) {
				if (aChord.getChordNotes().get(0).getName().equals(baseRoot + "3")) {
					majorChordChoice = aChord;
					minorChordChoice = allMinorChords.get(i);
					break;
				}
			}
			i++;
		}
		ArrayList<Chord> mixChords = mixedValues(majorChordChoice, minorChordChoice);
		randomChordChoice = ScreenPrompt.random(mixChords);
	}

	/**
	 * This method print out the success or failure text when the user has
	 * chosen an answer after clicking the play button.
	 * 
	 * @param check
	 *            - This states if the selected answer is correct
	 */
	public void printAnswer(boolean check) {
		String quizFeature = handlefeature;
		String quizTargetName = handlefeature.substring(0, handlefeature.length() - 1);
		if (check) {
			successCounter++;
			text.setText("");
			text.append("You are correct - It was\na " + userChoice.getName() + " " + quizTargetName);
			String correct = quizFeature + "Quiz: Easy Mode | No of success: " + successCounter;
			tempStoreScore.add(correct);
		} else {
			text.setText("");
			text.append("You are wrong. It was\na " + userChoice.getName() + " " + quizTargetName);
		}
	}

	/**
	 * This method checks if the quiz has been attempted correctly, and if the
	 * user's chosen answer is correct.
	 * 
	 * @param choice
	 *            - The string object derived from the user clicking on the
	 *            available "minor" and "major" answers.
	 */
	public void doesAnswerMatch(String choice) {
		boolean resultOne = false;
		String quizTargetName = handlefeature.substring(0, handlefeature.length() - 1);
		if (!userChoice.getName().equals("Major") && !userChoice.getName().equals("Minor")) {
			text.append("Please listen to the\n" + quizTargetName + " chord before deciding");
		} else {
			resultOne = userChoice.getName().equals(choice) ? true : false;
			printAnswer(resultOne);
		}
	}

	/**
	 * Processes playing the progression assigned to the play button in the
	 * progression quiz. In then assigns the progression's based scale name to
	 * the button to be later used in comparison between it, and the user's
	 * selection from the answer options.
	 */
	public void handleProgressions() {
		try {
			for (Chord aChordOne : randomProgressionName.getProgressionChords()) {
				PlaybackFunctions.playAnyChordLength(aChordOne);
				PlaybackFunctions.timeDelay(1000);
			}
			if (randomProgressionName.getProgressionName().contains(randomMajorProgressionString)) {
				userChoice.setName("Major");
			}

			else if (randomProgressionName.getProgressionName().contains(relativeMinorProgression)) {
				userChoice.setName("Minor");
			}
		} catch (InvalidMidiDataException | InterruptedException | EngineStateError e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes playing the chord assigned to the play button in the chord
	 * quiz. In then assigns the chord's type (major or minor) name to the
	 * button to be later used in comparison between it, and the user's
	 * selection from the answer options.
	 */
	public void handleChords() {
		try {
			PlaybackFunctions.playAnyChordLength(randomChordChoice);
			if (randomChordChoice.getChordName().contains("maj")) {
				userChoice.setName("Major");
			} else if (randomChordChoice.getChordName().contains("min")) {
				userChoice.setName("Minor");
			}

		} catch (InvalidMidiDataException | InterruptedException | EngineStateError e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes playing the scale assigned to the play button in the scale
	 * quiz. In then assigns the scale (major or minor) name to the button to be
	 * later used in comparison between it, and the user's selection from the
	 * answer options.
	 */
	public void handleScales() {
		try {
			PlaybackFunctions.playOrDisplay(true);
			PlaybackFunctions.displayOrPlayScale(randomScaleName);
			if (randomScaleName.getScaleName().contains("Ionian")) {
				userChoice.setName("Major");
			} else if (randomScaleName.getScaleName().contains("Aeolian")) {
				userChoice.setName("Minor");
			}
		} catch (InvalidMidiDataException | InterruptedException | EngineStateError e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent button) {
		Object obj = button.getSource();
		if (obj.equals(userChoice)) {
			if (handlefeature.equals("Chords")) {
				handleChords();
			} else if (handlefeature.equals("Scales")) {
				handleScales();
			} else if (handlefeature.equals("Progressions")) {
				handleProgressions();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
