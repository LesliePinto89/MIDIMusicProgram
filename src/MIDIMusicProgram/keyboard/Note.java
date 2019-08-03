package keyboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import midi.ListOfScales;

/**
 * This class defines the construct of a note, and it combines core aspects of
 * music theory.
 */
public class Note {
	private String name;
	private String accidental;
	private int pitch;
	private int velocity;
	private int octave;
	private static Map<String, Note> mapOfNotes = new LinkedHashMap<String, Note>();

	public Note() {
		;
	}

	/**
	 * A given notes's construction.
	 * 
	 * @param name
	 *            - note name from chromatic scale; e.g. C or D
	 * @param octave
	 *            - Value of octave to assign to note name.
	 * @param pitch
	 *            - Value of note/octave's MIDI sound; e.g. C4 equals 60
	 * @param velocity
	 *            - Value of how forceful a note is depressed.
	 * @param accidental
	 *            - Character representing of note's accidental; e.g. sharp
	 */
	public Note(String name, int pitch, int newOctave, int newVelocity, String noteType) {
		this.name = name; // latter includes any accidental and octave number
		this.pitch = pitch;
		this.octave = newOctave;
		this.velocity = newVelocity;
		this.accidental = noteType;
	}

	// Add each note created during button creation with string + octave, and
	// other note
	// values stored in note object
	public static Map<String, Note> getNotesMap() {
		return mapOfNotes;
	}

	public void storeNotes(String noteName, Note aNote) {
		mapOfNotes.put(noteName, aNote);
	}

	public static Note getNotes(String key) {
		return mapOfNotes.get(key);
	}

	public String getType() {
		return accidental;
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public String getName() {
		return name;
	}

	public int getOctave() {
		return octave;
	}

	public int getVelocity() {
		return velocity;
	}

	public enum allNotesType {
		C("C"), CSHARP("C#"), D("D"), DSHARP("D#"), E("E"), F("F"), FSHARP("F#"), G("G"), GSHARP("G#"), A("A"), ASHARP(
				"A#"), B("B");
		public final String note;

		allNotesType(String note) {
			this.note = note;
		}

		public String getNote() {
			return note;
		}
	}

	public static ArrayList<String> randomNotesForScaleKeys() {
		ArrayList<String> keyList = new ArrayList<String>();
		allNotesType[] noteKeysArray = allNotesType.values();
		for (allNotesType aValue : noteKeysArray) {
			keyList.add(aValue.getNote());
		}
		return keyList;
	}

	// Used in 61 key piano implementation
	///////////////////////
	// public enum NoteType {
	// C, D, E, F, G, A, B,
	// }

	public enum NoteType {
		A, B, C, D, E, F, G;
	}

	// Used in 61 key piano implementation
	//////////////////////
	// public enum SharpNoteType {
	// CSHARP("C#"),
	// DSHARP("D#"),
	// FSHARP("F#"),
	// GSHARP("G#"),
	// ASHARP("A#");
	//
	// public final String noteSharp;
	// SharpNoteType(String noteSharp) {
	// this.noteSharp = noteSharp;
	// }
	// public String getSharp() {
	// return noteSharp;
	// }
	// }

	public enum SharpNoteType {
		ASHARP("A#"), CSHARP("C#"), DSHARP("D#"), FSHARP("F#"), GSHARP("G#");

		public final String noteSharp;

		SharpNoteType(String noteSharp) {
			this.noteSharp = noteSharp;
		}

		public String getSharp() {
			return noteSharp;
		}
	}

	/**
	 * Construct's a given notes pitch value..
	 * 
	 * @param note
	 *            - the current note's name+octave to be used to derive its
	 *            pitch.
	 * @return The pitch numerical value to be assigned to the controller button
	 */
	public static int convertToPitch(String note) {
		String symbol = "";
		int octave = 0;
		;
		char[] splitNote = note.toCharArray();

		// If the length is two, then grab the symbol and number.
		// Otherwise, it must be a two-char note.
		if (splitNote.length == 2) {
			symbol += splitNote[0];
			octave = splitNote[1];
		} else if (splitNote.length == 3) {
			symbol += Character.toString(splitNote[0]);
			symbol += Character.toString(splitNote[1]);
			octave = splitNote[2];
		}

		for (int i = 0; i < ListOfScales.getChromaticScale().length; i++)
			for (int j = 0; j < ListOfScales.getChromaticScale()[i].length; j++) {
				if (ListOfScales.getChromaticScale()[i][j].equals(symbol)) {
					return Character.getNumericValue(octave + 1) * 12 + i;
				}
			}

		// If nothing was found, we return -1.
		return -1;
	}
}