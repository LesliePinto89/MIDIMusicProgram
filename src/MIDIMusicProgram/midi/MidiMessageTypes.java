package midi;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.PrintStream;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Set;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import keyboard.Note;
import midiDevices.PlayBackDevices;
import tools.DebugConsole;
import tools.PlaybackFunctions;
import tools.SwingComponents;

/**
 * This class stores MIDI message data that affects composition of tempo and
 * MIDI timing metrics
 */
public class MidiMessageTypes {

	private LinkedHashMap<String, Float> tempoMarkersMap = new LinkedHashMap<String, Float>();
	private DefaultListModel<String> temposInModel = new DefaultListModel<String>();
	private EnumSet<tempoNames> tempoEnums = null;
	private String rememberedTempo = "AllegroModerato";
	private static Track trk;
	private static MidiChannel channel;
	private int nextIntervalIndex;
	private boolean tempoChanged = false;
	private boolean tempoSliderChanged = false;
	private boolean randomState = false;
	private boolean melodyState = false;
	private boolean noColorFirst = false;

	private boolean debugMode = false;
	private boolean showDebugMode = false;
	private String debugMessage = "";
	private String defaultDebugMessage = "No timing measures have been recorded - Record a sequence to start debugging";

	private static volatile MidiMessageTypes instance = null;

	public static MidiMessageTypes getInstance() {
		if (instance == null) {
			synchronized (Chord.class) {
				if (instance == null) {
					instance = new MidiMessageTypes();
					channel = PlayBackDevices.getInstance().returnSynth().getChannels()[0];
					instance.storedTemposMap();
					instance.storeTemposInModel();
				}
			}
		}
		return instance;
	}

	private MidiMessageTypes() {
	}

	public enum tempoNames {
		Larghissimo(20), // very, very slow (20 bpm and below)
		Grave(44), // slow and solemn (20 to bpm)
		Lento(52), // slowly (40 to 60 bpm)
		Largo(50), // broadly (40 to 60 bpm)
		Larghetto(60), // rather broadly (60 to 66 bpm)
		Adagio(70), // slow and stately (literally, "at ease") (66 to 76 bpm)
		Adagietto(75), // rather slow (70 to 80 bpm)

		// Varies based on source of information
		Andante(90), // at a walking pace (76 to 108 bpm)
		AndanteModerato(93), // a bit slower than andante
		Andantino(87), // slightly faster than andante

		Moderato(110), // moderately (108 to 120 bpm)
		Allegretto(100), // moderately fast (but less so than allegro)
		AllegroModerato(120), // moderately quick (112 to 124 bpm)
		Allegro(140), // fast, quickly and bright (120 to 168 bpm)
		Vivace(150), // lively and fast (Around 140 bpm) (quicker than allegro)
		Vivacissimo(160), // very fast and lively
		Allegrissimo(170), // very fast
		Presto(180), // very fast (168 to 200 bpm)
		Prestissimo(200); // extremely fast (more than 200bpm)

		private final float id;

		tempoNames(float id) {
			this.id = id;
		}

		public float getValue() {
			return id;
		}
	}

	public MidiChannel getMidiChannel() {
		return channel;
	}

	/**
	 * Generate meta event data for recorded sequence and external MIDI file
	 * converter to sequence process i.e. MIDI File player colour visualiser
	 */
	public static final void generateMetaData(Track track, Track trk) throws InvalidMidiDataException {
		for (int ii = 0; ii < track.size(); ii++) {
			MidiEvent me = track.get(ii);
			MidiMessage mm = me.getMessage();
			if (mm instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) mm;
				int command = sm.getCommand();
				int com = -1;
				if (command == ShortMessage.NOTE_ON) {
					com = 1;
				} else if (command == ShortMessage.NOTE_OFF) {
					com = 2;
				}
				if (com > 0) {
					byte[] b = sm.getMessage();
					int l = (b == null ? 0 : b.length);
					MetaMessage metaMessage = new MetaMessage(com, b, l);
					MidiEvent me2 = new MidiEvent(metaMessage, me.getTick());
					trk.add(me2);
				}
			}
		}
	}

	public Track getMetaTrack() {
		return trk;
	}

	/**
	 * This method processes both MidiMessage and MetaMessage data, in order to
	 * change the piano notes' colour as they are are played. This enables
	 * coloured mode support for: MIDI Keyboard device input, playback of
	 * recorded sequences, and playback of any MIDI type 1 file. The latter
	 * includes notes with velocities that are either: NOTE_ON (>0) / NOTE_ON(O)
	 * MidiMessages, NOTE_ON (>0) / NOTE_OFF(>0) MidiMessages NOTE_ON (>0) /
	 * NOTE_ON(O) MetaMessages, and NOTE_ON (>0) / NOTE_OFF(>0) MetaMessages
	 * 
	 * @param event
	 *            - The message that can either be a MetaEvent or a MidiMessage
	 */
	public <T> void eventColors(T event) {
		int statusByteToInt = 0;
		int notePitch = 0;
		int velocity = 0;
		byte[] messageBytes = null;
		byte bytePitch = 0;

		if (event.getClass().equals(MetaMessage.class)) {
			MetaMessage convert = (MetaMessage) event;
			if (convert.getType() == 0x2F) {
				return;
			}
			messageBytes = convert.getMessage();

			statusByteToInt = messageBytes[3] & 0xFF & 0xF0;
			notePitch = messageBytes[4];
			velocity = messageBytes[5];
			bytePitch = messageBytes[4];
		}

		else if (event instanceof ShortMessage) {
			MidiMessage convert = (MidiMessage) event;
			messageBytes = convert.getMessage();
			statusByteToInt = messageBytes[0] & 0xFF & 0xF0;
			notePitch = messageBytes[1];
			velocity = messageBytes[2];
			bytePitch = messageBytes[1];
		}

		switch (statusByteToInt) {
		// By default, MIDI Keyboard's do not use NOTE ON and OFF messages.
		// It uses NOTE_ON velocity(1=> to <=100) for being on, and NOTE_ON (0)
		// as its NOTE OFF equivalent.
		case ShortMessage.NOTE_ON:
			// For when releasing depressed notes creates a NOTE_ON (0) message
			// with 0 velocity.
			if (velocity == 0) {
				PlaybackFunctions.resetLastNotePianoColor(notePitch);
			} else {
				Note playNote = null;
				for (Note aNote : Note.getNotesMap().values()) {
					if (aNote.getPitch() == bytePitch) {
						playNote = aNote;
						break;
					}
				}
				PlaybackFunctions.storedPreColorNotes(playNote);
				PlaybackFunctions.colorChordsAndScales(playNote, Color.YELLOW);
				break;
			}

			// Some notes' Meta or Midi message type use NOTE_OFF (velocity 0>)
			// rather than
			// NOTE_ON (velocity 0)
		case ShortMessage.NOTE_OFF:
			PlaybackFunctions.resetLastNotePianoColor(notePitch);
			break;
		}
	}

	public void storedTemposMap() {
		tempoEnums = EnumSet.allOf(tempoNames.class);
		for (tempoNames tempo : tempoEnums) {
			tempoMarkersMap.put(tempo.toString(), tempo.getValue());
		}
	}

	public LinkedHashMap<String, Float> getTemposMap() {
		return tempoMarkersMap;
	}

	/**
	 * Get map names
	 * 
	 * @return The array of tempo string values concatenated with the "BPM"
	 *         string
	 */
	public String[] storedTemposMapKeys() {
		int i = 0;
		Set<String> tempoMarksNames = tempoMarkersMap.keySet();
		String[] convertTempoKeys = new String[tempoMarksNames.size()];
		tempoMarksNames.toArray(convertTempoKeys);

		for (String change : convertTempoKeys) {
			if (change.equals("AllegroModerato")) {
				change = convertTempoKeys[i] + ": " + tempoMarkersMap.get(change).toString() + " BPM - Default";
			} else {
				change = convertTempoKeys[i] + ": " + tempoMarkersMap.get(change).toString() + " BPM";
			}
			convertTempoKeys[i++] = change;
		}
		return convertTempoKeys;
	}
	
	/**Add tempo value names to Default List Model
	 * */
	public void storeTemposInModel() {
		for (String s : storedTemposMapKeys()) {
			temposInModel.addElement(s);
		}
	}

	public DefaultListModel<String> getTemposInModel() {
		return temposInModel;
	}


	/**Used to keep selected tempo when the start recording functions reset it
	 * back to default.
	 * */
	public void saveTempoSeqEnd(String choice) {
		PlayBackDevices.getInstance().returnSequencer().setTempoInBPM(tempoMarkersMap.get(choice));
	}

	public void selectedTempo(String remembered) {
		rememberedTempo = remembered;
		tempoChanged = true;
	}

	public String getSelectedTempo() {
		return rememberedTempo;
	}

	public void tempoChanged(boolean change) {
		tempoChanged = change;
	}

	public boolean checkIfTempoChanged() {
		return tempoChanged;
	}

	public void tempoSliderChanged(boolean change) {
		tempoSliderChanged = change;
	}

	public boolean checkIfTempoSliderChanged() {
		return tempoSliderChanged;
	}

	/** NEEDED CONDITIONS FOR DEBUG MODE TO WORK DEPENDING ON TIMING VALUES
	*GENEREATED DURING INTERACTING WITH RECORD FEATURE AND DEBUG BUTTON
	**/
	public void turnOnDebug(boolean state) {
		debugMode = state;
	}

	public boolean getDebugStatus() {
		return debugMode;
	}

	public void recordedDebug(boolean state) {
		showDebugMode = state;
	}

	public boolean isRecordedDebug() {
		return showDebugMode;
	}

	public void sequenceTimingMessages(String message) {
		debugMessage = debugMessage + message;
		debugMessage = debugMessage + "\n\n";
	}

	public void clearTimingMessages() {
		debugMessage = "";
	}

	public String getTimingMessages() {
		return debugMessage;
	}

	public void editTimingMessages() {
		String temp = "";
		temp = temp + returnDefault();
		debugMessage = debugMessage.replace(temp, "");

	}

	public String returnDefault() {
		return defaultDebugMessage;
	}

	/** Print all debug text to console, which is actually stored in a JTextArea
	* in a new JFrame.
	 * */
	public void getSequenceTimingMessages() {
		System.out.print(debugMessage);
	}

	/**
	 * Feature - Displays the MIDI sequence's timing values through stages of
	 * its construction.
	 */
	public void loadDebug() {
		int screenWidth = SwingComponents.getInstance().getScreenWidth();
		int screenHeight = SwingComponents.getInstance().getScreenHeight();
		JFrame debugPanel = SwingComponents.getInstance().floatingDebugFrame(true, false, null,
				"Debug MIDI Timing Summary", 0, 0, screenWidth / 2 + screenWidth / 5,
				screenHeight / 2 + screenHeight / 5);
		debugPanel.getContentPane().setBackground(Color.decode("#303030"));
		JTextArea log = new JTextArea();
		JScrollPane debugDataScroll = new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		DebugConsole debugInfo = new DebugConsole(log, "DebugConsole");
		debugPanel.setLayout(new GridLayout(2, 1));
		debugPanel.getContentPane().add(debugDataScroll);
		System.setOut(new PrintStream(debugInfo));

		MidiMessageTypes.getInstance().getSequenceTimingMessages();
	}

	public void storeIntervalStateID(int intervalState) {
		nextIntervalIndex = intervalState;
	}

	public int getIntervalStateID() {
		return nextIntervalIndex;
	}

	public void storeRandomState(boolean isRandom) {
		randomState = isRandom;
	}

	public boolean getRandomState() {
		return randomState;
	}

	public void storeMelodyInterval(boolean isMelody) {
		melodyState = isMelody;
	}

	public boolean getMelodyInterval() {
		return melodyState;
	}

	public void noColorFirst(boolean bool) {
		noColorFirst = bool;
	}

	public boolean getNoColorFirst() {
		return noColorFirst;
	}
}