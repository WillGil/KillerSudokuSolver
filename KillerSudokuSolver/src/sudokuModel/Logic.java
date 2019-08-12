package sudokuModel;


import java.util.Map;


/**
 * A class that is used to represent information that is fed back to the user
 * 
 * @author William Gilgunn
 * @version 2.0
 *
 */
public class Logic {

	private int helpLevel;
	private String helpText;
	private Cell changedCell;
	private Map<Cell, CellAdjustment> cellsAdjustments;

	/**
	 * Constructor that takes a int helpLevel (1 - easy, 2 - mediocre and 3 -
	 * experienced) a string and the cell that has been changed
	 * 
	 * @param helpLevel
	 *            the level of help the user needs
	 * @param helpText
	 *            the text that explains the rationality behind doing a move.
	 * @param changedCell
	 *            the cell that has been changed
	 * 
	 * 
	 *            Use for committed values
	 */
	
	public Logic(int helpLevel, String helpText, Cell changedCell) {
		if (helpLevel < 1 || helpLevel > 3)
			throw new IllegalArgumentException("Invalid helplevel (1, 2, 3)");

		this.helpLevel = helpLevel;
		this.helpText = helpText;
		this.changedCell = changedCell;
		cellsAdjustments = null;
	}

	/**
	 * Constructor that takes a int helpLevel (1 - easy, 2 - mediocre and 3 -
	 * experienced) a string and a list of cells that have been changed.
	 * 
	 * @param helpLevel
	 *            the level of help requested by the user
	 * @param helpText
	 *            the text displayed to help the user understand why a step was
	 *            taken
	 * @param cellsChanged
	 *            the cells changed in the step
	 */
	public Logic(int helpLevel, String helpText,Map<Cell, CellAdjustment> cellsAdjustments) {
		if (helpLevel < 1 || helpLevel > 3)
			throw new IllegalArgumentException("Invalid helplevel (1,2,3)");
		this.helpLevel = helpLevel;
		this.helpText = helpText;
		this.cellsAdjustments = cellsAdjustments;
		this.changedCell = null;
	}

	/**
	 * Get the level of help of the rationality behind a step
	 * 
	 * @return the help level
	 */
	public int getHelpLevel() {
		return helpLevel;
	}

	/**
	 * Get the help text as to why a step was taken
	 * 
	 * @return a string that explains why a step was taken.
	 */
	public String getHelpText() {
		return helpText;
	}

	/**
	 * Get a list of cells that were changed in the step
	 * 
	 * @return the cell that was changed.
	 */
	public Cell getChangedCell() {
		return changedCell;
	}

	/**
	 * Set the cell that was changed in the step
	 * 
	 * @param cellChanged
	 *            setting the cell changed in the step
	 */
	public void setCellChanged(Cell cellChanged) {
		this.changedCell = cellChanged;
	}

	
	public Map<Cell, CellAdjustment> getCellsAdjustments() {
		return cellsAdjustments;
	}



	

}
