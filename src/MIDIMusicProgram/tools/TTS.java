package tools;
import java.util.ArrayList;
import java.util.Locale;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import midi.Chord;
import midi.ChordProgression;

/**
 * This class utilises the "FreetTTS" engine to output speech for given feature
 * text.
 */
public class TTS {
	private Synthesizer synthesizer;
	private static volatile TTS instance = null;

	private TTS() {
	}

	public static TTS getInstance() throws EngineException, AudioException, EngineStateError {
		if (instance == null) {
			synchronized (TTS.class) {
				if (instance == null) {
					instance = new TTS();
				}
			}
		}
		return instance;
	}

	/**
	 * When the system plays the current selected progression,is converted 
	 * from music theory notation to natural language numbers.
	 * @param sequence - The text name of the given progressoin's sequence name
	 * @return  The list containing the natural language version of the 
	 * argument's sequence
	 */
	public ArrayList<String> createProgressionTTS(String sequence) {
		ArrayList<String> convertToNumerals = new ArrayList<String>();
		int numeralListIndex = 0;
		ArrayList<String> getStoredInnerNumerals = ChordProgression.getNamedNumberOfNumeral();
		ArrayList<String> getStoredOuterNumerals = ChordProgression.getNamedOnlyNumberOfNumeral();

		for (int l = 0; l < getStoredOuterNumerals.size(); l++) {
			if (sequence.equals(getStoredOuterNumerals.get(numeralListIndex))) {
				convertToNumerals.add(getStoredInnerNumerals.get(numeralListIndex).toString());
				break;
			}
			numeralListIndex++;
		}
		return convertToNumerals;
	}

	/**
	 * This function takes in a chord or inverted chord, and
	 * creates in natural language name from its music theory notation name.
	 * @param foundChord - This given type of chord to output its name.
	 * @return  The natural language version of the chord argument's name.
	 */
	public String theoryToTTS(Chord foundChord) {
		PlaybackFunctions.timeDelay(1000);
		String name = foundChord.getChordNotes().get(0).getName();
		String editedNote = name.substring(0, name.length() - 1);
		editedNote = editedNote.contains("#") ? editedNote.replace("#", " SHARP") : editedNote;

		String adjust = "";
		String chordName = foundChord.getChordName();
		for (String aChord : Chord.getAllChordEnums()) {
			if (chordName.equals(aChord) && aChord.contains("min") && aChord.contains("Maj")) {
				adjust = aChord.replaceAll("minMaj", "minor major ");
				editedNote += " " + adjust;
				break;
			}

			else if (chordName.equals(aChord) && aChord.contains("maj")) {
				if (aChord.contains("Tetra")) {
					adjust = aChord.replaceAll("maj", "major ");
				} else {
					adjust = aChord.replaceAll("maj", "major");
				}
				editedNote += " " + adjust;
				break;
			}

			else if (chordName.equals(aChord) && aChord.contains("min")) {
				if (aChord.contains("Tetra")) {
					adjust = aChord.replaceAll("min", "minor ");
				}

				else {
					adjust = aChord.replaceAll("min", "minor ");
					if (aChord.contains("7")) {
						adjust = adjust.replaceAll("7", "7 ");
					}
					if (aChord.contains("b")) {
						adjust = adjust.replaceAll("b", "flat ");
					}
				}
				editedNote += " " + adjust;
				break;
			}

			else if (chordName.equals(aChord) && aChord.contains("add")) {
				adjust = aChord.replaceAll("add", "added");
				editedNote += " " + adjust;
				break;
			}

			else if (chordName.equals(aChord) && aChord.contains("aug")) {
				adjust = aChord.replaceAll("aug", "augmented");
				editedNote += " " + adjust;
				break;
			}

			else if (chordName.equals(aChord) && aChord.contains("sus")) {
				adjust = aChord.replaceAll("sus", "sustain");
				editedNote += " " + adjust;
				break;
			} else if (chordName.equals("11") || chordName.equals("7") || chordName.equals("13")
					|| chordName.equals("9")) {
				editedNote += chordName;
				break;
			}

			else if (chordName.equals(aChord) && aChord.contains("dim")) {
				adjust = aChord.replaceAll("dim", "diminished");
				editedNote += " " + adjust;
				break;
			}
		}
		return editedNote;
	}

	/**
	 * When the system plays the current selected progression,is converted 
	 * from music theory notation to natural language numbers.
	 * @param feature - The name of the given feature set to process TTS.
	 * @param text - The type of text to process with the specified feature
	 */
	public void prepareFunction(String feature, String text)
			throws EngineException, EngineStateError, IllegalArgumentException, InterruptedException, AudioException {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// Use shared volatile boolean, is triggered to true
					// depending on which feature state uses it.
					// This is because previous and home makes it true and this
					// is reflected here
					if (!ScreenPrompt.getInstance().getShared()) {
						ArrayList<String> conditionalData = new ArrayList<String>();
						switch (feature) {

						case "Progression":
							conditionalData = createProgressionTTS(text);
							break;
						case "Chord":
							conditionalData.add(text);
							break;
						case "Scale":
							conditionalData.add(text);
							break;
						case "Build":
							conditionalData.add(text);
							break;
						}

						System.setProperty("freetts.voices",
								"com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

						// Register Engine
						Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

						// Create a Synthesizer
						synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));

						// Allocate synthesizer
						synthesizer.allocate();

						// Resume Synthesizer
						synthesizer.resume();

						
						if(feature.equals("Progression")){
							PlaybackFunctions.timeDelay(400);
							synthesizer.speakPlainText(conditionalData.get(0), null);
						}
						else {
						for (String aString : conditionalData) {
							// speaks the given text until queue is empty.
							synthesizer.speakPlainText(aString, null);
							PlaybackFunctions.timeDelay(1000);
						}
						}
						synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
					}
				} catch (InterruptedException | AudioException | EngineStateError | EngineException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}