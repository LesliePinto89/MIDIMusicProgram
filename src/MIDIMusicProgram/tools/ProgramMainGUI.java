package tools;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import midiDevices.GetInstruments;
import keyboard.VirtualKeyboard;
import midi.ChordProgressionActions;
import midi.DurationTimer;
import midi.ListOfChords;
import midi.MidiMessageTypes;
import midiDevices.PlayBackDevices;
import java.awt.Color;
import java.awt.ComponentOrientation;

/**
 * This class handles loading all MIDI devices in memory, all music theory data
 * structures in memory, and the initial GUI to select either free play and
 * learn mode.
 */
public class ProgramMainGUI implements MouseListener {

	protected JFrame frame;
	protected JPanel backgroundImagePanel = new JPanel();
	private JPanel freePlayPanel = new JPanel();
	private JPanel learnMode = new JPanel();
	protected int screenWidth;
	protected int screenHeight;
	protected Dimension screenSize;

	private JPanel contentPane = new JPanel(new GridBagLayout());
	private JPanel centerPane = new JPanel(new GridBagLayout());
	private JPanel topBlockPane = new JPanel();
	private JPanel bottomBlockPane = new JPanel();
	private JPanel leftBlockPane = new JPanel();
	private JPanel rightBlockPane = new JPanel();
	private boolean startup = false;
	private GridBagConstraints outerFrameGUIConstraints = new GridBagConstraints();
	private SwingComponents components = SwingComponents.getInstance();

	private static volatile ProgramMainGUI instance = null;

	private ProgramMainGUI() {
	}

	public static ProgramMainGUI getInstance() {
		if (instance == null) {
			synchronized (ProgramMainGUI.class) {
				if (instance == null) {
					instance = new ProgramMainGUI();
					instance.loadApplication();
				}
			}
		}
		return instance;
	}

	/** Create the initial GUI dimensions */
	public void loadProgramWindowFrameGUI() throws IOException {
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = (int) screenSize.getWidth();
		screenHeight = (int) screenSize.getHeight();

		frame = new JFrame("Midi Keyboard: Welcome Screen");
		frame.setPreferredSize(new Dimension(screenWidth, screenHeight));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * Loads support for all required MIDI devices and data structures into
	 * memory.
	 */
	public void loadApplication() {

		try {
			loadProgramWindowFrameGUI();
			loadProgramOptions();

			if (startup == false) {

				// Each of these classes use the Singleton pattern as the
				// application only needs one instance of them for reference.
				PlayBackDevices.getInstance();
				PlayBackDevices.getInstance().startConnection();
				DurationTimer.getInstance();
				MIDIFileManager.getInstance();
				MIDIFilePlayer.getInstance();
				MidiMessageTypes.getInstance();
				GetInstruments.getInstance();
				Metronome.getInstance();
				ScreenPrompt.getInstance();
				ChordProgressionActions.getInstance();

				// Load all notes for set piano (e.g. 61, 88) on system startup
				VirtualKeyboard.getInstance().createWholeKeys();
				VirtualKeyboard.getInstance().createSharpKeys();
				VirtualKeyboard.getInstance().freePlayOrMakeTrack();
				////////////////////////////////////////////////////////////

				ListOfChords listInstance = ListOfChords.getInstance();
				listInstance.setAllKeyNotes();
				listInstance.loadMajorChords(listInstance.getAllKeyNotes());
				listInstance.loadMinorChords(listInstance.getAllKeyNotes());
				listInstance.loadHalfDimishedChords(listInstance.getAllKeyNotes());
				listInstance.loadFullyDiminishedScaleChords(listInstance.getAllKeyNotes());

				startup = true;
			}

		} catch (InvalidMidiDataException | MidiUnavailableException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Loads all welcome GUI visual elements into memory aMain features */
	public void loadProgramOptions() throws IOException {
		createGUIBorder();
		createFeatureImages();
	}

	/** Create border frame visual elements to be used in welcome GUI */
	public void createGUIBorder() throws IOException {
		BufferedImage topBarImage = ImageIO.read(new File("src/Images/TopBar.png"));
		BufferedImage bottomBarImage = ImageIO.read(new File("src/Images/BottomBar.png"));

		topBlockPane = components.guiBorderPanel(topBarImage, screenWidth, screenHeight / 6, Color.decode("#008080"), 0,
				0, 2, 0);
		leftBlockPane = components.generateEventPanel(screenWidth / 12, screenHeight / 2, null, Color.decode("#181818"),
				Color.decode("#008000"), 0, 0, 2, 0);
		rightBlockPane = components.generateEventPanel(screenWidth / 12, screenHeight / 2, null,
				Color.decode("#181818"), Color.decode("#008000"), 0, 0, 2, 0);

		bottomBlockPane = components.guiBorderPanel(bottomBarImage, screenWidth, screenHeight / 6,
				Color.decode("#008080"), 0, 0, 2, 0);
		bottomBlockPane.setBackground(Color.decode("#008000"));
		////////////////////////////////////////////////////////////////////////////////////////////

		centerPane.setPreferredSize(
				new Dimension(screenWidth / 2 + screenWidth / 4, screenHeight / 2 + screenHeight / 3));
		centerPane.setMinimumSize(new Dimension(screenWidth / 2 + screenWidth / 3, screenHeight / 3));
		centerPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		centerPane.setBackground(Color.decode("#696969"));
		centerPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.decode("#404040")));

		outerFrameGUIConstraints = components.conditionalConstraints(1, 0, 0, 0, GridBagConstraints.HORIZONTAL);
		outerFrameGUIConstraints.anchor = GridBagConstraints.PAGE_START;
		contentPane.add(topBlockPane, outerFrameGUIConstraints);

		outerFrameGUIConstraints = components.conditionalConstraints(0, 0, 0, 1, GridBagConstraints.VERTICAL);

		outerFrameGUIConstraints.anchor = GridBagConstraints.LINE_START;
		// The negative value is used as the panels use images, which removes
		// original aligned panel
		outerFrameGUIConstraints.insets = new Insets(0, 0, -5, 0);
		contentPane.add(leftBlockPane, outerFrameGUIConstraints);

		// Create feature GUI
		outerFrameGUIConstraints = components.conditionalConstraints(1, 1, 0, 1, GridBagConstraints.NONE);
		outerFrameGUIConstraints.insets = new Insets(10, 0, 10, 0);
		outerFrameGUIConstraints.anchor = GridBagConstraints.CENTER;
		contentPane.add(centerPane, outerFrameGUIConstraints);

		outerFrameGUIConstraints = components.conditionalConstraints(0, 0, 0, 1, GridBagConstraints.VERTICAL);
		outerFrameGUIConstraints.anchor = GridBagConstraints.LINE_END;
		outerFrameGUIConstraints.insets = new Insets(0, 0, -5, 0);
		contentPane.add(rightBlockPane, outerFrameGUIConstraints);

		outerFrameGUIConstraints = components.conditionalConstraints(1, 0, 0, 2, GridBagConstraints.HORIZONTAL);
		outerFrameGUIConstraints.anchor = GridBagConstraints.PAGE_END;
		outerFrameGUIConstraints.insets = new Insets(0, 0, 0, 0);
		contentPane.add(bottomBlockPane, outerFrameGUIConstraints);
	}

	/**
	 * Creates the select panel images that represent free play mode and learn
	 * mode. Clicking on either panel loads free play mode or learn mode
	 * respectfully.
	 */
	public void createFeatureImages() throws IOException {
		BufferedImage bufFreePlayPianoImage = ImageIO.read(new File("src/Images/piano-image.jpg"));
		freePlayPanel = components.customizeFeaturePanel(screenWidth / 4, screenHeight / 3, this, bufFreePlayPianoImage,
				"Free");
		freePlayPanel.setBackground(Color.decode("#181818"));

		BufferedImage bufLearnImage = ImageIO.read(new File("src/Images/Music score.jpg"));
		learnMode = components.customizeFeaturePanel(screenWidth / 4, screenHeight / 3, this, bufLearnImage, "Learn");
		learnMode.setBackground(Color.decode("#181818"));

		SwingComponents components = SwingComponents.getInstance();
		GridBagConstraints styleGUI = components.conditionalConstraints(1, 1, 0, 0, GridBagConstraints.NONE);
		centerPane.add(freePlayPanel, styleGUI);

		styleGUI.gridx = 1;
		centerPane.add(learnMode, styleGUI);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ProgramMainGUI.getInstance();
			}
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Object obj = e.getSource();

		if (obj.equals(freePlayPanel)) {
			try {
				frame.setVisible(false);
				VirtualKeyboard.getInstance().createVirtualKeyboard(false);
			} catch (InvalidMidiDataException | MidiUnavailableException | IOException e1) {
				e1.printStackTrace();
			}
		}

		else if (obj.equals(learnMode)) {
			try {
				frame.setVisible(false);
				VirtualKeyboard.getInstance().createVirtualKeyboard(true);

			} catch (InvalidMidiDataException | MidiUnavailableException | IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void enableFrame() {
		frame.setVisible(true);
	}
}
