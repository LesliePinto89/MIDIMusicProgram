package midiDevices;

import java.util.ArrayList;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

/**
 * This class defines all MIDI device support in the program, and interactions
 * between them and the system's other functionalities.
 */
public class PlayBackDevices implements Receiver {

	private MidiDevice device;
	private Transmitter transToSynReceiver = null;
	private Transmitter transToDummyReceiver = null;
	private Transmitter transToSeqReceiver = null;
	private Synthesizer synth;
	private Receiver synthRcvr;
	private Sequencer sequencer;
	private Receiver seqRcvr;

	private boolean stopRecording = true;
	private boolean stopPianoFreePlay = false;
	private boolean firstRecording = true;
	private Sequence sequence;
	private int resolution;
	private Track track;
	private ArrayList<Transmitter> listOfConnections = new ArrayList<Transmitter>();
	private String currentInputDevice = "";

	private static volatile PlayBackDevices instance = null;

	private PlayBackDevices() {
	}

	public static PlayBackDevices getInstance() {
		if (instance == null) {
			synchronized (PlayBackDevices.class) {
				if (instance == null) {
					instance = new PlayBackDevices();
				}
			}
		}
		return instance;
	}

	/**
	 * Experimental: Called when the user clicks on the input button to check if
	 * a MIDI keyboard is connected, while the program is already running.
	 */
	public boolean updateDetectedDevices() throws InvalidMidiDataException, MidiUnavailableException {
		;
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			device = MidiSystem.getMidiDevice(infos[i]);
			if (device.getDeviceInfo().getDescription().equals("No details available")) {
				device.open();

				transToSynReceiver = device.getTransmitter();
				// To display in input connections
				listOfConnections.add(transToSynReceiver);
				transToDummyReceiver = device.getTransmitter();
				transToSeqReceiver = device.getTransmitter();
				return true;
			}

			// If assigning MIDI input port to do stuff, however Java SDK is
			// temperamental with this feature. Left blank for now.
			else if (device.getDeviceInfo().getDescription().equals("External MIDI Port")) {
			}
		}
		return false;
	}

	/**
	 * Find all MIDI devices recognised by the system, and arranges them based
	 * on defined required MIDI devices.
	 */
	public void startConnection() throws InvalidMidiDataException, MidiUnavailableException {
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			device = MidiSystem.getMidiDevice(infos[i]);
			if (device instanceof Synthesizer) {
				device.open();
				synth = (Synthesizer) device;
				synthRcvr = synth.getReceiver();
			}

			else if (device.getDeviceInfo().getDescription().equals("No details available")) {
				device.open();

				transToSynReceiver = device.getTransmitter();
				// To display in input connections
				listOfConnections.add(transToSynReceiver);
				transToDummyReceiver = device.getTransmitter();
				transToSeqReceiver = device.getTransmitter();
				currentInputDevice = "Digital Piano";
			}

			// If assigning MIDI input port to do stuff, however Java SDK is
			// temperamental with this feature. Left blank for now.
			else if (device.getDeviceInfo().getDescription().equals("External MIDI Port")) {
			}
		}
		loadUp();
	}

	public ArrayList<Transmitter> getConnections() {
		return listOfConnections;
	}

	public int getCurrentSequenceResolution() {
		return resolution;
	}

	public String getCurrentInputDevice() {
		return currentInputDevice;
	}

	public void loadUp() throws MidiUnavailableException {
		sequencer = MidiSystem.getSequencer();
		sequencer.open();

		// MIDI keyboard is not active.
		if (transToSynReceiver == null) {
			transToSynReceiver = sequencer.getTransmitter();
		}
		// MIDI keyboard is active.
		else {
			transToDummyReceiver.setReceiver(new DummyReceiver());
			seqRcvr = sequencer.getReceiver();
			transToSeqReceiver.setReceiver(seqRcvr);
		}
		transToSynReceiver.setReceiver(synthRcvr);
	}

	public void storeTrack(Track track) {
		this.track = track;
	}

	public Transmitter getSeqTransmitter() {
		return transToSeqReceiver;
	}

	public Track getTrack() {
		return track;
	}

	public void storeSeq(Sequence sequence) {
		this.sequence = sequence;
		resolution = sequence.getResolution(); // stored resolution as its base
	}

	public Sequence getSequence() {
		return sequence;
	}

	public Synthesizer returnSynth() {
		return synth;
	}

	public Sequencer returnSequencer() {
		return sequencer;
	}

	public Receiver returnSeqRcvr() {
		return seqRcvr;
	}

	@Override
	public void close() {
	}

	/**
	 * Manually transmit MIDI wire protocol message to the default synthesiser
	 * to output sound.
	 * 
	 * @param message
	 *            - The type of message to send to the synthesiser.
	 * @param timeStamp
	 *            - The time it takes to reach the synthesiser.
	 */
	@Override
	public void send(MidiMessage message, long timeStamp) {
		synthRcvr.send(message, timeStamp);

	}

	/**
	 * Utilises the send method to play a given note with note defined start
	 * message. Results in it playing until it decays naturally
	 * 
	 * @param pitch
	 *            - The value used to create the short message
	 */
	public void freeNotePlay(int pitch) throws InvalidMidiDataException {
		ShortMessage noteOnMessage = new ShortMessage(ShortMessage.NOTE_ON, 0, pitch, 100);
		send(noteOnMessage, -1);
	}

	/**
	 * Utilises the send method to stop a given note with note defined end
	 * message. Results in it playing until it decays naturally
	 * 
	 * @param pitch
	 *            - The value used to create the short message
	 */
	public void freeNoteStop(int pitch) throws InvalidMidiDataException {
		// Changed from note off, 100 velocity to note on 0
		// velocity for testing purposes
		ShortMessage noteOnMessage = new ShortMessage(ShortMessage.NOTE_ON, 0, pitch, 0);
		send(noteOnMessage, -1);
	}

	/**
	 * CONDITIONAL VARIABLES
	 */

	public void setFirstRecording(boolean firstRec) {
		this.firstRecording = firstRec;
	}

	public boolean getFirstRecording() {
		return firstRecording;
	}

	public void endRecording(boolean stopRec) {
		this.stopRecording = stopRec;
	}

	public boolean isRecEnded() {
		return stopRecording;
	}

	public void endFreePlay(boolean stopFreePlay) {
		this.stopPianoFreePlay = stopFreePlay;
	}

	public boolean isFreePlayEnded() {
		return stopPianoFreePlay;
	}

	public boolean isRunning() {
		return sequencer.isRunning();
	}
}