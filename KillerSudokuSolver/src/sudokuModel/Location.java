package sudokuModel;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that represents the location of a cell on the killer sudoku board.
 * 
 * @author William Gilgunn
 * @version 2.0
 *
 */
public class Location {
	private int col, row, noNet;
	private String stringName;

	// Stores a map of all instances of location class.
	private static final Map<String, Location> CELL_LOCATIONS = new HashMap<>();

	/**
	 * A constructor that takes a integer row , column and a string which is the
	 * name of the cell and creates a Location object
	 * 
	 * @param row
	 *            the row of the location
	 * @param col
	 *            the column of the location
	 * @param nameOfCell
	 *            the string name of the cell
	 * @throws Exception
	 */
	private Location(int row, int col, String nameOfCell) {

		boolean invalidRow = (row < 1 || row > 9) ? true : false;
		boolean invalidCol = (col < 1 || col > 9) ? true : false;

		if (invalidRow)
			throw new IllegalArgumentException("Invalid row!");

		if (invalidCol)
			throw new IllegalArgumentException("Invalid column!");

		this.row = row;
		this.col = col;
		stringName = nameOfCell;
		noNet = determineNoNet(row, col);
	}

	/**
	 * Method that gets the instance of the location object using he row and
	 * column number
	 * 
	 * @param row
	 *            the number of the row
	 * @param column
	 *            the number of the column
	 * @return the Location instance.
	 */
	public static Location getInstance(int row, int column) {
		String stringRep = row + ", " + column;
		Location currentLocation = CELL_LOCATIONS.get(stringRep);
		if (currentLocation == null) { // Add new instance
			currentLocation = new Location(row, column, stringRep);
			CELL_LOCATIONS.put(stringRep, currentLocation);
			return currentLocation;
		}
		return currentLocation;
	}

	/**
	 * Get the nonet that the location is situated in.
	 * 
	 * @param currentRow
	 *            the nonet row looking at
	 * @param currentCol
	 *            the nonet column looking at.
	 * @return the nonet index that the location is situated in.
	 */
	private int determineNoNet(int currentRow, int currentCol) {
		if (currentCol <= 3) { // 1, 3, 7 nonets
			if (currentRow <= 3)
				return 1;
			else if (currentRow <= 6)
				return 4;
			else if (currentRow <= 9)
				return 7;

		} else if (currentCol <= 6) { // 2, 5, 8 nonets
			if (currentRow <= 3)
				return 2;
			else if (currentRow <= 6)
				return 5;
			else if (currentRow <= 9)
				return 8;
		} else if (currentCol <= 9) { // 3, 6, 9 nonets
			if (currentRow <= 3)
				return 3;
			else if (currentRow <= 6)
				return 6;
			else if (currentRow <= 9)
				return 9;
		}
		return 0;
	}

	/**
	 * Override hashcode method so equals can be overriden as well.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + noNet;
		result = prime * result + row;
		return result;
	}

	/**
	 * Override equals so comparison of location objects can work properly.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (col != other.col)
			return false;
		if (noNet != other.noNet)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

	/**
	 * Get the column that the location object is situated in
	 * 
	 * @return the column that the location is situated in.
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Get the row that the location object is situated in.
	 * 
	 * @return the column that the location is situated in.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Get the string representation of the location.
	 * 
	 * @return the string representation of the location.
	 */
	public String getStringName() {
		return stringName;
	}

	/**
	 * Set the column on the location
	 * 
	 * @param col
	 *            the column of the location.
	 */
	public void setCol(int col) {
		this.col = col;
	}

	/**
	 * Set the row of the location
	 * 
	 * @param row
	 *            the row of the location
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Set the string representation
	 * 
	 * @param stringName
	 *            the string representation.
	 */
	public void setStringName(String stringName) {
		this.stringName = stringName;
	}

	/**
	 * Get the nonet which the location is situated within
	 * 
	 * @return the index of the nonet that the lcoation is situated in
	 */
	public int getNoNet() {
		return noNet;
	}

	/**
	 * Overriding tostring to print location information.
	 */
	@Override
	public String toString() {
		return "R" + row + " C" + col;
	}

}
