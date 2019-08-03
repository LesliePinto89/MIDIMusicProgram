package tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import midi.TypesOfArpeggios;

/**
 * This class is used to create select the applications desired custom Swing
 * components.
 */
public class SwingComponents implements MouseListener {

	private boolean colorToggleState = false;
	private boolean colorRangeToggleState = false;
	private boolean displayScaleNotesOnly = false;
	private TypesOfArpeggios arpeggioType = TypesOfArpeggios.getInstance();
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int screenWidth = (int) screenSize.getWidth();
	private int screenHeight = (int) screenSize.getHeight();
	private JTabbedPane tabbedPane;
	private static int jListTableWidth;
	private static int jListYPos = 80;
	private static int jListTableHeight = 100;
	private static int jListYAndHeight = jListYPos + jListTableHeight + 50;

	private static volatile SwingComponents instance = null;

	private SwingComponents() {
	}

	public static SwingComponents getInstance() {
		if (instance == null) {
			synchronized (SwingComponents.class) {
				if (instance == null) {
					instance = new SwingComponents();
					instance.featureTabDimensions();
				}
			}
		}
		return instance;
	}

	/**
	 * Used to distinguish play back of scales to relative pitch's show scales
	 * only.
	 */
	public void displayScalesOnlyState(boolean scaleState) {
		displayScaleNotesOnly = scaleState;
	}

	public boolean getDisplayScaleState() {
		return displayScaleNotesOnly;
	}

	public void changeColorToggle(boolean state) {
		colorToggleState = state;
	}

	public boolean getColorToggleStatus() {
		return colorToggleState;
	}

	public void changeRangeColorToggle(boolean rangeState) {
		colorRangeToggleState = rangeState;
	}

	public boolean getRangeColorToggleStatus() {
		return colorRangeToggleState;
	}

	/**
	 * Highlights the text area in feature 5 as the progression plays.
	 * 
	 * @param area
	 *            - The JTextArea to edit
	 * @param i
	 *            - The index value to edit the area component
	 */
	public void colourJText(JTextArea area, int i) throws BadLocationException {
		int startIndex = area.getLineStartOffset(i);
		int endIndex = area.getLineEndOffset(i);
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.decode("#303030"));
		area.getHighlighter().addHighlight(startIndex, endIndex, painter);
	}

	public static int getJListWidth() {
		return jListTableWidth;
	}

	public static int getJListYPos() {
		return jListYPos;
	}

	public static int getJListTableHeight() {
		return jListTableHeight;
	}

	public static int getJListYAndHeight() {
		return jListYAndHeight;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void featureTabDimensions() {
		UIManager.put("TabbedPane.selected", Color.decode("#303030"));
		UIManager.put("TabbedPane.selectedForeground", Color.YELLOW);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setPreferredSize(new Dimension(screenWidth / 2 - 40, screenHeight / 2));
		jListTableWidth = screenWidth / 2;
		jListTableHeight = screenHeight / 3;
	}

	public JTabbedPane getFeatureTab() {
		return tabbedPane;
	}

	/** Series of custom swing components */

	/**
	 * Intentional null layout frame for debug over features.
	 */
	public JFrame floatingDebugFrame(boolean visible, boolean resize, Component c, String title, int x, int y,
			int width, int height) {
		JFrame aFrame = new JFrame();
		aFrame.setVisible(visible);
		aFrame.setResizable(resize);
		aFrame.setLocationRelativeTo(c);
		aFrame.setTitle(title);
		aFrame.setBounds(x, y, width, height);
		return aFrame;
	}

	/** Used with many of the components layout in the system's GUI */
	public GridBagConstraints conditionalConstraints(int weightx, int weighty, int gridx, int gridy, int fill) {
		GridBagConstraints conditionalConstraints = new GridBagConstraints();
		conditionalConstraints.weightx = weightx;
		conditionalConstraints.weighty = weighty;
		conditionalConstraints.gridx = gridx;
		conditionalConstraints.gridy = gridy;
		conditionalConstraints.fill = fill;
		return conditionalConstraints;
	}

	public void colourMenuPanels(JList<String> currentList, Color backPanel, Color innerPanel) {
		currentList.setCellRenderer(getRenderer(innerPanel));
		currentList.setBackground(backPanel);
	}

	public void colourFeatureTab(JList<String> currentList, Color innerPanel) {
		currentList.setCellRenderer(getRenderer(innerPanel));

	}

	/**
	 * Customer cell render for GUI components.
	 * 
	 * @param backgroundColour
	 *            - The colour used as an attribute's background colour.
	 * @return The edited cell component
	 */
	private ListCellRenderer<? super String> getRenderer(Color backgroundColour) {
		return new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index,
						isSelected, cellHasFocus);
				listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.BLACK));
				Font aFont = null;

				if (list.getName().equals("Arpeggios")) {
					System.out.println("Detected");
				}

				if (list.getName().equals("Instruments") || list.getName().equals("Tempos")
						|| list.getName().equals("allSongsList")) {
					int width = (int) tabbedPane.getPreferredSize().getWidth();
					int height = (int) tabbedPane.getPreferredSize().getHeight();

					if (list.getName().equals("Tempos") || list.getName().equals("allSongsList")) {
						if (list.getName().equals("allSongsList")) {
							aFont = new Font("Serif", Font.BOLD, 20);
						}

						// Tempos stuff
						else {
							aFont = new Font("Tahoma", Font.BOLD, 26);
							listCellRendererComponent.setPreferredSize(new Dimension(width / 2, height / 5));
							listCellRendererComponent.setMinimumSize(new Dimension(width / 2, getScreenHeight() / 5));
						}
					}

					// Instrument stuff
					else {
						listCellRendererComponent.setPreferredSize(new Dimension(width / 7, height / 20));
						listCellRendererComponent.setMinimumSize(new Dimension(width / 7, getScreenHeight() / 20));
						aFont = new Font("Tahoma", Font.BOLD, 26);
					}
					listCellRendererComponent.setBackground(backgroundColour);

					if (isSelected) {
						listCellRendererComponent.setBackground(Color.decode("#303030"));
						listCellRendererComponent.setForeground(Color.YELLOW);
					}
				}

				// Input options stuff
				else if (list.getName().equals("Input")) {
					listCellRendererComponent
							.setPreferredSize(new Dimension(getScreenWidth() / 6, getScreenHeight() / 6));
					listCellRendererComponent
							.setMinimumSize(new Dimension(getScreenWidth() / 6, getScreenHeight() / 6));
					listCellRendererComponent.setBackground(backgroundColour);
					aFont = new Font("Tahoma", Font.BOLD, 16);
				}
				// Screen prompt stuff
				else {
					listCellRendererComponent
							.setPreferredSize(new Dimension(getScreenWidth() / 8, getScreenHeight() / 20));
					listCellRendererComponent
							.setMinimumSize(new Dimension(getScreenWidth() / 8, getScreenHeight() / 20));
					listCellRendererComponent.setBackground(backgroundColour);
					aFont = new Font("Tahoma", Font.BOLD, 36);

					// Genre stuff
					if (isSelected) {
						listCellRendererComponent.setForeground(Color.YELLOW);
					}
				}
				listCellRendererComponent.setFont(aFont);
				return listCellRendererComponent;
			}
		};
	}

	public JButton customTrackJButton(int width, int height, String text, String name, MouseListener listen,
			Color border, int top, int left, int bottom, int right) {
		JButton aButton = new JButton();
		aButton.setPreferredSize(new Dimension(width, height));
		aButton.setText(text);
		aButton.setName(name);
		aButton.addMouseListener(listen);
		aButton.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, border));
		return aButton;
	}

	public JList<String> customJList(int width, int height, MouseListener listen) {
		JList<String> aJList = new JList<String>();
		aJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		aJList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		aJList.setVisibleRowCount(-1);
		aJList.addMouseListener(listen);
		return aJList;
	}

	public JScrollPane customJScrollPane(JList<String> aJList, int scrollW, int scrollH) {
		JScrollPane aScrollPane = new JScrollPane(aJList);
		aScrollPane.setPreferredSize(new Dimension(scrollW, scrollH));
		aScrollPane.setMinimumSize(new Dimension(scrollW, scrollH));
		return aScrollPane;
	}

	public JComboBox<String> customJComboBox(int width, int height, String text, ActionListener listen, int font) {
		JComboBox<String> aBox = new JComboBox<String>(arpeggioType.getNames());
		aBox.setPreferredSize(new Dimension(width, height));
		aBox.setName(text);
		aBox.addActionListener(listen);
		aBox.setBackground(Color.decode("#B8B8B8"));
		aBox.setFont(new Font("Tahoma", Font.BOLD, font));
		aBox.setForeground(Color.WHITE);
		aBox.setRenderer(getRenderer(Color.decode("#B8B8B8")));
		return aBox;
	}

	public JButton customJButton(int width, int height, String text, MouseListener listen, int font, Color aColor) {
		JButton aButton = new JButton();
		aButton.setPreferredSize(new Dimension(width, height));
		aButton.setText(text);
		aButton.setName(text);
		aButton.addMouseListener(listen);
		aButton.setBackground(aColor);
		aButton.setFont(new Font("Tahoma", Font.BOLD, font));
		aButton.setForeground(Color.WHITE);
		return aButton;
	}

	public JButton customJButtonTwo(int width, int height, String name, BufferedImage buff, Color c, Color cTwo,
			boolean con) {
		JButton aButton = new JButton();
		aButton.setPreferredSize(new Dimension(width, height));
		aButton.setName(name);
		Image image = buff.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(image);
		aButton.setIcon(imageIcon);
		aButton.setBackground(c);
		aButton.setBorder(new LineBorder(cTwo));
		aButton.setFocusPainted(con);
		return aButton;
	}

	public JToggleButton customActionJToggleButton(int width, int height, String text, MouseListener listen, int font,
			Color aColor) {
		JToggleButton aJToggleButton = new JToggleButton();
		aJToggleButton.setPreferredSize(new Dimension(width, height));
		aJToggleButton.setText(text);
		aJToggleButton.setName(text);
		aJToggleButton.addMouseListener(listen);
		aJToggleButton.setBackground(aColor);
		aJToggleButton.setFont(new Font("Tahoma", Font.BOLD, font));
		aJToggleButton.setForeground(Color.WHITE);
		return aJToggleButton;
	}

	// For JToggleButtons with a scaled icon inside, e.g. record button
	public JToggleButton featureJToggleButtonAlt(int width, int height, String name, boolean focus, boolean content,
			Color backColor, Color borderColor, BufferedImage aImageOff, BufferedImage aImageOn) {
		JToggleButton aJToggleButton = new JToggleButton();
		aJToggleButton.setPreferredSize(new Dimension(width, height));
		aJToggleButton.setBackground(backColor);
		aJToggleButton.setName(name);
		aJToggleButton.setBorder(new LineBorder(borderColor));
		aJToggleButton.setFocusPainted(focus);
		aJToggleButton.setContentAreaFilled(content);
		Image scaledOff = aImageOff.getScaledInstance(50, 42, Image.SCALE_SMOOTH);
		ImageIcon aIcon = new ImageIcon(scaledOff);
		Image scaledOn = aImageOn.getScaledInstance(50, 42, Image.SCALE_SMOOTH);
		ImageIcon aIconTwo = new ImageIcon(scaledOn);
		aJToggleButton.setIcon(aIcon);
		aJToggleButton.setSelectedIcon(aIconTwo);
		return aJToggleButton;
	}

	// Don't Record button image
	public JToggleButton featureJToggleButton(boolean toggle, int width, int height, String text, String name,
			Color backColor, Color textColor, Color borderColor, boolean focus, BufferedImage buff, BufferedImage buff2,
			boolean contentFilled) {
		JToggleButton aJToggleButton = new JToggleButton();
		aJToggleButton.setPreferredSize(new Dimension(width, height));
		aJToggleButton.setBackground(backColor);
		aJToggleButton.setForeground(textColor);
		aJToggleButton.setText(text);
		aJToggleButton.setFont(new Font("Tahoma", Font.BOLD, 20));
		aJToggleButton.setName(name);
		aJToggleButton.setBorder(new LineBorder(borderColor));
		aJToggleButton.setFocusPainted(focus);
		aJToggleButton.setContentAreaFilled(contentFilled);
		if (!toggle) {
			Image scaled = buff.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			ImageIcon aIcon = new ImageIcon(scaled);
			aJToggleButton.setIcon(aIcon);
		} else {
			ImageIcon recOffIcon = new ImageIcon(buff);
			aJToggleButton.setIcon(recOffIcon);
			ImageIcon recOnIcon = new ImageIcon(buff2);
			aJToggleButton.setSelectedIcon(recOnIcon);
		}
		return aJToggleButton;
	}

	public JPanel generateEventPanel(int width, int height, MouseListener listen, Color panelColor, Color border,
			int top, int left, int bottom, int right) {
		JPanel carriedJPanel = new JPanel();
		carriedJPanel.setBackground(panelColor);
		carriedJPanel.setPreferredSize(new Dimension(width, height));
		carriedJPanel.setMinimumSize(new Dimension(width, height));
		carriedJPanel.addMouseListener(listen);
		carriedJPanel.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, border));
		return carriedJPanel;
	}

	public JPanel customPanelTwo(int width, int height, Color panelColor, LayoutManager l) {
		JPanel carriedJPanel = new JPanel();
		carriedJPanel.setBackground(panelColor);
		carriedJPanel.setPreferredSize(new Dimension(width, height));
		carriedJPanel.setMinimumSize(new Dimension(width, height));
		carriedJPanel.setLayout(l);
		return carriedJPanel;
	}

	public JPanel customPanelThree(int width, int height, Color panelColor) {
		JPanel carriedJPanel = new JPanel();
		carriedJPanel.setBackground(panelColor);
		carriedJPanel.setPreferredSize(new Dimension(width, height));
		carriedJPanel.setMinimumSize(new Dimension(width, height));
		return carriedJPanel;
	}

	public JPanel guiBorderPanel(BufferedImage carriedBufferedImage, int width, int height, Color border, int top,
			int left, int bottom, int right) {
		JPanel carriedJPanel = new JPanel();
		carriedJPanel.setPreferredSize(new Dimension(width, height));
		carriedJPanel.setMinimumSize(new Dimension(width, height));
		carriedJPanel.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, border));
		JLabel temp = customiseImageAsJLabel(carriedBufferedImage, width, height * 3);
		carriedJPanel.add(temp);
		return carriedJPanel;
	}

	public JLabel customiseImageAsJLabel(BufferedImage carriedBufferedImage, int width, int height) {
		Image scaledOff = null;
		scaledOff = carriedBufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		JLabel picLabel = new JLabel(new ImageIcon(scaledOff));
		return picLabel;
	}

	public JPanel customizeFeaturePanel(int width, int height, MouseListener listen, BufferedImage carriedBufferedImage,
			String name) {
		JPanel carriedJPanel = new JPanel();
		carriedJPanel.setBackground(Color.decode("#000000"));
		carriedJPanel.setPreferredSize(new Dimension(width - 5, height - 5));
		carriedJPanel.setMinimumSize(new Dimension(width, height));
		if (name != null) {
			carriedJPanel.setName(name);
		}
		carriedJPanel.addMouseListener(listen);
		Image scaledOff = null;
		scaledOff = carriedBufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

		JLabel picLabel = new JLabel(new ImageIcon(scaledOff));
		carriedJPanel.add(picLabel);
		return carriedJPanel;
	}

	public JLabel customJLabelEditing(String text, int width, int height) {
		JLabel carriedJLabel = new JLabel();
		carriedJLabel.setText(text);
		carriedJLabel.setPreferredSize(new Dimension(width, height));
		carriedJLabel.setMinimumSize(new Dimension(width, height));
		return carriedJLabel;
	}

	public JFrame customJFrame(String title, int width, int height, JPanel content, Component c) {
		JFrame aFrame = new JFrame(title);
		aFrame.setPreferredSize(new Dimension(width, height));
		aFrame.setMinimumSize(new Dimension(width, height));
		aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aFrame.pack();
		aFrame.setContentPane(content);
		aFrame.setVisible(true);
		aFrame.setLocationRelativeTo(c);
		aFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		return aFrame;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

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