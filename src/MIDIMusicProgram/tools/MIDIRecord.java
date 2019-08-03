package tools;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.swing.JToggleButton;
import midi.MidiMessageTypes;
import midiDevices.PlayBackDevices;

/**
 * This class handles the recording of a new MIDI sequence, and the "Debug"
 * timings feature.
 */
public class MIDIRecord {

	private int recordClick = 0;
	private MidiMessageTypes messages = MidiMessageTypes.getInstance();
	private PlayBackDevices devices = PlayBackDevices.getInstance();
	private boolean startPianoSequence = false;

	private static volatile MIDIRecord instance = null;

	private MIDIRecord() {
	}

	public static MIDIRecord getInstance() {
		if (instance == null) {
			synchronized (MIDIRecord.class) {
				if (instance == null) {
					instance = new MIDIRecord();
				}
			}
		}
		return instance;
	}

	/**
	 * Triggered each time the user clicks of the "Rec" button. This method
	 * first disables the freeplay feature and enables the recording feature. It
	 * then uses modulus to differentiate "on and off" clicks on the rec button.
	 * 
	 * @param recordMIDI
	 *            - The "Rec" JToggleButton component
	 */
	public void recordAction(JToggleButton recordMIDI) throws InvalidMidiDataException {
		recordClick++;
		devices.endFreePlay(true);
		devices.endRecording(false);

		// User clicked record button to start cording
		if (recordClick % 2 == 1) {
			enableRecord(recordMIDI);
		}

		// User clicked record button to disable recording
		else if (recordClick % 2 == 0) {
			disableRecord(recordMIDI);
		}
	}

	public boolean getPianoRecStatus() {
		return startPianoSequence;
	}

	/**
	 * This method arranges the steps needed to create a sequence template, and
	 * to start recording of notes / messages. It also enables debug mode to
	 * start recording the states the recording goes through
	 * 
	 * @param recordMIDI
	 *            - The "Rec" JToggleButton component
	 */
	public void enableRecord(JToggleButton recordMIDI) {
		recordMIDI.setText("Rec");
		if (devices.getFirstRecording() == true) {
			try {
				devices.setFirstRecording(false);
				Sequence sequence = new Sequence(Sequence.PPQ, 480);
				devices.storeSeq(sequence);
				Track track = devices.getSequence().createTrack();
				devices.storeTrack(track);
				devices.returnSequencer().setSequence(sequence);
				devices.returnSequencer().setTempoInBPM(120);
				devices.returnSequencer().recordEnable(devices.getTrack(), 0);
				devices.returnSequencer().setTickPosition(0);

				// Turn on MIDI device specific recording
				if (devices.getCurrentInputDevice().equals("Digital Piano")) {
					if (devices.getTrack().size() == 1) {
						devices.returnSequencer().startRecording();
						startPianoSequence = true;
					}
				}

				// Debug mode used when a new recording has ended
				if (messages.getDebugStatus() == false) {
					messages.clearTimingMessages();
					messages.turnOnDebug(true);
				}
			} catch (InvalidMidiDataException e1) {
				e1.printStackTrace();
			}
		}

		// User clicked record button after stopping recording to start
		// a new recording
		else if (devices.getFirstRecording() == false) {
			try {
				// Removes all tracks from sequence to make a new sequence
				for (Track aTrack : devices.getSequence().getTracks()) {
					devices.getSequence().deleteTrack(aTrack);
				}
				Track newTrack = PlayBackDevices.getInstance().getSequence().createTrack();
				devices.storeTrack(newTrack);
				devices.returnSequencer().setSequence(devices.getSequence());
				devices.returnSequencer().recordEnable(devices.getTrack(), -1);

				// Fixes note delay when re-recording song similar to
				// memory leaks of scanner.next()
				devices.returnSequencer().setTickPosition(0);

				// Restart MIDI Keyboard device record sequence rather than
				// mouse version
				if (devices.getCurrentInputDevice().equals("Digital Piano")) {
					if (devices.getTrack().size() == 1) {
						devices.returnSequencer().startRecording();
						startPianoSequence = true;

						// If user presses record button while sequence is
						// playing,
						// this remove the coloured notes. New recording repeat
						// same process from above to below methods
						PlaybackFunctions.resetChordsColor();
					}
				}

				// REMOVED THIS FOR DEBUGGIN PIANO DEVICE RECORD SEQUENCE
				// FEATURE
				// devices.returnSequencer().startRecording();

				// Debug mode used when a new recording is started again while
				// in current feature
				if (messages.getDebugStatus() == false) {
					// Debug mode used when a new recording has ended
					messages.clearTimingMessages();
					messages.turnOnDebug(true);
					// Corrects click order: rec, key, debug, rec, then debug
					// user choices
					messages.recordedDebug(false);
				}
			} catch (InvalidMidiDataException error1) {
				error1.printStackTrace();
			}
		}
	}

	/**
	 * This function creates the cumulative timing values from the created
	 * sequence's track
	 */
	public void pianoSequenceTiming() {
		if (messages.getDebugStatus()) {
			int eventSize = devices.returnSequencer().getSequence().getTracks()[0].size();
			MidiEvent firstEvent = devices.returnSequencer().getSequence().getTracks()[0].get(0);
			long baseTime = firstEvent.getTick();

			String startTickString = Long.toString(baseTime);
			messages.sequenceTimingMessages(">>>>>Start timing");
			messages.sequenceTimingMessages("First Note start tick value is: " + startTickString);

			for (int i = 0; i < eventSize; i++) {
				MidiEvent event = devices.returnSequencer().getSequence().getTracks()[0].get(i);
				event.setTick(event.getTick() - baseTime);
				long cumulativeTime = event.getTick();
				String cumulativeTimeString = Long.toString(cumulativeTime);
				messages.sequenceTimingMessages(">>>>>CALCULATE CUMULATIVE TIME");
				messages.sequenceTimingMessages(
						"The cumultive value of start tick and duration is: " + cumulativeTimeString);
			}
		}
	}

	/**
	 * This method finalises the recording process, disables recording mode and
	 * turns freeplay mode back on. It also finalises the debug timing recording
	 * based on how the user interacting with it and the rec button.
	 * 
	 * @param recordMIDI
	 *            - The "Rec" JToggleButton component
	 */
	public void disableRecord(JToggleButton recordMIDI) throws InvalidMidiDataException {
		devices.endFreePlay(false);
		devices.endRecording(true);
		recordMIDI.setText("Off");
		devices.returnSequencer().stopRecording();
		devices.returnSequencer().recordDisable(devices.getTrack());

		// Turn off MIDI device specific recording
		if (MIDIRecord.getInstance().getPianoRecStatus()) {
			startPianoSequence = false;
			pianoSequenceTiming();
		}

		if (messages.getDebugStatus()) {
			// Debug mode used when a new recording has ended
			messages.turnOnDebug(false);

			// Ignores below condition
			if (messages.getTimingMessages().equals(messages.returnDefault() + "\n\n")) {
			}

			else if (messages.getTimingMessages().contains(messages.returnDefault())) {
				// Remove default text
				messages.editTimingMessages();
			}

			// For click rec, click debug, click button, click rec to turn off
			if (!messages.getTimingMessages().equals("")
					&& !messages.getTimingMessages().equals(messages.returnDefault() + "\n\n")) {
				messages.recordedDebug(true);
			}

			if (!messages.getTimingMessages().equals("")
					&& !messages.getTimingMessages().contains(messages.returnDefault())) {
				messages.sequenceTimingMessages(">>>>>MIDI TIMING COMPLETE");
			}
		}

		// Debug mode turned off when recording has ended
		MidiMessageTypes.getInstance().turnOnDebug(false);
		// Debugging purpose which shows new meta track made here but not above
		// track array
		// Track [] tracksEdited =
		// MidiReceiver.getInstance().getSequence().getTracks();
	}
}
