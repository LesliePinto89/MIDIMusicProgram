package tools;

import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * This class defines the construct of the debug mode feature attributes.
 */
public class DebugConsole extends OutputStream {
	private JTextArea textArea;
	private final StringBuilder sb = new StringBuilder();
	private String title;

	public DebugConsole(final JTextArea textArea, String title) {
		this.textArea = textArea;
		this.title = title;
		sb.append(title + "> ");
		textArea.setFont(new Font("Serif", Font.BOLD, 26));

	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}

	@Override
	public void write(int b) throws IOException {

		if (b == '\r')
			return;

		if (b == '\n') {
			final String text = sb.toString() + "\n";
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					textArea.append(text);
				}
			});
			sb.setLength(0);
			sb.append(title + "> ");
			return;
		}
		sb.append((char) b);
	}
}
