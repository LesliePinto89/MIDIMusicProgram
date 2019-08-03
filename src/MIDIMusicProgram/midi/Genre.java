package midi;
import java.util.ArrayList;
import javax.sound.midi.InvalidMidiDataException;
import javax.swing.DefaultListModel;
import keyboard.Note;
import tools.PlaybackFunctions;
import tools.ScreenPrompt;

/**
 * This class defines a genre in the system. It incorporates Chord progression
 * and optional arpeggios.
 */
public class Genre {

	private DefaultListModel<String> genreModel = new DefaultListModel<String>();
	private ArrayList<Chord> chordProgression = new ArrayList<Chord>();
	private ArrayList<Chord> breakProgression = new ArrayList<Chord>();
	private ChordProgressionActions prog = ChordProgressionActions.getInstance();

	// Not implemented fully yet
	// private ArrayList<Chord> diffChord = new ArrayList<Chord>();
	// private static volatile boolean playback = false;
	// private ArrayList<Note> licks = new ArrayList<Note>();

	private static volatile Genre instance = null;

	private Genre() {
	}

	public static Genre getInstance() {
		if (instance == null) {
			synchronized (Genre.class) {
				if (instance == null) {
					instance = new Genre();
				}
			}
		}
		return instance;
	}

	public void createSong(String key, String genre) {
		prog.storeAllProgressionInList();
		try {
			switch (genre) {

			case "Blues":
				bluesOneFourFive(key);
				twelveBarProgression();
				break;

			case "Classical":
				classicalProgression(key);
				playClassical();
				break;
			}
		} catch (InvalidMidiDataException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void classicalProgression(String key) {
		key = key + 3;
		Note oneChordRoot = Note.getNotesMap().get(key);
		Chord rootChord = ListOfChords.getInstance().findChord(oneChordRoot.getName(), "maj");
		chordProgression.add(rootChord);

		Note secondNote = ListOfScales.getInstance().getKey(oneChordRoot, 6);
		Chord secondChord = ListOfChords.getInstance().findChord(secondNote.getName(), "maj");
		chordProgression.add(secondChord);

		Note thirdNote = ListOfScales.getInstance().getKey(secondNote, 2);
		Chord thirdChord = ListOfChords.getInstance().findChord(thirdNote.getName(), "min");
		chordProgression.add(thirdChord);

		Note fourthNote = ListOfScales.getInstance().getKey(thirdNote, 7);
		Chord fourthChord = ListOfChords.getInstance().findChord(fourthNote.getName(), "min");
		chordProgression.add(fourthChord);

		Note fifthNote = ListOfScales.getInstance().getKey(fourthNote, 1);
		Chord fifthChord = ListOfChords.getInstance().findChord(fifthNote.getName(), "maj");
		chordProgression.add(fifthChord);

		chordProgression.add(rootChord);
		chordProgression.add(fifthChord);
		chordProgression.add(secondChord);
		chordProgression.add(rootChord);
	}

	public void playClassical() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (Chord aChord : chordProgression) {
						if (!ScreenPrompt.getInstance().getShared()) {
							PlaybackFunctions.playAnyChordLength(aChord);
							PlaybackFunctions.timeDelay(1000);
							PlaybackFunctions.resetChordsColor();
						} else {
							break;
						}
					}
					chordProgression.clear();
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void bluesOneFourFive(String key) {
		key = key + 4;
		Note oneChordRoot = Note.getNotesMap().get(key);
		Note fourthChordRoot = ListOfScales.getInstance().getKey(oneChordRoot, 5);
		Note fifthChordRoot = ListOfScales.getInstance().getKey(fourthChordRoot, 2);
		Note[] popularRoots = { oneChordRoot, fourthChordRoot, fifthChordRoot };

		for (Note aNote : popularRoots) {
			Scale bluesScale = ListOfScales.getInstance().bluesScale(aNote);
			ListOfChords.getInstance().setBluesChords(bluesScale);
			ArrayList<Chord> tempChords = ListOfChords.getInstance().getBluesChords();
			chordProgression.addAll(tempChords);
		}

		// Used in different list
		Note fillerNoteRoot = ListOfScales.getInstance().getKey(oneChordRoot, -7);
		Note fillerNoteNext = ListOfScales.getInstance().getKey(oneChordRoot, -6);
		Note fillerNoteEnd = ListOfScales.getInstance().getKey(oneChordRoot, -5);
		Note[] fillerNotes = { fillerNoteRoot, fillerNoteNext, fillerNoteEnd };
		for (Note aNote : fillerNotes) {
			Scale bluesScale = ListOfScales.getInstance().bluesScale(aNote);
			ListOfChords.getInstance().setFillerBluesChord(bluesScale);
			ArrayList<Chord> tempFillerChords = ListOfChords.getInstance().getFillerBluesChord();
			breakProgression.addAll(tempFillerChords);
		}

		// lick(oneChordRoot);
	}

	// EXPERIMENTAL
	////////////////
	// public void lick (Note root){
	//
	// Note lickOne = ListOfScales.getInstance().getKey(root, 6);
	// licks.add(lickOne);
	//
	// Note lickSecond = ListOfScales.getInstance().getKey(lickOne, 1);
	// licks.add(lickSecond);
	//
	// Note lickThirdFlat = ListOfScales.getInstance().getKey(lickSecond, 3);
	// licks.add(lickThirdFlat);
	//
	// Note lickFourthNat = ListOfScales.getInstance().getKey(lickOne, 5);
	// licks.add(lickFourthNat);
	//
	// Note lickFifth = ListOfScales.getInstance().getKey(lickFourthNat, 4);
	// licks.add(lickFifth);
	//
	// licks.add(root);
	//
	// Note lickNewOne = ListOfScales.getInstance().getKey(root, 3);
	// licks.add(lickNewOne);
	//
	// Note lickNewSecond = ListOfScales.getInstance().getKey(lickNewOne, 1);
	// licks.add(lickNewSecond);
	//
	// Note lickNewThird = ListOfScales.getInstance().getKey(lickNewSecond, 3);
	// licks.add(lickNewThird);
	//
	// Note lickNewFourth = ListOfScales.getInstance().getKey(lickNewSecond, 1);
	// licks.add(lickNewFourth);
	//
	// Note lickNewFifth = ListOfScales.getInstance().getKey(lickNewThird, 2);
	// licks.add(lickNewFifth);
	// }

	// EXPERIMENTAL
	////////////////
	// public void playLick() throws InvalidMidiDataException{
	//
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// //int i = 0;
	// // End thread if user leaves progressions page
	// if (!playback) {
	// PlaybackFunctions.timeDelay(300);
	// PlaybackFunctions.playIntervalNote(licks.get(0));
	// PlaybackFunctions.timeDelay(0);
	//
	// PlaybackFunctions.playIntervalNote(licks.get(1));
	// PlaybackFunctions.playIntervalNote(licks.get(2));
	// PlaybackFunctions.timeDelay(200);
	//
	// PlaybackFunctions.playIntervalNote(licks.get(3));
	// PlaybackFunctions.playIntervalNote(licks.get(4));
	// PlaybackFunctions.timeDelay(300);
	//
	// PlaybackFunctions.playIntervalNote(licks.get(5));
	// PlaybackFunctions.timeDelay(400);
	//
	// //PlaybackFunctions.timeDelay(322);
	// PlaybackFunctions.playIntervalNote(licks.get(6));
	// PlaybackFunctions.timeDelay(100);
	//
	// PlaybackFunctions.playIntervalNote(licks.get(7));
	// PlaybackFunctions.playIntervalNote(licks.get(8));
	// PlaybackFunctions.timeDelay(400);
	//
	// PlaybackFunctions.playIntervalNote(licks.get(9));
	// PlaybackFunctions.playIntervalNote(licks.get(10));
	// PlaybackFunctions.timeDelay(200);
	// }
	// else {
	// playback = false;
	// }
	// } catch ( InvalidMidiDataException e) {
	//
	// e.printStackTrace();
	// }
	//
	// }
	//
	// }).start();
	//
	// }

	public void playBar(int pos, boolean endBar, int limit, int index)
			throws InvalidMidiDataException, InterruptedException {
		if (!endBar) {
			for (int i = 1; i <= limit; i++) {
				PlaybackFunctions.playAnyChordLength(chordProgression.get(index));
				PlaybackFunctions.timeDelay(400);
				PlaybackFunctions.playAnyChordLength(chordProgression.get(index));
				PlaybackFunctions.timeDelay(200);
				Note adjustedNote = ListOfScales.getInstance()
						.getKey(chordProgression.get(index).getChordNotes().get(1), 2);
				ArrayList<Note> tempNotes = new ArrayList<Note>();
				tempNotes.add(chordProgression.get(index).getChordNotes().get(0));
				tempNotes.add(adjustedNote);
				Chord editedChord = new Chord("temp", tempNotes);
				PlaybackFunctions.playAnyChordLength(editedChord);
				PlaybackFunctions.timeDelay(400);
				PlaybackFunctions.playAnyChordLength(chordProgression.get(index));
				PlaybackFunctions.timeDelay(200);
			}
		} else if (endBar) {
			PlaybackFunctions.timeDelay(300);
			PlaybackFunctions.playAnyChordLength(breakProgression.get(0));
			PlaybackFunctions.timeDelay(400);
			PlaybackFunctions.playAnyChordLength(breakProgression.get(1));
			PlaybackFunctions.timeDelay(400);
			PlaybackFunctions.playAnyChordLength(breakProgression.get(2));
			PlaybackFunctions.timeDelay(500);
		}
	}

	public void twelveBarProgression() throws InvalidMidiDataException, InterruptedException {
		playBar(1, false, 8, 0);// 4 bars
		playBar(5, false, 4, 2);// 2 bars
		playBar(1, false, 4, 0);// 2 bars
		playBar(5, false, 2, 2);// 1 bar
		playBar(4, false, 2, 1);// 1 bar
		playBar(1, false, 2, 0);// 1 bar
		playBar(0, true, 0, 0);// 1 bar
	}

	public ArrayList<Chord> getBluesChords() {
		return chordProgression;
	}

	public enum genre {
		Blues("Blues"), Jazz("Jazz"), Pop("Pop"), Folk("Folk"), Classical("Classical");

		public final String gen;

		genre(String gen) {
			this.gen = gen;
		}

		public String getGen() {
			return gen;
		}
	}

	public void storeGenreNames() {
		genre[] genreArray = genre.values();
		for (genre aGenre : genreArray) {
			genreModel.addElement(aGenre.getGen());
		}
	}

	public DefaultListModel<String> getGenreNames() {
		return genreModel;
	}
}