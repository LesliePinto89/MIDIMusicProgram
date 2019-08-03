package tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import midi.MidiMessageTypes;
import midiDevices.PlayBackDevices;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This class defines the functionality of the MIDI file player. It includes
 * colour mode support, importing .mid files as sequences, and play back support
 * for MIDI type 0 and 1 files. It also supports NOTE_ON 0 velocity, and
 * NOTE_OFF 0> velocity end messages.
 */
public class MIDIFilePlayer implements MouseListener, MetaEventListener {

	// Dimensions
	private int screenWidth = SwingComponents.getInstance().getScreenWidth();
	private int screenHeight = SwingComponents.getInstance().getScreenHeight();

	// Variables
	private ArrayList<File> storedMIDISavedFiles = new ArrayList<File>();
	private String songFromList = "";
	private int btn_h = 35;
	private int _W = 330;
	private int h_list = 100;
	private File retrieveFile;

	// Components
	private DefaultListModel<String> songList = new DefaultListModel<String>();
	private JList<String> jSongList = new JList<String>(songList);
	private JScrollPane listScroller;
	private JButton btnPlay = new JButton();
	private JButton btnNext = new JButton();
	private JButton btnPrev = new JButton();
	private JButton selectMidiFileButton;
	private JLabel lblplaying = new JLabel();
	private JPanel backgroundPanel;
	private JPanel containsButtons = new JPanel();
	private JPanel playerOptions;
	private JPanel panelNP = new JPanel();

	private SwingComponents components = SwingComponents.getInstance();
	private MIDIFileManager manager = MIDIFileManager.getInstance();

	private static volatile MIDIFilePlayer instance = null;

	private MIDIFilePlayer() {
	}

	public static MIDIFilePlayer getInstance() {
		if (instance == null) {
			synchronized (MIDIFilePlayer.class) {
				if (instance == null) {
					instance = new MIDIFilePlayer();
				}
			}
		}
		return instance;
	}

	/** Content Holder JPanels */
	public void designPlayer() {
		backgroundPanel = components.generateEventPanel(screenWidth, screenHeight, null, Color.decode("#F0FFFF"),
				Color.decode("#F0FFFF"), 1, 1, 1, 1);
		backgroundPanel = new JPanel(new GridBagLayout());
		playerOptions = components.generateEventPanel(screenWidth / 3, 324, null, Color.decode("#303030"),
				Color.decode("#303030"), 1, 1, 1, 1);
		backgroundPanel.add(playerOptions);
	}

	/** Create and arrange action command buttons */
	public void drawActionsButtons() {
		Color aColor = Color.decode("#303030");
		btnPrev = components.customTrackJButton(60, 40, "<<", "prevFile", this, aColor, 1, 1, 1, 1);
		btnPrev.setForeground(Color.WHITE);
		btnPrev.setBackground(Color.decode("#303030"));
		btnPrev.setFont(new Font("Cooper Black", Font.BOLD, 40));
		btnPlay = components.customTrackJButton(60, 40, ">", "playFile", this, aColor, 1, 1, 1, 1);
		btnPlay.setForeground(Color.WHITE);
		btnPlay.setBackground(Color.decode("#303030"));
		btnPlay.setFont(new Font("Cooper Black", Font.BOLD, 40));
		btnNext = components.customTrackJButton(60, 40, ">>", "nextFile", this, aColor, 1, 1, 1, 1);
		btnNext.setForeground(Color.WHITE);
		btnNext.setBackground(Color.decode("#303030"));
		btnNext.setFont(new Font("Cooper Black", Font.BOLD, 40));

		containsButtons = components.generateEventPanel(480, btn_h, null, null, Color.BLACK, 0, 0, 0, 0);
		containsButtons.add(btnPrev);
		containsButtons.add(btnPlay);
		containsButtons.add(btnNext);
		playerOptions.add(containsButtons);

	}

	/** Load visuals and functionalities into GUI */
	public JPanel drawMusicPlayerGUI() {
		designPlayer();
		drawActionsButtons();

		panelNP = components.generateEventPanel(_W - 15, 20, null, null, Color.gray, 1, 0, 2, 0);
		panelNP.setLayout(new BoxLayout(panelNP, BoxLayout.PAGE_AXIS));
		playerOptions.add(panelNP);
		lblplaying = components.customJLabelEditing("Now Playing: ", 100, 4);
		lblplaying.setForeground(Color.WHITE);
		lblplaying.setFont(new Font("Serif", Font.BOLD, 18));
		panelNP.add(lblplaying);

		// Add file button and action event
		playerOptions.add(createSelectFileButton());

		// SongList ////////////////////////////////////////
		createJSongList();
		////////////////////////////////////////////////////
		return backgroundPanel;
	}

	/**
	 * When the user clicks "select file" button, its adds the file to the song
	 * list.
	 * 
	 * @param selectedFile
	 *            - The file name to be added to the list of songs,
	 */
	public void storedFoundFile(File selectedFile) {
		if (selectedFile != null) {
			this.retrieveFile = selectedFile;
			songList.addElement(retrieveFile.toString());
		}
	}

	/** Index of selected file getter and setter */
	public void storedJListSelectedSong(String storeSong) {
		this.songFromList = storeSong;
	}

	public String getListSelectedSong() {
		return songFromList;
	}

	/**
	 * Uses the name of song from the list to find the actual file in memory.
	 * 
	 * @param song
	 *            - The name of the song to find in memory.
	 * @return The converted sequence from the found file version of the song
	 *         that matches the argument.
	 */
	public Sequence playSelectedFile(String song) throws InvalidMidiDataException, IOException {
		storedMIDISavedFiles = MIDIFileManager.getInstance().getFilesSongList();
		File matchingFile = null;
		for (File aFile : storedMIDISavedFiles) {
			if (aFile.getName().equals(song)) {
				matchingFile = aFile;
				break;
			}
		}
		Sequence sequence = MidiSystem.getSequence(matchingFile);
		return sequence;
	}

	public void playMidiFile() throws InvalidMidiDataException, IOException {
		Sequence sequence = MidiSystem.getSequence(MIDIFileManager.getInstance().selectMIDIFile());
		// Load it into sequencer start the play back
		PlayBackDevices.getInstance().returnSequencer().setSequence(sequence);
		PlayBackDevices.getInstance().returnSequencer().start();
	}

	public File getStoredFile() {
		return retrieveFile;
	}

	public void createJSongList() {
		songList = manager.buildSongList();
		jSongList = components.customJList(_W, h_list, this);
		jSongList.setModel(songList);
		jSongList.setName("allSongsList");
		jSongList.setForeground(Color.WHITE);

		// Inner list
		SwingComponents.getInstance().colourFeatureTab(jSongList, Color.decode("#505050"));
		listScroller = components.customJScrollPane(jSongList, _W + 50, screenHeight / 2 - 160);

		playerOptions.add(listScroller);
	}

	public JButton createSelectFileButton() {
		selectMidiFileButton = components.customTrackJButton(101, 23, "Select File", "SelectMidiFile", this,
				Color.decode("#303030"), 1, 1, 1, 1);
		selectMidiFileButton.setForeground(Color.WHITE);
		selectMidiFileButton.setBackground(Color.decode("#B8B8B8"));
		selectMidiFileButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		return selectMidiFileButton;

	}

	@Override
	public void mouseClicked(MouseEvent music) {
		Object obj = music.getSource();
		if (obj.equals(jSongList)) {
			String song = jSongList.getSelectedValue();
			MIDIFilePlayer.getInstance().storedJListSelectedSong(song);
		}

		else if (obj.equals(selectMidiFileButton)) {
			File desiredFile = MIDIFileManager.getInstance().selectMIDIFile();
			storedFoundFile(desiredFile);
		}

		else if (obj.equals(btnPrev)) {
			String song = jSongList.getSelectedValue();
			if (song != null) {
				updateCurrentPlaying(song);
				playFeature(song);
			}
		}

		else if (obj.equals(btnPlay)) {
			String song = jSongList.getSelectedValue();
			if (song != null) {
				updateCurrentPlaying(song);
				playFeature(song);
			}
		} else if (obj.equals(btnNext)) {
			String song = jSongList.getSelectedValue();
			if (song != null) {
				updateCurrentPlaying(song);
				playFeature(song);
			}
		}
	}

	/** Updates the song name displayed to that of the currently playing song */
	public void updateCurrentPlaying(String playing) {
		panelNP.removeAll();
		lblplaying = components.customJLabelEditing("Now Playing: " + playing, 100, 4);
		lblplaying.setForeground(Color.WHITE);
		lblplaying.setFont(new Font("Serif", Font.BOLD, 18));
		panelNP.add(lblplaying);
		panelNP.validate();
		panelNP.repaint();
	}

	/**
	 * Processes the combinations of playing MIDI files. This includes play
	 * current song, play previous or next song.
	 * 
	 * @param song
	 *            - The name of the song.
	 */
	public void playFeature(String song) {
		try {
			if (PlayBackDevices.getInstance().isRunning() == true) {
				PlayBackDevices.getInstance().returnSequencer().stop();
				btnPlay.setText(">");
				btnPlay.setForeground(Color.WHITE);
				PlaybackFunctions.resetChordsColor();
				PlaybackFunctions.emptyNotes();
			}

			else {
				btnPlay.setText("||");
				btnPlay.setForeground(Color.YELLOW);
				PlayBackDevices.getInstance().returnSequencer().addMetaEventListener(this);

				// Replace sequence with file in this feature
				Sequence original = MIDIFilePlayer.getInstance().playSelectedFile(song);
				PlayBackDevices.getInstance().returnSequencer().setSequence(original);
				Track[] tracks = original.getTracks();
				Track trk = PlayBackDevices.getInstance().returnSequencer().getSequence().createTrack();
				for (Track track : tracks) {
					MidiMessageTypes.generateMetaData(track, trk);
				}

				Sequence updatedSeq = PlayBackDevices.getInstance().returnSequencer().getSequence();
				PlayBackDevices.getInstance().returnSequencer().setSequence(updatedSeq);
				PlayBackDevices.getInstance().returnSequencer().setTickPosition(0);
				PlayBackDevices.getInstance().returnSequencer().start();
			}

		} catch (InvalidMidiDataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Process all meta data from the currently playing MIDI file, which is
	 * currently a sequence.
	 * 
	 * @param metaPlayer
	 *            - The instance of the current meta message
	 */
	@Override
	public void meta(MetaMessage metaPlayer) {
		MidiMessageTypes.getInstance().eventColors(metaPlayer);
		// 0x2F in decimal is 47 - value for end MIDI track
		if (metaPlayer.getType() == 0x2F) {
			PlayBackDevices.getInstance().returnSequencer().stop();
			btnPlay.setText(">");
			btnPlay.setForeground(Color.WHITE);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
