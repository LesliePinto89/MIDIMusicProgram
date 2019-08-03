package keyboard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import midiDevices.GetInstruments;
import tools.MIDIFilePlayer;
import tools.Metronome;
import tools.SwingComponents;
import keyboard.KeyboardInteractions;
import midi.MidiMessageTypes;

/**
 * This class holds the application's set of instruments, the Metronome, and the
 * MIDI file player.
 */
public class FeatureTabs {

	private JTabbedPane tabbedPane = SwingComponents.getInstance().getFeatureTab();
	private int jListTableWidth;
	private SwingComponents components = SwingComponents.getInstance();
	private static volatile FeatureTabs instance = null;

	private FeatureTabs() {
	}

	public static FeatureTabs getInstance() {
		if (instance == null) {
			synchronized (FeatureTabs.class) {
				if (instance == null) {
					instance = new FeatureTabs();
				}
			}
		}
		return instance;
	}

	/**
	 * Creates the set of instruments in the GUI.
	 */
	public JPanel instrumentChoicesPanel() {
		GetInstruments loadedInstruments = GetInstruments.getInstance();
		JPanel instancePanel = new JPanel();
		// Inner border colour
		instancePanel.setBackground(Color.decode("#FFFFFF"));
		jListTableWidth = SwingComponents.getJListWidth();
		DefaultListModel<String> allInstruments = loadedInstruments.getAllInstruments();
		JList<String> jListInstruments = new JList<String>(allInstruments);
		components.colourFeatureTab(jListInstruments, Color.decode("#505050"));
		jListInstruments.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		jListInstruments.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jListInstruments.setVisibleRowCount(-1);
		jListInstruments.setName("Instruments");
		jListInstruments.setFixedCellHeight(50);
		jListInstruments.setFixedCellWidth(197);
		jListInstruments.setForeground(Color.WHITE);

		JScrollPane instrumentsScroll = new JScrollPane(jListInstruments);
		instrumentsScroll
				.setPreferredSize(new Dimension(jListTableWidth - 70, tabbedPane.getPreferredSize().height - 50));
		instrumentsScroll
				.setMinimumSize(new Dimension(jListTableWidth - 70, tabbedPane.getPreferredSize().height - 50));
		MouseListener instrumentsListener = new KeyboardInteractions(jListInstruments);
		jListInstruments.addMouseListener(instrumentsListener);
		instancePanel.add(instrumentsScroll);

		return instancePanel;
	}

	/**
	 * Creates the tempo panel in the GUi.
	 */
	public JPanel tempoPanel() {
		MidiMessageTypes midiMessages = MidiMessageTypes.getInstance();
		JPanel instancePanel = new JPanel();
		DefaultListModel<String> tempoList = midiMessages.getTemposInModel();
		JList<String> jListTempos = new JList<String>(tempoList);
		jListTempos.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		jListTempos.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jListTempos.setVisibleRowCount(-1);
		jListTempos.setName("Tempos");
		jListTempos.setForeground(Color.WHITE);
		SwingComponents.getInstance().colourFeatureTab(jListTempos, Color.decode("#505050"));

		JScrollPane tempoScroll = new JScrollPane(jListTempos);
		tempoScroll.setPreferredSize(new Dimension(jListTableWidth / 2, tabbedPane.getPreferredSize().height - 50));
		tempoScroll.setMinimumSize(new Dimension(jListTableWidth / 2, tabbedPane.getPreferredSize().height - 50));

		jListTempos.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent tempoPressed) {
				MidiMessageTypes messageTypes = MidiMessageTypes.getInstance();
				String selectedTempo = "";
				int index = jListTempos.locationToIndex(tempoPressed.getPoint());
				selectedTempo = messageTypes.getTemposInModel().getElementAt(index);
				selectedTempo = selectedTempo.substring(0, selectedTempo.indexOf(":"));

				// Might be needed if can use tempo for other functions
				messageTypes.selectedTempo(selectedTempo);
				messageTypes.saveTempoSeqEnd(selectedTempo);
				messageTypes.tempoChanged(true);
			}
		});
		instancePanel.add(tempoScroll);
		return instancePanel;
	}

	/**
	 * This creates the completed feature tabbed pane.
	 */
	public JTabbedPane createTabbedBar() throws InvalidMidiDataException {
		components.featureTabDimensions();
		JPanel instrumentsPane = new JPanel();
		instrumentsPane.add(instrumentChoicesPanel());
		instrumentsPane.setBackground(Color.decode("#303030"));
		tabbedPane.addTab("Instruments", instrumentsPane);
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 20));

		// Metronome related content
		JPanel leftTempoPane = new JPanel();
		leftTempoPane.add(tempoPanel());
		leftTempoPane.setBackground(Color.decode("#303030"));

		JPanel rightTempoPane = new JPanel();
		rightTempoPane.setBackground(Color.decode("#303030"));
		JToggleButton playTempo = components.customActionJToggleButton(130, 50, "Play", null, 24,
				Color.decode("#B8B8B8"));
		playTempo.setText("Play");

		// item listener removes state problems using a mouse listener
		playTempo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				if (ev.getStateChange() == ItemEvent.SELECTED) {
					playTempo.setText("Stop");
					try {
						Metronome.getInstance().chooseTempo();
					} catch (InvalidMidiDataException e) {
						e.printStackTrace();
					}
				} else if (ev.getStateChange() == ItemEvent.DESELECTED) {
					playTempo.setText("Play");
					Metronome.getInstance().stopLoop();
				}
			}
		});
		rightTempoPane.add(playTempo);
		JPanel tempoSliderPanel = Metronome.getInstance().tempoSlider();
		rightTempoPane.add(tempoSliderPanel);
		JSplitPane splitPaneTabThree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPaneTabThree.setContinuousLayout(true);
		splitPaneTabThree.setLeftComponent(leftTempoPane);
		splitPaneTabThree.setRightComponent(rightTempoPane);
		splitPaneTabThree.setOneTouchExpandable(true);
		splitPaneTabThree.setDividerLocation(SwingComponents.getJListWidth() / 2);
		tabbedPane.addTab("Metronome", splitPaneTabThree);

		JPanel midiFilePlayer = MIDIFilePlayer.getInstance().drawMusicPlayerGUI();
		midiFilePlayer.setBackground(Color.decode("#303030"));
		midiFilePlayer.setPreferredSize(new Dimension(410, 50));
		midiFilePlayer.setMinimumSize(new Dimension(410, 50));
		tabbedPane.addTab("MIDI File Player", midiFilePlayer);
		return tabbedPane;
	}
}