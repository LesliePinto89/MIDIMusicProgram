package midi;

import java.util.ArrayList;
import javax.swing.DefaultListModel;
import keyboard.Note;

/**
 * This class defines the construct of a scale object and its accessible
 * functions.
 */
public class Scale extends Note {

	private String scaleName = "";
	private static String currentScaleKey = "";
	private static String currentScaleName = "";
	private static ArrayList<String> scaleList = new ArrayList<String>();

	public enum ascendingSolfege {
		Do, Di, Re, Ri, Mi, Fa, Fi, Sol, Si, La, Li, Ti;
	}

	public enum descendingSolfege {
		Do, Ti, Te, La, Le, Sol, Se, Fa, Mi, Me, Re, Ra;
	}

	public enum pentatonicDegrees {
		tonic, subtonic, mediant, subdominant, dominant, newTonic;
	}

	public enum hexaTonicDegrees {
		tonic, subtonic, mediant, subdominant, dominant, submediant, newTonic;
	}

	public enum heptTonicDegree {
		tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic, newTonic;
	}

	public enum octaTonicDegree {
		tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic, eigthNote, newTonic;
	}

	public enum pureMajorBasedScales {
		diatonicMajor("Diantonic Ionian"), pentatonicMajor("Pentatonic Major");

		public final String scale;

		pureMajorBasedScales(String scale) {
			this.scale = scale;
		}

		public String getScaleName() {
			return scale;
		}
	}

	public static ArrayList<String> getPureMajorScales() {
		scaleList = new ArrayList<String>();
		pureMajorBasedScales[] pureMajorScalesArray = pureMajorBasedScales.values();
		for (pureMajorBasedScales aValue : pureMajorScalesArray) {
			scaleList.add(aValue.getScaleName());
		}
		return scaleList;
	}

	// Used to construct and edit list of notes to make a scale adjacent to
	// scale name
	private ArrayList<Note> scaleNotes = new ArrayList<Note>();
	private static ArrayList<Scale> tempScaleStorage = new ArrayList<Scale>();
	private static ArrayList<Scale> allScaleNames = new ArrayList<Scale>();
	private static DefaultListModel<String> listScaleNames = new DefaultListModel<String>();

	public static void storeScaleKey(String scaleKey) {
		currentScaleKey = scaleKey;
	}

	public static String getScaleKey() {
		return currentScaleKey;
	}

	public static void storeCurrentScaleName(String scaleName) {
		currentScaleName = scaleName;
	}

	/**
	 * Gets current scale name, not instantiated scale name as at bottom of this
	 * class.
	 */
	public static String getCurrentScaleName() {
		return currentScaleName;
	}

	public ArrayList<Note> getScaleNotesList() {
		return scaleNotes;
	}

	public Note getScaleNote(int index) {
		return scaleNotes.get(index);
	}

	public static DefaultListModel<String> getScales() {
		return listScaleNames;
	}

	public static ArrayList<Scale> getListOfScales() {
		return allScaleNames;
	}

	public static Scale getScaleFromList(int index) {
		return allScaleNames.get(index);
	}

	public static void storeScales(Scale aScale) {
		String keyName = "";

		// Use for either actual tonic or solfiege Do
		keyName = aScale.getTonic().getName();
		keyName = keyName.substring(0, keyName.length() - 1);

		if (allScaleNames.size() == 0) {
			allScaleNames.add(aScale);
			listScaleNames.addElement(keyName + " " + aScale.getScaleName());
		} else if (!aScale.getScaleName().equals(allScaleNames.get(allScaleNames.size() - 1).getScaleName())) {
			tempScaleStorage.add(aScale);
			
			// Added key name to scale name to make look better, but will
			// convert back to original to get matching pitches if needed
			listScaleNames.addElement(keyName + " " + aScale.getScaleName());
			allScaleNames.add(aScale);
			tempScaleStorage = new ArrayList<Scale>();
		}
	}

	public static void resetScalesLists() {
		tempScaleStorage = new ArrayList<Scale>();
		listScaleNames = new DefaultListModel<String>();
		allScaleNames = new ArrayList<Scale>();
	}

	/** Create Pentatonic scale template */
	public Scale(String scaleName, Note note1, Note note2, Note note3, Note note4, Note note5, Note newTonic) {
		this.scaleName = scaleName;
		this.scaleNotes.add(note1);
		this.scaleNotes.add(note2);
		this.scaleNotes.add(note3);
		this.scaleNotes.add(note4);
		this.scaleNotes.add(note5);
		this.scaleNotes.add(newTonic);
	}

	/** Create Hexatonic scale template */
	public Scale(String scaleName, Note note1, Note note2, Note note3, Note note4, Note note5, Note note6,
			Note newTonic) {
		this.scaleName = scaleName;
		this.scaleNotes.add(note1);
		this.scaleNotes.add(note2);
		this.scaleNotes.add(note3);
		this.scaleNotes.add(note4);
		this.scaleNotes.add(note5);
		this.scaleNotes.add(note6);
		this.scaleNotes.add(newTonic);
	}

	/** Create Hepatonic scale template */
	public Scale(String scaleName, Note note1, Note note2, Note note3, Note note4, Note note5, Note note6, Note note7,
			Note newTonic) {
		this.scaleName = scaleName;
		this.scaleNotes.add(note1);
		this.scaleNotes.add(note2);
		this.scaleNotes.add(note3);
		this.scaleNotes.add(note4);
		this.scaleNotes.add(note5);
		this.scaleNotes.add(note6);
		this.scaleNotes.add(note7);
		this.scaleNotes.add(newTonic);
	}

	/** Create Octatonic scale template */
	public Scale(String scaleName, Note note1, Note note2, Note note3, Note note4, Note note5, Note note6, Note note7,
			Note note8, Note newTonic) {
		scaleNotes = new ArrayList<Note>();
		this.scaleName = scaleName;
		this.scaleNotes.add(note1);
		this.scaleNotes.add(note2);
		this.scaleNotes.add(note3);
		this.scaleNotes.add(note4);
		this.scaleNotes.add(note5);
		this.scaleNotes.add(note6);
		this.scaleNotes.add(note7);
		this.scaleNotes.add(note8);
		this.scaleNotes.add(newTonic);
	}

	/** Create Chromatic scale template : Ascending and Descending */
	public Scale(String scaleName, Note note1, Note note2, Note note3, Note note4, Note note5, Note note6, Note note7,
			Note note8, Note note9, Note note10, Note note11, Note note12) {
		if (scaleName.equals("Ascending Chromatic")) {
			this.scaleName = scaleName;
			this.scaleNotes.add(note1);
			this.scaleNotes.add(note2);
			this.scaleNotes.add(note3);
			this.scaleNotes.add(note4);
			this.scaleNotes.add(note5);
			this.scaleNotes.add(note6);
			this.scaleNotes.add(note7);
			this.scaleNotes.add(note8);
			this.scaleNotes.add(note9);
			this.scaleNotes.add(note10);
			this.scaleNotes.add(note11);
			this.scaleNotes.add(note12);
		}

		// Descends. Sounds then same but would have difference chords
		// based on whether using flats or sharps
		else if (scaleName.contains("Descending Chromatic")) {
			this.scaleName = scaleName;
			this.scaleNotes.add(note1);
			this.scaleNotes.add(note2);
			this.scaleNotes.add(note3);
			this.scaleNotes.add(note4);
			this.scaleNotes.add(note5);
			this.scaleNotes.add(note6);
			this.scaleNotes.add(note7);
			this.scaleNotes.add(note8);
			this.scaleNotes.add(note9);
			this.scaleNotes.add(note10);
			this.scaleNotes.add(note11);
			this.scaleNotes.add(note12);
		}
	}

	public String getScaleName() {
		return scaleName;
	}

	public Note getTonic() {
		return scaleNotes.get(0);
	}

	public Note getSubTonic() {
		return scaleNotes.get(1);
	}

	public Note getMediant() {
		return scaleNotes.get(2);
	}

	public Note getSubDominant() {
		return scaleNotes.get(3);
	}

	public Note getDominant() {
		return scaleNotes.get(4);
	}

	public Note getSubMediant() {
		return scaleNotes.get(5);
	}

	public Note getSuperTonic() {
		return scaleNotes.get(6);
	}
}