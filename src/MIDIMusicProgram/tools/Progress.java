package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

/**Experimental: This class stores a user's score from their 
 * quiz attempts in volatile memory.
 * */
public class Progress {
	private File allScores;
	private BufferedReader bReader;
	private BufferedWriter bWriter;

	private static volatile Progress instance = null;
	private Progress() {
	}

	public static Progress getInstance() {
		if (instance == null) {
			synchronized (Progress.class) {
				if (instance == null) {
					instance = new Progress();
					try {
						instance.setUpFileDetails();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return instance;
	}

	public void setUpFileDetails() throws IOException {
		// allScores = File.createTempFile("src/Progress/Scores", ".txt");
		allScores = new File("src/Progress/Scores.txt");
		bWriter = new BufferedWriter(new FileWriter(allScores));
		bReader = new BufferedReader(new FileReader(allScores));

		// allScores = new File("src/Progress/Scores.txt");
		// allScores.createNewFile();
		// FileWriter writer = new FileWriter(allScores,false);

		// bWriter = new BufferedWriter(new FileWriter(allScores));
	}

	public void writeScoresToFile(ArrayList<String> allScoresData) throws IOException {
		for (String aScore : allScoresData) {
			bWriter.write(aScore);
			bWriter.flush();
		}
	}

	public void writeToFile(String scoreData) throws IOException {
		bWriter.write(scoreData);
		bWriter.flush();
	}

	public void updateScore(String currentScoreData) throws IOException {
		// String currentLine;
		// while((currentLine = bReader.readLine()) != null) {
		// // trim newline when comparing with lineToRemove
		// String trimmedLine = currentLine.trim();
		// if(trimmedLine.equals(currentScoreData)) continue;
		// bWriter.write("");
		// bWriter.write("\n");
		// }
		//

		Path path = allScores.toPath();
		try (Stream<String> stream = Files.lines(path)) {
			stream.filter(line -> !line.trim().equals(currentScoreData));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readFromFile() throws IOException {
		int line = 0;
		for (String data = bReader.readLine(); data != null; data = bReader.readLine()) {
			line++;
			// String[] tokens = data.split(" \\|");
		}
	}
}
