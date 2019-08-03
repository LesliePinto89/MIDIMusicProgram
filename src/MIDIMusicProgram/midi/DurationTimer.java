package midi;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import midiDevices.PlayBackDevices;

/**
 * This class handles calculating the duration time of each note input on the
 * internal controller.
 */
public class DurationTimer {

	private static int durationResolution = 0;
	static Timer timerDuration;
	private volatile boolean exit = false;
	private long lastNoteOffEpochTime;
	private long allDeltas = 0;
	private MidiMessageTypes messages = MidiMessageTypes.getInstance();
	private static volatile DurationTimer instance = null;

	private DurationTimer() {
	}

	public static DurationTimer getInstance() {
		if (instance == null) {
			synchronized (DurationTimer.class) {
				if (instance == null) {
					instance = new DurationTimer();
				}
			}
		}
		return instance;
	}

	// Stored previous note cumulative value for next note's delta
	public void storeCumulativeTime(long carriedDeltaTicks) {
		allDeltas = carriedDeltaTicks;
	}

	public long getCumulativeTime() {
		return allDeltas;
	}

	public void storeNoteOffTimeStamp(long noteOffTimeStamp) {
		lastNoteOffEpochTime = noteOffTimeStamp;
	}

	public long getNoteOffTimeStamp() {
		return lastNoteOffEpochTime;
	}

	public void buildDuration(int newDurationRes) {
		durationResolution = durationResolution + newDurationRes;
	}

	public int getCycledDuration() {
		return durationResolution;
	}

	public void resetDuration() {
		durationResolution = 0;
	}

	/**
	 * This method calculates the duration of a note by incrementing a value of
	 * 1/64 of the PPQ, every clock cycle. This cycle is a timer the runs every
	 * 63 milliseconds, which is 1/64 of a second.
	 * 
	 * @param passedButton
	 *            - The button for which to calculate its duration time.
	 * @param endCycleBool
	 *            - Termination condition.
	 */
	public void setDurationTimer(JButton passedButton, boolean endCycleBool) {
		if (endCycleBool == true) {
			timerDuration.cancel();
			if (messages.getDebugStatus()) {
				messages.sequenceTimingMessages("Timer for recording duration has ended");
				// Console debug
				// System.out.println("Timer for recording duration has ended");
			}
		} else {
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					// Added the smallest value of a 1/64 note of the set
					// resolution used in the
					// cycle to determine exact note through use of cumulative
					// tick values during sustain.
					buildDuration(PlayBackDevices.getInstance().getCurrentSequenceResolution() / 16);
				}
			};

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (!exit) {
						try {
							// MIGHT NEED TO GET RID OF THIS CODE INCASE THE
							// SUSTAIN AFFECT OF SOME
							// NOTES GET RUINED OR THE TIMING AFTER NO SOUND
							// GETS AFFECTED
							if (getCycledDuration() == PlayBackDevices.getInstance().getCurrentSequenceResolution()
									* 4) {
								if (messages.getDebugStatus()) {
									String durationValue = Integer.toString(getCycledDuration());
									String wholeNoteValue = Integer
											.toString(PlayBackDevices.getInstance().getCurrentSequenceResolution() * 4);

									messages.sequenceTimingMessages("Current duration value" + durationValue);
									messages.sequenceTimingMessages("Is this a whole note value" + wholeNoteValue);

									// Console debug
									// System.out.println("Current duration
									// value: "+getCycledDuration());
									// System.out.println("Is this a whole note
									// value:
									// "+MidiReceiver.getInstance().getCurrentSequenceResolution()
									// * 4);
								}
								timerDuration.cancel();
								stop();

							}
							Thread.sleep(1000);
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}
					}
				}

				public void stop() {
					exit = true;
				}
			});
			timerDuration = new Timer("MyTimer");

			// start timer in 63 milliseconds as this is 1/16 of a second
			// (1000ms) as its
			// allows more accurate accumulation of ticks to decide what note
			// speed is being made.
			timerDuration.scheduleAtFixedRate(timerTask, 0, 63);
			t.start();
		}
	}
}