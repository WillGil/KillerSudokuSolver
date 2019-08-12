package sudokuView;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * The Interaction Panel which will hold all the buttons the user can interact
 * with the solve the sudoku puzzle
 * 
 * @author William Gilgunn
 * @version 2.0
 *
 */
public class InteractionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JButton solveFully, solveSteps, clearSudoku;
	private JLabel labelForSkill, labelForCandidates, labelForSolvedCells;
	private JCheckBox detailedHelp;
	private JTextArea solutionArea;
	private JRadioButton easyHelp, mediumHelp, experiencedHelp;
	private ButtonGroup skillLevel;
	private final Color colourMainBackground = new Color(217, 217, 217);
	private final Color colorButtons = new Color(198, 232, 255);
	private final Color colorButtonPanel = new Color(238, 238, 238);
	private JTextField candidatesRemainingInGame, cellsSolved;

	private final Font buttonsPanelText = new Font("Helvetica", Font.BOLD, 12);
	private final Font textAreaBorder = new Font("Helvetica", Font.PLAIN, 12);
	private final Font textAreaText = new Font("Helvetica", Font.PLAIN, 14);

	public InteractionPanel() {
		/* Set layout and create constraints */
		this.setLayout(new GridBagLayout());
		GridBagConstraints cst = new GridBagConstraints();
		cst.anchor = GridBagConstraints.NORTH;

		// Set border
		setBorder(new EmptyBorder(15, 15, 15, 15));

		// add Solve button to the panel
		solveFully = new JButton("Solve");
		solveFully.setBackground(colorButtons);
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridx = 0;
		cst.gridy = 0;

		this.add(solveFully, cst);

		// Add SolveSteps button to the panel
		solveSteps = new JButton("Step");
		solveSteps.setBackground(colorButtons);

		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridx = 1;
		cst.gridy = 0;

		this.add(solveSteps, cst);

		// Add clear to the panel
		clearSudoku = new JButton("Clear");
		clearSudoku.setBackground(colorButtons);

		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridx = 2;
		cst.gridy = 0;

		this.add(clearSudoku, cst);
		// Add CheckBox to the panel
		detailedHelp = new JCheckBox("Detailed Help");
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridx = 3;
		cst.gridy = 0;
		this.add(detailedHelp, cst);

		/* Add skill level label */
		labelForSkill = new JLabel(" Player Level:");
		labelForSkill.setFont(buttonsPanelText);
		labelForSkill.setOpaque(true);
		labelForSkill.setBackground(colorButtonPanel);
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 0;
		cst.gridy = 1;
		this.add(labelForSkill, cst);

		/* Add radiobuttons */
		easyHelp = new JRadioButton("New Player");
		mediumHelp = new JRadioButton("Average Player");
		experiencedHelp = new JRadioButton("Experienced Player");

		/* Add buttons to button group */
		skillLevel = new ButtonGroup();
		skillLevel.add(easyHelp);
		skillLevel.add(mediumHelp);
		skillLevel.add(experiencedHelp);

		/* Add to panel */
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridx = 1;
		cst.gridy = 1;
		this.add(easyHelp, cst);
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridx = 2;
		cst.gridy = 1;
		this.add(mediumHelp, cst);
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridx = 3;
		cst.gridy = 1;
		this.add(experiencedHelp, cst);

		labelForCandidates = new JLabel(" Candidates Remaining: ");
		labelForCandidates.setOpaque(true);
		labelForCandidates.setBackground(colorButtonPanel);
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 0;
		cst.gridy = 2;
		this.add(labelForCandidates, cst);
		
		
		candidatesRemainingInGame = new JTextField("729");
		candidatesRemainingInGame.setFont(new Font("Arial", Font.BOLD, 12));
		candidatesRemainingInGame.setBorder(null);
		candidatesRemainingInGame.setHorizontalAlignment(JLabel.CENTER);
		candidatesRemainingInGame.setEditable(false);
		candidatesRemainingInGame.setOpaque(true);
		candidatesRemainingInGame.setBackground(colorButtonPanel);
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 1;
		cst.gridy = 2;
		this.add(candidatesRemainingInGame, cst);
		
		labelForSolvedCells = new JLabel(" Solved Cells: ");
		labelForSolvedCells.setOpaque(true);
		labelForSolvedCells.setBackground(colorButtonPanel);
		
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 2;
		cst.gridy = 2;
		this.add(labelForSolvedCells, cst);
		
		
		
		cellsSolved = new JTextField("0");
		cellsSolved.setFont(new Font("Arial", Font.BOLD, 13));
		cellsSolved.setBorder(null);
		cellsSolved.setHorizontalAlignment(JLabel.CENTER);
		cellsSolved.setEditable(false);
		cellsSolved.setOpaque(true);
		cellsSolved.setBackground(colorButtonPanel);
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 3;
		cst.gridy = 2;
		this.add(cellsSolved, cst);
		
		/*
		 * Intially the radio buttons are not enabled
		 */
		easyHelp.setEnabled(false);
		mediumHelp.setEnabled(false);
		experiencedHelp.setEnabled(false);

		cst.weightx = cst.weighty = 1.0;
		// add text box to to the panel
		solutionArea = new JTextArea(45, 30);
		solutionArea.setEditable(false);
		solutionArea.setToolTipText("Solution will be displayed here.");
		solutionArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
				"Solution Area", TitledBorder.LEFT, TitledBorder.TOP, textAreaBorder, Color.BLACK));
		solutionArea.setFont(textAreaText);

		// Wrap text
		solutionArea.setLineWrap(true);
		solutionArea.setWrapStyleWord(true);

		JScrollPane scrollPane = new JScrollPane(solutionArea);

		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridwidth = 4;
		cst.gridx = 0;
		cst.gridy = 3;

		this.add(scrollPane, cst);

		// Set colour
		setBackground(colourMainBackground);
	}


	public JTextField getCandidatesRemainingInGame() {
		return candidatesRemainingInGame;
	}


	public JTextField getCellsSolved() {
		return cellsSolved;
	}

	/**
	 * Get solve fully button
	 * 
	 * @return solve gully button
	 */
	public JButton getSolveFully() {
		return solveFully;
	}

	/**
	 * Get solve steps button
	 * 
	 * @return solve steps button
	 */
	public JButton getSolveSteps() {
		return solveSteps;
	}

	/**
	 * get detailed help checkbox
	 * 
	 * @return detailed help checkbox
	 */
	public JCheckBox getDetailedHelp() {
		return detailedHelp;
	}

	/**
	 * Solution area box
	 * 
	 * @return solution area box
	 */
	public JTextArea getSolutionArea() {
		return solutionArea;
	}

	/**
	 * Get easy help radio button
	 * 
	 * @return easy help radio button
	 */
	public JRadioButton getEasyHelp() {
		return easyHelp;
	}

	/**
	 * Get medium help radio button
	 * 
	 * @return medium help radio button
	 */
	public JRadioButton getMediumHelp() {
		return mediumHelp;
	}

	/**
	 * Get experienced help button
	 * 
	 * @return experienced help button
	 */
	public JRadioButton getExperiencedHelp() {
		return experiencedHelp;
	}

	/**
	 * Get the button group which determines the skill level of the user
	 * 
	 * @return the button group which determines the skill level of the user
	 */
	public ButtonGroup getSkillLevel() {
		return skillLevel;
	}

	public JButton getClearSudoku() {
		return clearSudoku;
	}

	public JLabel getLabelForSkill() {
		return labelForSkill;
	}

}
