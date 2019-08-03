package midi;

import java.util.ArrayList;
import java.util.Collections;
import javax.sound.midi.InvalidMidiDataException;
import keyboard.Note;
import keyboard.Note.allNotesType;

/**
 * This class handles all the variations of chord manipulation in the system.
 * This includes chords from a given scale, storing all chords in memory to be
 * gathered later, and chord inversion.
 */
public class ListOfChords {

	// Traditional note positions in a chord
	////////////////////////////////////////
	private Note rootNote; // tonic (first)
	private Note secondNote; // supertonic (second)
	private Note thirdNote; // mediant (third)
	private Note fourthNote; // subdominant (fourth)
	private Note fifthNote; // dominant (fifth)
	private Note sixthNote; // submediant (sixth)
	private Note seventhNote; // subtonic (seventh)

	private boolean inverted = false;
	private ArrayList<Chord> allMajorChords;
	private ArrayList<Chord> allMinorChords;
	private ArrayList<Chord> allHalfDimishedChords;
	private ArrayList<Chord> allBlueChords;
	private ArrayList<Chord> fillerBlueChord;
	private ArrayList<Chord> allFullyDimishedChords;
	private ArrayList<Chord> allDominantDimishedChords;

	private ArrayList<Note> noteNames = new ArrayList<Note>();
	private ArrayList<String> noteNamesStrings = new ArrayList<String>();
	private ArrayList<GivenKeyChords> allKeysChords = new ArrayList<GivenKeyChords>();

	// Store all key chords to link back to the name
	private ArrayList<GivenKeyScales> allScalesChords = new ArrayList<GivenKeyScales>();
	private ArrayList<Chord> keptMajors = new ArrayList<Chord>();
	private ArrayList<Chord> keptMinors = new ArrayList<Chord>();
	private Chord currentInversion; // Store any chord inversions
	private Chord firstInversion; // Store any chord inversions
	private static volatile ListOfChords instance = null;

	private ListOfChords() {
	}

	public static ListOfChords getInstance() {
		if (instance == null) {
			synchronized (ListOfChords.class) {
				if (instance == null) {
					instance = new ListOfChords();
				}
			}
		}
		return instance;
	}

	/**
	 * Creates the range of notes to allocate to the feature sets to be used for
	 * their content played on the internal controller.E.G. C Thirteen wont have
	 * notes any higher than C3 or C5.
	 */
	public void setAllKeyNotes() {
		allNotesType[] enumsToString = Note.allNotesType.values();
		for (allNotesType enumNote : enumsToString) {
			String foundNote = enumNote.getNote();
			Note aNote = Note.getNotes(foundNote + 3);
			noteNames.add(aNote);
			noteNamesStrings.add(aNote.getName());
		}
		for (allNotesType enumNote : enumsToString) {
			String foundNote = enumNote.getNote();
			Note aNote = Note.getNotes(foundNote + 4);
			noteNames.add(aNote);
		}

		for (allNotesType enumNote : enumsToString) {
			String foundNote = enumNote.getNote();
			Note aNote = Note.getNotes(foundNote + 5);
			noteNames.add(aNote);
		}
	}

	public ArrayList<Note> getAllKeyNotes() {
		return noteNames;
	}

	public ArrayList<String> getAllKeyNotesStrings() {
		return noteNamesStrings;
	}

	public void loadMajorChords(ArrayList<Note> noteNames) {
		allKeysChords = new ArrayList<GivenKeyChords>();
		Scale ionionScale = null;
		for (Note aNote : noteNames) {
			if (aNote.getName().equals("G#5")) {
				break;
			}
			ionionScale = ListOfScales.getInstance().majorOrIonianScale(aNote);

			// Keep major for quizzes
			ListOfScales.getInstance().keyDiatonicMajorScales(ionionScale);
			/////////////////////////

			ListOfChords.getInstance().setMajorChords(ionionScale);
			ArrayList<Chord> loadedScaleChords = getMajorChords();
			keepMajor(loadedScaleChords);

			GivenKeyChords givenKeyChords = new GivenKeyChords(aNote.getName(), loadedScaleChords);
			allKeysChords.add(givenKeyChords);
		}
		GivenKeyScales givenKeyScale = new GivenKeyScales(ionionScale.getScaleName(), allKeysChords);
		storeKeyScaleChords(givenKeyScale);
	}

	public void keepMajor(ArrayList<Chord> current) {
		keptMajors.addAll(current);
	}

	public ArrayList<Chord> getKeptMajors() {
		return keptMajors;
	}

	public void loadMinorChords(ArrayList<Note> noteNames) {
		allKeysChords = new ArrayList<GivenKeyChords>();
		Scale minorScale = null;
		for (Note aNote : noteNames) {
			if (aNote.getName().equals("G#5")) {
				break;
			}
			minorScale = ListOfScales.getInstance().minorOrAeolianScale(aNote);

			// Keep minor scales for quizzes
			ListOfScales.getInstance().keyDiatonicMinorScales(minorScale);
			/////////////////////////

			ListOfChords.getInstance().setMinorChords(minorScale);
			ArrayList<Chord> loadedScaleChords = getMinorChords();
			keepMinor(loadedScaleChords);

			GivenKeyChords givenKeyChords = new GivenKeyChords(aNote.getName(), loadedScaleChords);
			allKeysChords.add(givenKeyChords);
		}
		GivenKeyScales givenKeyScale = new GivenKeyScales(minorScale.getScaleName(), allKeysChords);
		storeKeyScaleChords(givenKeyScale);
	}

	public void keepMinor(ArrayList<Chord> current) {
		keptMinors.addAll(current);
	}

	public ArrayList<Chord> getKeptMinors() {
		return keptMinors;
	}

	public void loadHalfDimishedChords(ArrayList<Note> noteNames) {
		allKeysChords = new ArrayList<GivenKeyChords>();
		Scale halfDiminishedScale = null;
		for (Note aNote : noteNames) {
			halfDiminishedScale = ListOfScales.getInstance().halfDiminishedScale(aNote);
			ListOfChords.getInstance().setHalfDimishedChords(halfDiminishedScale);
			ArrayList<Chord> loadedScaleChords = getHalfDimishedChords();
			GivenKeyChords givenKeyChords = new GivenKeyChords(aNote.getName(), loadedScaleChords);
			allKeysChords.add(givenKeyChords);
		}
		GivenKeyScales givenKeyScale = new GivenKeyScales(halfDiminishedScale.getScaleName(), allKeysChords);
		storeKeyScaleChords(givenKeyScale);
	}

	public void loadFullyDiminishedScaleChords(ArrayList<Note> noteNames) {
		allKeysChords = new ArrayList<GivenKeyChords>();
		Scale fullyDiminishedScaleChords = null;
		for (Note aNote : noteNames) {
			fullyDiminishedScaleChords = ListOfScales.getInstance().fullyDiminishedScale(aNote);
			ListOfChords.getInstance().setFullyDimishedScaleChords(fullyDiminishedScaleChords);
			ArrayList<Chord> loadedScaleChords = getFullyDimishedScaleChords();
			GivenKeyChords givenKeyChords = new GivenKeyChords(aNote.getName(), loadedScaleChords);
			allKeysChords.add(givenKeyChords);

		}
		GivenKeyScales givenKeyScale = new GivenKeyScales("No Scale", allKeysChords);
		storeKeyScaleChords(givenKeyScale);
	}

	// Adds each full set of chords for each key in all scales
	public void storeKeyScaleChords(GivenKeyScales givenKeyScale) {
		allScalesChords.add(givenKeyScale);
	}

	public ArrayList<GivenKeyScales> getKeyScaleChords() {
		return allScalesChords;
	}

	/**
	 * Processes a chord or scale name (for feature 1 and 5 respectfully), to
	 * find the desired chord in memory.
	 * 
	 * @param i
	 *            - index used to traverse chords from all scale in memory.
	 * @param noteName
	 *            - The name of the note (single letter) to find
	 * @param chordOrScale
	 *            - The argument for either feature 1 or 5
	 * @return The found chord that matches the arguments provided.
	 */
	public <T> Chord getChord(int i, T noteName, T chordOrScale) {
		GivenKeyChords found = null;
		for (GivenKeyChords test : allScalesChords.get(i).getScaleKeys()) {
			if (test.getKeyName().equals(noteName)) {
				found = test;
				break;
			}
		}
		Chord foundChord = null;
		for (Chord findChord : found.getKeyChords()) {
			if (findChord.getChordName().equals(chordOrScale)) {
				foundChord = findChord;
				break;
			}
		}
		return foundChord;
	}

	/**
	 * Feature set 5: Used to find the chords to create a chord progression by
	 * search for them in memory.
	 * 
	 * @param noteName
	 *            - The name of the note (single letter) to find
	 * @param chordName
	 *            - The name of the chord name joined to the noteName.
	 * @return The found chord in either major or minor scale to create a
	 *         progression.
	 */
	public Chord findChord(String noteName, String chordName) {
		int i = 0;
		if (chordName.equals("min")) {
			i = 1;
		} else if (chordName.equals("maj")) {
			i = 0;
		}
		return getChord(i, noteName, chordName);
	}

	/**
	 * Feature set 1: Used to find the selected chord stored in memory.
	 * 
	 * @param noteName
	 *            - The name of the note (single letter) to find
	 * @param scaleName
	 *            - The name of the scale of which the chord is found in memory.
	 * @return The found chord in memory from all scales.
	 */
	public Chord getChordFromKeyScale(String noteName, String scaleName) {
		noteName += 3;
		int i = 0;
		ArrayList<String> MajorChords = Chord.getMajorEnums();
		ArrayList<String> MinorChords = Chord.getMinorEnums();
		ArrayList<String> HalfDimishedChords = Chord.getHalfDimsEnums();
		ArrayList<String> FullyDimishedChords = Chord.getFullDimsEnums();

		if (MajorChords.contains(scaleName)) {
			i = 0;
		} else if (MinorChords.contains(scaleName)) {
			i = 1;
		} else if (HalfDimishedChords.contains(scaleName)) {
			i = 2;
		} else if (FullyDimishedChords.contains(scaleName)) {
			i = 3;
		} else {
			return null;
		}
		return getChord(i, noteName, scaleName);
	}

	/**
	 * All chords from major (Ionian) scale are stored in memory
	 */
	public void setMajorChords(Scale carriedScale) {

		allMajorChords = new ArrayList<Chord>();
		allMajorChords.add(majorTetraChord(carriedScale));
		allMajorChords.add(majorChord(carriedScale));
		allMajorChords.add(majorSeventhChord(carriedScale));
		allMajorChords.add(majorNinthChord(carriedScale));

		allMajorChords.add(majorThirteenChord(carriedScale));
		allMajorChords.add(majorSixthChord(carriedScale));
		allMajorChords.add(suspendedFourthChord(carriedScale));
		allMajorChords.add(suspendedSecondChord(carriedScale));

		allMajorChords.add(dominantNinthChord(carriedScale));
		allMajorChords.add(dominantThirteenChord(carriedScale));
		allMajorChords.add(dominantEleventhChord(carriedScale));

		// Takes part of major scale
		allMajorChords.add(augmentedChord(carriedScale));
		allMajorChords.add(addTwoChord(carriedScale));
		allMajorChords.add(addNineChord(carriedScale));
		allMajorChords.add(dominantNinthChord(carriedScale));
		allMajorChords.add(dominantSeventhChord(carriedScale));

		// EXPERIMENTAL
		////////////////
		// allMajorChords.add(sevenSharpFiveChord(carriedScale));
		// allMajorChords.add(sevenFlatFiveChord(carriedScale));
	}

	public ArrayList<Chord> getMajorChords() {
		return allMajorChords;
	}

	public Chord getChordsFromMajor(String chordName) {

		for (Chord aChord : allMajorChords) {
			if (aChord.getChordName().equals(chordName)) {
				return aChord;
			}
		}
		return null;
	}

	/**
	 * All chords from the minor (Aeolian) scale are stored in memory.
	 */
	public void setMinorChords(Scale carriedScale) {
		allMinorChords = new ArrayList<Chord>();
		allMinorChords.add(minorTetraChord(carriedScale));
		allMinorChords.add(minorChord(carriedScale));
		allMinorChords.add(minorSeventhChord(carriedScale));
		allMinorChords.add(minorNinthChord(carriedScale));
		allMinorChords.add(minorThirteenChord(carriedScale));

		allMinorChords.add(minorSixthChord(carriedScale));
		allMinorChords.add(minorEleventhChord(carriedScale));

		// Based on minor scale
		allMinorChords.add(minorMajorSevenChord(carriedScale));
	}

	public ArrayList<Chord> getMinorChords() {
		return allMinorChords;
	}

	/**
	 * All chords from fully diminished scale are stored in memory.
	 */
	public void setFullyDimishedScaleChords(Scale carriedScale) {
		allFullyDimishedChords = new ArrayList<Chord>();
		allFullyDimishedChords.add(dimishedChord(carriedScale));
		allFullyDimishedChords.add(dimishedSeventhChord(carriedScale));
	}

	public ArrayList<Chord> getFullyDimishedScaleChords() {
		return allFullyDimishedChords;
	}

	/**
	 * Experimental: Dominant diminished chords
	 */
	public void setDominantDimishedScaleChords(Scale carriedScale) {
		allDominantDimishedChords = new ArrayList<Chord>();
	}

	public ArrayList<Chord> getDominantDimishedScaleChords() {
		return allDominantDimishedChords;
	}

	/**
	 * All chords from blues scale are stored in memory.
	 */
	public void setBluesChords(Scale carriedScale) {
		allBlueChords = new ArrayList<Chord>();
		allBlueChords.add(bluesChord(carriedScale));

	}

	public ArrayList<Chord> getBluesChords() {
		return allBlueChords;
	}

	/**
	 * All bridge or "filler" chords from blues scale stored in memory.
	 */
	public void setFillerBluesChord(Scale carriedScale) {
		fillerBlueChord = new ArrayList<Chord>();
		fillerBlueChord.add(fillerBluesChord(carriedScale));

	}

	public ArrayList<Chord> getFillerBluesChord() {
		return fillerBlueChord;
	}

	/**
	 * All chords from half diminished scale are stored in memory.
	 */
	public void setHalfDimishedChords(Scale carriedScale) {
		allHalfDimishedChords = new ArrayList<Chord>();
		allHalfDimishedChords.add(minorSevenFlatFiveChord(carriedScale));
	}

	public ArrayList<Chord> getHalfDimishedChords() {
		return allHalfDimishedChords;
	}

	/**
	 * If user types first inversion for chord, then to get second inversion,
	 * recall this function with a boolean condition.
	 * 
	 * @param carriedChord
	 *            - The chord to invert.
	 */
	public void chordInversion(Chord carriedChord) throws InvalidMidiDataException {

		// Repeat using previous stored array for second inversion.
		// Must have done first inversion first though
		if (inverted == true) {
			carriedChord = getCurrentInversion();

		}
		ArrayList<Note> notesInChord = new ArrayList<Note>();
		for (Note aNote : carriedChord.getChordNotes()) {
			notesInChord.add(aNote);
		}

		// Replace key with one octave higher, swap with last element, and
		// save array in memory
		Note increaseRootOctave = ListOfScales.getInstance().getKey(notesInChord.get(0), 12);
		notesInChord.set(0, increaseRootOctave);

		switch (notesInChord.size()) {
		case 3:
			Collections.rotate(notesInChord, 2);
			break;
		case 4:
			Collections.rotate(notesInChord, 3);
			break;
		case 5:
			Collections.rotate(notesInChord, 4);
			break;
		case 6:
			Collections.rotate(notesInChord, 5);
			break;
		case 7:
			Collections.rotate(notesInChord, 6);
			break;
		}
		Chord editedChord = new Chord(carriedChord.getChordName(), notesInChord);
		storeCurrentInversion(editedChord);
		inverted = true;
	}

	public void resetInversion() {
		inverted = false;
	}

	public void storeFirstInversion(Chord original) {
		firstInversion = original;
	}

	public Chord getFirstInversion() {
		return firstInversion;
	}

	public void storeCurrentInversion(Chord current) {
		currentInversion = current;
	}

	public Chord getCurrentInversion() {
		return currentInversion;
	}

	// Uses Ionian scale's first 4 scale degrees
	public Chord majorTetraChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getSubTonic(); // 2
		thirdNote = carriedScale.getMediant(); // 4
		fourthNote = carriedScale.getSubDominant(); // 5
		Chord aChord = new Chord("majTetra", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	// Uses Aeolian scale's first 4 scale degrees
	public Chord minorTetraChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getSubTonic(); // 2
		thirdNote = carriedScale.getMediant(); // 4
		fourthNote = carriedScale.getSubDominant(); // 5
		Chord aChord = new Chord("minTetra", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	// Uses Ionian scale
	public Chord majorChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = carriedScale.getDominant(); // 7
		Chord aChord = new Chord("maj", rootNote, secondNote, thirdNote);
		return aChord;
	}

	// Uses Aeolian scale
	public Chord minorChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 3
		thirdNote = carriedScale.getDominant(); // 7
		Chord aChord = new Chord("min", rootNote, secondNote, thirdNote);
		return aChord;
	}

	// Could use augmented scale
	public Chord augmentedChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = ListOfScales.getInstance().getKey(secondNote, 4); // 8
		Chord aChord = new Chord("aug", rootNote, secondNote, thirdNote);
		return aChord;
	}

	// Diminished cords use the Half Whole Diminished Scale
	public Chord dimishedChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 3
		thirdNote = carriedScale.getDominant(); // 6
		Chord aChord = new Chord("dim", rootNote, secondNote, thirdNote);
		return aChord;
	}

	/**
	 * This chord is based of the 5th degree of the diatonic scale, and has a
	 * root, a major third, a perfect fifth, and a diminished 7th. The dominant
	 * chord uses the mixolydian diatonic scale.
	 * 
	 * @param carriedScale
	 *            - The scale to use as a base to create the chord.
	 */
	public Chord dominantSeventhChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 3); // 10
		Chord aChord = new Chord("7", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	// A dominant chord could be the ionian or lydian scale
	public Chord majorSeventhChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = carriedScale.getSuperTonic(); // 11
		Chord aChord = new Chord("maj7", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	// This chord can use the dorian scale, phrygian scale, and aeolian scale
	public Chord minorSeventhChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 3
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = carriedScale.getSuperTonic(); // 10
		Chord aChord = new Chord("min7", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	// Can use Ionion scale
	public Chord suspendedFourthChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getSubDominant(); // 5
		thirdNote = carriedScale.getDominant(); // 7
		Chord aChord = new Chord("sus4", rootNote, secondNote, thirdNote);
		return aChord;
	}

	// Can use Ionion scale
	public Chord suspendedSecondChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getSubTonic(); // 2
		thirdNote = carriedScale.getDominant(); // 7
		Chord aChord = new Chord("sus2", rootNote, secondNote, thirdNote);
		return aChord;
	}

	// Can use IonionScale
	public Chord majorSixthChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = carriedScale.getSubMediant(); // 9
		Chord aChord = new Chord("maj6", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	// Can use dorian scale
	public Chord minorSixthChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 3
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = carriedScale.getSubMediant(); // 9
		Chord aChord = new Chord("min6", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	public Chord dominantNinthChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 3); // 10

		// Not as part of a known scale, but rather an added ninth to the
		// Ionian scale. Uses below function to get next step pitch against
		// former from map of notes
		fifthNote = ListOfScales.getInstance().getKey(fourthNote, 4); // 14
		Chord aChord = new Chord("9", rootNote, secondNote, thirdNote, fourthNote, fifthNote);
		return aChord;
	}

	public Chord majorNinthChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = carriedScale.getSuperTonic(); // 11
		// Same as ninth dominant chord
		fifthNote = ListOfScales.getInstance().getKey(fourthNote, 3); // 14
		Chord aChord = new Chord("maj9", rootNote, secondNote, thirdNote, fourthNote, fifthNote);
		return aChord;
	}

	// This chord can use the dorian scale, phrygian scale, and aeolian scale
	public Chord minorNinthChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // [0]; //0
		secondNote = carriedScale.getMediant(); // [2]; //3
		thirdNote = carriedScale.getDominant(); // [4]; //7
		fourthNote = carriedScale.getSuperTonic();// [6]; //10
		fifthNote = ListOfScales.getInstance().getKey(fourthNote, 4); // 14
		Chord aChord = new Chord("min9", rootNote, secondNote, thirdNote, fourthNote, fifthNote);
		return aChord;
	}

	// This is enharmonically equivalent to the major sixth. Might not be found
	// using a scale name
	public Chord dimishedSeventhChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 3
		thirdNote = carriedScale.getDominant();// 6
		fourthNote = carriedScale.getSuperTonic(); // 9
		Chord aChord = new Chord("dim7", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	// Ionian scale's 1st,3rd and 5th degree with added ninth
	public Chord addNineChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = carriedScale.getDominant(); // 7

		// Same as ninth dominant chord
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 7); // 14
		Chord aChord = new Chord("add9", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	// Ionian scale's 1st,3rd and 5th degree with added ninth
	public Chord addTwoChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = ListOfScales.getInstance().getKey(rootNote, 2); // 2
		thirdNote = ListOfScales.getInstance().getKey(secondNote, 2); // 4
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 3); // 7
		Chord aChord = new Chord("add2", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	public Chord minorEleventhChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 3
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 3); // 10
		fifthNote = ListOfScales.getInstance().getKey(fourthNote, 4); // 14
		sixthNote = ListOfScales.getInstance().getKey(fifthNote, 3); // 17
		Chord aChord = new Chord("min11", rootNote, secondNote, thirdNote, fourthNote, fifthNote, sixthNote);
		return aChord;
	}

	public Chord dominantEleventhChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 3); // 10
		fifthNote = ListOfScales.getInstance().getKey(fourthNote, 4); // 14
		sixthNote = ListOfScales.getInstance().getKey(fifthNote, 3); // 17
		Chord aChord = new Chord("11", rootNote, secondNote, thirdNote, fourthNote, fifthNote, sixthNote);
		return aChord;
	}

	public Chord dominantThirteenChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 3); // 10
		fifthNote = ListOfScales.getInstance().getKey(fourthNote, 4); // 14
		sixthNote = ListOfScales.getInstance().getKey(fifthNote, 3); // 17
		seventhNote = ListOfScales.getInstance().getKey(sixthNote, 4); // 21
		Chord aChord = new Chord("13", rootNote, secondNote, thirdNote, fourthNote, fifthNote, sixthNote, seventhNote);
		return aChord;

	}

	public Chord minorThirteenChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 3
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 3); // 10
		fifthNote = ListOfScales.getInstance().getKey(fourthNote, 4); // 14
		sixthNote = ListOfScales.getInstance().getKey(fifthNote, 3); // 17
		seventhNote = ListOfScales.getInstance().getKey(sixthNote, 4); // 21
		Chord aChord = new Chord("min13", rootNote, secondNote, thirdNote, fourthNote, fifthNote, sixthNote,
				seventhNote);
		return aChord;
	}

	public Chord majorThirteenChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = carriedScale.getDominant(); // 7
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 4); // 11
		fifthNote = ListOfScales.getInstance().getKey(fourthNote, 3); // 14
		sixthNote = ListOfScales.getInstance().getKey(fifthNote, 3); // 17
		seventhNote = ListOfScales.getInstance().getKey(sixthNote, 4); // 21
		Chord aChord = new Chord("maj13", rootNote, secondNote, thirdNote, fourthNote, fifthNote, sixthNote,
				seventhNote);
		return aChord;
	}

	/**
	 * NON SCALE BASED CHORDS
	 */

	/**
	 * Based of ionian scale: Enharmonically equivalent to the French sixth
	 * chord.
	 */
	public Chord sevenFlatFiveChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = ListOfScales.getInstance().getKey(secondNote, 2); // 6
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 4); // 10
		Chord aChord = new Chord("7b5", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	public Chord sevenSharpFiveChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = ListOfScales.getInstance().getKey(secondNote, 4); // 8
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 2); // 10
		Chord aChord = new Chord("7#5", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	// Based of minor scale
	////////////////////////////////////
	public Chord minorMajorSevenChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 3
		thirdNote = ListOfScales.getInstance().getKey(secondNote, 4); // 7
		fourthNote = ListOfScales.getInstance().getKey(thirdNote, 4); // 11
		Chord aChord = new Chord("minMaj7", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	// Based on locrian #2 (half-Diminished scale)
	public Chord minorSevenFlatFiveChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getMediant(); // 4
		thirdNote = carriedScale.getDominant(); // 6
		fourthNote = carriedScale.getSuperTonic(); // 10
		Chord aChord = new Chord("min7b5", rootNote, secondNote, thirdNote, fourthNote);
		return aChord;
	}

	public Chord bluesChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = carriedScale.getDominant(); // 7
		Chord aChord = new Chord("Number Chord", rootNote, secondNote);
		return aChord;
	}

	public Chord fillerBluesChord(Scale carriedScale) {
		rootNote = carriedScale.getTonic(); // 0
		secondNote = ListOfScales.getInstance().getKey(rootNote, 12); // 12
		Chord aChord = new Chord("Adjusted Chord", rootNote, secondNote);
		return aChord;
	}
}