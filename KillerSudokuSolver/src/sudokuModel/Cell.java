package sudokuModel;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A class that represents a cell on the killer sudoku board.
 * 
 * @author William Gilgunn
 * @version 2.0
 *
 */
public class Cell {
	private final static int DEFAULT_VALUE = 0;
	private final static int BOARD_SIZE = 9;
	private boolean committed;
	private int valueCommitted;
	private Location cellLocation;
	private Set<Integer> possibleValues;
	public static int cellsMadeCount = 0; // Validation and testing

	/**
	 * Constructor that takes a location object and creates a cell object
	 * 
	 * @param locationCell
	 *            location of the cell.
	 */
	public Cell(Location locationCell) {
		cellsMadeCount++;
		valueCommitted = DEFAULT_VALUE;
		committed = false;
		cellLocation = locationCell;
		possibleValues = addPossibleValues();
	}

	/**
	 * Method that gets all possible values for a new cell (1,...,9)
	 * 
	 * @return return a treeset
	 */
	private Set<Integer> addPossibleValues() {
		Set<Integer> result = new TreeSet<>();
		for (int x = 1; x <= BOARD_SIZE; x++) {
			result.add(x);
		}
		return result;
	}

	/**
	 * Sets the value to be committed to the cell.
	 * 
	 * @param valueCommitted
	 *            the value to be committed.
	 */

	public void setValueCommitted(int valueCommitted) {
		if (!committed) { // Check to ensure value has not been committed
			this.valueCommitted = valueCommitted;
			possibleValues = new TreeSet<>();
			possibleValues.add(valueCommitted); // Only possible value
			committed = true; // Cannot uncommit after committed.
		}
	}

	/**
	 * Returns a boolean value to determine if possibleValues is one element
	 * 
	 * @return a boolean value to determine if possibleValues is one element.
	 */
	public boolean isOnlyOnePossibility() {
		return possibleValues.size() == 1;

	}

	/**
	 * Method that checks to see if there is one possible value
	 * 
	 * @return checks the possible values list for one element.
	 */
	public int getOnlyPossibility() {
		int possibleValue = 0;
		if (isOnlyOnePossibility()) { // Check if only one value possible
			for (Integer value : possibleValues)
				possibleValue = value;
		}
		return possibleValue;
	}

	/**
	 * Determine if the cell is committed
	 * 
	 * @return if the cell is committed
	 */
	public boolean isCommitted() {
		return committed;
	}

	/**
	 * Get the value that has been committed
	 * 
	 * @return the value that has been committed
	 */
	public int getValueCommitted() {
		return valueCommitted;
	}

	/**
	 * Get the location of the cell.
	 * 
	 * @return the location of the cell
	 */
	public Location getCellLocation() {
		return cellLocation;
	}

	/**
	 * Get a set of possible values
	 * 
	 * @return a set of possible values.
	 */
	public Set<Integer> getPossibleValues() {
		return possibleValues;
	}

	/**
	 * Set the location of the cell
	 * 
	 * @param cellLocation
	 *            the location of the cell
	 */
	public void setCellLocation(Location cellLocation) {
		this.cellLocation = cellLocation;
	}

	/**
	 * Set possible values of the cell
	 * 
	 * @param possibleValues
	 *            the set of possible values.
	 */
	public void setPossibleValues(Set<Integer> possibleValues) {
		if (!committed)
			this.possibleValues = possibleValues;
	}

	/**
	 * Remove a possible value of the cell if its been confirmed that it cannot
	 * be valid.
	 * 
	 * @param removeNum
	 *            the number to be removed from the possible values.
	 */
	public void removePossibleValues(int removeNum) {
		if (!committed) {
			for (Iterator<Integer> iter = possibleValues.iterator(); iter.hasNext();) {
				int currentValue = iter.next();
				if (currentValue == removeNum) {
					iter.remove();
				}
			}
		}
	}

	/**
	 * Overriding toString method to print information about the cell if needed.
	 */
	@Override
	public String toString() {
		return "- " + cellLocation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cellLocation == null) ? 0 : cellLocation.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (cellLocation == null) {
			if (other.cellLocation != null)
				return false;
		} else if (!cellLocation.equals(other.cellLocation))
			return false;
		return true;
	}

	

}
