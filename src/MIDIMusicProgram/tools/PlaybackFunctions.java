package tools;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JButton;
import keyboard.Note;
import keyboard.VirtualKeyboard;
import midi.Chord;
import midi.ListOfScales;
import midi.MidiMessageTypes;
import midi.Scale;
import midiDevices.PlayBackDevices;

/**
 * This class defines all interaction MIDI play back and colour mode.
 */
public class PlaybackFunctions {

	private static ArrayList<Note> storedPreColorChords = new ArrayList<Note>();
	private static int medoldicIndexCounter = 0;
	private static int notesIndex = 0;
	private static int intervalPrevCounter;
	private static int randomIntervalCounter = 0;
	private static Note currentRandomInterval;
	private static Note currentMelodicInterval;
	private static int direction;
	private static boolean displayOrPlayScale;

	/**
	 * This method updates the argument note's colour on the internal
	 * controller. The system traverses all piano keys in memory (data structure
	 * of JButtons), and when it finds button whose pitch matches the note's
	 * pitch, it changes the colour using the argument colour.
	 * 
	 * @param aNote
	 *            - The note to have its colour changed
	 * @param aColor
	 *            - The colour to change the note to.
	 */
	public static void colorChordsAndScales(Note aNote, Color aColour) {
		JButton foundButton;
		Collection<JButton> buttonNotes = VirtualKeyboard.getInstance().getButtons();
		for (JButton buttonNote : buttonNotes) {
			if (buttonNote.getText().equals(aNote.getName())) {
				foundButton = buttonNote;
				foundButton.setBackground(aColour);
				break;
			}
		}
	}

	/**
	 * Retrieves the last stored set of chord notes, and resets them back to
	 * their original colour. Used mainly in feature 1 and 2.
	 */
	public static void resetChordsColor() {
		ArrayList<Note> getOriginal = getStoredPreNotes();
		for (Note aNote : getOriginal) {
			if (aNote.getType().equals("Sharp")) {
				colorChordsAndScales(aNote, Color.BLACK);
			} else {
				colorChordsAndScales(aNote, Color.WHITE);
			}
		}
	}

	/**
	 * Retrieves the last stored scale's notes, and resets them back to their
	 * original colour. Used mainly in feature 3 and 4.
	 */
	public static void resetScaleDisplayColor() {
		Scale currentScale = ListOfScales.getInstance().getDisplayedScaleNotes();
		for (Note aNote : currentScale.getScaleNotesList()) {
			if (aNote.getType().equals("Sharp")) {
				colorChordsAndScales(aNote, Color.BLACK);
			} else {
				colorChordsAndScales(aNote, Color.WHITE);
			}
		}
	}

	/**
	 * This method is used in the feature set 4: relative pitch. It resets the
	 * colour of the previously stored note in memory one at a time. This
	 * includes when a random note is chosen, or the next traversal note is
	 * chosen.It is also used in feature set 3.
	 */
	public static void resetLastNoteColor() {
		Note lastNote = null;
		if (MidiMessageTypes.getInstance().getRandomState()) {
			lastNote = storedPreColorChords.get(randomIntervalCounter - 1);
		} else if (MidiMessageTypes.getInstance().getMelodyInterval()) {
			lastNote = storedPreColorChords.get(medoldicIndexCounter - 1);
		}

		// Feature set 3 only
		else {
			lastNote = storedPreColorChords.get(medoldicIndexCounter - 1);
		}

		if (lastNote.getType().equals("Sharp")) {
			colorChordsAndScales(lastNote, Color.BLACK);

		} else {
			colorChordsAndScales(lastNote, Color.WHITE);
		}
	}

	/**
	 * This method resets the colour of the notes playing on an external MIDI
	 * keyboard during live play, and in a recorded sequence for the internal
	 * controller and external keyboard. Both input methods work with the same
	 * automatic colour mode, but the external keyboard provides the pitch
	 * argument, rather than getting it through a JButton / note. It also resets
	 * a JButton's colour on the internal controller while the MIDI file player
	 * is playing a song.
	 */
	public static void resetLastNotePianoColor(int pitch) {
		Note lastNote = null;
		for (Note aNote : storedPreColorChords) {
			if (aNote.getPitch() == pitch) {
				lastNote = aNote;
				break;
			}
		}
		if (lastNote.getType().equals("Sharp")) {
			colorChordsAndScales(lastNote, Color.BLACK);

		} else {
			colorChordsAndScales(lastNote, Color.WHITE);
		}
	}

	public static void storedPreColorNotes(Note aNote) {
		storedPreColorChords.add(aNote);
	}

	public static ArrayList<Note> getStoredPreNotes() {
		return storedPreColorChords;
	}

	public static void emptyNotes() {
		storedPreColorChords.clear();
	}

	/////////////////////////////////////////

	public static void currentRandom(Note cRandom) {
		currentRandomInterval = cRandom;
	}

	public static Note getCurrentRandom() {
		return currentRandomInterval;
	}

	public static void currentMedoldicInterval(Note cMelodic) {
		currentMelodicInterval = cMelodic;
	}

	public static Note getMedoldicInterval() {
		return currentMelodicInterval;
	}

	public static void setRandomIntervalCounter(int rValue) {
		randomIntervalCounter = rValue;
	}

	public static int currentRandomIntervalCounter() {
		return randomIntervalCounter;
	}
	/////////////////////////////

	public static void setMelodicIndexCounter(int iValue) {
		medoldicIndexCounter = iValue;
	}

	public static int getMelodicIndexCounter() {
		return medoldicIndexCounter;
	}
	///////////////////////////////////

	public static void setIndexCounter(int index) {
		notesIndex = index;
	}

	public static int getIndexCounter() {
		return notesIndex;
	}

	public static void setPrevIntervalCounter(int iPrevValue) {
		intervalPrevCounter = iPrevValue;
	}

	public static int getPrevIntervalCounter() {
		return intervalPrevCounter;
	}

	public static void currentDirection(int id) {
		direction = id;
	}

	public static int fixDirection() {
		return direction;
	}

	/** Uses epoch time to create a res-usable, custom time delay feature. */
	public static void timeDelay(int fixedTime) {
		Instant instantOnTime = Instant.now();
		long startTime = instantOnTime.toEpochMilli();

		Instant newInstantOnTime = Instant.now();
		long endTime = newInstantOnTime.toEpochMilli();
		while (!(endTime - startTime > fixedTime)) {

			newInstantOnTime = Instant.now();
			endTime = newInstantOnTime.toEpochMilli();
		}
	}

	///////////////////////////////////

	/**
	 * Used in feature set 4: Relative pitch. This processes the user's
	 * interactions with the random and next traversal actions. Each time an
	 * action is triggered, its pitch sound is played while its representation
	 * on the internal controller has its colour changed.
	 * 
	 * @param intervalNote
	 *            - The type of note selected by the user
	 */
	public static void playIntervalNote(Note intervalNote) throws InvalidMidiDataException {
		SwingComponents swingComponents = SwingComponents.getInstance();
		MidiMessageTypes messages = MidiMessageTypes.getInstance();

		// Only during color mode on and press random note button
		if (swingComponents.getColorToggleStatus() && messages.getRandomState() && randomIntervalCounter >= 1) {
			resetLastNoteColor();
		}

		// Only applied to relative pitch mode, with next interval
		else if (swingComponents.getColorToggleStatus() && messages.getMelodyInterval() && medoldicIndexCounter >= 1) {
			resetLastNoteColor();
		}

		// Use for feature set 3 only
		else if (swingComponents.getColorToggleStatus() && medoldicIndexCounter >= 1) {
			resetLastNoteColor();
		}

		if (!swingComponents.getColorToggleStatus()) {
			storedPreColorNotes(intervalNote);
		}

		else if (swingComponents.getColorToggleStatus()) {
			storedPreColorNotes(intervalNote);
			colorChordsAndScales(intervalNote, Color.YELLOW);
		}
		ShortMessage noteOne = new ShortMessage(ShortMessage.NOTE_ON, 0, intervalNote.getPitch(), 50);
		PlayBackDevices.getInstance().send(noteOne, -1);

		if (messages.getRandomState()) {
			if (messages.getNoColorFirst()) {
				messages.storeRandomState(false);

			}
			randomIntervalCounter++;
		} else if (messages.getIntervalStateID() == 2) {
			medoldicIndexCounter++;

		}

		else if (messages.getIntervalStateID() == 1) {
			PlaybackFunctions.setIndexCounter(PlaybackFunctions.getIndexCounter() - 1);
			intervalPrevCounter++;
		}
		/*
		 * Used for playing scales as opposes to traversal of a scale using
		 * relative pitch feature Might need to remove if causes problems
		 */
		else {
			medoldicIndexCounter++;
		}
	}

	/**
	 * The chord to be played through the user's interactions with the feature
	 * sets. Each chord played has its matching JButtons on the internal
	 * controller change colour.
	 * 
	 * @param foundChord
	 *            - The chord to be played on the internal controller, and to
	 *            also have the matching JButton changes colour in relation.
	 */
	public static void playAnyChordLength(Chord foundChord) throws InvalidMidiDataException, InterruptedException {
		SwingComponents swingComponents = SwingComponents.getInstance();
		ArrayList<Note> notesInChord = foundChord.getChordNotes();
		for (Note aNote : notesInChord) {
			if (swingComponents.getColorToggleStatus()) {
				storedPreColorNotes(aNote);
				colorChordsAndScales(aNote, Color.YELLOW);
			}

			ShortMessage noteOne = new ShortMessage(ShortMessage.NOTE_ON, 0, aNote.getPitch(), 50);
			PlayBackDevices.getInstance().send(noteOne, -1);
		}
	}

	/**
	 * This is used in feature set 3 and 4's display scale command. For feature
	 * set 3, this method only plays the degrees in a scale one a time, without
	 * colour automatically enabled. For the latter, it does not play the
	 * scale's degree pitches, it only displays their degrees on the internal
	 * controller in colour mode.
	 * 
	 * @param foundScale
	 *            - The scale to play output without colour mode enable, or
	 *            enter colour mode on the internal controller, without pitch
	 *            output.
	 */
	public static void displayOrPlayScale(Scale foundScale) throws InvalidMidiDataException, InterruptedException {

		SwingComponents swingComponents = SwingComponents.getInstance();
		ArrayList<Note> notesInChord = foundScale.getScaleNotesList();
		for (Note aNote : notesInChord) {

			if (isPlayOrDisplay()) {
				ShortMessage noteOne = new ShortMessage(ShortMessage.NOTE_ON, 0, aNote.getPitch(), 50);
				PlayBackDevices.getInstance().send(noteOne, -1);
				timeDelay(1000);
			}
			// Add second condition to other methods if needed
			else if (swingComponents.getColorToggleStatus() || swingComponents.getRangeColorToggleStatus()) {
				storedPreColorNotes(aNote);
				colorChordsAndScales(aNote, Color.YELLOW);

			}
		}
	}

	public static void playOrDisplay(boolean change) {
		displayOrPlayScale = change;
	}

	public static boolean isPlayOrDisplay() {
		return displayOrPlayScale;
	}
}