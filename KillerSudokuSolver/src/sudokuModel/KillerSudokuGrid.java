package sudokuModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class that represents the killer sudoku board. As a Killer Sudoku board
 * "IS-A" sudoku board with extra functionality it extends SudokuGrid
 * 
 * @author William Gilgunn
 * @version 2.0
 */
public class KillerSudokuGrid extends SudokuGrid {
	private Set<Cage> cages;
	private static final int SIZE_GRID = 9;
	private boolean solved;

	public KillerSudokuGrid(Set<Cage> cages) {
		super(); // Create sudoku board;
		this.cages = cages; // Assign cages
		solved = false;
	}

	/**
	 * Returns a set of cells in a cage.
	 * 
	 * @param cage
	 *            the cage that you want to find cells for
	 * @return a set of cells i the cage.
	 */
	public Set<Cell> getCellsInCage(Cage cage) {
		Set<Cell> cellsSet = new HashSet<Cell>();
		for (Location locCell : cage.getLocation()) {
			cellsSet.add(super.getCell(locCell));
		}
		return cellsSet;
	}

	/**
	 * Given a set of Cells returns the cage they are situated within
	 * 
	 * @param cellsInCage
	 *            a set of locations
	 * @return the cage that the cells are situated within.
	 */
	public Cage getCage(Set<Cell> cellsCage) {
		for (Cage currentCage : cages) {
			Set<Cell> cellsCurrentCage = getCellsInCage(currentCage);
			if (cellsCurrentCage.equals(cellsCage))
				return currentCage;
		}
		return null;

	}

	/**
	 * Given a location returns the cage that the location is situated within.
	 * 
	 * @param locationCage
	 *            the location of the cell
	 * @return the cage the location is within.
	 */

	public Cage getCage(Cell cell) {
		for (Cage currentCage : cages) {
			Set<Cell> currentCageCells = getCellsInCage(currentCage);
			if (currentCageCells.contains(cell))
				return currentCage;
		}
		return null;
	}

	/**
	 * Returns a set of the cages on the board
	 * 
	 * @return a set of the cages on the board.
	 */
	public Set<Cage> getCages() {
		return cages;
	}

	/**
	 * Method to set the cages on the board
	 * 
	 * @param cages
	 *            the set of cages to set.
	 */
	public void setCages(Set<Cage> cages) {
		this.cages = cages;
	}

	public String printGrid() {
		StringBuilder gridString = new StringBuilder();

		for (int row = 1; row <= SIZE_GRID; row++) {
			for (int col = 1; col <= SIZE_GRID; col++) {
				Cell currentCell = getCell(Location.getInstance(row, col));
				if (currentCell.isCommitted()) {
					gridString.append(" " + currentCell.getValueCommitted() + " ");
				} else {
					gridString.append(" -" + " ");
				}
			}
			gridString.append(System.getProperty("line.separator"));
		}
		return gridString.toString();
	}

	public boolean isGridDefault() {
		Set<Integer> defaultValues = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
		for (int row = 1; row < SIZE_GRID; row++) {
			for (int col = 1; col < SIZE_GRID; col++) {
				Cell currentCell = getCell(Location.getInstance(row, col));
				if (currentCell.isCommitted() || !currentCell.getPossibleValues().equals(defaultValues)) {
					return false;
				}
			}
		}

		return true;
	}

	public String printFileGrid() {
		StringBuilder result = new StringBuilder();
		List<Cage> cagesList = new ArrayList<Cage>(cages);

		for (int cageCount = 0; cageCount < cagesList.size(); cageCount++) {
			Cage currentCage = cagesList.get(cageCount);
			result.append(currentCage.getLocation().size() + " " + currentCage.getTotalValue());
			result.append(System.getProperty("line.separator"));

			List<Location> locationList = new ArrayList<Location>(currentCage.getLocation());
			for (int locationCount = 0; locationCount < locationList.size(); locationCount++) {
				Location currentCageLoc = locationList.get(locationCount);
				if ((cageCount == cagesList.size() - 1) && (locationCount == locationList.size() - 1)) {
					result.append(currentCageLoc.getRow() + " " + currentCageLoc.getCol());
				} else {
					result.append(currentCageLoc.getRow() + " " + currentCageLoc.getCol());
					result.append(System.getProperty("line.separator"));
				}
			}

		}
		return result.toString();
	}

	public String printCellStates() {
		StringBuilder result = new StringBuilder();
		for (int row = 1; row <= SIZE_GRID; row++) {
			for (int col = 1; col <= SIZE_GRID; col++) {
				Cell currentCell = getCell(Location.getInstance(row, col));
				if (currentCell.isCommitted()) {
					result.append(row + " " + col + " y" + System.getProperty("line.separator")
							+ currentCell.getValueCommitted());
				} else {
					result.append(row + " " + col + " n" + System.getProperty("line.separator")
							+ currentCell.getPossibleValues());
				}
				if (!(row == SIZE_GRID && col == SIZE_GRID))
					result.append(System.getProperty("line.separator"));
			}
		}
		return result.toString();

	}

	public boolean isGridSolved() {
		for (int row = 1; row <= SIZE_GRID; row++) {
			for (int col = 1; col <= SIZE_GRID; col++) {
				Cell currentCell = getCell(Location.getInstance(row, col));
				if (!currentCell.isCommitted()) {
				 return false;
				}
			}
		}
		solved = true;
		return true;
	}

}
