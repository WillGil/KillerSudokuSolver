package sudokuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a sudokugrid on a sudoku board.
 * 
 * @author William Gilgunn
 * @version 2.0
 *
 */
public class SudokuGrid {

	private Cell[][] sudokuCells;
	private final static int SIZE_GRID = 9;

	/**
	 * Constructor for a normal sudoku grid. It sets up all the cells on the
	 * board
	 */
	public SudokuGrid() {
		sudokuCells = new Cell[SIZE_GRID][SIZE_GRID]; // Creating grid

		for (int x = 0; x < SIZE_GRID; x++) {
			for (int y = 0; y < SIZE_GRID; y++) {
				sudokuCells[x][y] = new Cell(Location.getInstance(x + 1, y + 1));
			}
		}

	}

	/**
	 * Return a 2d array of sudoku cells.
	 * 
	 * @return a 2d array of sudoku cells.
	 */
	public Cell[][] getSudokuCells() {
		return sudokuCells;
	}

	/**
	 * Get a cell on the sudoku board
	 * 
	 * @param cellLocation
	 *            the location of the cell trying to find
	 * @return the cell which is at the passed location
	 */
	public Cell getCell(Location cellLocation) {
		return sudokuCells[cellLocation.getRow() - 1][cellLocation.getCol() - 1];
	}

	/**
	 * Returns a list of cells for the cells on the specified row
	 * 
	 * @param rowIdx
	 *            row of cells you want to obtain
	 * @return a list of cells on the row
	 */

	public List<Cell> getRow(int rowIdx) {
		List<Cell> cellsRow = new ArrayList<>();
		for (int x = 0; x < SIZE_GRID; x++) { // run through row
			cellsRow.add(sudokuCells[rowIdx - 1][x]); // run through all
		}
		return cellsRow;
	}

	/**
	 * Returns a list of cells for the cells on the specified column
	 * 
	 * @param colIdx
	 *            the column of cells you want to obtain
	 * @return a list of cells on the specified column
	 */
	public List<Cell> getCols(int colIdx) {
		List<Cell> cellsCol = new ArrayList<>();
		for (int x = 0; x < SIZE_GRID; x++) {
			cellsCol.add(sudokuCells[x][colIdx - 1]);
		}
		return cellsCol;
	}

	/**
	 * Get the cells in the nonet of the specified index
	 * 
	 * @param noNetIdx
	 *            the index of the nonet where you want to find cells
	 * @return A list of cells within the speicifed nonet.
	 */
	public List<Cell> getNoNetCells(int noNetIdx) {
		/* Run through all cells on board */
		List<Cell> cellsNonet = new ArrayList<>();

		for (int x = 0; x < SIZE_GRID; x++) {
			for (int y = 0; y < SIZE_GRID; y++) {
				if (sudokuCells[x][y].getCellLocation().getNoNet() == noNetIdx)
					cellsNonet.add(sudokuCells[x][y]);

			}
		}
		return cellsNonet;
	}

	/**
	 * Get a subset of the row based on the start index and end index passed in
	 * 
	 * @param rowNum
	 *            the row you want to look at
	 * @param startIdxCol
	 *            the index of the cells you want to start looking at
	 * @param endIdxCol
	 *            the index of the last cell you want to get
	 * @return a list of cells in the specified row and constraints
	 */

	public List<Cell> getRowSubset(int rowNum, int startIdxCol, int endIdxCol) {
		List<Cell> cellsRowSubset = new ArrayList<>();

		for (int x = startIdxCol; x <= endIdxCol; x++) {
			cellsRowSubset.add(sudokuCells[rowNum - 1][x]);
		}
		return cellsRowSubset;
	}

	/**
	 * 
	 * Get a subset of the column based on the start index and end index passed
	 * in
	 * 
	 * @param colNum
	 *            the column you want to look at
	 * @param startIdxRow
	 *            the starting index of the cells you want to look at
	 * @param endIdxRow
	 *            the end index of the cells you want to look at.
	 * @return the list of cells in the specified region
	 */

	public List<Cell> getColumnSubset(int colNum, int startIdxRow, int endIdxRow) {
		List<Cell> cellsColumnSubset = new ArrayList<>();
		for (int x = startIdxRow; x <= endIdxRow; x++) {
			cellsColumnSubset.add(sudokuCells[x][colNum - 1]);
		}
		return cellsColumnSubset;
	}

	public int numberOfsolvedCells() {
		int count = 0;
		for (int row = 1; row <= SIZE_GRID; row++) {
			for (int col = 1; col <= SIZE_GRID; col++) {
				if (getCell(Location.getInstance(row, col)).isCommitted()) {
					count++;
				}

			}
		}
		return count;

	}

	public int candidatesRemaining() {
		int count = 0;
		for (int row = 1; row <= SIZE_GRID; row++) {
			for (int col = 1; col <= SIZE_GRID; col++) {
				if (!getCell(Location.getInstance(row, col)).isCommitted()) {
					int cellCandidates = getCell(Location.getInstance(row, col)).getPossibleValues().size();
					count += cellCandidates;
				}

			}
		}
		return count;
	}

	public void setSudokuCells(Cell[][] sudokuCells) {
		this.sudokuCells = sudokuCells;
	}

	
}
