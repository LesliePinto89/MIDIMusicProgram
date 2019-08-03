package midi;
import java.util.ArrayList;

/**
 * This class defines the construct of a chord progression. It uses a list of
 * chords assigned to a sequence based of either a major or minor scale.
 */
public class ChordProgression {

	private String progressionName = "";
	private ArrayList<Chord> progressionChords = new ArrayList<Chord>();

	public ChordProgression(String currentProgressionName, ArrayList<Chord> progressionChords) {
		this.progressionName = currentProgressionName;
		this.progressionChords = progressionChords;
	}

	public String getProgressionName() {
		return progressionName;
	}

	public ArrayList<Chord> getProgressionChords() {
		return progressionChords;
	}

	/**
	 * In music theory, sequences are in Roman numerical. Keeping to this
	 * convention can educate the user to traditional concepts, but in code this
	 * enables the TTS to translate these symbols in natural numeral language.
	 */
	public enum chordSymbol {
		i(1), ii(2), iii(3), iv(4), v(5), vi(6), vii(7), I(1), II(2), III(3), IV(4), V(5), VI(6), VII(7);

		public final int prog;

		chordSymbol(int prog) {
			this.prog = prog;
		}

		public int getProg() {
			return prog;
		}
	}

	public static ArrayList<Integer> getNumeralNumbers() {
		ArrayList<Integer> storage = new ArrayList<Integer>();
		chordSymbol[] numbers = chordSymbol.values();
		for (chordSymbol sym : numbers) {
			storage.add(sym.getProg());
		}
		return storage;
	}

	public static ArrayList<String> getNamedOnlyNumberOfNumeral() {
		ArrayList<String> storage = new ArrayList<String>();
		chordNumerals[] numbers = chordNumerals.values();
		for (chordNumerals sym : numbers) {
			storage.add(sym.toString());
		}
		return storage;
	}

	public static ArrayList<String> getNamedNumberOfNumeral() {
		ArrayList<String> storage = new ArrayList<String>();
		chordNumerals[] numbers = chordNumerals.values();
		for (chordNumerals sym : numbers) {
			storage.add(sym.getProg());
		}
		return storage;
	}

	/**
	 * Enables the TTS to translate these symbols in natural verbal language.
	 */
	public enum chordNumerals {
		i("one"), ii("two"), iii("three"), iv("four"), v("five"), vi("six"), vii("seven"), I("one"), II("two"), III(
				"three"), IV("four"), V("five"), VI("six"), VII("seven");

		public final String prog;

		chordNumerals(String prog) {
			this.prog = prog;
		}

		public String getProg() {
			return prog;
		}
	}

	/**
	 * Known common major chord progression from enumeration form to traditional
	 * theory sequence form.
	 */
	public enum majorChordProgressions {
		I_IV_V("I IV V"), // ONE FOUR FIVE
		II_V_I("II IV V"), // TWO_FIVE_ONE
		I_vi_IV_V("I vi IV V"), // ONE_six_FOUR_FIVE
		I_vi_ii_V("I vi ii V"), // ONE_six_two_FIVE
		I_V_vi_IV("I V vi IV"), // ONE_FIVE_six_FOUR
		I_IV_vi_V("I IV vi V"), // ONE_FOUR_six_FIVE
		I_iii_IV_V("I iii IV V"), // ONE_three_FOUR_FIVE
		I_IV_I_V("I IV I V"), // ONE_FOUR_ONE_FIVE
		I_IV_ii_V("I IV ii V"), // ONE_FOUR_two_FIVE
		I_vi_vii("I vi vii"), // ONE_six_seven
		I_IV_vii("I IV vii"), // ONE_FOUR_seven
		I_iv_iii_vii("I iv iii vii"), // ONE_four_three_seven
		I_IV_V_I("I IV V I"), // ONE_FOUR_FIVE_ONE
		vi_vii_I("vi vii I"), // six_seven_ONE
		I_vii_vi_vii("I vii vi vii"), // ONE_seven_six_seven
		I_IV_I("I IV I");// ONE_FOUR_ONE

		public final String chord;

		majorChordProgressions(String chord) {
			this.chord = chord;
		}

		public String getChord() {
			return chord;
		}
	}

	/**
	 * Known common minor chord progression from enumeration form to traditional
	 * theory sequence form.
	 */
	public enum minorChordProgressions {
		i_iv_v("i iv v"), // one_four_five
		ii_v_i("ii v i"), // two_five_one
		i_VI_iv_v("i VI iv v"), // one_SIX_four_five
		i_VI_II_v("i VI II v"), // one_SIX_TWO_five
		i_v_VI_iv("i v VI iv"), // one_five_SIX_four
		i_iv_VI_v("i iv VI v"), // one_four_SIX_five
		i_III_iv_v("i III iv v"), // one_THREE_four_five
		i_iv_i_v("i iv i v"), // one_four_one_five
		i_iv_II_v("i iv II v"), // one_four_TWO_five
		i_VI_VII("i VI VII"), // one_SIX_SEVEN
		i_iv_VII("i iv VII"), // one_four_SEVEN
		i_IV_III_VII("i IV III VII"), // one_FOUR_THREE_SEVEN
		i_iv_v_i("i iv v i"), // one_four_five_one
		VI_VII_i("VI VII i"), // SIX_SEVEN_one
		i_VII_VI_VII("i VII VI VII"), // one_SEVEN_SIX_SEVEN
		i_iv_i("i iv i");// one_four_one

		public final String chord;

		minorChordProgressions(String chord) {
			this.chord = chord;
		}

		public String getChord() {
			return chord;
		}
	}
}