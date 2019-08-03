package midi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import keyboard.Note;

/**
 * This class handles all the variations of scale manipulation in the system.
 * This includes creation of all support scales, and storage in memory for
 * retrieval in scale related features.
 */
public class ListOfScales {

	private static volatile ListOfScales instance = null;

	private ListOfScales() {
	}

	public static ListOfScales getInstance() {
		if (instance == null) {
			synchronized (ListOfScales.class) {
				if (instance == null) {
					instance = new ListOfScales();
				}
			}
		}
		return instance;
	}

	private Map<String, Note> mapOfNotes = Note.getNotesMap();
	private Collection<Integer> currentScaleIntervals;
	private Scale currentDisplayedScaleColor;
	private ArrayList<Scale> diatonicMajorScales = new ArrayList<Scale>();
	private ArrayList<Scale> diatonicMinorScales = new ArrayList<Scale>();

	/**
	 * The function takes in the argument key (note) string, appends a third
	 * octave to it, and then retrieves the note from the map of all notes. This
	 * provides a note near or is middle C, which is used as the key to create
	 * all supported scales in that key. These scales are stored in memory, and
	 * this process is done for each key in the chromatic scale from the calling
	 * function, to have load-up access to all scales.
	 * 
	 * @param noteLetter
	 *            - The letter string used derived from a jList to create the
	 *            scales' key.
	 */
	public void generateScalesNames(String noteLetter) {
		noteLetter += 3;
		Note key = mapOfNotes.get(noteLetter);

		// Diatonic Scales / Modes
		Scale.storeScales(majorOrIonianScale(key));
		Scale.storeScales(dorianScale(key));
		Scale.storeScales(phrygianScale(key));
		Scale.storeScales(lydianScale(key));
		Scale.storeScales(mixolydianScale(key));
		Scale.storeScales(minorOrAeolianScale(key));
		Scale.storeScales(locrianScale(key));
		/////////////////////////////////////////////

		// Pentatonic Scales
		Scale.storeScales(majorPentatonicScale(key));
		Scale.storeScales(minorPentatonicScale(key));

		// Other Scales
		Scale.storeScales(bluesScale(key));
		Scale.storeScales(ascendingChromaticScale(key));
		Scale.storeScales(descendingChromaticScale(key));
		Scale.storeScales(augmentedScale(key));
		Scale.storeScales(wholeToneScale(key));
		Scale.storeScales(harmonicMinorScale(key));
		Scale.storeScales(ascendingMelodicMinorScale(key));
		Scale.storeScales(alteredScale(key));
		Scale.storeScales(halfDiminishedScale(key));
		Scale.storeScales(dominantDiminishedScale(key));
		Scale.storeScales(fullyDiminishedScale(key));
	}

	// Diatonic major getters and setters
	public void keyDiatonicMajorScales(Scale current) {
		diatonicMajorScales.add(current);
	}

	public ArrayList<Scale> getDiatonicMajorScales() {
		return diatonicMajorScales;
	}
	///////////////////////////////////////////////////

	// Diatonic minor getters and setters
	public void keyDiatonicMinorScales(Scale current) {
		diatonicMinorScales.add(current);
	}

	public ArrayList<Scale> getDiatonicMinorScales() {
		return diatonicMinorScales;
	}
	///////////////////////////////////////////////////

	// Alter current scale in colour mode getters and setters
	public void displayedScaleNotes(Scale currentScale) {
		currentDisplayedScaleColor = currentScale;
	}

	public Scale getDisplayedScaleNotes() {
		return currentDisplayedScaleColor;
	}
	///////////////////////////////////////////////////

	public void currentScalePitchValues(Collection<Integer> intervals) {
		currentScaleIntervals = intervals;
	}

	public Collection<Integer> getScalePitchValues() {
		return currentScaleIntervals;
	}

	/**
	 * Find next interval / step based on previous note in scale
	 * 
	 * @param passedNote
	 *            - The note to use as a comparison base.
	 * @param step
	 *            - The internal to be used to find the note with it incremented
	 *            on the passedNote.
	 * @return The found increase pitch note from memory or null if not found.
	 */
	public Note getKey(Note passedNote, int step) {
		for (Entry<String, Note> entry : mapOfNotes.entrySet()) {

			// For some reason, the minus arithmetic increases the compared
			// value. e.g: step = 2, To find 48 compared to 46, this makes 48
			// rather than 44.
			if (passedNote.getPitch() == entry.getValue().getPitch() - step) {
				String key = entry.getKey();
				Note stepNote = mapOfNotes.get(key);
				return stepNote;
			}

		}
		return null;
	}

	/**
	 * PENTATONIC SCALES: A scale with 5 notes or scale degrees
	 */

	/**
	 * This scale is similar to the Hepatonic major (Ionian), but it does not
	 * have a subtonic and submediant.
	 */
	public Scale majorPentatonicScale(Note rootKey) {
		String scaleName = "Pentatonic Major";
		Note tonic = rootKey; // do
		Note mediant = getKey(tonic, 2); // re
		Note subDominant = getKey(mediant, 2); // mi
		Note dominant = getKey(subDominant, 3); // so
		Note superTonic = getKey(dominant, 2); // so
		Note endOctaveNote = getKey(superTonic, 3);
		Scale aScale = new Scale(scaleName, tonic, mediant, subDominant, dominant, superTonic, endOctaveNote);
		return aScale;
	}

	/**
	 * This scale is similar to the Hepatonic minor (Aeolian), but it does not
	 * have a subtonic and submediant.
	 */
	public Scale minorPentatonicScale(Note rootKey) {
		String scaleName = "Pentatonic Minor";
		Note tonic = rootKey; // do
		Note mediant = getKey(tonic, 3); // re
		Note subDominant = getKey(mediant, 2); // mi
		Note dominant = getKey(subDominant, 2); // so
		Note superTonic = getKey(dominant, 3); // so
		Note endOctaveNote = getKey(superTonic, 2);
		Scale aScale = new Scale(scaleName, tonic, mediant, subDominant, dominant, superTonic, endOctaveNote);
		return aScale;
	}

	/**
	 * HEXATONIC SCALES: A scale with 6 notes or scale degrees, with There no
	 * sub mediant.
	 */

	/**
	 * This scale is made up of two augmented chords. Due to the existing
	 * symmetry in this scale, 3 from its 6 notes from a given key can be
	 * considered the tonic.
	 **/
	public Scale augmentedScale(Note rootKey) {
		String scaleName = "Augmented";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 3);
		Note mediant = getKey(subtonic, 1);
		Note subdominant = getKey(mediant, 3);
		Note dominant = getKey(subdominant, 1);
		Note submediant = getKey(dominant, 3);
		Note endOctaveNote = getKey(submediant, 1);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, endOctaveNote);
		return aScale;
	}

	/**
	 * This scale is based on the minor pentatonic scale, but with an added
	 * augmented fourth degree.
	 **/
	public Scale bluesScale(Note rootKey) {
		String scaleName = "Blues";
		Note tonic = rootKey; // do
		Note subTonic = getKey(tonic, 3); // re
		Note mediant = getKey(subTonic, 2); // re
		Note subDominant = getKey(mediant, 1); // mi
		Note dominant = getKey(subDominant, 1); // so
		Note superTonic = getKey(dominant, 3); // so
		Note endOctaveNote = getKey(superTonic, 2);
		Scale aScale = new Scale(scaleName, tonic, subTonic, mediant, subDominant, dominant, superTonic, endOctaveNote);
		return aScale;
	}

	/**
	 * Another hexatonic scale, its 6 notes have a whole step interval between
	 * each note.
	 */
	public Scale wholeToneScale(Note rootKey) {
		String scaleName = "Whole Tone";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 2);
		Note mediant = getKey(subtonic, 2);
		Note subdominant = getKey(mediant, 2);
		Note dominant = getKey(subdominant, 2);
		Note submediant = getKey(dominant, 2);
		Note endOctaveNote = getKey(submediant, 2);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, endOctaveNote);
		return aScale;
	}

	/**
	 * HEPTAONIC SCALES: A scale with 7 notes or scale degrees. Includes
	 * diatonic and non-diatonic.A diatonic scale is an heptatonic scale that is
	 * broken into 5 whole notes and 2 semi-tones. There are 7 modes of the
	 * Diatonic Major Scale, each one being based off one of the notes in a 7
	 * note octave.
	 */

	/**
	 * This is the scale commonly referred to as the major scale.
	 */
	public Scale majorOrIonianScale(Note rootKey) {
		String scaleName = "Diantonic Ionian";

		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 2);
		Note mediant = getKey(subtonic, 2);
		Note subdominant = getKey(mediant, 1);
		Note dominant = getKey(subdominant, 2);
		Note submediant = getKey(dominant, 2);
		Note supertonic = getKey(submediant, 2);
		Note endOctaveNote = getKey(supertonic, 1);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				endOctaveNote);
		return aScale;
	}

	/**
	 * Diatonic mode 2: Based of minor (Aeolian) scale.
	 */
	public Scale dorianScale(Note rootKey) {
		String scaleName = "Diantonic Dorian";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 2);
		Note mediant = getKey(subtonic, 1);
		Note subdominant = getKey(mediant, 2);
		Note dominant = getKey(subdominant, 2);
		Note submediant = getKey(dominant, 2);
		Note supertonic = getKey(submediant, 1);
		Note endOctaveNote = getKey(supertonic, 2);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				endOctaveNote);
		return aScale;
	}

	/**
	 * Diatonic mode 3: Based of minor (Aeolian) scale.
	 */
	public Scale phrygianScale(Note rootKey) {
		String scaleName = "Diatonic Phrygian";
		Note tonic = rootKey; // Is a semi note
		Note subtonic = getKey(tonic, 1);
		Note mediant = getKey(subtonic, 2);
		Note subdominant = getKey(mediant, 2);
		Note dominant = getKey(subdominant, 2);
		Note submediant = getKey(dominant, 1);
		Note supertonic = getKey(submediant, 2);
		Note endOctaveNote = getKey(supertonic, 2);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				endOctaveNote);
		return aScale;
	}

	/**
	 * Diatonic mode 4: Based of major (Ionian) scale.
	 */
	public Scale lydianScale(Note rootKey) {
		String scaleName = "Diatonic Lydian";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 2);
		Note mediant = getKey(subtonic, 2);
		Note subdominant = getKey(mediant, 2);
		Note dominant = getKey(subdominant, 1);
		Note submediant = getKey(dominant, 2);
		Note supertonic = getKey(submediant, 2);
		Note endOctaveNote = getKey(supertonic, 1);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				endOctaveNote);
		return aScale;
	}

	/**
	 * Diatonic mode 5: Based of major (Ionian) scale.
	 */
	public Scale mixolydianScale(Note rootKey) {
		String scaleName = "Diatonic Mixolydian";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 2);
		Note mediant = getKey(subtonic, 2);
		Note subdominant = getKey(mediant, 1);
		Note dominant = getKey(subdominant, 2);
		Note submediant = getKey(dominant, 2);
		Note supertonic = getKey(submediant, 1);
		Note endOctaveNote = getKey(supertonic, 2);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				endOctaveNote);
		return aScale;
	}

	/**
	 * Commonly referred to as the minor scale (technically its the "natural
	 * minor" or Aeolian Scale). This is based of the 6th note or 6th mode of
	 * the hepatonic diatonic scale.
	 */
	public Scale minorOrAeolianScale(Note rootKey) {
		String scaleName = "Diatonic Aeolian";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 2);
		Note mediant = getKey(subtonic, 1);
		Note subdominant = getKey(mediant, 2);
		Note dominant = getKey(subdominant, 2);
		Note submediant = getKey(dominant, 1);
		Note supertonic = getKey(submediant, 2);
		Note endOctaveNote = getKey(supertonic, 2);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				endOctaveNote);
		return aScale;
	}

	/**
	 * Diatonic mode 7: Based of a diminished note, and potentially scale.
	 */
	public Scale locrianScale(Note rootKey) {
		String scaleName = "Diatonic Locrian";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 1);
		Note mediant = getKey(subtonic, 2);
		Note subdominant = getKey(mediant, 2);
		Note dominant = getKey(subdominant, 1);
		Note submediant = getKey(dominant, 2);
		Note supertonic = getKey(submediant, 2);
		Note endOctaveNote = getKey(supertonic, 2);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				endOctaveNote);
		return aScale;
	}

	/**
	 * Second type of heptatonic diatonic minor scale. A harmonic scale is just
	 * a natural minor scale (Aeolian), but with an 1 and 1/2 step seventh and a
	 * half step from the seventh and eight note.
	 */
	public Scale harmonicMinorScale(Note rootKey) {
		String scaleName = "Harmonic Minor";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 2);
		Note mediant = getKey(subtonic, 1);
		Note subdominant = getKey(mediant, 2);
		Note dominant = getKey(subdominant, 2);
		Note submediant = getKey(dominant, 1);
		Note supertonic = getKey(submediant, 3);
		Note endOctaveNote = getKey(supertonic, 1);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				endOctaveNote);
		return aScale;

	}

	/**
	 * Third type of heptatonic diatonic minor scale.
	 */
	public Scale ascendingMelodicMinorScale(Note rootKey) {
		String scaleName = "Melodic Minor";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 2);
		Note mediant = getKey(subtonic, 1);
		Note subdominant = getKey(mediant, 2);
		Note dominant = getKey(subdominant, 2);
		Note submediant = getKey(dominant, 2);
		Note supertonic = getKey(submediant, 2);
		Note endOctaveNote = getKey(supertonic, 1);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				endOctaveNote);
		return aScale;
	}

	/**
	 * This scale ascends using melodic scale, but descends using the natural
	 * minor scale
	 */
	public void descendingMelodicMinorScale(Note rootKey) {
		minorOrAeolianScale(rootKey);
	}

	/**
	 * Also known as the half-diminished scale, it has 7 notes not 8.
	 */
	public Scale halfDiminishedScale(Note rootKey) {
		String scaleName = "Half-Dimished";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 2); // 2nd note is major, not minor
		Note mediant = getKey(subtonic, 1);
		Note subdominant = getKey(mediant, 2);
		Note dominant = getKey(subdominant, 1);
		Note submediant = getKey(dominant, 2);
		Note supertonic = getKey(submediant, 2);
		Note endOctaveNote = getKey(supertonic, 2);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				endOctaveNote);
		return aScale;
	}

	/**
	 * Altered scale - This is also known as the super locrian or the diminished
	 * whole tone scale.
	 **/
	public Scale alteredScale(Note rootKey) {
		String scaleName = "Altered";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 1);
		Note mediant = getKey(subtonic, 2);
		Note subdominant = getKey(mediant, 1);
		Note dominant = getKey(subdominant, 2);
		Note submediant = getKey(dominant, 2);
		Note supertonic = getKey(submediant, 2);
		Note endOctaveNote = getKey(supertonic, 2);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				endOctaveNote);
		return aScale;
	}

	/**
	 * OCTATONIC SCALES: A scale with 8 notes or scale degrees.
	 */

	/**
	 * This scale is also called the half-whole scale, and its step intervals
	 * alternate between half then whole in a linear order.
	 **/
	public Scale dominantDiminishedScale(Note rootKey) {
		String scaleName = "Dominant Diminished";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 1);
		Note mediant = getKey(subtonic, 2);
		Note subdominant = getKey(mediant, 1);
		Note dominant = getKey(subdominant, 2);
		Note submediant = getKey(dominant, 1);
		Note supertonic = getKey(submediant, 2);
		Note eigthNote = getKey(supertonic, 1);
		Note endOctaveNote = getKey(eigthNote, 2);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				eigthNote, endOctaveNote);
		return aScale;
	}

	/**
	 * This octatonic scale is also called the Whole-half, diminished or
	 * diminished 7th scale.Its step intervals alternate whole/half.
	 */
	public Scale fullyDiminishedScale(Note rootKey) {
		String scaleName = "Fully Diminished";
		Note tonic = rootKey;
		Note subtonic = getKey(tonic, 2);
		Note mediant = getKey(subtonic, 1);
		Note subdominant = getKey(mediant, 2);
		Note dominant = getKey(subdominant, 1);
		Note submediant = getKey(dominant, 2);
		Note supertonic = getKey(submediant, 1);
		Note eigthNote = getKey(supertonic, 2);
		Note endOctaveNote = getKey(eigthNote, 1);
		Scale aScale = new Scale(scaleName, tonic, subtonic, mediant, subdominant, dominant, submediant, supertonic,
				eigthNote, endOctaveNote);
		return aScale;
	}

	/**
	 * CHROMATIC SCALE: This scale covers all 12 pitches and is made up of only
	 * semi-tones. Because each pitch in the scale is equidistant, there is no
	 * tonic, so it is known as a non-diatonic scale.It uses 12-tone equal
	 * temperament and it starts from C to gain each octave's pitches value.
	 **/
	private static String[][] createChromaticScale = { { "C" }, { "Db", "C#" }, { "D" }, { "Eb", "D#" }, { "E" },
			{ "F" }, { "Gb", "F#" }, { "G" }, { "Ab", "G#" }, { "A" }, { "Bb", "A#" }, { "B" } };

	public static String[][] getChromaticScale() {
		return createChromaticScale;
	}

	/**
	 * Traversal solfege order - includes sharp accidentals through rise.
	 **/
	public Scale ascendingChromaticScale(Note rootKey) {
		String scaleName = "Ascending Chromatic";
		Note Do = rootKey; // note name is in capital as java does not allow do
							// keyword as name
		Note di = getKey(Do, 1);
		Note re = getKey(di, 1);
		Note ri = getKey(re, 1);
		Note mi = getKey(ri, 1);
		Note fa = getKey(mi, 1);
		Note fi = getKey(fa, 1);
		Note sol = getKey(fi, 1);
		Note si = getKey(sol, 1);
		Note la = getKey(si, 1);
		Note li = getKey(la, 1);
		Note ti = getKey(li, 1);
		Scale aScale = new Scale(scaleName, Do, di, re, ri, mi, fa, fi, sol, si, la, li, ti);
		return aScale;
	}

	/**
	 * Traversal solfege order - includes flat accidentals through reverse.
	 */
	public Scale descendingChromaticScale(Note rootKey) {
		String scaleName = "Descending Chromatic";
		Note Do = rootKey; // Again, note name is in capital as java does
							// not allow "do" keyword as name.
		Note ti = getKey(Do, -1);
		Note te = getKey(ti, -1);
		Note la = getKey(te, -1);
		Note le = getKey(la, -1);
		Note sol = getKey(le, -1);
		Note se = getKey(sol, -1);
		Note fa = getKey(se, -1);
		Note mi = getKey(fa, -1);
		Note me = getKey(mi, -1);
		Note re = getKey(me, -1);
		Note ra = getKey(re, -1);
		Scale aScale = new Scale(scaleName, Do, ti, te, la, le, sol, se, fa, mi, me, re, ra);
		return aScale;
	}
}

/**
 * This class creates a data structure to allocates all scales created from a
 * given key to its given key in memory. This is used later in the system to
 * load up scales supported for a given key
 */
class GivenKeyScales {
	private String scaleName = "";
	private ArrayList<GivenKeyChords> ScaleKeys;

	public GivenKeyScales(String scaleName, ArrayList<GivenKeyChords> aKeyChords) {
		this.scaleName = scaleName;
		this.ScaleKeys = aKeyChords;
	}

	public ArrayList<GivenKeyChords> getScaleKeys() {
		return ScaleKeys;
	}
}