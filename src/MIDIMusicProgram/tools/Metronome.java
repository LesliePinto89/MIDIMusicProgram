package tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import midi.MidiMessageTypes;

/**
 * This class defines the construct of the metronome. It utilises its own
 * sequencer to avoid interference with the internal controller sequencer.
 */
public class Metronome implements MetaEventListener {

	private JSlider tempoSlider;
	private static volatile Metronome instance = null;

	private Metronome() {
	}

	public static Metronome getInstance() {
		if (instance == null) {
			synchronized (Metronome.class) {
				if (instance == null) {
					instance = new Metronome();
				}
			}
		}
		return instance;
	}

	private Track metroTrack;
	private float chosenBPM;
	private float defaultScaleBPM = 100;
	private Sequencer sequencer;
	private MidiMessageTypes msgTypes = MidiMessageTypes.getInstance();
	private JLabel currentTempo;

	/**
	 * Setup sequencer to be used in the metronome, and allocated the current
	 * tempo value. This can be definded from the list of tempos or from the
	 * tempo slider.
	 */
	public void chooseTempo() throws InvalidMidiDataException {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		if (MidiMessageTypes.getInstance().checkIfTempoSliderChanged() == true) {
			chosenBPM = tempoSlider.getValue();
			MidiMessageTypes.getInstance().tempoSliderChanged(false);
		} else {
			String newTempoName = msgTypes.getSelectedTempo();
			chosenBPM = msgTypes.getTemposMap().get(newTempoName);
		}
		setupMetronomeSequence();
		startSequence();

	}

	public void setupMetronomeSequence() throws InvalidMidiDataException {
		Sequence metroSeq = new Sequence(Sequence.PPQ, 1);
		metroTrack = metroSeq.createTrack();
		sequencer.setSequence(metroSeq);
		addNoteEvent(metroTrack, 0);
		addNoteEvent(metroTrack, 1);
		addNoteEvent(metroTrack, 2);
		addNoteEvent(metroTrack, 3);
		sequencer.addMetaEventListener(this);
	}

	private void startSequence() throws InvalidMidiDataException {
		sequencer.setTempoInBPM(chosenBPM);
		sequencer.start();
	}

	@Override
	public void meta(MetaMessage message) {
		if (message.getType() != 47) { // 47 is end of track
			return;
		}
		doLoop();
	}

	private void doLoop() {
		if (sequencer == null || !sequencer.isOpen()) {
			return;
		}
		sequencer.setTickPosition(0);
		sequencer.start();
		sequencer.setTempoInBPM(chosenBPM);
	}

	public void stopLoop() {
		sequencer.stop();
		// return to standard tempo for when user goes to different features
		sequencer.setTempoInBPM(120);
		// This stops metronome affecting playing MIDI file tempo
		sequencer.close();
	}

	public JPanel tempoSlider() {
		JPanel instancePanel = new JPanel();
		int featureWidth = SwingComponents.getInstance().getScreenWidth();
		instancePanel.setBackground(Color.decode("#303030"));
		tempoSlider = new JSlider(0, 218, (int) defaultScaleBPM);
		
		// DOES NOT NEED MINIMUM SIZE - RUINS SCALING #6495ED
		tempoSlider.setPreferredSize(new Dimension(featureWidth / 3 - 20, featureWidth / 6));
		tempoSlider.setForeground(Color.WHITE);
		tempoSlider.setBackground(Color.decode("#303030"));
		tempoSlider.setPaintTrack(true);
		tempoSlider.setPaintTicks(true);
		tempoSlider.setPaintLabels(true);
		tempoSlider.setMajorTickSpacing(50);
		tempoSlider.setMinorTickSpacing(5);
		tempoSlider.setOrientation(SwingConstants.VERTICAL);
		tempoSlider.setFont(new Font("Serif", Font.ITALIC, 22));

		tempoSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				currentTempo.setText("BPM: " + tempoSlider.getValue());
				MidiMessageTypes.getInstance().tempoSliderChanged(true);
			}
		});

		currentTempo = new JLabel();
		currentTempo.setText("BPM: " + tempoSlider.getValue());
		currentTempo.setForeground(Color.WHITE);
		currentTempo.setFont(new Font("Tahoma", Font.BOLD, 22));

		// DOES NOT NEED MINIMUM SIZE - RUINS SCALING
		currentTempo.setPreferredSize(new Dimension(150, 40));

		instancePanel.add(currentTempo);
		instancePanel.add(tempoSlider);
		instancePanel.setPreferredSize(new Dimension(featureWidth / 4 - 20, featureWidth / 4));
		return instancePanel;
	}

	private void addNoteEvent(Track track, long tick) throws InvalidMidiDataException {
		ShortMessage message = new ShortMessage(ShortMessage.NOTE_ON, 9, 37, 100);
		MidiEvent event = new MidiEvent(message, tick);
		metroTrack.add(event);
	}
}