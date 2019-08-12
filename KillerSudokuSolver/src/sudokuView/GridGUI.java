package sudokuView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import sudokuModel.Cage;
import sudokuModel.Cell;
import sudokuModel.KillerSudokuGrid;
import sudokuModel.Location;
import sudokuModel.Logic;

/**
 * A class that represents the sudoku grid GUI.
 * 
 * @author William Gilgunn
 * @version 2.0
 *
 * 
 */
public class GridGUI extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int SIZE = 9;
	private static final int SIZE_GRID = 3;
	private static final int NONET_SIZE = 210;

	/* Colours needed for cages */
	private final static Color YELLOW = new Color(255, 253, 152);
	private final static Color PINK = new Color(248, 207, 223);
	private final static Color BLUE = new Color(203, 232, 250);
	private final static Color GREEN = new Color(207, 231, 153);
	private final static Color PURPLE = new Color(231, 204, 252);

	/* Variables needed */
	private KillerSudokuGrid grid; // The grid to display
	private Set<Cage> cages; // Cages on the grid
	private List<CellGUI> listCellGUI; // List of cell GUI elements.
	private Map<Cage, Color> cageColourMap; // Map cage to colour of it

	/**
	 * A GridGUI constructor which takes a killer sudoku grid and produces a
	 * gridGUI object.
	 * 
	 * @param sudokuGrid
	 *            the sudoku grid working with.
	 */
	public GridGUI(KillerSudokuGrid sudokuGrid) {
		grid = sudokuGrid;
		cages = sudokuGrid.getCages();
		listCellGUI = new ArrayList<CellGUI>();
		cageColourMap = new HashMap<Cage, Color>();

		assignColours();
		setupGridGUI();

	}

	/**
	 * Method that sets up the grid GUI when its been called from the
	 * constructor.
	 */
	public void setupGridGUI() {
		listCellGUI = new ArrayList<CellGUI>(); // Just added
		setLayout(new GridLayout(SIZE_GRID, SIZE_GRID));
		Set<Cell> cageHintPresent = getCageHint();

		/*if (cageHintPresent.size() != Cage.numberOfCages) {
			System.out.println("Error has occured.");
			System.exit(0);
		}*/

		for (int i = 1; i <= SIZE; i++) {
			JPanel nonetPanel = new JPanel(new GridLayout(3, 3));
			nonetPanel.setPreferredSize(new Dimension(NONET_SIZE, NONET_SIZE));

			List<Cell> nonet = grid.getNoNetCells(i);

			for (Cell cell : nonet) {
				Cage cage = grid.getCage(cell);
				Color cageColour = cageColourMap.get(cage);
				if (cageHintPresent.contains(cell)) {
					CellGUI cellGui = new CellGUI(cell, cageColour, cage.getTotalValue());
					nonetPanel.add(cellGui);
					listCellGUI.add(cellGui);
				} else {
					CellGUI cellGui = new CellGUI(cell, cageColour);
					nonetPanel.add(cellGui);
					listCellGUI.add(cellGui);
				}
				nonetPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				add(nonetPanel);
			}
		}
		repaint();
		revalidate();

	}

	public void DisplayGridChanges(Logic logic) {
		for (Cell currentCell : logic.getCellsAdjustments().keySet()) {
			for (CellGUI cellGui : listCellGUI) {
				if (currentCell.equals(cellGui.getSudokuCell())) {
					Set<Integer> valuesChanged = logic.getCellsAdjustments().get(currentCell).getValuesRemoved();
					Set<Integer> valuesCausing = logic.getCellsAdjustments().get(currentCell).getValuesCausing();
					cellGui.cleanUp();
					cellGui.showChanges(valuesChanged, valuesCausing,
							logic.getCellsAdjustments().get(currentCell).isCellCausingChange());
				}
			}
		}
	}

	public void DisplaySingleCommittedCell(Logic logic) {
		Cell cellCommitted = logic.getChangedCell();
		if (cellCommitted == null) {
			throw new IllegalArgumentException("Cell is not committed!");
		}
		for (CellGUI cellGui : listCellGUI) {
			if (cellCommitted.equals(cellGui.getSudokuCell())) {
				cellGui.setCommittedColour();
			}
		}
	}

	public void changeGridDisplaying(KillerSudokuGrid grid) {
		removeAll();
		this.grid = grid;
		cages = grid.getCages();
		cageColourMap = new HashMap<>();
		assignColours();
		setupGridGUI();
	}

	/**
	 * Get the cell that has the lowest row and column and use it to represent
	 * the cage hint.
	 * 
	 * @return a set of cells that should represent the cage hint.
	 */
	private Set<Cell> getCageHint() {
		Set<Cell> cageHint = new HashSet<Cell>();

		for (Cage cage : cages) {
			/* Find highest up */
			int minRow = Integer.MAX_VALUE;

			Set<Cell> potentialHint = new HashSet<Cell>();
			for (Cell cell : grid.getCellsInCage(cage)) { // Get smallest row
				/* Find smallest row */
				int currentRow = cell.getCellLocation().getRow();
				if (currentRow < minRow)
					minRow = currentRow;
			}
			/*
			 * Add all cells in that cage that have the minimum row into a set
			 * to be checked
			 */
			int minCol = Integer.MAX_VALUE;
			for (Cell cell : grid.getCellsInCage(cage)) {
				if (cell.getCellLocation().getRow() == minRow)
					potentialHint.add(cell);
			}
			/*
			 * Check all cells with the minimum row for the minimum column.
			 */
			for (Cell cell : potentialHint) {
				if (cell.getCellLocation().getCol() < minCol) {
					minCol = cell.getCellLocation().getCol();
				}

			}

			cageHint.add(grid.getCell(Location.getInstance(minRow, minCol)));
		}

		return cageHint;

	}

	/**
	 * Method to assign colours to all of the cages on the board
	 */
	private void assignColours() {
		Map<Cage, Set<Cage>> neighbourCages = getNeighbourCages();
		for (Cage cage : cages) {
			assignColourCages(cage, neighbourCages);
		}
	}

	private void assignColourCages(Cage cage, Map<Cage, Set<Cage>> neighbourCages) {
		Color colour = YELLOW;
		Set<Cage> neighbours = neighbourCages.get(cage);
		boolean nullCheck = (neighbours == null) ? true : false;
		if (!nullCheck) {
			/* No colours have been used yet on the cells */
			boolean yellowUsed = false;
			boolean pinkUsed = false;
			boolean blueUsed = false;
			boolean greenUsed = false;

			/* Loop through all cages that are neighbours */
			for (Cage c : neighbours) {
				Color cageColour = cageColourMap.get(c);
				
			
				nullCheck = (cageColour == null) ? true : false;
				if (!nullCheck) {

					boolean yellowNeighbour = cageColour.equals(YELLOW);
					boolean pinkNeighbour = cageColour.equals(PINK);
					boolean blueNeighbour = cageColour.equals(BLUE);
					boolean greenNeighbour = cageColour.equals(GREEN);

					if (yellowNeighbour) {
						yellowUsed = true;
					} else if (pinkNeighbour) {
						pinkUsed = true;
					} else if (blueNeighbour) {
						blueUsed = true;
					} else if (greenNeighbour) {
						greenUsed = true;
					}

					/*
					 * Since initially no colours will be assigned it will be
					 * defaulted to yellow which was assigned above
					 */
					
					if (yellowUsed) {
						colour = PINK;
						if (pinkUsed) {
							colour = BLUE;
							if (blueUsed) {
								colour = GREEN;
								if (greenUsed) {
									colour = PURPLE;
								}
							}
						}

					}
				}

			}

		}
		cageColourMap.put(cage, colour);
	}

	/**
	 * Method that runs through all cages and cells and works out which cages
	 * are next to eachother.
	 * 
	 * @return a map that when given a cage object returns a set of cages that
	 *         are next to the given cage
	 */
	private Map<Cage, Set<Cage>> getNeighbourCages() {
		Map<Cage, Set<Cage>> neighbourCages = new HashMap<Cage, Set<Cage>>();
		for (Cage cage : cages) { // Run through all cages
			Set<Cage> cageSet = new HashSet<Cage>();
			for (Cell cell : grid.getCellsInCage(cage)) {
				/*
				 * Need to identify all cells around each cage and determine
				 * which cage they are in.
				 */
				int rowVal = cell.getCellLocation().getRow();
				int colVal = cell.getCellLocation().getCol();

				/* Check to see if the has cells around it in every direction */
				boolean cellAbove = (rowVal - 1 > 0) ? true : false;
				boolean cellBelow = (rowVal + 1 <= SIZE) ? true : false;
				boolean cellLeft = (colVal - 1 > 0) ? true : false;
				boolean cellRight = (colVal + 1 <= SIZE) ? true : false;

				if (cellAbove) { // Get cell north
					Cell northCell = grid.getCell(Location.getInstance(rowVal - 1, colVal));
					if (!cage.getLocation().contains(northCell.getCellLocation()))
						cageSet.add(grid.getCage(northCell));

				}
				if (cellBelow) { // Get cell south
					Cell southCell = grid.getCell(Location.getInstance(rowVal + 1, colVal));
					if (!cage.getLocation().contains(southCell.getCellLocation()))
						cageSet.add(grid.getCage(southCell));

				}
				if (cellLeft) {
					Cell westCell = grid.getCell(Location.getInstance(rowVal, colVal - 1));
					if (!cage.getLocation().contains(westCell.getCellLocation()))
						cageSet.add(grid.getCage(westCell));

				}
				if (cellRight) {
					Cell eastCell = grid.getCell(Location.getInstance(rowVal, colVal + 1));
					if (!cage.getLocation().contains(eastCell.getCellLocation()))
						cageSet.add(grid.getCage(eastCell));
				}
			}
			neighbourCages.put(cage, cageSet);
		}

		return neighbourCages;
	}

	/**
	 * Get the killer sudoku grid being used
	 * 
	 * @return the killer grid being displayed.
	 */
	public KillerSudokuGrid getGrid() {
		return grid;
	}

	/**
	 * Get the cages on the killer sudoku board
	 * 
	 * @return the cages on the killer sudoku board
	 */
	public Set<Cage> getCages() {
		return cages;
	}

	/**
	 * Get the cell GUI for each cell represented on the GUI
	 * 
	 * @return the cell GUI
	 */
	public List<CellGUI> getListCellGUI() {
		return listCellGUI;
	}

	/**
	 * Get the map that maps cages to colours
	 * 
	 * @return the map that maps cages to colours.
	 */
	public Map<Cage, Color> getCageColourMap() {
		return cageColourMap;
	}

	/**
	 * Repaint the grid components when needed.
	 */
	public void refresh() {
		revalidate();
		repaint();
	}

	/**
	 * Method to make the whole grid not contain the possible values of each
	 * cell
	 */
	public void setBlankGrid() {
		for (CellGUI cellGui : listCellGUI) {
			cellGui.cleanUp();
		}
	}

}
