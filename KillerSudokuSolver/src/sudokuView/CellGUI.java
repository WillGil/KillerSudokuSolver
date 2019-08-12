package sudokuView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import sudokuModel.*;

/**
 * A class that represents a cell on the sudoku board/
 * 
 * @author William Gilgunn
 * @version 2.0
 */
public class CellGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int SIZE_VALUES = 9;
	private static final int POSSIBLE_VALUES_SIZE = 3;
	private static final int CELL_SIZE = 70;

	private Cell sudokuCell;
	/* Fonts */
	private Font fontCage = new Font("Times New Roman", Font.CENTER_BASELINE, 14);
	private Font fontSolved = new Font("Arial", Font.BOLD, 25);
	private Font fontPossibleValues = new Font("Times New Roman", Font.PLAIN, 14);

	/* Borders */
	private final Border lineBorder = new MatteBorder(1, 1, 1, 1, Color.BLACK);
	private final Border emptyBorder = BorderFactory.createEmptyBorder(18, 6, 6, 6);

	/**
	 * A constructor that takes a sudoku cell a colour and a hint of a cage and
	 * produces a CellGUI object. (Use this constructor if this is the cell that
	 * will display the hint of the cage)
	 * 
	 * @param sudokuCell
	 *            the cell being displayed.
	 * @param colour
	 *            the colour of the cell
	 * @param hintCage
	 *            the hint of the cage
	 */
	public CellGUI(Cell sudokuCell, Color colour, int hintCage) {
		setBackground(colour);// set colour
		setLayout(new GridLayout(POSSIBLE_VALUES_SIZE, POSSIBLE_VALUES_SIZE));
		this.sudokuCell = sudokuCell;

		TitledBorder titleBorder = new TitledBorder(new EmptyBorder(0, 0, 0, 0), String.valueOf(hintCage));
		titleBorder.setTitleFont(fontCage);
		Border combinedBorder = new CompoundBorder(lineBorder, titleBorder);

		this.setBorder(combinedBorder);

		createCell();
	}

	/**
	 * A constructor for subsequent cells of the cage which do not display the
	 * hint within them
	 * 
	 * @param sudokuCell
	 *            the cell being displayed
	 * @param colour
	 *            the colour of the cell.
	 */
	public CellGUI(Cell sudokuCell, Color colour) {
		setBackground(colour); // set background
		setLayout(new GridLayout(POSSIBLE_VALUES_SIZE, POSSIBLE_VALUES_SIZE));
		this.sudokuCell = sudokuCell;

		Border border = new CompoundBorder(lineBorder, emptyBorder);
		this.setBorder(border);

		createCell();
	}

	/**
	 * Method to setup the cells and display the relevant information to the
	 * user.
	 */
	private void createCell() {
		/* Is the cell committed and it's the only possible value */
		if (sudokuCell.isCommitted() && sudokuCell.isOnlyOnePossibility()) {
			setLayout(new BorderLayout());
			String value = String.valueOf(sudokuCell.getValueCommitted());
			JLabel valueLabel = new JLabel(value);
			valueLabel.setFont(fontSolved);
			valueLabel.setHorizontalAlignment(JLabel.CENTER);
			valueLabel.setVerticalAlignment(JLabel.CENTER);
			valueLabel.setVisible(true);
			add(valueLabel, BorderLayout.CENTER);

		} else {
			for (int i = 1; i <= SIZE_VALUES; i++) {
				JLabel value = new JLabel(String.valueOf(i));
				value.setHorizontalAlignment(JLabel.CENTER);
				value.setFont(fontPossibleValues);
				/* Check for value in possible values */
				if (!sudokuCell.getPossibleValues().contains(i)) {
					value.setVisible(false);
					add(value);
					continue;
				}
				value.setVisible(true);
				add(value);
			}

		}
		super.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
	}

	public void showChanges(Set<Integer> valuesRemoved, Set<Integer> valuesCausing, boolean isCellCausingChange) {

		/* Is the cell committed and it's the only possible value */
		if (sudokuCell.isCommitted() && sudokuCell.isOnlyOnePossibility()) {
			if (valuesCausing.contains(sudokuCell.getOnlyPossibility()) && valuesRemoved.isEmpty()
					&& isCellCausingChange) {
				setBackground(Color.GREEN);
				setLayout(new BorderLayout());
				String value = String.valueOf(sudokuCell.getValueCommitted());
				JLabel valueLabel = new JLabel(value);
				valueLabel.setFont(fontSolved);
				valueLabel.setHorizontalAlignment(JLabel.CENTER);
				valueLabel.setVerticalAlignment(JLabel.CENTER);
				valueLabel.setVisible(true);
				add(valueLabel, BorderLayout.CENTER);
				
			} else {
				setLayout(new BorderLayout());
				String value = String.valueOf(sudokuCell.getValueCommitted());
				JLabel valueLabel = new JLabel(value);
				valueLabel.setFont(fontSolved);
				valueLabel.setHorizontalAlignment(JLabel.CENTER);
				valueLabel.setVerticalAlignment(JLabel.CENTER);
				valueLabel.setVisible(true);
				add(valueLabel, BorderLayout.CENTER);
			}

		} else {
			for (int i = 1; i <= SIZE_VALUES; i++) {
				if (valuesRemoved.contains(i)) {
					JLabel value = new JLabel(String.valueOf(i));
					value.setHorizontalAlignment(JLabel.CENTER);
					value.setFont(fontPossibleValues);
					value.setVisible(true);
					value.setOpaque(true);
					value.setBackground(Color.YELLOW);
					value.setMaximumSize(new Dimension(10, 10));

					add(value);
					continue;
				} else {
					if (valuesCausing != null) {
						if (valuesCausing.contains(i)) {
							JLabel value = new JLabel(String.valueOf(i));
							value.setHorizontalAlignment(JLabel.CENTER);
							value.setFont(fontPossibleValues);
							value.setVisible(true);
							value.setOpaque(true);
							value.setBackground(Color.GREEN);
							value.setMaximumSize(new Dimension(10, 10));

							add(value);
							continue;
						}
					}

					JLabel value = new JLabel(String.valueOf(i));
					value.setHorizontalAlignment(JLabel.CENTER);
					value.setFont(fontPossibleValues);

					/* Check for value in possible values */
					if (!sudokuCell.getPossibleValues().contains(i)) {
						value.setVisible(false);
						add(value);
						continue;
					}
					value.setVisible(true);
					add(value);
				}
			}
		}

		super.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
	}

	/**
	 * Get the sudoku cell that is currently being modified
	 * 
	 * @return the current sudoku cell.
	 */
	public Cell getSudokuCell() {
		return sudokuCell;
	}

	/**
	 * Remove cell implmenetation from the grid. Used for when the possible
	 * values need to be removed.
	 */
	public void cleanUp() {
		this.removeAll();
	}

	public void setCommittedColour() {
		setBackground(Color.GREEN);
	}
}
