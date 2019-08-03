package midi;

import java.util.ArrayList;
import keyboard.Note;

/**
 * This class defines the construct of a chord object and its accessible
 * functions.
 */
public class Chord {

	private String aChordName = "";
	private ArrayList<Note> chordNotes = new ArrayList<Note>();
	private static ArrayList<String> chordList = new ArrayList<String>();

	private static String uiSelectedChordName = "";
	private static String uiSelectedChord = "";
	private static String uiSelectedRoot = "";

	public static void storeRoot(String uiRoot) {
		uiSelectedRoot = uiRoot;
	}

	public static String getStoredRoot() {
		return uiSelectedRoot;
	}

	public static void storeChordName(String uiChordName) {
		uiSelectedChordName = uiChordName;
	}

	public static String getStoredChordName() {
		return uiSelectedChordName;
	}

	public static String getStoredChord() {
		uiSelectedChord = uiSelectedRoot + uiSelectedChordName;
		return uiSelectedChord;
	}

	public Chord(String chordName, ArrayList<Note> editedChordNotes) {
		this.aChordName = chordName;
		this.chordNotes = editedChordNotes;
	}

	// Used in Blues scale, which can add variation to
	public Chord(String chordName, Note note1, Note note2) {
		this.aChordName = chordName;
		this.chordNotes.add(note1);
		this.chordNotes.add(note2);
	}

	// 3 notes chord
	// The arraylist segments the notes from the chord name for
	// easy manipulation of the notes, and to get its size of notes
	public Chord(String chordName, Note note1, Note note2, Note note3) {
		this.aChordName = chordName;
		this.chordNotes.add(note1);
		this.chordNotes.add(note2);
		this.chordNotes.add(note3);
	}

	// 4 notes chord - add6, add9, etc
	public Chord(String chordName, Note note1, Note note2, Note note3, Note note4) {
		this.aChordName = chordName;
		this.chordNotes.add(note1);
		this.chordNotes.add(note2);
		this.chordNotes.add(note3);
		this.chordNotes.add(note4);
	}

	// 5 notes chord - dominant seventh, minor Ninth,etc
	public Chord(String chordName, Note note1, Note note2, Note note3, Note note4, Note note5) {
		this.aChordName = chordName;
		this.chordNotes.add(note1);
		this.chordNotes.add(note2);
		this.chordNotes.add(note3);
		this.chordNotes.add(note4);
		this.chordNotes.add(note5);
	}

	// 6 notes chord - minor Eleventh, dominant Eleventh, etc
	public Chord(String chordName, Note note1, Note note2, Note note3, Note note4, Note note5, Note note6) {
		this.aChordName = chordName;
		this.chordNotes.add(note1);
		this.chordNotes.add(note2);
		this.chordNotes.add(note3);
		this.chordNotes.add(note4);
		this.chordNotes.add(note5);
		this.chordNotes.add(note6);
	}

	// 7 notes chord - minor Thirteen, dominant Thirteen, etc
	public Chord(String chordName, Note note1, Note note2, Note note3, Note note4, Note note5, Note note6, Note note7) {
		this.aChordName = chordName;
		this.chordNotes.add(note1);
		this.chordNotes.add(note2);
		this.chordNotes.add(note3);
		this.chordNotes.add(note4);
		this.chordNotes.add(note5);
		this.chordNotes.add(note6);
		this.chordNotes.add(note7);
	}

	public void setChordName(String currentChord) {
		this.aChordName = currentChord;
	}

	public String getChordName() {
		return aChordName;
	}

	public ArrayList<Note> getChordNotes() {
		return chordNotes;
	}

	//////////////////
	public static ArrayList<String> getAllChordEnums() {
		chordList = new ArrayList<String>();
		allChords[] allChordsArray = allChords.values();
		for (allChords aValue : allChordsArray) {
			chordList.add(aValue.getChord());
		}
		return chordList;
	}

	//////////////////

	public static ArrayList<String> getMajorEnums() {
		chordList = new ArrayList<String>();
		majorBasedChords[] majorChordsArray = majorBasedChords.values();
		for (majorBasedChords aValue : majorChordsArray) {
			chordList.add(aValue.getChord());
		}
		return chordList;
	}

	public static ArrayList<String> getPureMajorEnums() {
		chordList = new ArrayList<String>();
		pureMajorBasedChords[] pureMajorChordsArray = pureMajorBasedChords.values();
		for (pureMajorBasedChords aValue : pureMajorChordsArray) {
			chordList.add(aValue.getChord());
		}
		return chordList;
	}
	//////////////////

	public static ArrayList<String> getMinorEnums() {
		chordList = new ArrayList<String>();
		minorBasedChords[] minorChordsArray = minorBasedChords.values();
		for (minorBasedChords aValue : minorChordsArray) {
			chordList.add(aValue.getChord());
		}
		return chordList;
	}

	public static ArrayList<String> getHalfDimsEnums() {
		chordList = new ArrayList<String>();
		halfDimishedChords[] halfDimChordsArray = halfDimishedChords.values();
		for (halfDimishedChords aValue : halfDimChordsArray) {
			chordList.add(aValue.getChord());
		}
		return chordList;
	}

	public static ArrayList<String> getFullDimsEnums() {
		chordList = new ArrayList<String>();
		fullyDimishedChords[] fullDimChordsArray = fullyDimishedChords.values();
		for (fullyDimishedChords aValue : fullDimChordsArray) {
			chordList.add(aValue.getChord());
		}
		return chordList;
	}

	public static void resetChordsLists() {
		chordList = new ArrayList<String>();
	}

	public enum allChords {
		majTetra("majTetra"), maj("maj"), maj7("maj7"), maj9("maj9"), maj13("maj13"), maj6("maj6"), sus4("sus4"), sus2(
				"sus2"), seven("7"), nine("9"), add9("add9"), add2("add2"), eleven("11"), thirteen("13"), aug("aug"),

		minTetra("minTetra"), min("min"), min7("min7"), min9("min9"), min13("min13"), min11("min11"), minMajSeven(
				"minMaj7"),

		// sevenFlatFive("7b5"),
		// sevenSharpFive("maj7#5"),
		// sevenFlatFiveFlatNine("7b5b9"),
		// sevenSharpFiveFlatNine("7#5b9"),
		// sevenSharpFiveSharpNine("7#5#9"),

		minSevenFlatFive("min7b5"),

		dim("dim"), dim7("dim7");

		public final String chord;

		allChords(String chord) {
			this.chord = chord;
		}

		public String getChord() {
			return chord;
		}
	}

	public enum majorBasedChords {
		majTetra("majTetra"), maj("maj"), maj7("maj7"), maj9("maj9"), maj13("maj13"), sus4("sus4"), sus2("sus2"), seven(
				"7"), maj6("maj6"), nine("9"), add9("add9"), add2("add2"), eleven("11"), thirteen("13"), aug("aug");

		public final String chord;

		majorBasedChords(String chord) {
			this.chord = chord;
		}

		public String getChord() {
			return chord;
		}
	}

	public enum pureMajorBasedChords {
		majTetra("majTetra"), maj("maj"), maj7("maj7"), maj9("maj9"), maj13("maj13");

		public final String chord;

		pureMajorBasedChords(String chord) {
			this.chord = chord;
		}

		public String getChord() {
			return chord;
		}
	}

	public enum chordNoteNames {
		majTetra(new String[] { "First Note: root", "Second Note: major second", "Third Note: major third",
				"Fourth Note: perfect fourth" }), maj(new String[] { "First Note: root,", "Second Note: major third",
						"Third Note: perfect fifth" }), maj7(new String[] { "First Note: root,",
								"Second Note: major third", "Third Note: perfect fifth",
								"Fourth Note: major seventh" }), maj9(new String[] { "First Note: root,",
										"Second Note: major third", "Third Note: perfect fifth",
										"Fourth Note: major seventh", "Fifth Note: major ninth" }), maj13(
												new String[] { "First Note: root,", "Second Note: major third",
														"Third Note: perfect fifth", "Fourth Note: major seventh",
														"Fifth Note: major ninth", "Sixth Note: major eleventh",
														"Seventh Note: major thirteen" }), min11(new String[] {
																"First Note: root,", "Second Note: minor third",
																"Third Note: perfect fifth",
																"Fourth Note: minor seventh", "Fifth Note: major ninth",
																"Sixth Note: minor eleventh" }),

		sus4(new String[] { "First Note: root,", "Second Note: perfect fourth", "Third Note: perfect fifth" }), sus2(
				new String[] { "First Note: root,", "Second Note: major second", "Third Note: perfect fifth" }),

		seven(new String[] { "First Note: root,", "Second Note: major third", "Third Note: perfect fifth",
				"Fourth Note: dimished seventh" }), maj6(new String[] { "First Note: root,", "Second Note: major third",
						"Third Note: perfect fifth", "Fourth Note: major sixth" }), nine(new String[] {
								"First Note: root,", "Second Note: major third", "Third Note: perfect fifth",
								"Fourth Note: minor seventh", "Fifth Note: major ninth" }), add9(new String[] {
										"First Note: root,", "Second Note: major third", "Third Note: perfect",
										"Fourth Note: major ninth" }), add2(new String[] { "First Note: root,",
												"Second Note: major second",
												"Third Note: perfect fifth" }), eleven(new String[] {
														"First Note: root,", "Second Note: major third",
														"Third Note: perfect fifth", "Fourth Note: minor seventh",
														"Fifth Note: major ninth",
														"Sixth Note: minor eleventh" }), thirteen(new String[] {
																"First Note: root,", "Second Note: major third",
																"Third Note: perfect fifth",
																"Fourth Note: minor seventh", "Fifth Note: major ninth",
																"Sixth Note: minor eleventh",
																"Seventh Note: major thirteenth" }), aug(
																		new String[] { "First Note: root,",
																				"Second Note: major third",
																				"Third Note: augmented fifth" });

		public final String[] chord;

		chordNoteNames(String[] chord) {
			this.chord = chord;
		}

		public String[] getChord() {
			return chord;
		}
	}

	public enum minorBasedChords {
		minTetra("minTetra"), min("min"), min7("min7"), min9("min9"), min13("min13"), min11("min11"), minMajSeven(
				"minMaj7");

		public final String chord;

		minorBasedChords(String chord) {
			this.chord = chord;
		}

		public String getChord() {
			return chord;
		}
	}

	public enum noneScaleChords {
		sevenFlatFive("7b5"), sevenSharpFive("7#5"), sevenFlatFiveFlatNine("7b5b9"), sevenSharpFiveFlatNine(
				"7#5b9"), sevenSharpFiveSharpNine("7#5#9");

		public final String chord;

		noneScaleChords(String chord) {
			this.chord = chord;
		}

		public String getChord() {
			return chord;
		}
	}

	public enum halfDimishedChords {
		minSevenFlatFive("min7b5");

		public final String chord;

		halfDimishedChords(String chord) {
			this.chord = chord;
		}

		public String getChord() {
			return chord;
		}
	}

	public enum fullyDimishedChords {
		dim("dim"), dim7("dim7");

		public final String chord;

		fullyDimishedChords(String chord) {
			this.chord = chord;
		}

		public String getChord() {
			return chord;
		}
	}
}

/**
 * Inner class to create a data structure that allocates all supported chords to
 * a given key.
 */
class GivenKeyChords {
	String key = "";
	ArrayList<Chord> chords;

	public GivenKeyChords(String key, ArrayList<Chord> aChord) {
		this.key = key;
		this.chords = aChord;
	}

	public ArrayList<Chord> getKeyChords() {
		return chords;
	}

	public String getKeyName() {
		return key;
	}
}