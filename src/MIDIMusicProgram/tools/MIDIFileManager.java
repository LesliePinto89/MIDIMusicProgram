package tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import javax.sound.midi.MidiSystem;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import midiDevices.PlayBackDevices;

/**
 * This class defines the internal file manager. It's uses include selecting a
 * MIDI file to import in the system as a sequence, and a save manager to save a
 * recorded sequence externally as a .mid file.
 */
public class MIDIFileManager {

	private File createdMIDIFile;
	private ArrayList<File> storedMIDISavedFiles = new ArrayList<File>();
	private static JFileChooser fileChooser;
	private static File defaultDirectory;

	private static volatile MIDIFileManager instance = null;

	private MIDIFileManager() {
	}

	public static MIDIFileManager getInstance() {
		if (instance == null) {
			synchronized (PlayBackDevices.class) {
				if (instance == null) {
					instance = new MIDIFileManager();
					fileChooser = new JFileChooser();
					defaultDirectory = new File("src/Tracks");

					// Will vary based on default directory
					fileChooser.setCurrentDirectory(defaultDirectory);
				}
			}
		}

		return instance;
	}

	public DefaultListModel<String> buildSongList() {
		DefaultListModel<String> carriedListModel = new DefaultListModel<String>();
		for (File fileEntry : defaultDirectory.listFiles()) {
			if (fileEntry.isDirectory()) {
			} else {
				String removeDirectoryName = fileEntry.toString();
				removeDirectoryName = removeDirectoryName.substring(11, removeDirectoryName.length());
				carriedListModel.addElement(removeDirectoryName);
			}
		}
		return carriedListModel;
	}

	public ArrayList<File> getFilesSongList() {
		for (File fileEntry : defaultDirectory.listFiles()) {
			storedMIDISavedFiles.add(fileEntry);
		}
		return storedMIDISavedFiles;
	}

	public File selectMIDIFile() {
		fileChooser.showOpenDialog(fileChooser);
		// Store so can get later in memory
		File newFile = fileChooser.getSelectedFile();

		return newFile;
	}

	public void saveNewMIDIFile(JToggleButton saveMIDI) {
		// Valid when user has made a sequence
		if (PlayBackDevices.getInstance().getSequence() != null) {

			int sf = fileChooser.showSaveDialog(fileChooser);
			// Store so can get later in memory
			File newFile = fileChooser.getSelectedFile();
			storeMIDIFileArray(newFile);
			if (sf == JFileChooser.APPROVE_OPTION) {
				int[] allowedMidiTypes = MidiSystem.getMidiFileTypes(PlayBackDevices.getInstance().getSequence());
				if (allowedMidiTypes.length == 0) {
					System.err.println("No supported MIDI file types.");
				} else {
					try {
						MidiSystem.write(PlayBackDevices.getInstance().getSequence(), allowedMidiTypes[0],
								getCurrentMIDIFile());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				JOptionPane.showMessageDialog(null, "File has been saved", "File Saved",
						JOptionPane.INFORMATION_MESSAGE);
			}
			saveMIDI.setSelected(false);
			saveMIDI.setEnabled(true);
		}
	}

	public void storeMIDIFileArray(File storedMIDIFile) {
		// Add to all midi files in memory
		storedMIDISavedFiles.add(storedMIDIFile);

		// Used to add selected MIDI file to save output
		this.createdMIDIFile = storedMIDIFile;
	}

	/**
	 * Used to add custom images based on user action.
	 */
	public Vector<String> getMIDIFilesNames() throws IOException {
		String path = storedMIDISavedFiles.get(0).getParentFile().toString();
		File folder = new File(path);

		Vector<String> imageList = new Vector<String>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				// matchingFile(addedButton,fileEntry);
			} else {
				imageList.add(fileEntry.toString());
			}
		}
		return imageList;

	}

	/**
	 * To be used to write created sequence MIDI externally to program.
	 */
	public File getCurrentMIDIFile() {
		return createdMIDIFile;
	}
}