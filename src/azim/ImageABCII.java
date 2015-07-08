package azim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ImageABCII {

	private JFrame frame;

	/* Components */
	JPanel imagePanel;
	JPanel menuPanel;
	GridBagConstraints menuGBC;
	JButton openButton;
	JButton resetButton;
	JButton ditheringButton;
	JButton medianButton;
	JPanel openPanel;
	JPanel consolePanel;
	JScrollPane consoleScrollPane;
	JTextPane consoleTextPane;
	StyledDocument consoleDoc;
	SimpleAttributeSet dirString;
	JLabel consoleLabel;
	JSpinner ditherSpinner;
	JTextField medianField;
	SpinnerListModel ditherModel;
	JCheckBox bw_CheckBox;
	JLabel grayscaleLabel;
	
	
	/* Variables */
	JFileChooser fc;
	ImageDrawingPanel IDP;
	int imagePanelW = 590;// DO NOT CHANGE
	int imagePanelH = 400; // DO NOT CHANGE
	String[] levels = {"2","4","8","16"};
	int currentLevel = 2;
	int medianLevel = 2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageABCII window = new ImageABCII();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ImageABCII() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(102, 153, 51));
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Image ABC II");
		frame.getContentPane().setLayout(null);
		
								/* OpenPanel -------- JPanel */
		
		openPanel = new JPanel();
		openPanel.setBackground(new Color(153, 204, 51));
		openPanel.setBounds(12, 12, 173, 153);
		frame.getContentPane().add(openPanel);
		openPanel.setLayout(null);
		
		
								/* OpenButton -------- JButton */
		
		openButton = new JButton("Open");
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				File selectedFile = null;
				fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fc.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
				    selectedFile = fc.getSelectedFile();
				    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
				    try {
						consoleDoc.insertString(consoleDoc.getLength(), "Selected file: ", null);
						consoleDoc.insertString(consoleDoc.getLength(), selectedFile.getAbsolutePath() + "\n", dirString);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
				
				if(imagePanel.isAncestorOf(IDP))
				{
					IDP.deallocateIDP();
					imagePanel.remove(IDP);
				}
				//Draw the opened image on an instance of the ImageDrawingPanel
				IDP = new ImageDrawingPanel(selectedFile);
				IDP.setOperationIndex(0); // zero because we only want to draw it on the IDP without any filter
				//IDP.setPreferredSize(new Dimension(IDP.getImageW(), IDP.getImageH()));
				IDP.setPreferredSize(new Dimension(imagePanelW, imagePanelH));
				//pass the width and the height of the imagePanel to the IDP class
				IDP.setImagePanelH(imagePanelH);
				IDP.setImagePanelW(imagePanelW);
				
				imagePanel.add(IDP);
				imagePanel.validate();
			}
		});
		openButton.setBounds(23, 12, 100, 14);
		openButton.setBackground(new Color(102, 153, 51));
		openButton.setForeground(new Color(153, 204, 51));
		openButton.setBorder(null);
		openPanel.add(openButton);
		
								/*	resetButton--------------JButton	*/
		
		resetButton = new JButton("RESET");
		resetButton.setBackground(new Color(102, 153, 51));
		resetButton.setForeground(new Color(153, 204, 51));
		resetButton.setBorder(null);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//ACTION PERFORMED
				IDP.setOperationIndex(3);
				IDP.repaint();
			}
		});
		
								/*	ditheringButton ---------JButton	*/
		
		ditheringButton = new JButton("Dither");
		ditheringButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				IDP.setGivenLevel(currentLevel);//passes the chosen level (2 by default)
				IDP.setOperationIndex(1);
				try {
					consoleDoc.insertString(consoleDoc.getLength(), "Level: " + currentLevel + "\n", dirString);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				IDP.repaint();
			}
		});
		ditheringButton.setBackground(new Color(102, 153, 51));
		ditheringButton.setForeground(new Color(153, 204, 51));
		ditheringButton.setBorder(null);
		
		
								/*	ditherSpinner-----------JSpinner	*/
		
		ditherModel = new SpinnerListModel(levels);
		ditherSpinner = new JSpinner(ditherModel);
		ditherSpinner.setEnabled(true);
		JFormattedTextField cx = ((JSpinner.DefaultEditor)ditherSpinner.getEditor()).getTextField();//setFocusable(false);
		cx.setForeground(new Color(51,153,255));
		cx.setBackground(new Color(238,238,238));
		cx.setHorizontalAlignment(JFormattedTextField.LEFT);
		cx.setColumns(2);
		cx.setFocusable(false);
		ditherSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				currentLevel = Integer.parseInt(ditherSpinner.getValue().toString());
				System.out.println(">> Current level = " + currentLevel);
			}
		});
		
								/*		JLabel------------grayscaleLabel		*/
				
				
		grayscaleLabel = new JLabel();
		grayscaleLabel.setText("GRAY SCALE");
		grayscaleLabel.setForeground(new Color(102, 153, 51));
		grayscaleLabel.setFont(new Font("Serif", Font.PLAIN, 10));
		grayscaleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
								/* bw_CheckBox -------- JCheckBox */
		
		
		bw_CheckBox = new JCheckBox();
		bw_CheckBox.setBackground(new Color(102, 153, 51));
		bw_CheckBox.isEnabled();
		
		bw_CheckBox.addItemListener(new ItemListener() {
	         public void itemStateChanged(ItemEvent e) {
	        	 
	        	 IDP.setBWCheckbox(e.getStateChange());
	         }
	      });
		
		
								/*	medianButton ---------JButton	*/
		
		medianButton = new JButton("Median Cut");
		medianButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				medianLevel = Integer.parseInt(medianField.getText());
				IDP.setMedianLevel(medianLevel);//passes the chosen level (2 by default)
				IDP.setOperationIndex(2);
				try {
					consoleDoc.insertString(0, "Median Level: " + medianLevel + "\n", null);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				IDP.repaint();
			}
		});
		medianButton.setBackground(new Color(102, 153, 51));
		medianButton.setForeground(new Color(153, 204, 51));
		medianButton.setBorder(null);
		
		
								/*	medianField --------JTextField	*/
		
		medianField = new JTextField("2", 3);
		medianField.setForeground(new Color(153, 204, 51));
		medianField.setBackground(new Color(102, 153, 51));
		medianField.setBorder(null);
		
		
		
								/* imagePanel -------- JPanel */
		
		imagePanel = new JPanel();
		imagePanel.setBounds(198, 12, imagePanelW, imagePanelH);
		imagePanel.setBackground(new Color(153, 204, 51));
		imagePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
		
		frame.getContentPane().add(imagePanel);
		
		System.out.println("W: " + imagePanel.getWidth() + "H: " + imagePanel.getHeight());
		

		
								/* consolePanel -------- JPanel */
		
		consolePanel = new JPanel();
		consolePanel.setBorder(new MatteBorder(0, 2, 0, 2, (Color) new Color(153, 204, 0)));
		consolePanel.setBackground(new Color(102, 153, 51));
		consolePanel.setBounds(198, 451, 401, 97);
		frame.getContentPane().add(consolePanel);
		consolePanel.setLayout(null);
		
		
								/* consoleTextPane -------- JTextPane */
		
		consoleTextPane = new JTextPane();
		consoleTextPane.setForeground(new Color(255, 255, 255));
		consoleTextPane.setEditable(false);
		consoleTextPane.setBackground(new Color(102, 153, 51));
		consoleTextPane.setBounds(12, 55, 349, 50);
		
		consolePanel.add(consoleTextPane);
		
		consoleDoc = consoleTextPane.getStyledDocument();
		dirString = new SimpleAttributeSet();
		StyleConstants.setForeground(dirString, Color.LIGHT_GRAY);
		//StyleConstants.setBold(dirString, true);
		StyleConstants.setItalic(dirString, true);
		
		
		
								/* consoleLabel -------- JLabel */
		
		consoleLabel = new JLabel("Console:");
		consoleLabel.setBounds(25, 0, 70, 15);
		consolePanel.add(consoleLabel);
		
		
		
		
		
		/* menuPanel -------- JPanel */

		menuPanel = new JPanel();
		menuPanel.setBounds(12, 177, 173, 371);
		menuPanel.setBackground(new Color(153, 204, 51));
		frame.getContentPane().add(menuPanel);
		
		GridBagLayout gbl_menuPanel = new GridBagLayout();
//		gbl_menuPanel.columnWidths = new int[]{0};
//		gbl_menuPanel.rowHeights = new int[]{0};
//		gbl_menuPanel.columnWeights = new double[]{Double.MIN_VALUE};
//		gbl_menuPanel.rowWeights = new double[]{Double.MIN_VALUE};
		menuPanel.setLayout(gbl_menuPanel);
		
		menuGBC = new GridBagConstraints();
		menuGBC.insets = new Insets(5, 6, 5, 6);
		
		menuGBC.gridx = 0;
		menuGBC.gridy = 0;
		menuGBC.gridheight = 1;
		menuGBC.gridwidth = 2;
		menuGBC.fill = GridBagConstraints.HORIZONTAL;
		menuPanel.add(ditheringButton, menuGBC);
		
		menuGBC.gridx = 2;
		menuGBC.gridy = 0;
		menuGBC.gridheight = 1;
		menuGBC.gridwidth = 1;
		menuGBC.fill = GridBagConstraints.HORIZONTAL;
		menuPanel.add(ditherSpinner, menuGBC);
		
		
		menuGBC.gridx = 0;
		menuGBC.gridy = 1;
		menuGBC.gridheight = 1;
		menuGBC.gridwidth = 2;
		menuGBC.fill = GridBagConstraints.HORIZONTAL;
		menuPanel.add(grayscaleLabel, menuGBC);
		
		
		menuGBC.gridx = 2;
		menuGBC.gridy = 1;
		menuGBC.gridheight = 1;
		menuGBC.gridwidth = 1;
		menuGBC.fill = GridBagConstraints.HORIZONTAL;
		menuPanel.add(bw_CheckBox, menuGBC);
		
		menuGBC.gridx = 0;
		menuGBC.gridy = 2;
		menuGBC.gridheight = 1;
		menuGBC.gridwidth = 2;
		menuGBC.fill = GridBagConstraints.HORIZONTAL;
		menuPanel.add(medianButton, menuGBC);
		
		menuGBC.gridx = 2;
		menuGBC.gridy = 2;
		menuGBC.gridheight = 1;
		menuGBC.gridwidth = 1;
		menuGBC.fill = GridBagConstraints.HORIZONTAL;
		menuPanel.add(medianField, menuGBC);
		
		//ALWAYS KEEP THIS BUTTON AT THE END OF THE LIST
		menuGBC.gridx = 0;
		menuGBC.gridy = 3;
		menuGBC.gridheight = 1;
		menuGBC.gridwidth = 3;
		menuGBC.fill = GridBagConstraints.HORIZONTAL;
		menuPanel.add(resetButton, menuGBC);
	}
}
