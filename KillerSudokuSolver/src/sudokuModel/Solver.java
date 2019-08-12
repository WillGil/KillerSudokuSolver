package sudokuModel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.plaf.synth.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A class that solves the killer sudoku puzzle.
 * 
 * @author William Gilgunn
 * @version 2.0
 *
 */
public class Solver {
	private final int REGION_TOTAL = 45;
	private KillerSudokuGrid grid;
	private final int SIZE = 9;

	private static final int DIFFICULTY_EASY = 1;
	private static final int DIFFICULTY_MEDIUM = 2;
	private static final int DIFFICULTY_EXPERIENCED = 3;

	private static Map<Integer, List<Integer>> NEIGHBOUR_NONETS = new HashMap<>();

	public Solver(KillerSudokuGrid grid) {
		this.grid = grid;

		assignNeighbourNonets();
	}

	private void assignNeighbourNonets() {
		for (int nonets = 1; nonets <= SIZE; nonets++) {
			List<Integer> neighbournonets = new ArrayList<>();

			switch (nonets) {
			case 1:
				neighbournonets.add(2);
				neighbournonets.add(4);

				NEIGHBOUR_NONETS.put(nonets, neighbournonets);

				break;

			case 2:
				neighbournonets.add(1);
				neighbournonets.add(3);
				neighbournonets.add(5);

				NEIGHBOUR_NONETS.put(nonets, neighbournonets);

				break;

			case 3:
				neighbournonets.add(2);
				neighbournonets.add(6);
				NEIGHBOUR_NONETS.put(nonets, neighbournonets);

				break;

			case 4:
				neighbournonets.add(1);
				neighbournonets.add(5);
				neighbournonets.add(7);
				NEIGHBOUR_NONETS.put(nonets, neighbournonets);

				break;

			case 5:
				neighbournonets.add(2);
				neighbournonets.add(4);
				neighbournonets.add(6);
				neighbournonets.add(8);
				NEIGHBOUR_NONETS.put(nonets, neighbournonets);

				break;

			case 6:
				neighbournonets.add(3);
				neighbournonets.add(5);
				neighbournonets.add(9);
				NEIGHBOUR_NONETS.put(nonets, neighbournonets);

				break;

			case 7:
				neighbournonets.add(4);
				neighbournonets.add(8);
				NEIGHBOUR_NONETS.put(nonets, neighbournonets);

				break;

			case 8:
				neighbournonets.add(5);
				neighbournonets.add(7);
				neighbournonets.add(9);
				NEIGHBOUR_NONETS.put(nonets, neighbournonets);

				break;

			case 9:
				neighbournonets.add(6);
				neighbournonets.add(8);
				NEIGHBOUR_NONETS.put(nonets, neighbournonets);

				break;

			}

		}
	}

	/**
	 * SKILL LEVEL 1 (Beginner) Check to see if there are any cages with a
	 * single cell within them.
	 * 
	 * @param board
	 *            to check
	 * @return cell that is on it's own.
	 */
	public Logic checkSingleCages(KillerSudokuGrid grid) {
		Set<Cage> cages = grid.getCages(); // Get all cages

		for (Cage cage : cages) { // For loop to run through cages
			Set<Cell> currentCageCells = grid.getCellsInCage(cage);

			if (cage.isCommitted())
				continue;

			if (currentCageCells.size() == 1) { // Cage has one elem

				for (Cell currentCell : currentCageCells) {

					String reason = "Cell (" + currentCell.getCellLocation().getRow() + ","
							+ currentCell.getCellLocation().getCol()
							+ ") is the only element in a single cage so it has to be equal to the sum of the cage which is "
							+ cage.getTotalValue() + "\n";

					solveCell(currentCell, cage.getTotalValue());
					Logic result = new Logic(DIFFICULTY_EASY, reason, currentCell);
					return result;
				}

			}

		}
		return null;

	}

	public Logic boxLineReduction(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		StringBuilder result = new StringBuilder();
		for (int row = 1; row <= SIZE; row++) {
			List<Cell> cellsInRow = grid.getRow(row);

			for (int possibleVal = 1; possibleVal <= SIZE; possibleVal++) {
				for (int nonet = 1; nonet <= SIZE; nonet++) {
					List<Cell> currentNonetCells = grid.getNoNetCells(nonet);
					List<Cell> cellsInSuitableRow = new ArrayList<>();
					for (Cell currentCell : currentNonetCells) {
						if (cellsInRow.contains(currentCell))
							cellsInSuitableRow.add(currentCell);

					}

					List<Cell> suitableCells = new ArrayList<>();

					for (Cell cells : cellsInSuitableRow) {
						if (cells.getPossibleValues().contains(possibleVal) && !cells.isCommitted()) {
							suitableCells.add(cells);
						}

					}
					Set<Integer> valueToRemove = new HashSet<>(Arrays.asList(possibleVal));
					Set<Cell> cellsWorkingWith = new HashSet<>(currentNonetCells);

					cellsWorkingWith.removeAll(suitableCells);

					if (suitableCells.size() > 1 && suitableCells.size() <= 3) {
						if (cellsDoNotContainValue(suitableCells, cellsInRow, possibleVal)) {

							if (isThereAnythingToRemoveFromCells(valueToRemove, cellsWorkingWith)) {

								result.append(suitableCells + " contain " + possibleVal
										+ " and is grouped together within nonet:" + nonet + " and unique within row:"
										+ row + " therefore it can be removed from the nonet\n");
								for (Cell nonetCells : cellsWorkingWith) {
									Set<Integer> ValuesRemoved = new HashSet<>();

									if (!suitableCells.contains(nonetCells)
											&& nonetCells.getPossibleValues().contains(possibleVal)) {
										result.append(nonetCells + " removing " + possibleVal);
										nonetCells.removePossibleValues(possibleVal);
										ValuesRemoved.add(possibleVal);
										result.append("\n");
									}
									cellsChanged.put(nonetCells, new CellAdjustment(ValuesRemoved, new HashSet<>()));
								}

								Set<Integer> value = new HashSet<>(Arrays.asList(possibleVal));

								if (cellsChanged.isEmpty())
									continue;

								for (Cell currentCell : suitableCells) {
									cellsChanged.put(currentCell, new CellAdjustment(new HashSet<>(), value));
								}

								Logic returnVal = new Logic(DIFFICULTY_EXPERIENCED, result.toString(), cellsChanged);
								return returnVal;
							}
						}
					}

				}

			}

		}
		cellsChanged = new HashMap<>();
		result = new StringBuilder();

		for (int col = 1; col <= SIZE; col++) {
			List<Cell> cellsInCol = grid.getCols(col);

			for (int possibleVal = 1; possibleVal <= SIZE; possibleVal++) {
				for (int nonet = 1; nonet <= SIZE; nonet++) {
					List<Cell> currentNonetCells = grid.getNoNetCells(nonet);
					List<Cell> cellsInSuitableCol = new ArrayList<>();

					for (Cell currentCell : currentNonetCells) {
						if (cellsInCol.contains(currentCell))
							cellsInSuitableCol.add(currentCell);

						List<Cell> suitableCells = new ArrayList<>();

						for (Cell cells : cellsInSuitableCol) {
							if (cells.getPossibleValues().contains(possibleVal)) {
								suitableCells.add(cells);
							}
						}

						Set<Integer> valueToRemove = new HashSet<>(Arrays.asList(possibleVal));
						Set<Cell> cellsWorkingWith = new HashSet<>(currentNonetCells);
						cellsWorkingWith.removeAll(suitableCells);

						if (suitableCells.size() > 1 && suitableCells.size() <= 3) {
							if (cellsDoNotContainValue(suitableCells, cellsInCol, possibleVal)) {
								if (isThereAnythingToRemoveFromCells(valueToRemove, cellsWorkingWith)) {
									result.append(possibleVal + " is grouped together within nonet:" + nonet
											+ " and unique within col:" + col
											+ " therefore it can be removed from the nonet\n");

									for (Cell nonetCells : cellsWorkingWith) {
										Set<Integer> ValuesRemoved = new HashSet<>();

										if (!suitableCells.contains(nonetCells)
												&& nonetCells.getPossibleValues().contains(possibleVal)) {
											result.append(nonetCells + " removing " + possibleVal);
											nonetCells.removePossibleValues(possibleVal);
											ValuesRemoved.add(possibleVal);
											result.append("\n");

										}
										cellsChanged.put(nonetCells,
												new CellAdjustment(ValuesRemoved, new HashSet<>()));
									}
									if (cellsChanged.isEmpty())
										continue;

									Set<Integer> value = new HashSet<>(Arrays.asList(possibleVal));

									for (Cell cellsCausingChangeCell : suitableCells) {
										cellsChanged.put(cellsCausingChangeCell,
												new CellAdjustment(new HashSet<>(), value));
									}

									Logic returnVal = new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
											cellsChanged);

									return returnVal;
								}
							}
						}
					}

				}
			}

		}
		return null;
	}

	private boolean cellsDoNotContainValue(List<Cell> suitableCells, List<Cell> cellsInRow, int possibleVal) {
		for (Cell currentCell : cellsInRow) {
			if (!suitableCells.contains(currentCell) && currentCell.getPossibleValues().contains(possibleVal)) {
				return false;
			}
		}
		return true;
	}

	public boolean singleCagesPresent(KillerSudokuGrid grid) {
		for (Cage cages : grid.getCages()) {
			if (cages.getLocation().size() == 1 && !cages.isCommitted()) {
				return true;
			}
		}

		return false;
	}

	/*
	 * And box line reduction add in also
	 * 
	 */
	public Logic pointingPairsAndTriples(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		StringBuilder sb = new StringBuilder();
		for (int nonet = 1; nonet <= SIZE; nonet++) {
			List<Cell> cellsInNet = grid.getNoNetCells(nonet);
			for (int possibleValues = 1; possibleValues <= SIZE; possibleValues++) {
				Set<Cell> cells = new HashSet<>();
				for (Cell currentCell : cellsInNet) {
					if (currentCell.getPossibleValues().contains(possibleValues))
						cells.add(currentCell);

				}
				Set<Integer> value = new HashSet<>(Arrays.asList(possibleValues));
				if ((cells.size() > 1 && cells.size() <= 3)) {
					if (areCellsSameCol(cells)) {

						int col = getCellsSameCol(cells);
						List<Cell> cellOnCol = grid.getCols(col);

						Set<Cell> cellsOnColSet = new HashSet<>(cellOnCol);
						cellsOnColSet.removeAll(cells);

						if (isThereAnythingToRemoveFromCells(value, cellsOnColSet)) {
							sb.append(cells + " contain duplicate value " + possibleValues
									+ " which is a unique within it's nonet therefore it can be removed from all cells in the same col\n");

							for (Cell currentCell : cellsOnColSet) {
								Set<Integer> valuesChanged = new HashSet<>();

								if (!currentCell.isCommitted()
										&& currentCell.getPossibleValues().contains(possibleValues)
										&& !cells.contains(currentCell)) {
									sb.append(currentCell + " removing " + possibleValues + "\n");
									valuesChanged.add(possibleValues);
									currentCell.removePossibleValues(possibleValues);
								}
								cellsChanged.put(currentCell, new CellAdjustment(valuesChanged, new HashSet<>()));
							}

							if (cellsChanged.isEmpty())
								continue;

							for (Cell cellsCausingChangeCell : cells) {
								cellsChanged.put(cellsCausingChangeCell, new CellAdjustment(new HashSet<>(), value));
							}

							Logic result = new Logic(DIFFICULTY_EXPERIENCED, sb.toString(), cellsChanged);
							return result;
						}
					} else {
						if (areCellsSameRow(cells)) {

							int row = getCellsSameRow(cells);
							List<Cell> cellOnRow = grid.getRow(row);

							Set<Cell> cellsOnRowSet = new HashSet<>(cellOnRow);
							cellsOnRowSet.removeAll(cells);

							if (isThereAnythingToRemoveFromCells(value, cellsOnRowSet)) {

								sb.append(cells + " contain duplicate value " + possibleValues
										+ " which is a unique within it's nonet therefore it can be removed from all cells in the same row\n");
								for (Cell currentCell : cellsOnRowSet) {
									Set<Integer> valuesChanged = new HashSet<>();

									if (!currentCell.isCommitted()
											&& currentCell.getPossibleValues().contains(possibleValues)
											&& !cells.contains(currentCell)) {
										sb.append(currentCell + " removing " + possibleValues + "\n");
										valuesChanged.add(possibleValues);
										currentCell.removePossibleValues(possibleValues);
									}
									cellsChanged.put(currentCell, new CellAdjustment(valuesChanged, new HashSet<>()));
								}
								if (cellsChanged.isEmpty())
									continue;

								for (Cell cellsCausingChangeCell : cells) {
									cellsChanged.put(cellsCausingChangeCell,
											new CellAdjustment(new HashSet<>(), value));
								}

								Logic result = new Logic(DIFFICULTY_EXPERIENCED, sb.toString(), cellsChanged);
								return result;
							}
						}
					}
				}
			}

		}

		return null;
	}

	private int getCellsSameRow(Set<Cell> cells) {
		int row = -1;
		if (areCellsSameRow(cells)) {
			for (Cell currentCell : cells) {
				row = currentCell.getCellLocation().getRow();
			}
		}
		return row;
	}

	private int getCellsSameCol(Set<Cell> cells) {
		int col = -1;
		if (areCellsSameCol(cells)) {
			for (Cell currentCell : cells) {
				col = currentCell.getCellLocation().getCol();
			}
		}

		return col;
	}

	private boolean areCellsSameCol(Set<Cell> cells) {
		boolean colSet = false;
		int col = -1;
		for (Cell currentCell : cells) {
			Location cellLocation = currentCell.getCellLocation();
			if (!colSet) {
				col = cellLocation.getCol();
				colSet = true;
			}

			if (col != cellLocation.getCol()) {
				return false;
			}
		}

		return true;
	}

	private boolean areCellsSameRow(Set<Cell> cells) {
		boolean rowSet = false;
		int row = -1;
		for (Cell currentCell : cells) {
			Location cellLocation = currentCell.getCellLocation();
			if (!rowSet) {
				row = cellLocation.getRow();
				rowSet = true;
			}
			if (row != cellLocation.getRow()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Currently does single lines fix for multiple
	 * 
	 * @param grid
	 * @return
	 */

	public Logic solveInniesAndOuties(KillerSudokuGrid grid) {

		Logic rows = solveRows(grid);
		if (rows != null) {
			return rows;
		}

		Logic col = SolveColumn(grid);
		if (col != null) {
			return col;
		}

		Logic nonets = SolveNonets(grid);
		if (nonets != null) {
			return nonets;
		}

		return null;

	}

	public Logic solveInniesAndOutiesTwoCells(KillerSudokuGrid grid) {

		Logic rows = solveTwoCellRows(grid);
		if (rows != null) {
			return rows;
		}

		Logic col = solveTwoCellCols(grid);
		if (col != null) {
			return col;
		}

		Logic nonet = solveTwoCellNonets(grid);
		if (nonet != null) {
			return nonet;
		}

		return null;
	}

	private Logic solveTwoCellCols(KillerSudokuGrid grid) {

		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		for (int numberOfCols = 1; numberOfCols < SIZE; numberOfCols++) {

			Set<Integer> cols = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

			Set<Set<Integer>> combinationOfColsUnchecked = SumToN.getSubsetsOfSize(numberOfCols, cols);
			Set<Set<Integer>> combinationOfColsChecked = new HashSet<Set<Integer>>();

			for (Set<Integer> checkSet : combinationOfColsUnchecked) {
				if (rowsAndColsNextToEachother(checkSet)) {
					combinationOfColsChecked.add(checkSet);
				}
			}

			for (Set<Integer> colsUsed : combinationOfColsChecked) {

				Set<Cell> cellsInColsSet = new HashSet<>();

				for (int currentCol : colsUsed) {
					cellsInColsSet.addAll(grid.getCols(currentCol));
				}

				Set<Cage> cagesWithinCols = new HashSet<>();

				for (Cell currentColCell : cellsInColsSet) {
					Cage currentCellCage = grid.getCage(currentColCell);
					cagesWithinCols.add(currentCellCage);
				}

				Set<Cage> cagesSuitableOutie = new HashSet<>();
				int totalSumOfCols = 0;
				int totalNumberOfCellsInCages = 0;
				for (Cage currentColCage : cagesWithinCols) {

					int totalCageRemove = 0;
					if (!cageContainedInCols(currentColCage, colsUsed)) {

						int cellsCommittedOutside = 0;
						int cellsOutside = 0;
						for (Location loc : currentColCage.getLocation()) {
							Cell currentCell = grid.getCell(loc);

							if (isCellInCols(currentCell, colsUsed)) {
								totalNumberOfCellsInCages++;
							} else {
								cellsOutside++;
								if (!currentCell.isCommitted()) {
									totalNumberOfCellsInCages++;
								} else {
									totalCageRemove += currentCell.getValueCommitted();
									cellsCommittedOutside++;
								}
							}
						}

						if (cellsOutside - cellsCommittedOutside == 1) {
							cagesSuitableOutie.add(currentColCage);
						}
						totalSumOfCols += (currentColCage.getTotalValue() - totalCageRemove);
						continue;
					}
					totalSumOfCols += currentColCage.getTotalValue();
					totalNumberOfCellsInCages += currentColCage.getSize();

				}
				if (totalNumberOfCellsInCages == (colsUsed.size() * SIZE) + 2) {
					StringBuilder resultString = new StringBuilder();

					List<Cell> cellsNotInCage = findOutieCellsOfCols(cagesWithinCols, colsUsed);
					Cell cellOne = cellsNotInCage.get(0);
					Cell cellTwo = cellsNotInCage.get(1);

					Set<Cell> cellsNotInCageSet = new HashSet<>();
					cellsNotInCageSet.add(cellOne);
					cellsNotInCageSet.add(cellTwo);

					Set<Integer> possibleValues = new TreeSet<>(cellOne.getPossibleValues());
					possibleValues.addAll(cellTwo.getPossibleValues());

					if (cellOne.isCommitted() || cellTwo.isCommitted())
						continue;

					int cageTotal = totalSumOfCols - (REGION_TOTAL * colsUsed.size());

					List<SumCombination> combinations = SumToN.SumUpTo(cageTotal, 2, possibleValues);

					if (combinations.isEmpty())
						continue;

					resultString.append(
							"Cells " + cellOne + " and " + cellTwo + " are both outies within col region " + colsUsed
									+ " therefore they can form a virtual cage where the cage is of size two with the hint "
									+ cageTotal + "\n\n");

					Set<Integer> valuesInCombos = new HashSet<>();

					for (SumCombination combo : combinations) {
						valuesInCombos.addAll(combo.getValuesThatSum());
					}

					Set<Integer> valuesToRemove = getCellsToRemove(cellOne.getPossibleValues(), valuesInCombos);
					Set<Integer> valuesToRemoveTwo = getCellsToRemove(cellTwo.getPossibleValues(), valuesInCombos);
					Set<Integer> combinedValuesToRemove = new HashSet<>(valuesToRemove);
					combinedValuesToRemove.addAll(valuesToRemoveTwo);

					if (isThereAnythingToRemoveFromCells(combinedValuesToRemove, cellsNotInCageSet)) {

						for (Cell currentCell : cellsNotInCage) {
							Set<Integer> valuesRemoved = new HashSet<>();

							Set<Integer> getRemoveCell = getCellsToRemove(currentCell.getPossibleValues(),
									valuesInCombos);

							List<Integer> valsToRemove = new ArrayList<>(getRemoveCell);

							boolean valueToRemove = false;
							for (int i = 0; i < valsToRemove.size(); i++) {
								if (!valueToRemove) {
									resultString.append(currentCell + " removing ");
									valueToRemove = true;
								}
								if (i == valsToRemove.size() - 1) {
									currentCell.removePossibleValues(valsToRemove.get(i));
									resultString.append(valsToRemove.get(i) + "\n");
									valuesRemoved.add(valsToRemove.get(i));
									continue;
								}
								currentCell.removePossibleValues(valsToRemove.get(i));
								resultString.append(valsToRemove.get(i) + "/");
								valuesRemoved.add(valsToRemove.get(i));
							}

							Set<Integer> valuesUsed = new HashSet<>(currentCell.getPossibleValues());

							valuesUsed.retainAll(valuesInCombos);

							cellsChanged.put(currentCell, new CellAdjustment(valuesRemoved, valuesUsed));

						}
						// Return
						Logic result = new Logic(DIFFICULTY_EXPERIENCED, resultString.toString(), cellsChanged);

						return result;
					}
				} else {
					totalSumOfCols = 0;
					totalNumberOfCellsInCages = 0;
					Set<Cage> cagesSuitableInnie = new HashSet<>();

					for (Cage currentColCage : cagesWithinCols) {

						if (cageContainedInCols(currentColCage, cols)) {
							totalNumberOfCellsInCages += currentColCage.getSize();
							totalSumOfCols += currentColCage.getTotalValue();
						} else {
							int cellsCommittedInside = 0;
							int cellsInside = 0;
							int totalToAdd = 0;

							for (Location currentLoc : currentColCage.getLocation()) {
								Cell currentCell = grid.getCell(currentLoc);

								if (isCellInCols(currentCell, colsUsed)) {
									cellsInside++;
									if (currentCell.isCommitted()) {
										totalNumberOfCellsInCages++;
										cellsCommittedInside++;
										totalToAdd += currentCell.getValueCommitted();
									}
								}
							}

							totalSumOfCols += totalToAdd;
							// continue;
						}
					}
					if (totalNumberOfCellsInCages == ((SIZE * colsUsed.size()) - 2)) {
						StringBuilder resultString = new StringBuilder();

						List<Cell> cellsNotInCage = findInnieCellsOfCols(cagesWithinCols, colsUsed);

						Cell cellOne = cellsNotInCage.get(0);
						Cell cellTwo = cellsNotInCage.get(1);

						Set<Cell> cellsNotInCageSet = new HashSet<>(cellsNotInCage);

						Set<Integer> possibleValues = new TreeSet<>(cellOne.getPossibleValues());
						possibleValues.addAll(cellTwo.getPossibleValues());

						if (cellOne.isCommitted() || cellTwo.isCommitted())
							continue;

						int cageTotal = (REGION_TOTAL * colsUsed.size()) - totalSumOfCols;

						List<SumCombination> combinations = SumToN.SumUpTo(cageTotal, 2, possibleValues);

						if (combinations.isEmpty())
							continue;

						resultString.append("Cells " + cellOne + " and " + cellTwo
								+ " are both innies within column region " + colsUsed
								+ " therefore they can form a virtual cage where the cage is of size two with the hint "
								+ cageTotal + "\n\n");

						Set<Integer> valuesInCombos = new HashSet<>();

						for (SumCombination combo : combinations) {
							valuesInCombos.addAll(combo.getValuesThatSum());
						}

						Set<Integer> valuesToRemove = getCellsToRemove(cellOne.getPossibleValues(), valuesInCombos);
						Set<Integer> valuesToRemoveTwo = getCellsToRemove(cellTwo.getPossibleValues(), valuesInCombos);
						Set<Integer> combinedValuesToRemove = new HashSet<>(valuesToRemove);
						combinedValuesToRemove.addAll(valuesToRemoveTwo);

						if (isThereAnythingToRemoveFromCells(combinedValuesToRemove, cellsNotInCageSet)) {

							for (Cell currentCell : cellsNotInCage) {
								Set<Integer> valuesRemoved = new HashSet<>();

								Set<Integer> getRemoveCell = getCellsToRemove(currentCell.getPossibleValues(),
										valuesInCombos);

								List<Integer> valsToRemove = new ArrayList<>(getRemoveCell);

								boolean valueToRemove = false;
								for (int i = 0; i < valsToRemove.size(); i++) {
									if (!valueToRemove) {
										resultString.append(currentCell + " removing ");
										valueToRemove = true;
									}
									if (i == valsToRemove.size() - 1) {
										currentCell.removePossibleValues(valsToRemove.get(i));
										resultString.append(valsToRemove.get(i) + "\n");
										valuesRemoved.add(valsToRemove.get(i));
										continue;
									}
									currentCell.removePossibleValues(valsToRemove.get(i));
									resultString.append(valsToRemove.get(i) + "/");
									valuesRemoved.add(valsToRemove.get(i));
								}

								Set<Integer> valuesUsed = new HashSet<>(currentCell.getPossibleValues());

								valuesUsed.retainAll(valuesInCombos);

								cellsChanged.put(currentCell, new CellAdjustment(valuesRemoved, valuesUsed));

							}
							// Return
							Logic result = new Logic(DIFFICULTY_EXPERIENCED, resultString.toString(), cellsChanged);

							return result;
						}
					}
				}
			}
		}

		return null;
	}

	private List<Cell> findInnieCellsOfCols(Set<Cage> cagesSuitableInnie, Set<Integer> colsUsed) {
		List<Cell> innieCells = new ArrayList<>();
		for (Cage currentCage : cagesSuitableInnie) {
			if (!cageContainedInCols(currentCage, colsUsed)) {
				for (Location currentLoc : currentCage.getLocation()) {
					if (colsUsed.contains(currentLoc.getCol()) && !grid.getCell(currentLoc).isCommitted()) {
						innieCells.add(grid.getCell(currentLoc));
					}
				}
			}
		}
		return innieCells;
	}

	private List<Cell> findOutieCellsOfCols(Set<Cage> cagesWithinCols, Set<Integer> colsUsed) {
		List<Cell> outieCells = new ArrayList<>();
		for (Cage currentCage : cagesWithinCols) {
			for (Location currentLoc : currentCage.getLocation()) {
				if (!colsUsed.contains(currentLoc.getCol()) && !grid.getCell(currentLoc).isCommitted()) {
					outieCells.add(grid.getCell(currentLoc));
				}
			}
		}
		return outieCells;
	}

	private Logic solveTwoCellRows(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		for (int numberOfRows = 1; numberOfRows < SIZE; numberOfRows++) {

			Set<Integer> rows = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

			Set<Set<Integer>> combinationOfRowsUnchecked = SumToN.getSubsetsOfSize(numberOfRows, rows);
			Set<Set<Integer>> combinationOfRowsChecked = new HashSet<Set<Integer>>();

			for (Set<Integer> checkSet : combinationOfRowsUnchecked) {
				if (rowsAndColsNextToEachother(checkSet)) {
					combinationOfRowsChecked.add(checkSet);
				}
			}

			for (Set<Integer> rowsUsed : combinationOfRowsChecked) {

				Set<Cell> cellsInRowSet = new HashSet<>();

				for (int currentRow : rowsUsed) {
					cellsInRowSet.addAll(grid.getRow(currentRow));
				}

				Set<Cage> cagesWithinRow = new HashSet<>();

				for (Cell currentRowCell : cellsInRowSet) {
					Cage currentCellCage = grid.getCage(currentRowCell);
					cagesWithinRow.add(currentCellCage);
				}

				Set<Cage> cagesSuitableOutie = new HashSet<>();
				int totalSumOfRows = 0;
				int totalNumberOfCellsInCages = 0;
				for (Cage currentRowCage : cagesWithinRow) {

					int totalCageRemove = 0;
					if (!cageContainedInRows(currentRowCage, rowsUsed)) {

						int cellsCommittedOutside = 0;
						int cellsOutside = 0;
						for (Location loc : currentRowCage.getLocation()) {
							Cell currentCell = grid.getCell(loc);

							if (isCellInRows(currentCell, rowsUsed)) {
								totalNumberOfCellsInCages++;
							} else {
								cellsOutside++;
								if (!currentCell.isCommitted()) {
									totalNumberOfCellsInCages++;
								} else {
									totalCageRemove += currentCell.getValueCommitted();
									cellsCommittedOutside++;
								}
							}
						}

						if (cellsOutside - cellsCommittedOutside == 1) {
							cagesSuitableOutie.add(currentRowCage);
						}
						totalSumOfRows += (currentRowCage.getTotalValue() - totalCageRemove);
						continue;
					}
					totalSumOfRows += currentRowCage.getTotalValue();
					totalNumberOfCellsInCages += currentRowCage.getSize();

				}
				if (totalNumberOfCellsInCages == (rowsUsed.size() * SIZE) + 2) {

					StringBuilder resultString = new StringBuilder();

					List<Cell> cellsNotInCage = findOutieCellsOfRows(cagesWithinRow, rowsUsed);
					Cell cellOne = cellsNotInCage.get(0);
					Cell cellTwo = cellsNotInCage.get(1);

					Set<Cell> cellsNotInCageSet = new HashSet<>();
					cellsNotInCageSet.add(cellOne);
					cellsNotInCageSet.add(cellTwo);

					Set<Integer> possibleValues = new TreeSet<>(cellOne.getPossibleValues());
					possibleValues.addAll(cellTwo.getPossibleValues());

					if (cellOne.isCommitted() || cellTwo.isCommitted())
						continue;

					int cageTotal = totalSumOfRows - (REGION_TOTAL * rowsUsed.size());

					List<SumCombination> combinations = SumToN.SumUpTo(cageTotal, 2, possibleValues);

					if (combinations.isEmpty())
						continue;

					resultString.append(
							"Cells " + cellOne + " and " + cellTwo + " are both outies within row region " + rowsUsed
									+ " therefore they can form a virtual cage where the cage is of size two with the hint "
									+ cageTotal + "\n\n");

					Set<Integer> valuesInCombos = new HashSet<>();

					for (SumCombination combo : combinations) {
						valuesInCombos.addAll(combo.getValuesThatSum());
					}

					Set<Integer> valuesToRemove = getCellsToRemove(cellOne.getPossibleValues(), valuesInCombos);
					Set<Integer> valuesToRemoveTwo = getCellsToRemove(cellTwo.getPossibleValues(), valuesInCombos);
					Set<Integer> combinedValuesToRemove = new HashSet<>(valuesToRemove);
					combinedValuesToRemove.addAll(valuesToRemoveTwo);

					if (isThereAnythingToRemoveFromCells(combinedValuesToRemove, cellsNotInCageSet)) {

						for (Cell currentCell : cellsNotInCage) {
							Set<Integer> valuesRemoved = new HashSet<>();

							Set<Integer> getRemoveCell = getCellsToRemove(currentCell.getPossibleValues(),
									valuesInCombos);

							List<Integer> valsToRemove = new ArrayList<>(getRemoveCell);

							boolean valueToRemove = false;
							for (int i = 0; i < valsToRemove.size(); i++) {
								if (!valueToRemove) {
									resultString.append(currentCell + " removing ");
									valueToRemove = true;
								}
								if (i == valsToRemove.size() - 1) {
									currentCell.removePossibleValues(valsToRemove.get(i));
									resultString.append(valsToRemove.get(i) + "\n");
									valuesRemoved.add(valsToRemove.get(i));
									continue;
								}
								currentCell.removePossibleValues(valsToRemove.get(i));
								resultString.append(valsToRemove.get(i) + "/");
								valuesRemoved.add(valsToRemove.get(i));
							}

							Set<Integer> valuesUsed = new HashSet<>(currentCell.getPossibleValues());

							valuesUsed.retainAll(valuesInCombos);

							cellsChanged.put(currentCell, new CellAdjustment(valuesRemoved, valuesUsed));

						} // Return Logic
						Logic result = new Logic(DIFFICULTY_EXPERIENCED, resultString.toString(), cellsChanged);

						return result;
					}

				} else {
					totalSumOfRows = 0;
					totalNumberOfCellsInCages = 0;
					for (Cage currentRowCage : cagesWithinRow) {

						if (cageContainedInRows(currentRowCage, rowsUsed)) {
							totalNumberOfCellsInCages += currentRowCage.getSize();
							totalSumOfRows += currentRowCage.getTotalValue();
						} else {
							int cellsCommittedInside = 0;
							int cellsInside = 0;
							int totalToAdd = 0;

							for (Location currentLoc : currentRowCage.getLocation()) {
								Cell currentCell = grid.getCell(currentLoc);

								if (isCellInRows(currentCell, rowsUsed)) {
									cellsInside++;
									if (currentCell.isCommitted()) {
										totalNumberOfCellsInCages++;
										cellsCommittedInside++;
										totalToAdd += currentCell.getValueCommitted();
									}
								}
							}

							totalSumOfRows += totalToAdd;
							// continue;
						}
					}

					if (totalNumberOfCellsInCages == ((SIZE * rowsUsed.size()) - 2)) {
						StringBuilder resultString = new StringBuilder();

						List<Cell> cellsNotInCage = findInnieCellsOfRows(cagesWithinRow, rowsUsed);

						if (cellsNotInCage.size() == 2) {
							Cell cellOne = cellsNotInCage.get(0);
							Cell cellTwo = cellsNotInCage.get(1);

							Set<Cell> cellsNotInCageSet = new HashSet<>(cellsNotInCage);

							Set<Integer> possibleValues = new TreeSet<>(cellOne.getPossibleValues());
							possibleValues.addAll(cellTwo.getPossibleValues());

							if (cellOne.isCommitted() || cellTwo.isCommitted())
								continue;

							int cageTotal = (REGION_TOTAL * rowsUsed.size()) - totalSumOfRows;

							List<SumCombination> combinations = SumToN.SumUpTo(cageTotal, 2, possibleValues);

							if (combinations.isEmpty())
								continue;

							resultString.append("Cells " + cellOne + " and " + cellTwo
									+ " are both innies within rows region " + rowsUsed
									+ " therefore they can form a virtual cage where the cage is of size two with the hint "
									+ cageTotal + "\n\n");

							Set<Integer> valuesInCombos = new HashSet<>();

							for (SumCombination combo : combinations) {
								valuesInCombos.addAll(combo.getValuesThatSum());
							}

							Set<Integer> valuesToRemove = getCellsToRemove(cellOne.getPossibleValues(), valuesInCombos);
							Set<Integer> valuesToRemoveTwo = getCellsToRemove(cellTwo.getPossibleValues(),
									valuesInCombos);
							Set<Integer> combinedValuesToRemove = new HashSet<>(valuesToRemove);
							combinedValuesToRemove.addAll(valuesToRemoveTwo);

							if (isThereAnythingToRemoveFromCells(combinedValuesToRemove, cellsNotInCageSet)) {

								for (Cell currentCell : cellsNotInCage) {
									Set<Integer> valuesRemoved = new HashSet<>();

									Set<Integer> getRemoveCell = getCellsToRemove(currentCell.getPossibleValues(),
											valuesInCombos);

									List<Integer> valsToRemove = new ArrayList<>(getRemoveCell);

									boolean valueToRemove = false;
									for (int i = 0; i < valsToRemove.size(); i++) {
										if (!valueToRemove) {
											resultString.append(currentCell + " removing ");
											valueToRemove = true;
										}
										if (i == valsToRemove.size() - 1) {
											currentCell.removePossibleValues(valsToRemove.get(i));
											resultString.append(valsToRemove.get(i) + "\n");
											valuesRemoved.add(valsToRemove.get(i));
											continue;
										}
										currentCell.removePossibleValues(valsToRemove.get(i));
										resultString.append(valsToRemove.get(i) + "/");
										valuesRemoved.add(valsToRemove.get(i));
									}

									Set<Integer> valuesUsed = new HashSet<>(currentCell.getPossibleValues());

									valuesUsed.retainAll(valuesInCombos);

									cellsChanged.put(currentCell, new CellAdjustment(valuesRemoved, valuesUsed));

								}
								// Return
								Logic result = new Logic(DIFFICULTY_EXPERIENCED, resultString.toString(), cellsChanged);

								return result;
							}
						}
					}
				}
			}
		}

		return null;
	}

	private List<Cell> findInnieCellsOfRows(Set<Cage> cagesSuitableInnie, Set<Integer> rowsUsed) {
		List<Cell> innieCells = new ArrayList<>();

		for (Cage currentCage : cagesSuitableInnie) {
			if (!cageContainedInRows(currentCage, rowsUsed))
				for (Location currentLoc : currentCage.getLocation()) {
					if (rowsUsed.contains(currentLoc.getRow()) && !grid.getCell(currentLoc).isCommitted()) {
						innieCells.add(grid.getCell(currentLoc));
					}
				}
		}
		return innieCells;
	}

	private Logic solveTwoCellNonets(KillerSudokuGrid grid2) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		for (int numberOfNets = 1; numberOfNets < SIZE; numberOfNets++) {

			Set<Integer> nonets = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

			Set<Set<Integer>> combinationOfNonetsUnchecked = SumToN.getSubsetsOfSize(numberOfNets, nonets);
			Set<Set<Integer>> combinationOfNonetsChecked = new HashSet<Set<Integer>>();

			for (Set<Integer> checkSet : combinationOfNonetsUnchecked) {
				if (pathBetweenNonets(checkSet)) {
					combinationOfNonetsChecked.add(checkSet);
				}
			}

			for (Set<Integer> nonetsUsed : combinationOfNonetsChecked) {

				Set<Cell> cellsInNonetSet = new HashSet<>();

				for (int currentNonets : nonetsUsed) {
					cellsInNonetSet.addAll(grid.getNoNetCells(currentNonets));
				}

				Set<Cage> cagesWithinNonet = new HashSet<>();

				for (Cell currentNonetCell : cellsInNonetSet) {
					Cage currentCellCage = grid.getCage(currentNonetCell);
					cagesWithinNonet.add(currentCellCage);
				}

				Set<Cage> cagesSuitableOutie = new HashSet<>();
				int totalSumOfNonet = 0;
				int totalNumberOfCellsInCages = 0;
				for (Cage currentNonetCage : cagesWithinNonet) {

					int totalCageRemove = 0;
					if (!cageContainedInNonets(currentNonetCage, nonetsUsed)) {

						int cellsCommittedOutside = 0;
						int cellsOutside = 0;
						for (Location loc : currentNonetCage.getLocation()) {
							Cell currentCell = grid.getCell(loc);

							if (isCellInNonets(currentCell, nonetsUsed)) {
								totalNumberOfCellsInCages++;
							} else {
								cellsOutside++;
								if (!currentCell.isCommitted()) {
									totalNumberOfCellsInCages++;
								} else {
									totalCageRemove += currentCell.getValueCommitted();
									cellsCommittedOutside++;
								}
							}
						}

						if (cellsOutside - cellsCommittedOutside == 1) {
							cagesSuitableOutie.add(currentNonetCage);
						}
						totalSumOfNonet += (currentNonetCage.getTotalValue() - totalCageRemove);
						continue;
					}
					totalSumOfNonet += currentNonetCage.getTotalValue();
					totalNumberOfCellsInCages += currentNonetCage.getSize();
				}

				if (totalNumberOfCellsInCages == (nonetsUsed.size() * SIZE) + 2) {
					StringBuilder resultString = new StringBuilder();

					List<Cell> cellsNotInCage = findOutieCellsOfNonets(cagesWithinNonet, nonetsUsed);
					Cell cellOne = cellsNotInCage.get(0);
					Cell cellTwo = cellsNotInCage.get(1);

					Set<Cell> cellsNotInCageSet = new HashSet<>();
					cellsNotInCageSet.add(cellOne);
					cellsNotInCageSet.add(cellTwo);

					Set<Integer> possibleValues = new TreeSet<>(cellOne.getPossibleValues());
					possibleValues.addAll(cellTwo.getPossibleValues());

					if (cellOne.isCommitted() || cellTwo.isCommitted())
						continue;

					int cageTotal = totalSumOfNonet - (REGION_TOTAL * nonetsUsed.size());

					List<SumCombination> combinations = SumToN.SumUpTo(cageTotal, 2, possibleValues);

					if (combinations.isEmpty())
						continue;

					resultString.append("Cells " + cellOne + " and " + cellTwo + " are both outies within nonet region "
							+ nonetsUsed
							+ " therefore they can form a virtual cage where the cage is of size two with the hint "
							+ cageTotal + "\n\n");

					Set<Integer> valuesInCombos = new HashSet<>();

					for (SumCombination combo : combinations) {
						valuesInCombos.addAll(combo.getValuesThatSum());
					}

					Set<Integer> valuesToRemove = getCellsToRemove(cellOne.getPossibleValues(), valuesInCombos);
					Set<Integer> valuesToRemoveTwo = getCellsToRemove(cellTwo.getPossibleValues(), valuesInCombos);
					Set<Integer> combinedValuesToRemove = new HashSet<>(valuesToRemove);
					combinedValuesToRemove.addAll(valuesToRemoveTwo);

					if (isThereAnythingToRemoveFromCells(combinedValuesToRemove, cellsNotInCageSet)) {

						for (Cell currentCell : cellsNotInCage) {
							Set<Integer> valuesRemoved = new HashSet<>();

							Set<Integer> getRemoveCell = getCellsToRemove(currentCell.getPossibleValues(),
									valuesInCombos);

							List<Integer> valsToRemove = new ArrayList<>(getRemoveCell);

							boolean valueToRemove = false;
							for (int i = 0; i < valsToRemove.size(); i++) {
								if (!valueToRemove) {
									resultString.append(currentCell + " removing ");
									valueToRemove = true;
								}
								if (i == valsToRemove.size() - 1) {
									currentCell.removePossibleValues(valsToRemove.get(i));
									resultString.append(valsToRemove.get(i) + "\n");
									valuesRemoved.add(valsToRemove.get(i));
									continue;
								}
								currentCell.removePossibleValues(valsToRemove.get(i));
								resultString.append(valsToRemove.get(i) + "/");
								valuesRemoved.add(valsToRemove.get(i));
							}

							Set<Integer> valuesUsed = new HashSet<>(currentCell.getPossibleValues());

							valuesUsed.retainAll(valuesInCombos);

							cellsChanged.put(currentCell, new CellAdjustment(valuesRemoved, valuesUsed));

						}
						// Return
						Logic result = new Logic(DIFFICULTY_EXPERIENCED, resultString.toString(), cellsChanged);

						return result;
					}
				} else {
					totalSumOfNonet = 0;
					totalNumberOfCellsInCages = 0;

					for (Cage currentNonetCage : cagesWithinNonet) {

						if (cageContainedInNonets(currentNonetCage, nonetsUsed)) {
							totalNumberOfCellsInCages += currentNonetCage.getSize();
							totalSumOfNonet += currentNonetCage.getTotalValue();
						} else {
							int cellsCommittedInside = 0;
							int cellsInside = 0;
							int totalToAdd = 0;

							for (Location currentLoc : currentNonetCage.getLocation()) {
								Cell currentCell = grid.getCell(currentLoc);

								if (isCellInNonets(currentCell, nonetsUsed)) {
									cellsInside++;
									if (currentCell.isCommitted()) {
										totalNumberOfCellsInCages++;
										cellsCommittedInside++;
										totalToAdd += currentCell.getValueCommitted();
									}
								}
							}

							totalSumOfNonet += totalToAdd;
							// continue;
						}
					}
					if (totalNumberOfCellsInCages == ((SIZE * nonetsUsed.size()) - 2)) {
						StringBuilder resultString = new StringBuilder();

						List<Cell> cellsNotInCage = findInnieCellsOfNonets(cagesWithinNonet, nonetsUsed);

						Cell cellOne = cellsNotInCage.get(0);
						Cell cellTwo = cellsNotInCage.get(1);

						Set<Cell> cellsNotInCageSet = new HashSet<>(cellsNotInCage);

						Set<Integer> possibleValues = new TreeSet<>(cellOne.getPossibleValues());
						possibleValues.addAll(cellTwo.getPossibleValues());

						if (cellOne.isCommitted() || cellTwo.isCommitted())
							continue;

						int cageTotal = (REGION_TOTAL * nonetsUsed.size()) - totalSumOfNonet;

						List<SumCombination> combinations = SumToN.SumUpTo(cageTotal, 2, possibleValues);

						if (combinations.isEmpty())
							continue;

						resultString.append("Cells " + cellOne + " and " + cellTwo
								+ " are both innies within nonet region " + nonetsUsed
								+ " therefore they can form a virtual cage where the cage is of size two with the hint "
								+ cageTotal + "\n\n");

						Set<Integer> valuesInCombos = new HashSet<>();

						for (SumCombination combo : combinations) {
							valuesInCombos.addAll(combo.getValuesThatSum());
						}

						Set<Integer> valuesToRemove = getCellsToRemove(cellOne.getPossibleValues(), valuesInCombos);
						Set<Integer> valuesToRemoveTwo = getCellsToRemove(cellTwo.getPossibleValues(), valuesInCombos);
						Set<Integer> combinedValuesToRemove = new HashSet<>(valuesToRemove);
						combinedValuesToRemove.addAll(valuesToRemoveTwo);

						if (isThereAnythingToRemoveFromCells(combinedValuesToRemove, cellsNotInCageSet)) {

							for (Cell currentCell : cellsNotInCage) {
								Set<Integer> valuesRemoved = new HashSet<>();

								Set<Integer> getRemoveCell = getCellsToRemove(currentCell.getPossibleValues(),
										valuesInCombos);

								List<Integer> valsToRemove = new ArrayList<>(getRemoveCell);

								boolean valueToRemove = false;
								for (int i = 0; i < valsToRemove.size(); i++) {
									if (!valueToRemove) {
										resultString.append(currentCell + " removing ");
										valueToRemove = true;
									}
									if (i == valsToRemove.size() - 1) {
										currentCell.removePossibleValues(valsToRemove.get(i));
										resultString.append(valsToRemove.get(i) + "\n");
										valuesRemoved.add(valsToRemove.get(i));
										continue;
									}
									currentCell.removePossibleValues(valsToRemove.get(i));
									resultString.append(valsToRemove.get(i) + "/");
									valuesRemoved.add(valsToRemove.get(i));
								}

								Set<Integer> valuesUsed = new HashSet<>(currentCell.getPossibleValues());

								valuesUsed.retainAll(valuesInCombos);

								cellsChanged.put(currentCell, new CellAdjustment(valuesRemoved, valuesUsed));

							}
							// Return
							Logic result = new Logic(DIFFICULTY_EXPERIENCED, resultString.toString(), cellsChanged);

							return result;
						}
					}

				}
			}
		}

		return null;
	}

	private List<Cell> findInnieCellsOfNonets(Set<Cage> cagesWithinNonet, Set<Integer> nonetsUsed) {
		List<Cell> innieCells = new ArrayList<>();
		for (Cage currentCage : cagesWithinNonet) {
			if (!cageContainedInNonets(currentCage, nonetsUsed)) {
				for (Location currentLoc : currentCage.getLocation()) {
					if (nonetsUsed.contains(currentLoc.getNoNet()) && !grid.getCell(currentLoc).isCommitted()) {
						innieCells.add(grid.getCell(currentLoc));
					}
				}
			}
		}
		return innieCells;
	}

	public Logic hiddenPairs(KillerSudokuGrid grid) {

		Logic logic = hiddenPairsRows(grid);
		if (logic != null)
			return logic;

		logic = hiddenPairsCols(grid);
		if (logic != null)
			return logic;

		logic = hiddenPairsNonet(grid);
		if (logic != null)
			return logic;

		return null;
	}

	private Logic hiddenPairsNonet(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		StringBuilder result = new StringBuilder();

		final int PAIR_SIZE = 2;

		for (int nonet = 1; nonet <= SIZE; nonet++) {
			List<Cell> nonetCells = grid.getNoNetCells(nonet);
			for (int firstComp = 0; firstComp < nonetCells.size(); firstComp++) {
				Set<Set<Integer>> firstCombinations = SumToN.getSubsetsOfSize(PAIR_SIZE,
						nonetCells.get(firstComp).getPossibleValues());

				for (int secondComp = firstComp + 1; secondComp < nonetCells.size(); secondComp++) {
					Set<Set<Integer>> secondCombinations = SumToN.getSubsetsOfSize(PAIR_SIZE,
							nonetCells.get(firstComp).getPossibleValues());

					Set<Cell> cellsWorkingWith = new HashSet<>();
					cellsWorkingWith.add(nonetCells.get(firstComp));
					cellsWorkingWith.add(nonetCells.get(secondComp));
					
					if(nonetCells.get(firstComp).isCommitted() || nonetCells.get(secondComp).isCommitted())
						continue;

					for (Set<Integer> firstCombos : firstCombinations) {
						for (Set<Integer> secondCombos : secondCombinations) {
							/* Check if subsets equal eachother */
							if (firstCombos.equals(secondCombos)) {

								if (uniqueToRegion(cellsWorkingWith, nonetCells, firstCombos)) {
									if ((!nonetCells.get(firstComp).getPossibleValues().equals(firstCombos)
											|| !nonetCells.get(secondComp).getPossibleValues().equals(firstCombos))) {

										Set<Cell> cellToRemoveFromOne = new HashSet<>();
										cellToRemoveFromOne.add(nonetCells.get(firstComp));

										Set<Cell> cellToRemoveFromTwo = new HashSet<>();
										cellToRemoveFromTwo.add(nonetCells.get(secondComp));

										List<Integer> valuesToRemoveFirst = new ArrayList<>(getCellsToRemove(
												nonetCells.get(firstComp).getPossibleValues(), firstCombos));
										List<Integer> valuesToRemoveSecond = new ArrayList<>(getCellsToRemove(
												nonetCells.get(secondComp).getPossibleValues(), firstCombos));

										if (isThereAnythingToRemoveFromCells(
												getCellsToRemove(nonetCells.get(firstComp).getPossibleValues(),
														firstCombos),
												cellToRemoveFromOne)) {
											Set<Integer> valuesChanged = new HashSet<>();

											result.append(nonetCells.get(firstComp) + " and "
													+ nonetCells.get(secondComp)
													+ " contain a hidden pair unique within nonet " + nonet
													+ " values candidates are " + firstCombos
													+ " therefore the candidates must be assigned to the cells\nNONET:\n");

											;

											result.append(nonetCells.get(firstComp) + " removing ");
											for (int value = 0; value < valuesToRemoveFirst.size(); value++) {
												if (value == valuesToRemoveFirst.size() - 1) {
													result.append(valuesToRemoveFirst.get(value));
													valuesChanged.add(valuesToRemoveFirst.get(value));
													nonetCells.get(firstComp)
															.removePossibleValues(valuesToRemoveFirst.get(value));
													continue;
												}
												result.append(valuesToRemoveFirst.get(value) + "/");
												nonetCells.get(firstComp)
														.removePossibleValues(valuesToRemoveFirst.get(value));
												valuesChanged.add(valuesToRemoveFirst.get(value));

											}
											cellsChanged.put(nonetCells.get(firstComp),
													new CellAdjustment(valuesChanged, firstCombos));
										}

										if (isThereAnythingToRemoveFromCells(
												getCellsToRemove(nonetCells.get(secondComp).getPossibleValues(),
														firstCombos),
												cellToRemoveFromTwo)) {
											Set<Integer> valuesChanged = new HashSet<>();

											result.append("\n");
											result.append(nonetCells.get(secondComp) + " removing ");
											for (int value = 0; value < valuesToRemoveSecond.size(); value++) {
												if (value == valuesToRemoveSecond.size() - 1) {
													result.append(valuesToRemoveSecond.get(value));
													valuesChanged.add(valuesToRemoveSecond.get(value));
													nonetCells.get(secondComp)
															.removePossibleValues(valuesToRemoveSecond.get(value));
													continue;
												}
												result.append(valuesToRemoveSecond.get(value) + "/");
												nonetCells.get(secondComp)
														.removePossibleValues(valuesToRemoveSecond.get(value));
												valuesChanged.add(valuesToRemoveSecond.get(value));

											}

											cellsChanged.put(nonetCells.get(secondComp),
													new CellAdjustment(valuesChanged, firstCombos));

										}
										if (cellsChanged.isEmpty())
											continue;

										return new Logic(DIFFICULTY_MEDIUM, result.toString(), cellsChanged);

									}
								}
							}
						}
					}
				}
			}
		}

		return null;
	}

	private Logic hiddenPairsCols(KillerSudokuGrid grid) {

		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		StringBuilder result = new StringBuilder();

		final int PAIR_SIZE = 2;

		for (int col = 1; col <= SIZE; col++) {
			List<Cell> colCells = grid.getCols(col);
			for (int firstComp = 0; firstComp < colCells.size(); firstComp++) {
				Set<Set<Integer>> firstCombinations = SumToN.getSubsetsOfSize(PAIR_SIZE,
						colCells.get(firstComp).getPossibleValues());

				for (int secondComp = firstComp + 1; secondComp < colCells.size(); secondComp++) {
					Set<Set<Integer>> secondCombinations = SumToN.getSubsetsOfSize(PAIR_SIZE,
							colCells.get(firstComp).getPossibleValues());

					Set<Cell> cellsWorkingWith = new HashSet<>();
					cellsWorkingWith.add(colCells.get(firstComp));
					cellsWorkingWith.add(colCells.get(secondComp));

					if(colCells.get(firstComp).isCommitted() || colCells.get(secondComp).isCommitted())
						continue;

					for (Set<Integer> firstCombos : firstCombinations) {
						for (Set<Integer> secondCombos : secondCombinations) {
							/* Check if subsets equal eachother */
							if (firstCombos.equals(secondCombos)) {

								if (uniqueToRegion(cellsWorkingWith, colCells, firstCombos)) {
									if (!colCells.get(firstComp).getPossibleValues().equals(firstCombos)
											|| !colCells.get(secondComp).getPossibleValues().equals(firstCombos)) {

										Set<Cell> cellToRemoveFromOne = new HashSet<>();
										cellToRemoveFromOne.add(colCells.get(firstComp));

										Set<Cell> cellToRemoveFromTwo = new HashSet<>();
										cellToRemoveFromTwo.add(colCells.get(secondComp));

										List<Integer> valuesToRemoveFirst = new ArrayList<>(getCellsToRemove(
												colCells.get(firstComp).getPossibleValues(), firstCombos));
										List<Integer> valuesToRemoveSecond = new ArrayList<>(getCellsToRemove(
												colCells.get(secondComp).getPossibleValues(), firstCombos));

										if (isThereAnythingToRemoveFromCells(
												getCellsToRemove(colCells.get(firstComp).getPossibleValues(),
														firstCombos),
												cellToRemoveFromOne)) {
											Set<Integer> valuesChanged = new HashSet<>();

											result.append(colCells.get(firstComp) + " and " + colCells.get(secondComp)
													+ " contain a hidden pair unique within column " + col
													+ " values candidates are " + firstCombos
													+ " therefore the candidates must be assigned to the cells\nCOLUMN:\n");

											result.append(colCells.get(firstComp) + " removing ");
											for (int value = 0; value < valuesToRemoveFirst.size(); value++) {
												if (value == valuesToRemoveFirst.size() - 1) {
													result.append(valuesToRemoveFirst.get(value));
													valuesChanged.add(valuesToRemoveFirst.get(value));
													colCells.get(firstComp)
															.removePossibleValues(valuesToRemoveFirst.get(value));
													continue;
												}
												result.append(valuesToRemoveFirst.get(value) + "/");
												colCells.get(firstComp)
														.removePossibleValues(valuesToRemoveFirst.get(value));
												valuesChanged.add(valuesToRemoveFirst.get(value));

											}
											cellsChanged.put(colCells.get(firstComp),
													new CellAdjustment(valuesChanged, firstCombos));

										}
										if (isThereAnythingToRemoveFromCells(
												getCellsToRemove(colCells.get(secondComp).getPossibleValues(),
														firstCombos),
												cellToRemoveFromTwo)) {
											Set<Integer> valuesChanged = new HashSet<>();

											result.append("\n");
											result.append(colCells.get(secondComp) + " removing ");
											for (int value = 0; value < valuesToRemoveSecond.size(); value++) {
												if (value == valuesToRemoveSecond.size() - 1) {
													result.append(valuesToRemoveSecond.get(value));
													valuesChanged.add(valuesToRemoveSecond.get(value));
													colCells.get(secondComp)
															.removePossibleValues(valuesToRemoveSecond.get(value));
													continue;
												}
												result.append(valuesToRemoveSecond.get(value) + "/");
												colCells.get(secondComp)
														.removePossibleValues(valuesToRemoveSecond.get(value));
												valuesChanged.add(valuesToRemoveSecond.get(value));

											}
											cellsChanged.put(colCells.get(secondComp),
													new CellAdjustment(valuesChanged, firstCombos));

										}
										if (cellsChanged.isEmpty())
											continue;

										return new Logic(DIFFICULTY_MEDIUM, result.toString(), cellsChanged);
									}
								}

							}
						}
					}
				}
			}
		}
		return null;
	}

	private Logic hiddenPairsRows(KillerSudokuGrid grid) {

		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		StringBuilder result = new StringBuilder();

		final int PAIR_SIZE = 2;
		for (int row = 1; row <= SIZE; row++) {
			List<Cell> rowCells = grid.getRow(row);
			for (int firstComp = 0; firstComp < rowCells.size(); firstComp++) {
				Set<Set<Integer>> firstCombinations = SumToN.getSubsets(rowCells.get(firstComp).getPossibleValues());

				/* Only interested in subsets of size 2 */
				for (Iterator<Set<Integer>> iter = firstCombinations.iterator(); iter.hasNext();) {
					Set<Integer> combo = iter.next();
					if (combo.size() != PAIR_SIZE) {
						iter.remove();
					}
				}

				for (int secondComp = firstComp + 1; secondComp < rowCells.size(); secondComp++) {
					Set<Set<Integer>> secondCombinations = SumToN
							.getSubsets(rowCells.get(firstComp).getPossibleValues());

					/* Only interested in subsets of size 2 */
					for (Iterator<Set<Integer>> iter = secondCombinations.iterator(); iter.hasNext();) {
						Set<Integer> combo = iter.next();
						if (combo.size() != PAIR_SIZE) {
							iter.remove();
						}
					}

					Set<Cell> cellsWorkingWith = new HashSet<>();
					cellsWorkingWith.add(rowCells.get(firstComp));
					cellsWorkingWith.add(rowCells.get(secondComp));
					
					if(rowCells.get(firstComp).isCommitted() || rowCells.get(secondComp).isCommitted())
						continue;

					for (Set<Integer> firstCombos : firstCombinations) {
						for (Set<Integer> secondCombos : secondCombinations) {
							/* Check if subsets equal eachother */
							if (firstCombos.equals(secondCombos)) {

								if (uniqueToRegion(cellsWorkingWith, rowCells, firstCombos)) {

									/* Check its not the values already */
									if (!rowCells.get(firstComp).getPossibleValues().equals(firstCombos)
											&& !rowCells.get(secondComp).getPossibleValues().equals(firstCombos)) {

										Set<Cell> cellToRemoveFromOne = new HashSet<>();
										cellToRemoveFromOne.add(rowCells.get(firstComp));

										Set<Cell> cellToRemoveFromTwo = new HashSet<>();
										cellToRemoveFromTwo.add(rowCells.get(secondComp));

										List<Integer> valuesToRemoveFirst = new ArrayList<>(getCellsToRemove(
												rowCells.get(firstComp).getPossibleValues(), firstCombos));
										List<Integer> valuesToRemoveSecond = new ArrayList<>(getCellsToRemove(
												rowCells.get(secondComp).getPossibleValues(), firstCombos));

										if (isThereAnythingToRemoveFromCells(
												getCellsToRemove(rowCells.get(firstComp).getPossibleValues(),
														firstCombos),
												cellToRemoveFromOne)) {
											Set<Integer> valuesChanged = new HashSet<>();

											result.append(rowCells.get(firstComp) + " and " + rowCells.get(secondComp)
													+ " contain a hidden pair unique within row " + row
													+ " values candidates are " + firstCombos
													+ " therefore the candidates must be assigned to the cells\nROW:\n");

											result.append(rowCells.get(firstComp) + " removing ");
											for (int value = 0; value < valuesToRemoveFirst.size(); value++) {
												if (value == valuesToRemoveFirst.size() - 1) {
													result.append(valuesToRemoveFirst.get(value));
													valuesChanged.add(valuesToRemoveFirst.get(value));
													rowCells.get(firstComp)
															.removePossibleValues(valuesToRemoveFirst.get(value));
													continue;
												}
												result.append(valuesToRemoveFirst.get(value) + "/");
												rowCells.get(firstComp)
														.removePossibleValues(valuesToRemoveFirst.get(value));
												valuesChanged.add(valuesToRemoveFirst.get(value));

											}
											cellsChanged.put(rowCells.get(firstComp),
													new CellAdjustment(valuesChanged, firstCombos));
										}

										if (isThereAnythingToRemoveFromCells(
												getCellsToRemove(rowCells.get(secondComp).getPossibleValues(),
														firstCombos),
												cellToRemoveFromTwo)) {

											Set<Integer> valuesChanged = new HashSet<>();

											result.append("\n");
											result.append(rowCells.get(secondComp) + " removing ");
											for (int value = 0; value < valuesToRemoveSecond.size(); value++) {
												if (value == valuesToRemoveSecond.size() - 1) {
													result.append(valuesToRemoveSecond.get(value));
													valuesChanged.add(valuesToRemoveSecond.get(value));
													rowCells.get(secondComp)
															.removePossibleValues(valuesToRemoveSecond.get(value));
													continue;
												}
												result.append(valuesToRemoveSecond.get(value) + "/");
												rowCells.get(secondComp)
														.removePossibleValues(valuesToRemoveSecond.get(value));
												valuesChanged.add(valuesToRemoveSecond.get(value));

											}
											cellsChanged.put(rowCells.get(secondComp),
													new CellAdjustment(valuesChanged, firstCombos));
										}
										if (cellsChanged.isEmpty())
											continue;

										return new Logic(DIFFICULTY_MEDIUM, result.toString(), cellsChanged);

									}
								}
							}

						}

					}

				}

			}

		}

		return null;
	}

	public Logic hiddenTriples(KillerSudokuGrid grid) {
		Logic logic = hiddenTriplesRow(grid);
		if (logic != null) {
			return logic;
		}

		logic = hiddenTriplesCol(grid);
		if (logic != null) {
			return logic;
		}

		logic = hiddenTriplesNonet(grid);
		if (logic != null) {
			return logic;
		}

		return null;
	}

	private Logic hiddenTriplesNonet(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textShown = false;

		StringBuilder result = new StringBuilder();

		for (int nonet = 1; nonet <= SIZE; nonet++) {
			List<Cell> nonetCells = grid.getNoNetCells(nonet);

			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {

						Set<Integer> threeNumbers = new HashSet<>(Arrays.asList(firstVal, secondVal, thirdVal));

						for (int firstComp = 0; firstComp < nonetCells.size(); firstComp++) {
							Cell firstCell = nonetCells.get(firstComp);
							for (int secondComp = firstComp + 1; secondComp < nonetCells.size(); secondComp++) {
								Cell secondCell = nonetCells.get(secondComp);
								for (int thirdComp = secondComp + 1; thirdComp < nonetCells.size(); thirdComp++) {
									Cell thirdCell = nonetCells.get(thirdComp);

									Set<Cell> cellsWorkingWith = new HashSet<>();

									cellsWorkingWith.add(firstCell);
									cellsWorkingWith.add(secondCell);
									cellsWorkingWith.add(thirdCell);
									
									if(firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted())
										continue;

									if (uniqueToRegion(cellsWorkingWith, nonetCells, threeNumbers)) {
										if (!threeNumbers.containsAll(firstCell.getPossibleValues())
												|| !threeNumbers.containsAll(secondCell.getPossibleValues())
												|| !threeNumbers.containsAll(thirdCell.getPossibleValues())) {

											Set<Cell> cellToRemoveFromOne = new HashSet<>();
											cellToRemoveFromOne.add(nonetCells.get(firstComp));

											Set<Cell> cellToRemoveFromTwo = new HashSet<>();
											cellToRemoveFromTwo.add(nonetCells.get(secondComp));

											Set<Cell> cellsToRemoveFromThree = new HashSet<>();
											cellsToRemoveFromThree.add(nonetCells.get(thirdComp));

											List<Integer> valuesToRemoveFirst = new ArrayList<>(getCellsToRemove(
													nonetCells.get(firstComp).getPossibleValues(), threeNumbers));
											List<Integer> valuesToRemoveSecond = new ArrayList<>(getCellsToRemove(
													nonetCells.get(secondComp).getPossibleValues(), threeNumbers));

											List<Integer> valuesToRemoveThird = new ArrayList<>(getCellsToRemove(
													nonetCells.get(thirdComp).getPossibleValues(), threeNumbers));

											boolean firstCellChanged = false;
											boolean secondCellChanged = false;
											boolean thirdCellChanged = false;

											if (isThereAnythingToRemoveFromCells(
													getCellsToRemove(nonetCells.get(firstComp).getPossibleValues(),
															threeNumbers),
													cellToRemoveFromOne)) {

												firstCellChanged = true;

												Set<Integer> valuesChanged = new HashSet<>();
												if (!textShown) {
													result.append(nonetCells.get(firstComp) + ", "
															+ nonetCells.get(secondComp) + " and "
															+ nonetCells.get(thirdComp)
															+ " contain a hidden triple unique within nonet " + nonet
															+ " values candidates are " + threeNumbers
															+ " therefore the canidates must be assigned to the cells\nNONET:\n");
													textShown = true;
												} else {
													result.append("\n");
												}
												result.append(nonetCells.get(firstComp) + " removing ");
												for (int value = 0; value < valuesToRemoveFirst.size(); value++) {
													if (value == valuesToRemoveFirst.size() - 1) {
														result.append(valuesToRemoveFirst.get(value));
														valuesChanged.add(valuesToRemoveFirst.get(value));
														nonetCells.get(firstComp)
																.removePossibleValues(valuesToRemoveFirst.get(value));
														continue;
													}
													result.append(valuesToRemoveFirst.get(value) + "/");
													nonetCells.get(firstComp)
															.removePossibleValues(valuesToRemoveFirst.get(value));
													valuesChanged.add(valuesToRemoveFirst.get(value));

												}

												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(nonetCells.get(firstComp).getPossibleValues());

												cellsChanged.put(nonetCells.get(firstComp),
														new CellAdjustment(valuesChanged, candidatesUsed));

											}
											if (isThereAnythingToRemoveFromCells(
													getCellsToRemove(nonetCells.get(secondComp).getPossibleValues(),
															threeNumbers),
													cellToRemoveFromTwo)) {

												secondCellChanged = false;
												Set<Integer> valuesChanged = new HashSet<>();

												if (!textShown) {
													result.append(nonetCells.get(firstComp) + ", "
															+ nonetCells.get(secondComp) + " and "
															+ nonetCells.get(thirdComp)
															+ " contain a hidden triple unique within nonet " + nonet
															+ " values candidates are " + threeNumbers
															+ " therefore the canidates must be assigned to the cells\nNONET:\n");
													textShown = true;
												} else {
													result.append("\n");
												}

												result.append(nonetCells.get(secondComp) + " removing ");
												for (int value = 0; value < valuesToRemoveSecond.size(); value++) {
													if (value == valuesToRemoveSecond.size() - 1) {
														result.append(valuesToRemoveSecond.get(value));
														valuesChanged.add(valuesToRemoveSecond.get(value));
														nonetCells.get(secondComp)
																.removePossibleValues(valuesToRemoveSecond.get(value));
														continue;
													}
													result.append(valuesToRemoveSecond.get(value) + "/");
													nonetCells.get(secondComp)
															.removePossibleValues(valuesToRemoveSecond.get(value));
													valuesChanged.add(valuesToRemoveSecond.get(value));
												}

												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed
														.retainAll(nonetCells.get(secondComp).getPossibleValues());

												cellsChanged.put(nonetCells.get(secondComp),
														new CellAdjustment(valuesChanged, candidatesUsed));
											}

											if (isThereAnythingToRemoveFromCells(
													getCellsToRemove(nonetCells.get(thirdComp).getPossibleValues(),
															threeNumbers),
													cellsToRemoveFromThree)) {

												thirdCellChanged = true;

												Set<Integer> valuesChanged = new HashSet<>();

												if (!textShown) {
													result.append(nonetCells.get(firstComp) + ", "
															+ nonetCells.get(secondComp) + " and "
															+ nonetCells.get(thirdComp)
															+ " contain a hidden triple unique within nonet " + nonet
															+ " values candidates are " + threeNumbers
															+ " therefore the canidates must be assigned to the cells\nNONET:\n");
													textShown = true;
												} else {
													result.append("\n");
												}

												result.append(nonetCells.get(thirdComp) + " removing ");
												for (int value = 0; value < valuesToRemoveThird.size(); value++) {
													if (value == valuesToRemoveThird.size() - 1) {
														result.append(valuesToRemoveThird.get(value));
														valuesChanged.add(valuesToRemoveThird.get(value));
														nonetCells.get(thirdComp)
																.removePossibleValues(valuesToRemoveThird.get(value));
														continue;
													}
													result.append(valuesToRemoveThird.get(value) + "/");
													nonetCells.get(thirdComp)
															.removePossibleValues(valuesToRemoveThird.get(value));
													valuesChanged.add(valuesToRemoveThird.get(value));

												}

												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(nonetCells.get(thirdComp).getPossibleValues());

												cellsChanged.put(nonetCells.get(thirdComp),
														new CellAdjustment(valuesChanged, candidatesUsed));
											}

											if (cellsChanged.isEmpty())
												continue;

											if (!firstCellChanged) {
												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(nonetCells.get(firstComp).getPossibleValues());

												cellsChanged.put(nonetCells.get(firstComp),
														new CellAdjustment(new HashSet<>(), candidatesUsed));
											}
											if (!secondCellChanged) {
												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed
														.retainAll(nonetCells.get(secondComp).getPossibleValues());

												cellsChanged.put(nonetCells.get(secondComp),
														new CellAdjustment(new HashSet<>(), candidatesUsed));
											}
											if (!thirdCellChanged) {
												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(nonetCells.get(thirdComp).getPossibleValues());

												cellsChanged.put(nonetCells.get(thirdComp),
														new CellAdjustment(new HashSet<>(), candidatesUsed));
											}
											return new Logic(DIFFICULTY_EXPERIENCED, result.toString(), cellsChanged);
										}

									}
								}
							}
						}

					}
				}
			}
		}
		return null;
	}

	private Logic hiddenTriplesCol(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textShown = false;
		StringBuilder result = new StringBuilder();

		for (int col = 1; col <= SIZE; col++) {
			List<Cell> colCells = grid.getCols(col);
			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {

						Set<Integer> threeNumbers = new HashSet<>(Arrays.asList(firstVal, secondVal, thirdVal));

						for (int firstComp = 0; firstComp < colCells.size(); firstComp++) {
							Cell firstCell = colCells.get(firstComp);
							for (int secondComp = firstComp + 1; secondComp < colCells.size(); secondComp++) {
								Cell secondCell = colCells.get(secondComp);
								for (int thirdComp = secondComp + 1; thirdComp < colCells.size(); thirdComp++) {
									Cell thirdCell = colCells.get(thirdComp);

									Set<Cell> cellsWorkingWith = new HashSet<>();

									cellsWorkingWith.add(firstCell);
									cellsWorkingWith.add(secondCell);
									cellsWorkingWith.add(thirdCell);

									if(firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted())
										continue;

									
									if (uniqueToRegion(cellsWorkingWith, colCells, threeNumbers)) {
										if (!threeNumbers.containsAll(firstCell.getPossibleValues())
												|| !threeNumbers.containsAll(secondCell.getPossibleValues())
												|| !threeNumbers.containsAll(thirdCell.getPossibleValues())) {

											Set<Cell> cellToRemoveFromOne = new HashSet<>();
											cellToRemoveFromOne.add(colCells.get(firstComp));

											Set<Cell> cellToRemoveFromTwo = new HashSet<>();
											cellToRemoveFromTwo.add(colCells.get(secondComp));

											Set<Cell> cellsToRemoveFromThree = new HashSet<>();
											cellsToRemoveFromThree.add(colCells.get(thirdComp));

											List<Integer> valuesToRemoveFirst = new ArrayList<>(getCellsToRemove(
													colCells.get(firstComp).getPossibleValues(), threeNumbers));
											List<Integer> valuesToRemoveSecond = new ArrayList<>(getCellsToRemove(
													colCells.get(secondComp).getPossibleValues(), threeNumbers));

											List<Integer> valuesToRemoveThird = new ArrayList<>(getCellsToRemove(
													colCells.get(thirdComp).getPossibleValues(), threeNumbers));

											boolean firstCellChanged = false;
											boolean secondCellChanged = false;
											boolean ThirdCellChanged = false;

											if (isThereAnythingToRemoveFromCells(
													getCellsToRemove(colCells.get(firstComp).getPossibleValues(),
															threeNumbers),
													cellToRemoveFromOne)) {
												firstCellChanged = true;
												Set<Integer> valuesChanged = new HashSet<>();

												if (!textShown) {
													result.append(colCells.get(firstComp) + ", "
															+ colCells.get(secondComp) + " and "
															+ colCells.get(thirdComp)
															+ " contain a hidden triple unique within column " + col
															+ " values candidates are " + threeNumbers
															+ " therefore the canidates must be assigned to the cells\nCOLUMN:\n");

													textShown = true;
												} else {
													result.append("\n");
												}
												result.append(colCells.get(firstComp) + " removing ");
												for (int value = 0; value < valuesToRemoveFirst.size(); value++) {
													if (value == valuesToRemoveFirst.size() - 1) {
														result.append(valuesToRemoveFirst.get(value));
														valuesChanged.add(valuesToRemoveFirst.get(value));
														colCells.get(firstComp)
																.removePossibleValues(valuesToRemoveFirst.get(value));
														continue;
													}
													result.append(valuesToRemoveFirst.get(value) + "/");
													colCells.get(firstComp)
															.removePossibleValues(valuesToRemoveFirst.get(value));
													valuesChanged.add(valuesToRemoveFirst.get(value));
												}

												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(colCells.get(firstComp).getPossibleValues());

												cellsChanged.put(colCells.get(firstComp),
														new CellAdjustment(valuesChanged, candidatesUsed));

											}
											if (isThereAnythingToRemoveFromCells(
													getCellsToRemove(colCells.get(secondComp).getPossibleValues(),
															threeNumbers),
													cellToRemoveFromTwo)) {
												secondCellChanged = true;

												Set<Integer> valuesChanged = new HashSet<>();
												if (!textShown) {
													result.append(colCells.get(firstComp) + ", "
															+ colCells.get(secondComp) + " and "
															+ colCells.get(thirdComp)
															+ " contain a hidden triple unique within column " + col
															+ " values candidates are " + threeNumbers
															+ " therefore the canidates must be assigned to the cells\nCOLUMN:\n");

													textShown = true;
												} else {
													result.append("\n");
												}

												result.append(colCells.get(secondComp) + " removing ");
												for (int value = 0; value < valuesToRemoveSecond.size(); value++) {
													if (value == valuesToRemoveSecond.size() - 1) {
														result.append(valuesToRemoveSecond.get(value));
														valuesChanged.add(valuesToRemoveSecond.get(value));
														colCells.get(secondComp)
																.removePossibleValues(valuesToRemoveSecond.get(value));
														continue;
													}
													result.append(valuesToRemoveSecond.get(value) + "/");
													colCells.get(secondComp)
															.removePossibleValues(valuesToRemoveSecond.get(value));
													valuesChanged.add(valuesToRemoveSecond.get(value));

												}

												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(colCells.get(secondComp).getPossibleValues());

												cellsChanged.put(colCells.get(secondComp),
														new CellAdjustment(valuesChanged, candidatesUsed));
											}

											if (isThereAnythingToRemoveFromCells(
													getCellsToRemove(colCells.get(thirdComp).getPossibleValues(),
															threeNumbers),
													cellsToRemoveFromThree)) {
												ThirdCellChanged = true;
												Set<Integer> valuesChanged = new HashSet<>();

												if (!textShown) {
													result.append(colCells.get(firstComp) + ", "
															+ colCells.get(secondComp) + " and "
															+ colCells.get(thirdComp)
															+ " contain a hidden triple unique within column " + col
															+ " values candidates are " + threeNumbers
															+ " therefore the canidates must be assigned to the cells\nCOLUMN:\n");

													textShown = true;
												} else {
													result.append("\n");
												}

												result.append(colCells.get(thirdComp) + " removing ");
												for (int value = 0; value < valuesToRemoveThird.size(); value++) {
													if (value == valuesToRemoveThird.size() - 1) {
														result.append(valuesToRemoveThird.get(value));
														valuesChanged.add(valuesToRemoveThird.get(value));
														colCells.get(thirdComp)
																.removePossibleValues(valuesToRemoveThird.get(value));
														continue;
													}
													result.append(valuesToRemoveThird.get(value) + "/");
													colCells.get(thirdComp)
															.removePossibleValues(valuesToRemoveThird.get(value));
													valuesChanged.add(valuesToRemoveThird.get(value));

												}

												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(colCells.get(thirdComp).getPossibleValues());

												cellsChanged.put(colCells.get(thirdComp),
														new CellAdjustment(valuesChanged, candidatesUsed));
											}

											if (cellsChanged.isEmpty())
												continue;

											if (!firstCellChanged) {
												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(colCells.get(firstComp).getPossibleValues());

												cellsChanged.put(colCells.get(firstComp),
														new CellAdjustment(new HashSet<>(), candidatesUsed));
											}
											if (!secondCellChanged) {
												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(colCells.get(secondComp).getPossibleValues());

												cellsChanged.put(colCells.get(secondComp),
														new CellAdjustment(new HashSet<>(), candidatesUsed));
											}
											if (!ThirdCellChanged) {
												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(colCells.get(thirdComp).getPossibleValues());
												cellsChanged.put(colCells.get(thirdComp),
														new CellAdjustment(new HashSet<>(), candidatesUsed));
											}
											return new Logic(DIFFICULTY_EXPERIENCED, result.toString(), cellsChanged);

										}
									}
								}
							}

						}
					}
				}
			}

		}
		return null;

	}

	private Logic hiddenTriplesRow(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textShown = false;
		StringBuilder result = new StringBuilder();

		for (int row = 1; row <= SIZE; row++) {
			List<Cell> rowCells = grid.getRow(row);

			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {

						Set<Integer> threeNumbers = new HashSet<>(Arrays.asList(firstVal, secondVal, thirdVal));

						for (int firstComp = 0; firstComp < rowCells.size(); firstComp++) {
							Cell firstCell = rowCells.get(firstComp);
							for (int secondComp = firstComp + 1; secondComp < rowCells.size(); secondComp++) {
								Cell secondCell = rowCells.get(secondComp);
								for (int thirdComp = secondComp + 1; thirdComp < rowCells.size(); thirdComp++) {
									Cell thirdCell = rowCells.get(thirdComp);

									Set<Cell> cellsWorkingWith = new HashSet<>();

									cellsWorkingWith.add(firstCell);
									cellsWorkingWith.add(secondCell);
									cellsWorkingWith.add(thirdCell);

									if(firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted())
										continue;

									
									if (uniqueToRegion(cellsWorkingWith, rowCells, threeNumbers)) {

										if (!threeNumbers.containsAll(firstCell.getPossibleValues())
												|| !threeNumbers.containsAll(secondCell.getPossibleValues())
												|| !threeNumbers.containsAll(thirdCell.getPossibleValues())) {

											Set<Cell> cellToRemoveFromOne = new HashSet<>();
											cellToRemoveFromOne.add(rowCells.get(firstComp));

											Set<Cell> cellToRemoveFromTwo = new HashSet<>();
											cellToRemoveFromTwo.add(rowCells.get(secondComp));

											Set<Cell> cellsToRemoveFromThree = new HashSet<>();
											cellsToRemoveFromThree.add(rowCells.get(thirdComp));

											List<Integer> valuesToRemoveFirst = new ArrayList<>(getCellsToRemove(
													rowCells.get(firstComp).getPossibleValues(), threeNumbers));
											List<Integer> valuesToRemoveSecond = new ArrayList<>(getCellsToRemove(
													rowCells.get(secondComp).getPossibleValues(), threeNumbers));

											List<Integer> valuesToRemoveThird = new ArrayList<>(getCellsToRemove(
													rowCells.get(thirdComp).getPossibleValues(), threeNumbers));

											boolean firstCellChanged = false;
											boolean secondCellChanged = false;
											boolean thirdCellChanged = false;

											if (isThereAnythingToRemoveFromCells(
													getCellsToRemove(rowCells.get(firstComp).getPossibleValues(),
															threeNumbers),
													cellToRemoveFromOne)) {
												firstCellChanged = true;

												Set<Integer> valuesChanged = new HashSet<>();
												if (!textShown) {
													result.append(rowCells.get(firstComp) + ", "
															+ rowCells.get(secondComp) + " and "
															+ rowCells.get(thirdComp)
															+ " contain a hidden triple unique within row " + row
															+ " values candidates are " + threeNumbers
															+ " therefore the canidates must be assigned to the cells\nROW:\n");

													textShown = false;
												} else {
													result.append("\n");
												}
												result.append(rowCells.get(firstComp) + " removing ");
												for (int value = 0; value < valuesToRemoveFirst.size(); value++) {
													if (value == valuesToRemoveFirst.size() - 1) {
														result.append(valuesToRemoveFirst.get(value));
														valuesChanged.add(valuesToRemoveFirst.get(value));
														rowCells.get(firstComp)
																.removePossibleValues(valuesToRemoveFirst.get(value));
														continue;
													}
													result.append(valuesToRemoveFirst.get(value) + "/");
													rowCells.get(firstComp)
															.removePossibleValues(valuesToRemoveFirst.get(value));
													valuesChanged.add(valuesToRemoveFirst.get(value));

												}

												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(rowCells.get(firstComp).getPossibleValues());

												cellsChanged.put(rowCells.get(firstComp),
														new CellAdjustment(valuesChanged, candidatesUsed));

											}
											if (isThereAnythingToRemoveFromCells(
													getCellsToRemove(rowCells.get(secondComp).getPossibleValues(),
															threeNumbers),
													cellToRemoveFromTwo)) {
												secondCellChanged = true;

												if (!textShown) {
													result.append(rowCells.get(firstComp) + ", "
															+ rowCells.get(secondComp) + " and "
															+ rowCells.get(thirdComp)
															+ " contain a hidden triple unique within row " + row
															+ " values candidates are " + threeNumbers
															+ " therefore the canidates must be assigned to the cells\nROW:\n");

													textShown = false;
												} else {
													result.append("\n");
												}
												Set<Integer> valuesChanged = new HashSet<>();

												result.append(rowCells.get(secondComp) + " removing ");
												for (int value = 0; value < valuesToRemoveSecond.size(); value++) {
													if (value == valuesToRemoveSecond.size() - 1) {
														result.append(valuesToRemoveSecond.get(value));
														valuesChanged.add(valuesToRemoveSecond.get(value));
														rowCells.get(secondComp)
																.removePossibleValues(valuesToRemoveSecond.get(value));
														continue;
													}
													result.append(valuesToRemoveSecond.get(value) + "/");
													rowCells.get(secondComp)
															.removePossibleValues(valuesToRemoveSecond.get(value));
													valuesChanged.add(valuesToRemoveSecond.get(value));

												}
												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(rowCells.get(secondComp).getPossibleValues());

												cellsChanged.put(rowCells.get(secondComp),
														new CellAdjustment(valuesChanged, candidatesUsed));
											}
											if (isThereAnythingToRemoveFromCells(
													getCellsToRemove(rowCells.get(thirdComp).getPossibleValues(),
															threeNumbers),
													cellsToRemoveFromThree)) {

												thirdCellChanged = true;
												Set<Integer> valuesChanged = new HashSet<>();

												if (!textShown) {
													result.append(rowCells.get(firstComp) + ", "
															+ rowCells.get(secondComp) + " and "
															+ rowCells.get(thirdComp)
															+ " contain a hidden triple unique within row " + row
															+ " values candidates are " + threeNumbers
															+ " therefore the canidates must be assigned to the cells\nROW:\n");

													textShown = false;
												} else {
													result.append("\n");
												}

												result.append(rowCells.get(thirdComp) + " removing ");
												for (int value = 0; value < valuesToRemoveThird.size(); value++) {
													if (value == valuesToRemoveThird.size() - 1) {
														result.append(valuesToRemoveThird.get(value));
														valuesChanged.add(valuesToRemoveThird.get(value));
														rowCells.get(thirdComp)
																.removePossibleValues(valuesToRemoveThird.get(value));
														continue;
													}
													result.append(valuesToRemoveThird.get(value) + "/");
													rowCells.get(thirdComp)
															.removePossibleValues(valuesToRemoveThird.get(value));
													valuesChanged.add(valuesToRemoveThird.get(value));

												}
												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(rowCells.get(thirdComp).getPossibleValues());

												cellsChanged.put(rowCells.get(thirdComp),
														new CellAdjustment(valuesChanged, candidatesUsed));
											}

											if (cellsChanged.isEmpty())
												continue;

											if (!firstCellChanged) {
												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(rowCells.get(firstComp).getPossibleValues());

												cellsChanged.put(rowCells.get(firstComp),
														new CellAdjustment(new HashSet<>(), candidatesUsed));
											}
											if (!secondCellChanged) {
												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(rowCells.get(secondComp).getPossibleValues());

												cellsChanged.put(rowCells.get(secondComp),
														new CellAdjustment(new HashSet<>(), candidatesUsed));
											}
											if (!thirdCellChanged) {
												Set<Integer> candidatesUsed = new HashSet<>(threeNumbers);
												candidatesUsed.retainAll(rowCells.get(thirdComp).getPossibleValues());

												cellsChanged.put(rowCells.get(thirdComp),
														new CellAdjustment(new HashSet<>(), candidatesUsed));
											}
											return new Logic(DIFFICULTY_EXPERIENCED, result.toString(), cellsChanged);

										}
									}
								}
							}

						}

					}
				}
			}
		}
		return null;
	}

	public Logic nakedQuads(KillerSudokuGrid killerSudokuGrid) {
		Logic result = nakedQuadsRegions();
		if (result != null) {
			return result;
		}
		return null;
	}

	public Logic hiddenQuads(KillerSudokuGrid killerSudokuGrid) {
		Logic result = hiddenQuadsRegions();
		if (result != null) {
			return result;
		}
		return null;
	}

	private Logic nakedQuadsRegions() {
		Logic result = nakedQuadsRow();
		if (result != null)
			return result;

		result = nakedQuadsCol();
		if (result != null)
			return result;

		result = nakedQuadsNonet();
		if (result != null)
			return result;
		return null;
	}

	private Logic nakedQuadsNonet() {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textDisplayed = false;
		StringBuilder result = new StringBuilder();

		for (int nonet = 1; nonet <= SIZE; nonet++) {
			List<Cell> nonetCells = grid.getNoNetCells(nonet);

			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {
						for (int fourthVal = thirdVal + 1; fourthVal <= SIZE; fourthVal++) {

							Set<Integer> fourNumbers = new HashSet<>(
									Arrays.asList(firstVal, secondVal, thirdVal, fourthVal));
							for (int firstComp = 0; firstComp < nonetCells.size(); firstComp++) {
								Cell firstCell = nonetCells.get(firstComp);
								for (int secondComp = firstComp + 1; secondComp < nonetCells.size(); secondComp++) {
									Cell secondCell = nonetCells.get(secondComp);
									for (int thirdComp = secondComp + 1; thirdComp < nonetCells.size(); thirdComp++) {
										Cell thirdCell = nonetCells.get(thirdComp);
										for (int fourthComp = thirdComp + 1; fourthComp < nonetCells
												.size(); fourthComp++) {
											Cell fourthCell = nonetCells.get(fourthComp);

											if (firstCell.isCommitted() || secondCell.isCommitted()
													|| thirdCell.isCommitted() || fourthCell.isCommitted())
												continue;

											Set<Cell> cellsWorkingWith = new HashSet<>();

											cellsWorkingWith.add(firstCell);
											cellsWorkingWith.add(secondCell);
											cellsWorkingWith.add(thirdCell);
											cellsWorkingWith.add(fourthCell);

											if (!uniqueToRegion(cellsWorkingWith, nonetCells, fourNumbers)) {

												if (cellsOnlyContainNumbers(cellsWorkingWith, fourNumbers)) {

													Set<Cell> cellsToCheck = new HashSet<>();
													cellsToCheck.addAll(nonetCells);
													cellsToCheck.removeAll(cellsWorkingWith);

													if (isThereAnythingToRemoveFromCells(fourNumbers, cellsToCheck)) {

														for (Cell currentCell : cellsToCheck) {

															List<Integer> valuesToRemove = new ArrayList<>(fourNumbers);
															valuesToRemove.retainAll(currentCell.getPossibleValues());

															if (valuesToRemove.isEmpty())
																continue;

															if (!currentCell.isCommitted()) {
																Set<Integer> valuesChanged = new HashSet<>();

																if (!textDisplayed) {
																	result.append(nonetCells.get(firstComp) + ", "
																			+ nonetCells.get(secondComp) + ",  "
																			+ nonetCells.get(thirdComp) + " and "
																			+ nonetCells.get(fourthComp)
																			+ " contain a naked quad unique within nonet "
																			+ nonet + " values candidates are "
																			+ fourNumbers
																			+ " therefore the candidates must be removed from the cells\nNONET:\n");
																	textDisplayed = true;
																}

																result.append(currentCell + " removing ");
																for (int value = 0; value < valuesToRemove
																		.size(); value++) {

																	if (value == valuesToRemove.size() - 1) {
																		result.append(valuesToRemove.get(value));
																		valuesChanged.add(valuesToRemove.get(value));
																		currentCell.removePossibleValues(
																				valuesToRemove.get(value));
																		result.append("\n");
																		continue;
																	}

																	result.append(valuesToRemove.get(value) + "/");
																	currentCell.removePossibleValues(
																			valuesToRemove.get(value));
																	valuesChanged.add(valuesToRemove.get(value));

																}
																cellsChanged.put(currentCell, new CellAdjustment(
																		valuesChanged, new HashSet<>()));
															}

														}
														if (cellsChanged.isEmpty())
															continue;

														for (Cell currentCell : cellsWorkingWith) {
															Set<Integer> candidates = new HashSet<>(fourNumbers);
															candidates.retainAll(currentCell.getPossibleValues());

															cellsChanged.put(currentCell,
																	new CellAdjustment(new HashSet<>(), candidates));
														}

														return new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
																cellsChanged);
													}

												}

											}

										}
									}
								}
							}
						}
					}
				}
			}
		}

		return null;

	}

	private Logic nakedQuadsCol() {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textDisplayed = false;
		StringBuilder result = new StringBuilder();

		for (int col = 1; col <= SIZE; col++) {
			List<Cell> colCells = grid.getCols(col);

			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {
						for (int fourthVal = thirdVal + 1; fourthVal <= SIZE; fourthVal++) {

							Set<Integer> fourNumbers = new HashSet<>(
									Arrays.asList(firstVal, secondVal, thirdVal, fourthVal));
							for (int firstComp = 0; firstComp < colCells.size(); firstComp++) {
								Cell firstCell = colCells.get(firstComp);
								for (int secondComp = firstComp + 1; secondComp < colCells.size(); secondComp++) {
									Cell secondCell = colCells.get(secondComp);
									for (int thirdComp = secondComp + 1; thirdComp < colCells.size(); thirdComp++) {
										Cell thirdCell = colCells.get(thirdComp);
										for (int fourthComp = thirdComp + 1; fourthComp < colCells
												.size(); fourthComp++) {
											Cell fourthCell = colCells.get(fourthComp);

											if (firstCell.isCommitted() || secondCell.isCommitted()
													|| thirdCell.isCommitted() || fourthCell.isCommitted())
												continue;
											Set<Cell> cellsWorkingWith = new HashSet<>();

											cellsWorkingWith.add(firstCell);
											cellsWorkingWith.add(secondCell);
											cellsWorkingWith.add(thirdCell);
											cellsWorkingWith.add(fourthCell);

											if (!uniqueToRegion(cellsWorkingWith, colCells, fourNumbers)) {

												if (cellsOnlyContainNumbers(cellsWorkingWith, fourNumbers)) {

													Set<Cell> cellsToCheck = new HashSet<>();
													cellsToCheck.addAll(colCells);
													cellsToCheck.removeAll(cellsWorkingWith);

													if (isThereAnythingToRemoveFromCells(fourNumbers, cellsToCheck)) {

														for (Cell currentCell : cellsToCheck) {

															List<Integer> valuesToRemove = new ArrayList<>(fourNumbers);
															valuesToRemove.retainAll(currentCell.getPossibleValues());

															if (valuesToRemove.isEmpty())
																continue;

															if (!currentCell.isCommitted()) {
																Set<Integer> valuesChanged = new HashSet<>();

																if (!textDisplayed) {
																	result.append(colCells.get(firstComp) + ", "
																			+ colCells.get(secondComp) + ",  "
																			+ colCells.get(thirdComp) + " and "
																			+ colCells.get(fourthComp)
																			+ " contain a naked quad unique within col "
																			+ col + " values candidates are "
																			+ fourNumbers
																			+ " therefore the candidates must be removed from the cells\nCOLUMN:\n");
																	textDisplayed = true;
																}

																result.append(currentCell + " removing ");
																for (int value = 0; value < valuesToRemove
																		.size(); value++) {
																	if (value == valuesToRemove.size() - 1) {
																		result.append(valuesToRemove.get(value));
																		valuesChanged.add(valuesToRemove.get(value));
																		currentCell.removePossibleValues(
																				valuesToRemove.get(value));
																		result.append("\n");
																		continue;
																	}
																	result.append(valuesToRemove.get(value) + "/");
																	currentCell.removePossibleValues(
																			valuesToRemove.get(value));
																	valuesChanged.add(valuesToRemove.get(value));

																}
																cellsChanged.put(currentCell, new CellAdjustment(
																		valuesChanged, new HashSet<>()));
															}
														}
														if (cellsChanged.isEmpty())
															continue;

														for (Cell currentCell : cellsWorkingWith) {

															Set<Integer> candidates = new HashSet<>(fourNumbers);
															candidates.retainAll(currentCell.getPossibleValues());

															cellsChanged.put(currentCell,
																	new CellAdjustment(new HashSet<>(), candidates));
														}

														return new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
																cellsChanged);
													}

												}
											}

										}
									}
								}
							}

						}
					}
				}
			}
		}
		return null;

	}

	private Logic nakedQuadsRow() {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textDisplayed = false;
		StringBuilder result = new StringBuilder();

		for (int row = 1; row <= SIZE; row++) {
			List<Cell> rowCells = grid.getRow(row);

			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {
						for (int fourthVal = thirdVal + 1; fourthVal <= SIZE; fourthVal++) {

							Set<Integer> fourNumbers = new HashSet<>(
									Arrays.asList(firstVal, secondVal, thirdVal, fourthVal));

							for (int firstComp = 0; firstComp < rowCells.size(); firstComp++) {
								Cell firstCell = rowCells.get(firstComp);
								for (int secondComp = firstComp + 1; secondComp < rowCells.size(); secondComp++) {
									Cell secondCell = rowCells.get(secondComp);
									for (int thirdComp = secondComp + 1; thirdComp < rowCells.size(); thirdComp++) {
										Cell thirdCell = rowCells.get(thirdComp);
										for (int fourthComp = thirdComp + 1; fourthComp < rowCells
												.size(); fourthComp++) {
											Cell fourthCell = rowCells.get(fourthComp);

											if (firstCell.isCommitted() || secondCell.isCommitted()
													|| thirdCell.isCommitted() || fourthCell.isCommitted())
												continue;

											Set<Cell> cellsWorkingWith = new HashSet<>();

											cellsWorkingWith.add(firstCell);
											cellsWorkingWith.add(secondCell);
											cellsWorkingWith.add(thirdCell);
											cellsWorkingWith.add(fourthCell);

											if (!uniqueToRegion(cellsWorkingWith, rowCells, fourNumbers)) {

												if (cellsOnlyContainNumbers(cellsWorkingWith, fourNumbers)) {

													Set<Cell> cellsToCheck = new HashSet<>();
													cellsToCheck.addAll(rowCells);
													cellsToCheck.removeAll(cellsWorkingWith);

													if (isThereAnythingToRemoveFromCells(fourNumbers, cellsToCheck)) {

														for (Cell currentCell : cellsToCheck) {

															List<Integer> valuesToRemove = new ArrayList<>(fourNumbers);
															valuesToRemove.retainAll(currentCell.getPossibleValues());

															if (valuesToRemove.isEmpty())
																continue;

															if (!currentCell.isCommitted()) {

																Set<Integer> valuesChanged = new HashSet<>();

																if (!textDisplayed) {
																	result.append(rowCells.get(firstComp) + ", "
																			+ rowCells.get(secondComp) + ",  "
																			+ rowCells.get(thirdComp) + " and "
																			+ rowCells.get(fourthComp)
																			+ " contain a naked quad unique within row "
																			+ row + " values candidates are "
																			+ fourNumbers
																			+ " therefore the candidates must be removed from all other cells in the row\nROW:\n");
																	textDisplayed = true;
																}

																result.append(currentCell + " removing ");
																for (int value = 0; value < valuesToRemove
																		.size(); value++) {
																	if (value == valuesToRemove.size() - 1) {
																		result.append(valuesToRemove.get(value));
																		valuesChanged.add(valuesToRemove.get(value));
																		currentCell.removePossibleValues(
																				valuesToRemove.get(value));
																		result.append("\n");
																		continue;
																	}
																	result.append(valuesToRemove.get(value) + "/");
																	currentCell.removePossibleValues(
																			valuesToRemove.get(value));
																	valuesChanged.add(valuesToRemove.get(value));

																}
																cellsChanged.put(currentCell, new CellAdjustment(
																		valuesChanged, new HashSet<>()));
															}
														}
														if (cellsChanged.isEmpty())
															continue;

														for (Cell currentCell : cellsWorkingWith) {
															Set<Integer> candidates = new HashSet<>(fourNumbers);
															candidates.retainAll(currentCell.getPossibleValues());

															cellsChanged.put(currentCell,
																	new CellAdjustment(new HashSet<>(), candidates));
														}

														return new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
																cellsChanged);
													}

												}
											}

										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private boolean cellsOnlyContainNumbers(Set<Cell> cellsWorkingWith, Set<Integer> numbers) {
		for (Cell currentCell : cellsWorkingWith) {
			Set<Integer> cellValues = new HashSet<>(currentCell.getPossibleValues());

			if (!numbers.containsAll(cellValues)) {
				return false;
			}
		}
		return true;
	}

	private Logic hiddenQuadsRegions() {
		Logic result = hiddenQuadsRow();
		if (result != null)
			return result;

		result = hiddenQuadsCol();
		if (result != null)
			return result;

		result = hiddenQuadsNonet();
		if (result != null)
			return result;

		return null;
	}

	private Logic hiddenQuadsNonet() {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textDisplayed = false;
		StringBuilder result = new StringBuilder();

		for (int nonet = 1; nonet <= SIZE; nonet++) {
			List<Cell> nonetCells = grid.getNoNetCells(nonet);

			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {
						for (int fourthVal = thirdVal + 1; fourthVal <= SIZE; fourthVal++) {

							Set<Integer> fourNumbers = new HashSet<>(
									Arrays.asList(firstVal, secondVal, thirdVal, fourthVal));

							for (int firstComp = 0; firstComp < nonetCells.size(); firstComp++) {
								Cell firstCell = nonetCells.get(firstComp);
								for (int secondComp = firstComp + 1; secondComp < nonetCells.size(); secondComp++) {
									Cell secondCell = nonetCells.get(secondComp);
									for (int thirdComp = secondComp + 1; thirdComp < nonetCells.size(); thirdComp++) {
										Cell thirdCell = nonetCells.get(thirdComp);
										for (int fourthComp = thirdComp + 1; fourthComp < nonetCells
												.size(); fourthComp++) {
											Cell fourthCell = nonetCells.get(fourthComp);

											Set<Cell> cellsWorkingWith = new HashSet<>();

											cellsWorkingWith.add(firstCell);
											cellsWorkingWith.add(secondCell);
											cellsWorkingWith.add(thirdCell);
											cellsWorkingWith.add(fourthCell);
											if(firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted()|| fourthCell.isCommitted())
												continue;

											if (uniqueToRegion(cellsWorkingWith, nonetCells, fourNumbers)) {
												if (!fourNumbers.containsAll(firstCell.getPossibleValues())
														|| !fourNumbers.containsAll(secondCell.getPossibleValues())
														|| !fourNumbers.containsAll(thirdCell.getPossibleValues())
														|| !fourNumbers.containsAll(fourthCell.getPossibleValues())) {

													Set<Cell> cellsToRemoveFromOne = new HashSet<>();
													cellsToRemoveFromOne.add(nonetCells.get(firstComp));

													Set<Cell> cellsToRemoveFromTwo = new HashSet<>();
													cellsToRemoveFromTwo.add(nonetCells.get(secondComp));

													Set<Cell> cellsToRemoveFromThree = new HashSet<>();
													cellsToRemoveFromThree.add(nonetCells.get(thirdComp));

													Set<Cell> cellsToRemoveFromFour = new HashSet<>();
													cellsToRemoveFromFour.add(nonetCells.get(fourthComp));

													List<Integer> valuesToRemoveFirst = new ArrayList<>(
															getCellsToRemove(
																	nonetCells.get(firstComp).getPossibleValues(),
																	fourNumbers));
													List<Integer> valuesToRemoveSecond = new ArrayList<>(
															getCellsToRemove(
																	nonetCells.get(secondComp).getPossibleValues(),
																	fourNumbers));

													List<Integer> valuesToRemoveThird = new ArrayList<>(
															getCellsToRemove(
																	nonetCells.get(thirdComp).getPossibleValues(),
																	fourNumbers));

													List<Integer> valuesToRemoveFour = new ArrayList<>(getCellsToRemove(
															nonetCells.get(fourthComp).getPossibleValues(),
															fourNumbers));

													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															nonetCells.get(firstComp).getPossibleValues(), fourNumbers),
															cellsToRemoveFromOne)) {

														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(nonetCells.get(firstComp) + ", "
																	+ nonetCells.get(secondComp) + ",  "
																	+ nonetCells.get(thirdComp) + " and "
																	+ nonetCells.get(fourthComp)
																	+ " contain a hidden quad unique within nonet "
																	+ nonet + " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nNONET:\n");
															textDisplayed = true;
														}

														result.append(nonetCells.get(firstComp) + " removing ");
														for (int value = 0; value < valuesToRemoveFirst
																.size(); value++) {
															if (value == valuesToRemoveFirst.size() - 1) {
																result.append(valuesToRemoveFirst.get(value));
																valuesChanged.add(valuesToRemoveFirst.get(value));
																nonetCells.get(firstComp).removePossibleValues(
																		valuesToRemoveFirst.get(value));
																continue;
															}
															result.append(valuesToRemoveFirst.get(value) + "/");
															nonetCells.get(firstComp).removePossibleValues(
																	valuesToRemoveFirst.get(value));
															valuesChanged.add(valuesToRemoveFirst.get(value));

														}

														cellsChanged.put(nonetCells.get(firstComp),
																new CellAdjustment(valuesChanged, fourNumbers));
													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																nonetCells.get(firstComp).getPossibleValues());
														cellsChanged.put(nonetCells.get(firstComp),
																new CellAdjustment(new HashSet<>(), valuesContained));
													}
													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															nonetCells.get(secondComp).getPossibleValues(),
															fourNumbers), cellsToRemoveFromTwo)) {

														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(nonetCells.get(firstComp) + ", "
																	+ nonetCells.get(secondComp) + ",  "
																	+ nonetCells.get(thirdComp) + " and "
																	+ nonetCells.get(fourthComp)
																	+ " contain a hidden quad unique within nonet "
																	+ nonet + " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nNonet:\n");
															textDisplayed = true;
														} else {
															result.append("\n");
														}

														result.append(nonetCells.get(secondComp) + " removing ");
														for (int value = 0; value < valuesToRemoveSecond
																.size(); value++) {
															if (value == valuesToRemoveSecond.size() - 1) {
																result.append(valuesToRemoveSecond.get(value));
																valuesChanged.add(valuesToRemoveSecond.get(value));
																nonetCells.get(secondComp).removePossibleValues(
																		valuesToRemoveSecond.get(value));
																continue;
															}
															result.append(valuesToRemoveSecond.get(value) + "/");
															nonetCells.get(secondComp).removePossibleValues(
																	valuesToRemoveSecond.get(value));
															valuesChanged.add(valuesToRemoveSecond.get(value));

														}
														cellsChanged.put(nonetCells.get(secondComp),
																new CellAdjustment(valuesChanged, fourNumbers));
													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																nonetCells.get(secondComp).getPossibleValues());
														cellsChanged.put(nonetCells.get(secondComp),
																new CellAdjustment(new HashSet<>(), valuesContained));
													}
													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															nonetCells.get(thirdComp).getPossibleValues(), fourNumbers),
															cellsToRemoveFromThree)) {

														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(nonetCells.get(firstComp) + ", "
																	+ nonetCells.get(secondComp) + ",  "
																	+ nonetCells.get(thirdComp) + " and "
																	+ nonetCells.get(fourthComp)
																	+ " contain a hidden quad unique within nonet "
																	+ nonet + " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nNonet:\n");
															textDisplayed = true;
														} else {
															result.append("\n");
														}

														result.append(nonetCells.get(thirdComp) + " removing ");
														for (int value = 0; value < valuesToRemoveThird
																.size(); value++) {
															if (value == valuesToRemoveThird.size() - 1) {
																result.append(valuesToRemoveThird.get(value));
																valuesChanged.add(valuesToRemoveThird.get(value));
																nonetCells.get(thirdComp).removePossibleValues(
																		valuesToRemoveThird.get(value));
																continue;
															}
															result.append(valuesToRemoveThird.get(value) + "/");
															nonetCells.get(thirdComp).removePossibleValues(
																	valuesToRemoveThird.get(value));
															valuesChanged.add(valuesToRemoveThird.get(value));

														}
														cellsChanged.put(nonetCells.get(thirdComp),
																new CellAdjustment(valuesChanged, fourNumbers));
													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																nonetCells.get(thirdComp).getPossibleValues());
														cellsChanged.put(nonetCells.get(thirdComp),
																new CellAdjustment(new HashSet<>(), valuesContained));
													}
													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															nonetCells.get(fourthComp).getPossibleValues(),
															fourNumbers), cellsToRemoveFromFour)) {
														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(nonetCells.get(firstComp) + ", "
																	+ nonetCells.get(secondComp) + ",  "
																	+ nonetCells.get(thirdComp) + " and "
																	+ nonetCells.get(fourthComp)
																	+ " contain a hidden quad unique within nonet "
																	+ nonet + " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nNonet:\n");
															textDisplayed = true;
														} else {
															result.append("\n");
														}

														result.append(nonetCells.get(fourthComp) + " removing ");
														for (int value = 0; value < valuesToRemoveFour
																.size(); value++) {
															if (value == valuesToRemoveFour.size() - 1) {
																result.append(valuesToRemoveFour.get(value));
																valuesChanged.add(valuesToRemoveFour.get(value));
																nonetCells.get(fourthComp).removePossibleValues(
																		valuesToRemoveFour.get(value));
																continue;
															}
															result.append(valuesToRemoveFour.get(value) + "/");
															nonetCells.get(fourthComp).removePossibleValues(
																	valuesToRemoveFour.get(value));
															valuesChanged.add(valuesToRemoveFour.get(value));

														}
														cellsChanged.put(nonetCells.get(fourthComp),
																new CellAdjustment(valuesChanged, fourNumbers));
													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																nonetCells.get(fourthComp).getPossibleValues());
														cellsChanged.put(nonetCells.get(fourthComp),
																new CellAdjustment(new HashSet<>(), valuesContained));
													}
													if (cellsChanged.isEmpty())
														continue;

													return new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
															cellsChanged);

												}

											}
										}
									}
								}
							}
						}
					}
				}
			}

		}

		return null;

	}

	private Logic hiddenQuadsCol() {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textDisplayed = false;
		StringBuilder result = new StringBuilder();

		for (int col = 1; col <= SIZE; col++) {
			List<Cell> colCells = grid.getCols(col);

			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {
						for (int fourthVal = thirdVal + 1; fourthVal <= SIZE; fourthVal++) {

							Set<Integer> fourNumbers = new HashSet<>(
									Arrays.asList(firstVal, secondVal, thirdVal, fourthVal));

							for (int firstComp = 0; firstComp < colCells.size(); firstComp++) {
								Cell firstCell = colCells.get(firstComp);
								for (int secondComp = firstComp + 1; secondComp < colCells.size(); secondComp++) {
									Cell secondCell = colCells.get(secondComp);
									for (int thirdComp = secondComp + 1; thirdComp < colCells.size(); thirdComp++) {
										Cell thirdCell = colCells.get(thirdComp);
										for (int fourthComp = thirdComp + 1; fourthComp < colCells
												.size(); fourthComp++) {
											Cell fourthCell = colCells.get(fourthComp);

											Set<Cell> cellsWorkingWith = new HashSet<>();

											cellsWorkingWith.add(firstCell);
											cellsWorkingWith.add(secondCell);
											cellsWorkingWith.add(thirdCell);
											cellsWorkingWith.add(fourthCell);

											
											if(firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted()|| fourthCell.isCommitted())
												continue;
											if (uniqueToRegion(cellsWorkingWith, colCells, fourNumbers)) {
												if (!fourNumbers.containsAll(firstCell.getPossibleValues())
														|| !fourNumbers.containsAll(secondCell.getPossibleValues())
														|| !fourNumbers.containsAll(thirdCell.getPossibleValues())
														|| !fourNumbers.containsAll(fourthCell.getPossibleValues())) {

													Set<Cell> cellsToRemoveFromOne = new HashSet<>();
													cellsToRemoveFromOne.add(colCells.get(firstComp));

													Set<Cell> cellsToRemoveFromTwo = new HashSet<>();
													cellsToRemoveFromTwo.add(colCells.get(secondComp));

													Set<Cell> cellsToRemoveFromThree = new HashSet<>();
													cellsToRemoveFromThree.add(colCells.get(thirdComp));

													Set<Cell> cellsToRemoveFromFour = new HashSet<>();
													cellsToRemoveFromFour.add(colCells.get(fourthComp));

													List<Integer> valuesToRemoveFirst = new ArrayList<>(
															getCellsToRemove(
																	colCells.get(firstComp).getPossibleValues(),
																	fourNumbers));
													List<Integer> valuesToRemoveSecond = new ArrayList<>(
															getCellsToRemove(
																	colCells.get(secondComp).getPossibleValues(),
																	fourNumbers));

													List<Integer> valuesToRemoveThird = new ArrayList<>(
															getCellsToRemove(
																	colCells.get(thirdComp).getPossibleValues(),
																	fourNumbers));

													List<Integer> valuesToRemoveFour = new ArrayList<>(getCellsToRemove(
															colCells.get(fourthComp).getPossibleValues(), fourNumbers));

													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															colCells.get(firstComp).getPossibleValues(), fourNumbers),
															cellsToRemoveFromOne)) {

														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(colCells.get(firstComp) + ", "
																	+ colCells.get(secondComp) + ",  "
																	+ colCells.get(thirdComp) + " and "
																	+ colCells.get(fourthComp)
																	+ " contain a hidden quad unique within col " + col
																	+ " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nCOLUMN:\n");
															textDisplayed = true;
														}

														result.append(colCells.get(firstComp) + " removing ");
														for (int value = 0; value < valuesToRemoveFirst
																.size(); value++) {
															if (value == valuesToRemoveFirst.size() - 1) {
																result.append(valuesToRemoveFirst.get(value));
																valuesChanged.add(valuesToRemoveFirst.get(value));
																colCells.get(firstComp).removePossibleValues(
																		valuesToRemoveFirst.get(value));
																continue;
															}
															result.append(valuesToRemoveFirst.get(value) + "/");
															colCells.get(firstComp).removePossibleValues(
																	valuesToRemoveFirst.get(value));
															valuesChanged.add(valuesToRemoveFirst.get(value));

														}
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained
																.retainAll(colCells.get(firstComp).getPossibleValues());
														cellsChanged.put(colCells.get(firstComp),
																new CellAdjustment(valuesChanged, valuesContained));
													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained
																.retainAll(colCells.get(firstComp).getPossibleValues());
														cellsChanged.put(colCells.get(firstComp),
																new CellAdjustment(new HashSet<>(), valuesContained));
													}
													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															colCells.get(secondComp).getPossibleValues(), fourNumbers),
															cellsToRemoveFromTwo)) {

														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(colCells.get(firstComp) + ", "
																	+ colCells.get(secondComp) + ",  "
																	+ colCells.get(thirdComp) + " and "
																	+ colCells.get(fourthComp)
																	+ " contain a hidden quad unique within col " + col
																	+ " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nCOLUMN:\n");
															textDisplayed = true;
														} else {
															result.append("\n");
														}

														result.append(colCells.get(secondComp) + " removing ");
														for (int value = 0; value < valuesToRemoveSecond
																.size(); value++) {
															if (value == valuesToRemoveSecond.size() - 1) {
																result.append(valuesToRemoveSecond.get(value));
																valuesChanged.add(valuesToRemoveSecond.get(value));
																colCells.get(secondComp).removePossibleValues(
																		valuesToRemoveSecond.get(value));
																continue;
															}
															result.append(valuesToRemoveSecond.get(value) + "/");
															colCells.get(secondComp).removePossibleValues(
																	valuesToRemoveSecond.get(value));
															valuesChanged.add(valuesToRemoveSecond.get(value));

														}
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																colCells.get(secondComp).getPossibleValues());
														cellsChanged.put(colCells.get(secondComp),
																new CellAdjustment(valuesChanged, valuesContained));
													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																colCells.get(secondComp).getPossibleValues());
														cellsChanged.put(colCells.get(secondComp),
																new CellAdjustment(new HashSet<>(), valuesContained));
													}
													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															colCells.get(thirdComp).getPossibleValues(), fourNumbers),
															cellsToRemoveFromThree)) {

														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(colCells.get(firstComp) + ", "
																	+ colCells.get(secondComp) + ",  "
																	+ colCells.get(thirdComp) + " and "
																	+ colCells.get(fourthComp)
																	+ " contain a hidden quad unique within col " + col
																	+ " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nCOLUMN:\n");
															textDisplayed = true;
														} else {
															result.append("\n");
														}

														result.append(colCells.get(thirdComp) + " removing ");
														for (int value = 0; value < valuesToRemoveThird
																.size(); value++) {
															if (value == valuesToRemoveThird.size() - 1) {
																result.append(valuesToRemoveThird.get(value));
																valuesChanged.add(valuesToRemoveThird.get(value));
																colCells.get(thirdComp).removePossibleValues(
																		valuesToRemoveThird.get(value));
																continue;
															}
															result.append(valuesToRemoveThird.get(value) + "/");
															colCells.get(thirdComp).removePossibleValues(
																	valuesToRemoveThird.get(value));
															valuesChanged.add(valuesToRemoveThird.get(value));

														}
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained
																.retainAll(colCells.get(thirdComp).getPossibleValues());
														cellsChanged.put(colCells.get(thirdComp),
																new CellAdjustment(valuesChanged, valuesContained));
													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained
																.retainAll(colCells.get(thirdComp).getPossibleValues());
														cellsChanged.put(colCells.get(thirdComp),
																new CellAdjustment(new HashSet<>(), valuesContained));
													}
													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															colCells.get(fourthComp).getPossibleValues(), fourNumbers),
															cellsToRemoveFromFour)) {
														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(colCells.get(firstComp) + ", "
																	+ colCells.get(secondComp) + ",  "
																	+ colCells.get(thirdComp) + " and "
																	+ colCells.get(fourthComp)
																	+ " contain a hidden quad unique within col " + col
																	+ " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nCOLUMN::\n");
															textDisplayed = true;
														} else {
															result.append("\n");
														}

														result.append(colCells.get(fourthComp) + " removing ");
														for (int value = 0; value < valuesToRemoveFour
																.size(); value++) {
															if (value == valuesToRemoveFour.size() - 1) {
																result.append(valuesToRemoveFour.get(value));
																valuesChanged.add(valuesToRemoveFour.get(value));
																colCells.get(fourthComp).removePossibleValues(
																		valuesToRemoveFour.get(value));
																continue;
															}
															result.append(valuesToRemoveFour.get(value) + "/");
															colCells.get(fourthComp).removePossibleValues(
																	valuesToRemoveFour.get(value));
															valuesChanged.add(valuesToRemoveFour.get(value));

														}
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																colCells.get(fourthComp).getPossibleValues());
														cellsChanged.put(colCells.get(fourthComp),
																new CellAdjustment(valuesChanged, valuesContained));
													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																colCells.get(fourthComp).getPossibleValues());
														cellsChanged.put(colCells.get(fourthComp),
																new CellAdjustment(new HashSet<>(), valuesContained));
													}
													if (cellsChanged.isEmpty())
														continue;

													return new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
															cellsChanged);

												}
											}

										}
									}
								}
							}
						}
					}
				}
			}

		}
		return null;
	}

	private Logic hiddenQuadsRow() {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textDisplayed = false;
		StringBuilder result = new StringBuilder();

		for (int row = 1; row <= SIZE; row++) {
			List<Cell> rowCells = grid.getRow(row);

			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {
						for (int fourthVal = thirdVal + 1; fourthVal <= SIZE; fourthVal++) {

							Set<Integer> fourNumbers = new HashSet<>(
									Arrays.asList(firstVal, secondVal, thirdVal, fourthVal));

							for (int firstComp = 0; firstComp < rowCells.size(); firstComp++) {
								Cell firstCell = rowCells.get(firstComp);
								for (int secondComp = firstComp + 1; secondComp < rowCells.size(); secondComp++) {
									Cell secondCell = rowCells.get(secondComp);
									for (int thirdComp = secondComp + 1; thirdComp < rowCells.size(); thirdComp++) {
										Cell thirdCell = rowCells.get(thirdComp);
										for (int fourthComp = thirdComp + 1; fourthComp < rowCells
												.size(); fourthComp++) {
											Cell fourthCell = rowCells.get(fourthComp);

											Set<Cell> cellsWorkingWith = new HashSet<>();

											cellsWorkingWith.add(firstCell);
											cellsWorkingWith.add(secondCell);
											cellsWorkingWith.add(thirdCell);
											cellsWorkingWith.add(fourthCell);

											if(firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted()|| fourthCell.isCommitted())
												continue;
											
											if (uniqueToRegion(cellsWorkingWith, rowCells, fourNumbers)) {
												if (!fourNumbers.containsAll(firstCell.getPossibleValues())
														|| !fourNumbers.containsAll(secondCell.getPossibleValues())
														|| !fourNumbers.containsAll(thirdCell.getPossibleValues())
														|| !fourNumbers.containsAll(fourthCell.getPossibleValues())) {

													Set<Cell> cellsToRemoveFromOne = new HashSet<>();
													cellsToRemoveFromOne.add(rowCells.get(firstComp));

													Set<Cell> cellsToRemoveFromTwo = new HashSet<>();
													cellsToRemoveFromTwo.add(rowCells.get(secondComp));

													Set<Cell> cellsToRemoveFromThree = new HashSet<>();
													cellsToRemoveFromThree.add(rowCells.get(thirdComp));

													Set<Cell> cellsToRemoveFromFour = new HashSet<>();
													cellsToRemoveFromFour.add(rowCells.get(fourthComp));

													List<Integer> valuesToRemoveFirst = new ArrayList<>(
															getCellsToRemove(
																	rowCells.get(firstComp).getPossibleValues(),
																	fourNumbers));
													List<Integer> valuesToRemoveSecond = new ArrayList<>(
															getCellsToRemove(
																	rowCells.get(secondComp).getPossibleValues(),
																	fourNumbers));

													List<Integer> valuesToRemoveThird = new ArrayList<>(
															getCellsToRemove(
																	rowCells.get(thirdComp).getPossibleValues(),
																	fourNumbers));

													List<Integer> valuesToRemoveFour = new ArrayList<>(getCellsToRemove(
															rowCells.get(fourthComp).getPossibleValues(), fourNumbers));

													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															rowCells.get(firstComp).getPossibleValues(), fourNumbers),
															cellsToRemoveFromOne)) {
														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(rowCells.get(firstComp) + ", "
																	+ rowCells.get(secondComp) + ",  "
																	+ rowCells.get(thirdComp) + " and "
																	+ rowCells.get(fourthComp)
																	+ " contain a hidden quad unique within row " + row
																	+ " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nROW:\n");
															textDisplayed = true;
														}

														result.append(rowCells.get(firstComp) + " removing ");
														for (int value = 0; value < valuesToRemoveFirst
																.size(); value++) {
															if (value == valuesToRemoveFirst.size() - 1) {
																result.append(valuesToRemoveFirst.get(value));
																valuesChanged.add(valuesToRemoveFirst.get(value));
																rowCells.get(firstComp).removePossibleValues(
																		valuesToRemoveFirst.get(value));
																continue;
															}
															result.append(valuesToRemoveFirst.get(value) + "/");
															rowCells.get(firstComp).removePossibleValues(
																	valuesToRemoveFirst.get(value));
															valuesChanged.add(valuesToRemoveFirst.get(value));

														}
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained
																.retainAll(rowCells.get(firstComp).getPossibleValues());
														cellsChanged.put(rowCells.get(firstComp),
																new CellAdjustment(valuesChanged, valuesContained));
													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained
																.retainAll(rowCells.get(firstComp).getPossibleValues());
														cellsChanged.put(rowCells.get(firstComp),
																new CellAdjustment(new HashSet<>(), valuesContained));

													}
													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															rowCells.get(secondComp).getPossibleValues(), fourNumbers),
															cellsToRemoveFromTwo)) {

														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(rowCells.get(firstComp) + ", "
																	+ rowCells.get(secondComp) + ",  "
																	+ rowCells.get(thirdComp) + " and "
																	+ rowCells.get(fourthComp)
																	+ " contain a hidden quad unique within row " + row
																	+ " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nROW:\n");
															textDisplayed = true;
														} else {
															result.append("\n");
														}

														result.append(rowCells.get(secondComp) + " removing ");
														for (int value = 0; value < valuesToRemoveSecond
																.size(); value++) {
															if (value == valuesToRemoveSecond.size() - 1) {
																result.append(valuesToRemoveSecond.get(value));
																valuesChanged.add(valuesToRemoveSecond.get(value));
																rowCells.get(secondComp).removePossibleValues(
																		valuesToRemoveSecond.get(value));
																continue;
															}
															result.append(valuesToRemoveSecond.get(value) + "/");
															rowCells.get(secondComp).removePossibleValues(
																	valuesToRemoveSecond.get(value));
															valuesChanged.add(valuesToRemoveSecond.get(value));

														}
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																rowCells.get(secondComp).getPossibleValues());
														cellsChanged.put(rowCells.get(secondComp),
																new CellAdjustment(valuesChanged, valuesContained));
													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																rowCells.get(secondComp).getPossibleValues());
														cellsChanged.put(rowCells.get(secondComp),
																new CellAdjustment(new HashSet<>(), valuesContained));
													}
													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															rowCells.get(thirdComp).getPossibleValues(), fourNumbers),
															cellsToRemoveFromThree)) {
														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(rowCells.get(firstComp) + ", "
																	+ rowCells.get(secondComp) + ",  "
																	+ rowCells.get(thirdComp) + " and "
																	+ rowCells.get(fourthComp)
																	+ " contain a hidden quad unique within row " + row
																	+ " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nROW:\n");
															textDisplayed = true;
														} else {
															result.append("\n");
														}

														result.append(rowCells.get(thirdComp) + " removing ");
														for (int value = 0; value < valuesToRemoveThird
																.size(); value++) {
															if (value == valuesToRemoveThird.size() - 1) {
																result.append(valuesToRemoveThird.get(value));
																valuesChanged.add(valuesToRemoveThird.get(value));
																rowCells.get(thirdComp).removePossibleValues(
																		valuesToRemoveThird.get(value));
																continue;
															}
															result.append(valuesToRemoveThird.get(value) + "/");
															rowCells.get(thirdComp).removePossibleValues(
																	valuesToRemoveThird.get(value));
															valuesChanged.add(valuesToRemoveThird.get(value));

														}
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained
																.retainAll(rowCells.get(thirdComp).getPossibleValues());
														cellsChanged.put(rowCells.get(thirdComp),
																new CellAdjustment(valuesChanged, valuesContained));

													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained
																.retainAll(rowCells.get(thirdComp).getPossibleValues());
														cellsChanged.put(rowCells.get(thirdComp),
																new CellAdjustment(new HashSet<>(), valuesContained));
													}
													if (isThereAnythingToRemoveFromCells(getCellsToRemove(
															rowCells.get(fourthComp).getPossibleValues(), fourNumbers),
															cellsToRemoveFromFour)) {
														Set<Integer> valuesChanged = new HashSet<>();

														if (!textDisplayed) {
															result.append(rowCells.get(firstComp) + ", "
																	+ rowCells.get(secondComp) + ",  "
																	+ rowCells.get(thirdComp) + " and "
																	+ rowCells.get(fourthComp)
																	+ " contain a hidden quad unique within row " + row
																	+ " values candidates are " + fourNumbers
																	+ " therefore the canidates must be assigned to the cells\nROW:\n");
															textDisplayed = true;
														} else {
															result.append("\n");
														}

														result.append(rowCells.get(fourthComp) + " removing ");
														for (int value = 0; value < valuesToRemoveFour
																.size(); value++) {
															if (value == valuesToRemoveFour.size() - 1) {
																result.append(valuesToRemoveFour.get(value));
																valuesChanged.add(valuesToRemoveFour.get(value));
																rowCells.get(fourthComp).removePossibleValues(
																		valuesToRemoveFour.get(value));
																continue;
															}
															result.append(valuesToRemoveFour.get(value) + "/");
															rowCells.get(fourthComp).removePossibleValues(
																	valuesToRemoveFour.get(value));
															valuesChanged.add(valuesToRemoveFour.get(value));

														}
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																rowCells.get(fourthComp).getPossibleValues());
														cellsChanged.put(rowCells.get(fourthComp),
																new CellAdjustment(valuesChanged, valuesContained));
													} else {
														Set<Integer> valuesContained = new HashSet<>(fourNumbers);
														valuesContained.retainAll(
																rowCells.get(fourthComp).getPossibleValues());
														cellsChanged.put(rowCells.get(fourthComp),
																new CellAdjustment(new HashSet<>(), valuesContained));
													}
													if (cellsChanged.isEmpty())
														continue;

													return new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
															cellsChanged);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return null;
	}

	private boolean uniqueToRegion(Set<Cell> cellsWorkingWith, List<Cell> cells, Set<Integer> firstCombos) {
		for (Cell rowCell : cells) {
			if (!cellsWorkingWith.contains(rowCell)) {
				for (int value : firstCombos) {
					if (rowCell.getPossibleValues().contains(value))
						return false;
				}

			}
		}
		return true;
	}

	private Logic SolveColumn(KillerSudokuGrid grid) {

		for (int numberOfCols = 1; numberOfCols < SIZE; numberOfCols++) {

			Set<Integer> cols = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

			Set<Set<Integer>> combinationOfColsUnchecked = SumToN.getSubsetsOfSize(numberOfCols, cols);
			Set<Set<Integer>> combinationOfColsChecked = new HashSet<Set<Integer>>();

			for (Set<Integer> checkSet : combinationOfColsUnchecked) {
				if (rowsAndColsNextToEachother(checkSet)) {
					combinationOfColsChecked.add(checkSet);
				}
			}

			for (Set<Integer> colsUsed : combinationOfColsChecked) {

				Set<Cell> cellsInColsSet = new HashSet<>();

				for (int currentCol : colsUsed) {
					cellsInColsSet.addAll(grid.getCols(currentCol));
				}

				Set<Cage> cagesWithinCols = new HashSet<>();

				for (Cell currentColCell : cellsInColsSet) {
					Cage currentCellCage = grid.getCage(currentColCell);
					cagesWithinCols.add(currentCellCage);
				}

				Set<Cage> cagesSuitableOutie = new HashSet<>();
				int totalSumOfCols = 0;
				int totalNumberOfCellsInCages = 0;
				for (Cage currentColCage : cagesWithinCols) {

					int totalCageRemove = 0;
					if (!cageContainedInCols(currentColCage, colsUsed)) {

						int cellsCommittedOutside = 0;
						int cellsOutside = 0;
						for (Location loc : currentColCage.getLocation()) {
							Cell currentCell = grid.getCell(loc);

							if (isCellInCols(currentCell, colsUsed)) {
								totalNumberOfCellsInCages++;
							} else {
								cellsOutside++;
								if (!currentCell.isCommitted()) {
									totalNumberOfCellsInCages++;
								} else {
									totalCageRemove += currentCell.getValueCommitted();
									cellsCommittedOutside++;
								}
							}
						}

						if (cellsOutside - cellsCommittedOutside == 1) {
							cagesSuitableOutie.add(currentColCage);
						}
						totalSumOfCols += (currentColCage.getTotalValue() - totalCageRemove);
						continue;
					}
					totalSumOfCols += currentColCage.getTotalValue();
					totalNumberOfCellsInCages += currentColCage.getSize();

				}

				if (totalNumberOfCellsInCages == (colsUsed.size() * SIZE) + 1) {
					for (Cage currentColCage : cagesWithinCols) {
						if (!cageContainedInCols(currentColCage, colsUsed)
								&& cagesSuitableOutie.contains(currentColCage)) {
							Cell cellNotInCage = findOutieCellOfCols(currentColCage, colsUsed);
							if (cellNotInCage.isCommitted())
								continue; // Changed

							solveCell(cellNotInCage, totalSumOfCols - (colsUsed.size() * REGION_TOTAL));
							Location cellLocation = cellNotInCage.getCellLocation();
							String reasoning = "OUTIE: \n there is an outie in position R" + cellLocation.getRow()
									+ " C" + cellLocation.getCol() + " since the values of cages within the cols "
									+ colsUsed + " sum up to " + totalSumOfCols
									+ " the value of the outie is equal to the total of the cages minus the total of the regions "
									+ totalSumOfCols + " - " + (colsUsed.size() * REGION_TOTAL) + " = "
									+ (totalSumOfCols - (colsUsed.size() * REGION_TOTAL));

							Logic result = new Logic(DIFFICULTY_EXPERIENCED, reasoning, cellNotInCage);
							return result;
						}
					}
				} else {
					totalSumOfCols = 0;
					totalNumberOfCellsInCages = 0;
					Set<Cage> cagesSuitableInnie = new HashSet<>();

					for (Cage currentColCage : cagesWithinCols) {
						if (cageContainedInCols(currentColCage, colsUsed)) {
							totalNumberOfCellsInCages += currentColCage.getSize();
							totalSumOfCols += currentColCage.getTotalValue();
						} else {
							int cellsCommittedInside = 0;
							int cellsInside = 0;
							int totalToAdd = 0;

							for (Location currentLoc : currentColCage.getLocation()) {
								Cell currentCell = grid.getCell(currentLoc);

								if (isCellInCols(currentCell, colsUsed)) {
									cellsInside++;
									if (currentCell.isCommitted()) {
										totalNumberOfCellsInCages++;
										cellsCommittedInside++;
										totalToAdd += currentCell.getValueCommitted();
									}
								}
							}
							if (cellsInside - cellsCommittedInside == 1) {
								cagesSuitableInnie.add(currentColCage);
							}
							totalSumOfCols += totalToAdd;

						}

						if (totalNumberOfCellsInCages == (colsUsed.size() * SIZE) - 1) {
							for (Cage currentCage : cagesWithinCols) {

								if (!cageContainedInCols(currentCage, colsUsed)
										&& cagesSuitableInnie.contains(currentCage)) {

									Cell cellInNet = findInnieCellOfCols(currentCage, colsUsed);

									if (cellInNet.isCommitted()) {
										continue;
									}

									solveCell(cellInNet, (colsUsed.size() * REGION_TOTAL) - totalSumOfCols);
									Location cellLocation = cellInNet.getCellLocation();
									String reasoning = "INNIE: \nInnie in position R" + cellLocation.getRow() + " C"
											+ cellLocation.getCol() + " since the values of cages within the cols "
											+ colsUsed + " sum up to " + totalSumOfCols
											+ " the value of the innie is equal to the total of the cols minus the total of the cages that are fully contained within the row "
											+ (REGION_TOTAL * colsUsed.size()) + " - " + totalSumOfCols + " = "
											+ ((REGION_TOTAL * colsUsed.size()) - totalSumOfCols);

									Logic reason = new Logic(DIFFICULTY_EXPERIENCED, reasoning, cellInNet);
									return reason;
								}
							}
						}
					}
				}

			}
		}

		return null;
	}

	private boolean isCellInCols(Cell currentCell, Set<Integer> colsUsed) {
		if (colsUsed.contains(currentCell.getCellLocation().getCol()))
			return true;
		return false;
	}

	private Cell findInnieCellOfCols(Cage cage, Set<Integer> colsUsedSet) {
		for (Location currentLoc : cage.getLocation()) {
			if (colsUsedSet.contains(currentLoc.getCol()) && !grid.getCell(currentLoc).isCommitted()) {
				return grid.getCell(currentLoc);
			}
		}
		return null;
	}

	private Cell findOutieCellOfCols(Cage currentColCage, Set<Integer> cols) {
		for (Location currentLoc : currentColCage.getLocation()) {
			if (!cols.contains(currentLoc.getCol()) && !grid.getCell(currentLoc).isCommitted()) {
				return grid.getCell(currentLoc);
			}
		}
		return null;
	}

	private boolean cageContainedInCols(Cage currentColCage, Set<Integer> cols) {
		for (Location currentLoc : currentColCage.getLocation()) {
			if (!cols.contains(currentLoc.getCol()))
				return false;
		}
		return true;
	}

	private Logic solveRows(KillerSudokuGrid grid) {

		for (int numberOfRows = 1; numberOfRows < SIZE; numberOfRows++) {

			Set<Integer> rows = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

			Set<Set<Integer>> combinationOfRowsUnchecked = SumToN.getSubsetsOfSize(numberOfRows, rows);
			Set<Set<Integer>> combinationOfRowsChecked = new HashSet<Set<Integer>>();

			for (Set<Integer> checkSet : combinationOfRowsUnchecked) {
				if (rowsAndColsNextToEachother(checkSet)) {
					combinationOfRowsChecked.add(checkSet);
				}
			}

			for (Set<Integer> rowsUsed : combinationOfRowsChecked) {

				Set<Cell> cellsInRowSet = new HashSet<>();

				for (int currentRow : rowsUsed) {
					cellsInRowSet.addAll(grid.getRow(currentRow));
				}

				Set<Cage> cagesWithinRow = new HashSet<>();

				for (Cell currentRowCell : cellsInRowSet) {
					Cage currentCellCage = grid.getCage(currentRowCell);
					cagesWithinRow.add(currentCellCage);
				}
				

				Set<Cage> cagesSuitableOutie = new HashSet<>();
				int totalSumOfRows = 0;
				int totalNumberOfCellsInCages = 0;
				for (Cage currentRowCage : cagesWithinRow) {

					int totalCageRemove = 0;
					if (!cageContainedInRows(currentRowCage, rowsUsed)) {

						int cellsCommittedOutside = 0;
						int cellsOutside = 0;
						for (Location loc : currentRowCage.getLocation()) {
							Cell currentCell = grid.getCell(loc);

							if (isCellInRows(currentCell, rowsUsed)) {
								totalNumberOfCellsInCages++;
							} else {
								cellsOutside++;
								if (!currentCell.isCommitted()) {
									totalNumberOfCellsInCages++;
								} else {
									totalCageRemove += currentCell.getValueCommitted();
									cellsCommittedOutside++;
								}
							}
						}

						if (cellsOutside - cellsCommittedOutside == 1) {
							cagesSuitableOutie.add(currentRowCage);
						}
						totalSumOfRows += (currentRowCage.getTotalValue() - totalCageRemove);
						continue;
					}
					totalSumOfRows += currentRowCage.getTotalValue();
					totalNumberOfCellsInCages += currentRowCage.getSize();

				}
				
				if (totalNumberOfCellsInCages == (rowsUsed.size() * SIZE) + 1) {
					for (Cage currentRowCage : cagesWithinRow) {
						if (!cageContainedInRows(currentRowCage, rowsUsed)
								&& cagesSuitableOutie.contains(currentRowCage)) {
							Cell cellNotInCage = findOutieCellOfRow(currentRowCage, rowsUsed);

							if (cellNotInCage.isCommitted())
								continue; // Changed

							solveCell(cellNotInCage, totalSumOfRows - (REGION_TOTAL * rowsUsed.size()));
							Location cellLocation = cellNotInCage.getCellLocation();
							String reasoning = "OUTIE:\nOutie in position R" + cellLocation.getRow() + " C"
									+ cellLocation.getCol() + " since the values of cages within the rows" + rowsUsed
									+ "sum up to " + totalSumOfRows
									+ " the value of the outie is equal to the total of the cages minus the total of a region "
									+ totalSumOfRows + " - " + (REGION_TOTAL * rowsUsed.size()) + " = "
									+ (totalSumOfRows - (REGION_TOTAL * rowsUsed.size()));

							Logic result = new Logic(DIFFICULTY_EXPERIENCED, reasoning, cellNotInCage);
							return result;
						}
					}

				} else {
					totalSumOfRows = 0;
					totalNumberOfCellsInCages = 0;
					Set<Cage> cagesSuitableInnie = new HashSet<>();

					for (Cage currentRowCage : cagesWithinRow) {
						if (cageContainedInRows(currentRowCage, rowsUsed)) {
							totalNumberOfCellsInCages += currentRowCage.getSize();
							totalSumOfRows += currentRowCage.getTotalValue();
						} else {
							int cellsCommittedInside = 0;
							int cellsInside = 0;
							int totalToAdd = 0;

							for (Location currentLoc : currentRowCage.getLocation()) {
								Cell currentCell = grid.getCell(currentLoc);

								if (isCellInRows(currentCell, rowsUsed)) {
									cellsInside++;
									if (currentCell.isCommitted()) {
										totalNumberOfCellsInCages++;
										cellsCommittedInside++;
										totalToAdd += currentCell.getValueCommitted();
									}
								}
							}
							if (cellsInside - cellsCommittedInside == 1) {
								cagesSuitableInnie.add(currentRowCage);
							}
							totalSumOfRows += totalToAdd;

						}
					}
					
					
					if (totalNumberOfCellsInCages == (rowsUsed.size() * SIZE) - 1) {
						for (Cage currentCage : cagesWithinRow) {

							if (!cageContainedInRows(currentCage, rowsUsed)
									&& cagesSuitableInnie.contains(currentCage)) {

								Cell cellInNet = findInnieCellOfRows(currentCage, rowsUsed);

								if (cellInNet.isCommitted()) {
									continue;
								}
								solveCell(cellInNet, (rowsUsed.size() * REGION_TOTAL) - totalSumOfRows);
								Location cellLocation = cellInNet.getCellLocation();
								String reasoning = "INNIE: \nThere is an innie in position R" + cellLocation.getRow()
										+ " C" + cellLocation.getCol() + " since the values of cages within the rows "
										+ rowsUsed + " sum up to " + totalSumOfRows
										+ " the value of the innie is equal to the total of the rows minus the total of the cages that are fully contained within the row "
										+ (REGION_TOTAL * rowsUsed.size()) + " - " + totalSumOfRows + " = "
										+ ((REGION_TOTAL * rowsUsed.size()) - totalSumOfRows);

								Logic reason = new Logic(DIFFICULTY_EXPERIENCED, reasoning, cellInNet);
								return reason;
							}

						}
					}
				}
			}
		}

		return null;

	}

	private Cell findOutieCellOfRow(Cage currentRowCage, Set<Integer> rowsUsed) {
		for (Location currentLoc : currentRowCage.getLocation()) {
			if (!rowsUsed.contains(currentLoc.getRow()) && !grid.getCell(currentLoc).isCommitted()) {
				return grid.getCell(currentLoc);
			}
		}
		return null;
	}

	private boolean isCellInRows(Cell currentCell, Set<Integer> rowsUsed) {
		if (rowsUsed.contains(currentCell.getCellLocation().getRow()))
			return true;
		return false;
	}

	private boolean rowsAndColsNextToEachother(Set<Integer> checkSet) {
		if (checkSet.size() == 1)
			return true;

		int[] values = new int[checkSet.size()];
		int i = 0;
		for (Integer val : checkSet)
			values[i++] = val;

		Arrays.sort(values);

		for (int x = 0; x < values.length - 1; x++) {
			if (values[x] + 1 != values[x + 1]) {
				return false;
			}
		}

		return true;
	}

	private Cell findInnieCellOfRows(Cage cage, Set<Integer> rows) {

		for (Location currentLoc : cage.getLocation()) {
			if (rows.contains(currentLoc.getRow()) && !grid.getCell(currentLoc).isCommitted()) {
				return grid.getCell(currentLoc);
			}
		}
		return null;
	}

	private boolean cageContainedInRows(Cage currentRowCage, Set<Integer> rows) {
		for (Location currentLoc : currentRowCage.getLocation()) {
			if (!rows.contains(currentLoc.getRow()))
				return false;
		}
		return true;
	}

	private List<Cell> findOutieCellsOfRows(Set<Cage> cagesWithinRow, Set<Integer> rows) {
		List<Cell> outieCells = new ArrayList<>();
		for (Cage currentCage : cagesWithinRow) {
			for (Location currentLoc : currentCage.getLocation()) {
				if (!rows.contains(currentLoc.getRow()) && !grid.getCell(currentLoc).isCommitted()) {
					outieCells.add(grid.getCell(currentLoc));
				}
			}
		}
		return outieCells;
	}

	/**
	 * Try to do with committed cells
	 * 
	 * @param grid
	 * @return
	 */
	private Logic SolveNonets(KillerSudokuGrid grid) {
		for (int numberOfNets = 1; numberOfNets < SIZE; numberOfNets++) {

			Set<Integer> nonets = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

			Set<Set<Integer>> combinationOfNonetsUnchecked = SumToN.getSubsetsOfSize(numberOfNets, nonets);
			Set<Set<Integer>> combinationOfNonetsChecked = new HashSet<Set<Integer>>();

			for (Set<Integer> checkSet : combinationOfNonetsUnchecked) {
				if (pathBetweenNonets(checkSet)) {
					combinationOfNonetsChecked.add(checkSet);
				}
			}

			for (Set<Integer> nonetsUsed : combinationOfNonetsChecked) {

				Set<Cell> cellsInNonetSet = new HashSet<>();

				for (int currentNonets : nonetsUsed) {
					cellsInNonetSet.addAll(grid.getNoNetCells(currentNonets));
				}

				Set<Cage> cagesWithinNonet = new HashSet<>();

				for (Cell currentNonetCell : cellsInNonetSet) {
					Cage currentCellCage = grid.getCage(currentNonetCell);
					cagesWithinNonet.add(currentCellCage);
				}

				Set<Cage> cagesSuitableOutie = new HashSet<>();
				int totalSumOfNonet = 0;
				int totalNumberOfCellsInCages = 0;
				for (Cage currentNonetCage : cagesWithinNonet) {

					int totalCageRemove = 0;
					if (!cageContainedInNonets(currentNonetCage, nonetsUsed)) {

						int cellsCommittedOutside = 0;
						int cellsOutside = 0;
						for (Location loc : currentNonetCage.getLocation()) {
							Cell currentCell = grid.getCell(loc);

							if (isCellInNonets(currentCell, nonetsUsed)) {
								totalNumberOfCellsInCages++;
							} else {
								cellsOutside++;
								if (!currentCell.isCommitted()) {
									totalNumberOfCellsInCages++;
								} else {
									totalCageRemove += currentCell.getValueCommitted();
									cellsCommittedOutside++;
								}
							}
						}

						if (cellsOutside - cellsCommittedOutside == 1) {
							cagesSuitableOutie.add(currentNonetCage);
						}
						totalSumOfNonet += (currentNonetCage.getTotalValue() - totalCageRemove);
						continue;
					}
					totalSumOfNonet += currentNonetCage.getTotalValue();
					totalNumberOfCellsInCages += currentNonetCage.getSize();
				}

				if (totalNumberOfCellsInCages == (nonetsUsed.size() * SIZE) + 1) {
					for (Cage currentNonetCage : cagesWithinNonet) {
						if (!cageContainedInNonets(currentNonetCage, nonetsUsed)
								&& cagesSuitableOutie.contains(currentNonetCage)) {
							Cell cellNotInCage = findOutieCellOfNonets(currentNonetCage, nonetsUsed);
							if (cellNotInCage.isCommitted())
								continue;

							solveCell(cellNotInCage, totalSumOfNonet - (nonetsUsed.size() * REGION_TOTAL));
							Location cellLocation = cellNotInCage.getCellLocation();

							String reasoning = "OUTIE: \nOutie in position R" + cellLocation.getRow() + " C"
									+ cellLocation.getCol() + " since the values of cages within the nonets "
									+ nonetsUsed + " sum up to " + totalSumOfNonet
									+ " the value of the outie is equal to the total of the cages minus the total of the regions "
									+ totalSumOfNonet + " - " + (nonetsUsed.size() * REGION_TOTAL) + " = "
									+ (totalSumOfNonet - (nonetsUsed.size() * REGION_TOTAL));

							Logic result = new Logic(DIFFICULTY_EXPERIENCED, reasoning, cellNotInCage);

							return result;
						}
					}
				} else {
					totalSumOfNonet = 0;
					totalNumberOfCellsInCages = 0;
					Set<Cage> cagesSuitableInnie = new HashSet<>();

					for (Cage currentNonetCage : cagesWithinNonet) {
						if (cageContainedInNonets(currentNonetCage, nonetsUsed)) {
							totalNumberOfCellsInCages += currentNonetCage.getSize();
							totalSumOfNonet += currentNonetCage.getTotalValue();
						} else {
							int cellsCommittedInside = 0;
							int cellsInside = 0;
							int totalToAdd = 0;

							for (Location currentLoc : currentNonetCage.getLocation()) {
								Cell currentCell = grid.getCell(currentLoc);

								if (isCellInNonets(currentCell, nonetsUsed)) {
									cellsInside++;
									if (currentCell.isCommitted()) {
										totalNumberOfCellsInCages++;
										cellsCommittedInside++;
										totalToAdd += currentCell.getValueCommitted();
									}
								}
							}
							if (cellsInside - cellsCommittedInside == 1) {
								cagesSuitableInnie.add(currentNonetCage);
							}
							totalSumOfNonet += totalToAdd;
							// continue;
						}
					}

					if (totalNumberOfCellsInCages == ((SIZE * nonetsUsed.size()) - 1)) {

						for (Cage currentCage : cagesWithinNonet) {

							if (!cageContainedInNonets(currentCage, nonetsUsed)
									&& cagesSuitableInnie.contains(currentCage)) {

								Cell cellInNet = findInnieCellOfNets(currentCage, nonetsUsed);
								if (cellInNet.isCommitted()) {
									continue;
								}

								// System.out.println(cellInNet);
								solveCell(cellInNet, (REGION_TOTAL * nonetsUsed.size()) - totalSumOfNonet);

								Location cellLocation = cellInNet.getCellLocation();
								String reasoning = "INNIE: \nInnie in position R" + cellLocation.getRow() + " C"
										+ cellLocation.getCol() + " since the values of cages within the nonets "
										+ nonetsUsed + " sum up to " + totalSumOfNonet
										+ " the value of the innie is equal to the total of the nonets minus the total of the cages that are fully contained within the nonets "
										+ (REGION_TOTAL * nonetsUsed.size()) + " - " + totalSumOfNonet + " = "
										+ ((REGION_TOTAL * nonetsUsed.size()) - totalSumOfNonet);

								Logic reason = new Logic(DIFFICULTY_EXPERIENCED, reasoning, cellInNet);
								return reason;
							}
						}
					}
				}
			}
		}

		return null;

	}

	private List<Cell> findOutieCellsOfNonets(Set<Cage> cagesWithinNonet, Set<Integer> nonetsUsed) {
		List<Cell> outieCells = new ArrayList<>();
		for (Cage currentCage : cagesWithinNonet) {
			for (Location currentLoc : currentCage.getLocation()) {
				if (!nonetsUsed.contains(currentLoc.getNoNet()) && !grid.getCell(currentLoc).isCommitted()) {
					outieCells.add(grid.getCell(currentLoc));
				}
			}
		}
		return outieCells;
	}

	private boolean pathBetweenNonets(Set<Integer> checkSet) {

		if (checkSet.size() == 1)
			return true;

		for (int nonet : checkSet) {
			List<Integer> neighbourNonets = NEIGHBOUR_NONETS.get(nonet);

			boolean neighbourWithin = false;
			for (int neighbours : neighbourNonets) {
				if (checkSet.contains(neighbours)) {
					neighbourWithin = true;
				}

			}
			if (!neighbourWithin)
				return false;
		}

		return true;
	}

	private boolean isCellInNonets(Cell currentCell, Set<Integer> nonets) {
		if (nonets.contains(currentCell.getCellLocation().getNoNet()))
			return true;
		return false;
	}

	private Cell findInnieCellOfNets(Cage cageNotFullyInNoNet, Set<Integer> nets) {
		for (Location currentLoc : cageNotFullyInNoNet.getLocation()) {
			if (nets.contains(currentLoc.getNoNet()) && !grid.getCell(currentLoc).isCommitted()) {
				return grid.getCell(currentLoc);
			}
		}
		return null;
	}

	private boolean cageContainedInNonets(Cage currentNonetCage, Set<Integer> nonets) {
		for (Location currentLoc : currentNonetCage.getLocation()) {
			if (!nonets.contains(currentLoc.getNoNet()))
				return false;
		}
		return true;
	}

	private Cell findOutieCellOfNonets(Cage currentNonetCage, Set<Integer> nonets) {
		for (Location currentLoc : currentNonetCage.getLocation()) {
			if (!nonets.contains(currentLoc.getNoNet()) && !grid.getCell(currentLoc).isCommitted()) {
				return grid.getCell(currentLoc);
			}
		}
		return null;
	}

	/**
	 * SKILL LEVEL 1(Beginner) Definition taken from
	 * (http://killersudokuonline.com/tips.html) This comes directly from the
	 * definition of sudoku. No region can contain any duplicate digits. In a
	 * sudoku region each digit appears exactly once. For example, if a digit
	 * appears in a row, it cannot be in any other cell in the row. Likewise,
	 * each digit can appear in a cage only once. If a digit is in a cage, it
	 * cannot appear in that cage again.
	 * 
	 *
	 * 
	 */
	public Logic ruleOfOne(KillerSudokuGrid grid) {

		for (Cage cage : grid.getCages()) {
			for (Cell currentCell : grid.getCellsInCage(cage)) {
				if (currentCell.isCommitted()) {
					boolean changedRow = false, changedCol = false, changedCage = false, changedNonet = false;

					Logic reasonRow = removeSolvedRow(currentCell);
					Logic reasonCol = removeSolvedCol(currentCell);
					Logic reasonCage = removeSolvedCage(currentCell);
					Logic reasonNonet = removeSolvedNonet(currentCell);

					StringBuilder reasoning = new StringBuilder(
							currentCell + " HAS BEEN SET WITH VALUE " + currentCell.getValueCommitted() + "\n\n");

					if (reasonRow != null) {
						reasoning.append(reasonRow.getHelpText() + "\n");
						changedRow = true;
					}
					if (reasonCol != null) {
						reasoning.append(reasonCol.getHelpText() + "\n");
						changedCol = true;
					}
					if (reasonCage != null) {
						reasoning.append(reasonCage.getHelpText() + "\n");
						changedCage = true;

					}
					if (reasonNonet != null) {
						reasoning.append(reasonNonet.getHelpText() + "\n");
						changedNonet = true;

					}

					Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

					if (!changedRow && !changedCol && !changedNonet && !changedCage) {
						continue;
					}

					if (changedRow) {
						cellsChanged.putAll(reasonRow.getCellsAdjustments());
					}

					if (changedCol) {
						cellsChanged.putAll(reasonCol.getCellsAdjustments());
					}

					if (changedCage) {
						cellsChanged.putAll(reasonCage.getCellsAdjustments());
					}

					if (changedNonet) {
						cellsChanged.putAll(reasonNonet.getCellsAdjustments());
					}

					/* Get reasoning behind each step */

					cellsChanged.put(currentCell, new CellAdjustment(new HashSet<>(),
							new HashSet<>(Arrays.asList(currentCell.getValueCommitted()))));

					return new Logic(DIFFICULTY_EASY, reasoning.toString(), cellsChanged);

				}

			}
		}
		return null;

	}

	public void changeGridForSolving(KillerSudokuGrid grid) {
		this.grid = grid;
	}

	public Logic killerCageSumsHarder(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		StringBuilder result = new StringBuilder();

		for (Cage currentCage : grid.getCages()) {

			if (!currentCage.isUniqueValuesAssigned() && !currentCage.isCommitted()) {

				List<SumCombination> combination = SumToN.SumUpTo(currentCage.getRemainingValue(),
						currentCage.getUnsolvedLocations().size(), getAllCagePossibleValues(currentCage));

				Set<Integer> allPossibleValues = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
				Set<Integer> checkValues = new HashSet<>();
				for (SumCombination currentCombination : combination) {
					checkValues.addAll(currentCombination.getValuesThatSum());
				}
				if (checkValues.equals(allPossibleValues))
					continue;

				Set<Cell> cellsInCage = new HashSet<>();
				for (Location loc : currentCage.getUnsolvedLocations()) {
					if (!grid.getCell(loc).isCommitted())
						cellsInCage.add(grid.getCell(loc));
				}

				Set<Integer> valuesInCombination = new TreeSet<>();

				for (SumCombination currentCombination : combination) {
					valuesInCombination.addAll(currentCombination.getValuesThatSum());
				}
				Set<Integer> valuesNotInCombination = new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
				valuesNotInCombination.removeAll(valuesInCombination);

				boolean anythingToRemove = false;
				for (Location currentCageCell : currentCage.getUnsolvedLocations()) {

					Cell currentCell = grid.getCell(currentCageCell);

					Set<Integer> cellPossibleValues = new HashSet<>(currentCell.getPossibleValues());

					cellPossibleValues.removeAll(valuesInCombination);
					if (!cellPossibleValues.isEmpty())
						anythingToRemove = true;

				}

				if (anythingToRemove) {
					for (Cell currentCell : cellsInCage) {
						Set<Integer> cellsRemoved = new HashSet<>(currentCell.getPossibleValues());
						Set<Integer> cellsCausing = new HashSet<>(currentCell.getPossibleValues());
						cellsRemoved.retainAll(valuesInCombination);
						cellsCausing.retainAll(valuesNotInCombination);

						cellsChanged.put(currentCell, new CellAdjustment(cellsCausing, cellsRemoved));
					}

					result.append("\n" + currentCage.getUnsolvedLocations() + " has combinations: ");

					for (SumCombination currentCombination : combination) {
						result.append(currentCombination + " ");
					}
					result.append("\n");

					result.append("therefore the cage cannot contain " + valuesNotInCombination.toString() + "\n");

					for (Location currentLoc : currentCage.getUnsolvedLocations()) {
						Cell currentCell = grid.getCell(currentLoc);

						List<Integer> valuesToRemoveFromCells = new ArrayList<>(valuesNotInCombination);

						valuesToRemoveFromCells.retainAll(currentCell.getPossibleValues());
						boolean firstVal = true;
						Set<Integer> valuesChanged = new HashSet<>();

						for (int valueRemove = 0; valueRemove < valuesToRemoveFromCells.size(); valueRemove++) {

							if (currentCell.getPossibleValues().contains(valuesToRemoveFromCells.get(valueRemove))) {
								if (firstVal) {
									result.append(currentCell + " removing ");
									firstVal = false;
								}
								if (valueRemove == valuesToRemoveFromCells.size() - 1) {
									result.append(valuesToRemoveFromCells.get(valueRemove));
									currentCell.removePossibleValues(valuesToRemoveFromCells.get(valueRemove));
									valuesChanged.add(valuesToRemoveFromCells.get(valueRemove));
									result.append("\n");
									continue;
								}

								result.append(valuesToRemoveFromCells.get(valueRemove) + "/");
								currentCell.removePossibleValues(valuesToRemoveFromCells.get(valueRemove));
								valuesChanged.add(valuesToRemoveFromCells.get(valueRemove));
							}

						}

					}

				}
			}

		}
		if (cellsChanged.isEmpty())
			return null;

		return new Logic(DIFFICULTY_EXPERIENCED, result.toString(), cellsChanged);

	}

	public Logic nakedPairs(KillerSudokuGrid grid) {
		Logic result = nakedPairsRows(grid);
		if (result != null) {
			return result;
		}
		result = nakedPairsCols(grid);
		if (result != null) {
			return result;
		}

		result = nakedPairsNonets(grid);
		if (result != null) {
			return result;
		}

		return null;
	}

	private Logic nakedPairsNonets(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean descriptionTextShown = false;

		StringBuilder result = new StringBuilder();
		final int PAIR_SIZE = 2;

		for (int nonet = 1; nonet <= SIZE; nonet++) {
			List<Cell> nonetCells = grid.getNoNetCells(nonet);
			for (int i = 0; i < nonetCells.size(); i++) {
				Set<Integer> firstPossibleVals = nonetCells.get(i).getPossibleValues();
				for (int k = i + 1; k < nonetCells.size(); k++) {
					Set<Integer> secondPossibleVals = nonetCells.get(k).getPossibleValues();

					if (firstPossibleVals.size() == PAIR_SIZE && secondPossibleVals.size() == PAIR_SIZE
							&& firstPossibleVals.equals(secondPossibleVals)) {

						Set<Cell> cellsChecking = new HashSet<>();
						cellsChecking.add(nonetCells.get(i));
						cellsChecking.add(nonetCells.get(k));

						if(nonetCells.get(i).isCommitted() || nonetCells.get(k).isCommitted() )
							continue;
						Set<Cell> checkIfRemovingIsNeeded = new HashSet<>(nonetCells);
						checkIfRemovingIsNeeded.removeAll(cellsChecking);

						if (isThereAnythingToRemoveFromCells(firstPossibleVals, checkIfRemovingIsNeeded)) {
							if (!descriptionTextShown) {
								result.append(nonetCells.get(i) + " and " + nonetCells.get(k)
										+ " belong to the same nonet therefore the two numbers can be removed from all other cells in the nonet\n\nNONET:\n");
								descriptionTextShown = true;
							}

							for (Cell nonetCell : checkIfRemovingIsNeeded) {
								if (!nonetCell.isCommitted() && !nonetCell.equals(nonetCells.get(i))
										&& !nonetCell.equals(nonetCells.get(k))) {

									Set<Integer> valuesChanged = new HashSet<>();

									List<Integer> valuesToRemove = new ArrayList<>(firstPossibleVals);
									valuesToRemove.retainAll(nonetCell.getPossibleValues());
									if (valuesToRemove.isEmpty())
										continue;

									boolean valueRemoved = false;

									for (int removeVal = 0; removeVal < valuesToRemove.size(); removeVal++) {

										if (nonetCell.getPossibleValues().contains(valuesToRemove.get(removeVal))) {
											if (!valueRemoved) {
												result.append(nonetCell + " removing ");
											}
											if (removeVal == valuesToRemove.size() - 1) {
												result.append(valuesToRemove.get(removeVal));
												nonetCell.removePossibleValues(valuesToRemove.get(removeVal));
												valuesChanged.add(valuesToRemove.get(removeVal));
												result.append("\n");
												continue;
											}

											result.append(valuesToRemove.get(removeVal) + "/");
											nonetCell.removePossibleValues(valuesToRemove.get(removeVal));
											valueRemoved = true;
											valuesChanged.add(valuesToRemove.get(removeVal));

										}
									}
									cellsChanged.put(nonetCell, new CellAdjustment(valuesChanged, new HashSet<>()));
								}

							}

							if (cellsChanged.isEmpty())
								continue;

							for (Cell currentCell : cellsChecking) {
								cellsChanged.put(currentCell, new CellAdjustment(new HashSet<>(), firstPossibleVals));

							}

							return new Logic(DIFFICULTY_MEDIUM, result.toString(), cellsChanged);
						}
					}
				}
			}

		}
		return null;
	}

	private Logic nakedPairsCols(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean descriptionTextShown = false;
		final int PAIR_SIZE = 2;
		StringBuilder result = new StringBuilder();

		for (int col = 1; col <= SIZE; col++) {
			List<Cell> colElements = grid.getCols(col);

			for (int i = 0; i < colElements.size(); i++) {
				Set<Integer> firstPossibleVals = colElements.get(i).getPossibleValues();
				for (int k = i + 1; k < colElements.size(); k++) {
					result = new StringBuilder();

					Set<Integer> secondPossibleVals = colElements.get(k).getPossibleValues();

					if (firstPossibleVals.size() == PAIR_SIZE && secondPossibleVals.size() == PAIR_SIZE
							&& firstPossibleVals.equals(secondPossibleVals)) {

						Set<Cell> cellsChecking = new HashSet<>();
						cellsChecking.add(colElements.get(i));
						cellsChecking.add(colElements.get(k));
						
						if(colElements.get(i).isCommitted() || colElements.get(k).isCommitted() )
							continue;
						
						if (areCellsSameNonet(cellsChecking)) {

							List<Cell> nonetCells = grid.getNoNetCells(colElements.get(i).getCellLocation().getNoNet());

							Set<Cell> checkIfRemovingIsNeeded = new HashSet<>(colElements);
							checkIfRemovingIsNeeded.addAll(nonetCells);
							checkIfRemovingIsNeeded.removeAll(cellsChecking);

							if (isThereAnythingToRemoveFromCells(firstPossibleVals, checkIfRemovingIsNeeded)) {

								if (!descriptionTextShown) {
									result.append(colElements.get(i) + " and " + colElements.get(k)
											+ " belong to the same col and nonet therefore the two numbers can be removed from all other cells in the col and nonet\n\nCOLUMN/NONET:\n");
									descriptionTextShown = true;
								}
								for (Cell currentCell : checkIfRemovingIsNeeded) {
									if (!currentCell.isCommitted()) {

										Set<Integer> valuesChanged = new HashSet<>();

										List<Integer> valuesToRemove = new ArrayList<>(firstPossibleVals);
										valuesToRemove.retainAll(currentCell.getPossibleValues());
										if (valuesToRemove.isEmpty())
											continue;

										boolean valueRemoved = false;

										for (int removeVal = 0; removeVal < valuesToRemove.size(); removeVal++) {

											if (currentCell.getPossibleValues()
													.contains(valuesToRemove.get(removeVal))) {
												if (!valueRemoved) {
													result.append(currentCell + " removing ");
												}
												if (removeVal == valuesToRemove.size() - 1) {
													result.append(valuesToRemove.get(removeVal));
													currentCell.removePossibleValues(valuesToRemove.get(removeVal));
													valuesChanged.add(valuesToRemove.get(removeVal));
													result.append("\n");
													continue;
												}

												result.append(valuesToRemove.get(removeVal) + "/");
												currentCell.removePossibleValues(valuesToRemove.get(removeVal));
												valueRemoved = true;
												valuesChanged.add(valuesToRemove.get(removeVal));

											}
										}
										cellsChanged.put(currentCell,
												new CellAdjustment(valuesChanged, new HashSet<>()));

									}
								}

								for (Cell currentCell : cellsChecking) {
									cellsChanged.put(currentCell,
											new CellAdjustment(new HashSet<>(), firstPossibleVals));

								}
								return new Logic(DIFFICULTY_MEDIUM, result.toString(), cellsChanged);
							}
						} else {

							Set<Cell> checkIfRemovingIsNeeded = new HashSet<>(colElements);
							checkIfRemovingIsNeeded.removeAll(cellsChecking);

							if (isThereAnythingToRemoveFromCells(firstPossibleVals, checkIfRemovingIsNeeded)) {
								if (!descriptionTextShown) {
									result.append(colElements.get(i) + " and " + colElements.get(k)
											+ " belong to the same col therefore the two numbers can be removed from all other cells in the col\n\nCOLUMN:\n");
									descriptionTextShown = true;

								} // Remove from col
								for (Cell colCell : checkIfRemovingIsNeeded) {
									if (!colCell.isCommitted() && !colCell.equals(colElements.get(i))
											&& !colCell.equals(colElements.get(k))) {
										Set<Integer> valuesChanged = new HashSet<>();

										List<Integer> valuesToRemove = new ArrayList<>(firstPossibleVals);
										valuesToRemove.retainAll(colCell.getPossibleValues());
										if (valuesToRemove.isEmpty())
											continue;

										boolean valueRemoved = false;

										for (int removeVal = 0; removeVal < valuesToRemove.size(); removeVal++) {

											if (colCell.getPossibleValues().contains(valuesToRemove.get(removeVal))) {
												if (!valueRemoved) {
													result.append(colCell + " removing ");
												}
												if (removeVal == valuesToRemove.size() - 1) {
													result.append(valuesToRemove.get(removeVal));
													colCell.removePossibleValues(valuesToRemove.get(removeVal));
													valuesChanged.add(valuesToRemove.get(removeVal));
													result.append("\n");
													continue;
												}

												result.append(valuesToRemove.get(removeVal) + "/");
												colCell.removePossibleValues(valuesToRemove.get(removeVal));
												valueRemoved = true;
												valuesChanged.add(valuesToRemove.get(removeVal));
											}
										}
										cellsChanged.put(colCell, new CellAdjustment(valuesChanged, new HashSet<>()));
									}
								}

								for (Cell currentCell : cellsChecking) {
									cellsChanged.put(currentCell,
											new CellAdjustment(new HashSet<>(), firstPossibleVals));

								}

								return new Logic(DIFFICULTY_MEDIUM, result.toString(), cellsChanged);
							}
						}
					}
				}

			}
		}
		return null;
	}

	private Logic nakedPairsRows(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean descriptionTextShown = false;

		StringBuilder result = new StringBuilder();

		final int PAIR_SIZE = 2;
		/* Run through rows looking for pairs */
		for (int row = 1; row <= SIZE; row++) {

			List<Cell> rowElements = grid.getRow(row);

			for (int i = 0; i < rowElements.size(); i++) {
				Set<Integer> firstPossibleVals = rowElements.get(i).getPossibleValues();
				for (int k = i + 1; k < rowElements.size(); k++) {
					Set<Integer> secondPossibleVals = rowElements.get(k).getPossibleValues();
					if (firstPossibleVals.size() == PAIR_SIZE && secondPossibleVals.size() == PAIR_SIZE
							&& firstPossibleVals.equals(secondPossibleVals)) {

						result = new StringBuilder();
						Set<Cell> cellsChecking = new HashSet<>();
						cellsChecking.add(rowElements.get(i));
						cellsChecking.add(rowElements.get(k));

						if(rowElements.get(i).isCommitted() || rowElements.get(k).isCommitted() )
							continue;
						
						boolean somethingRemoved = false;
						if (areCellsSameNonet(cellsChecking)) {

							// Rewmove from nonet and row
							List<Cell> nonetCells = grid.getNoNetCells(rowElements.get(i).getCellLocation().getNoNet());

							Set<Cell> checkIfRemovingIsNeeded = new HashSet<>(rowElements);
							checkIfRemovingIsNeeded.addAll(nonetCells);
							checkIfRemovingIsNeeded.removeAll(cellsChecking);

							if (isThereAnythingToRemoveFromCells(firstPossibleVals, checkIfRemovingIsNeeded)) {
								if (!descriptionTextShown) {
									result.append(rowElements.get(i) + " and " + rowElements.get(k)
											+ " belong to the same row and nonet therefore the two numbers can be removed from all other cells in the row and nonet\n\nROW/NONET:\n");
									descriptionTextShown = true;
								}
								for (Cell currentCell : checkIfRemovingIsNeeded) {
									Set<Integer> valuesChanged = new HashSet<>();

									if (!currentCell.isCommitted()) {

										List<Integer> valuesToRemove = new ArrayList<>(firstPossibleVals);
										valuesToRemove.retainAll(currentCell.getPossibleValues());
										if (valuesToRemove.isEmpty())
											continue;

										boolean valueRemoved = false;
										for (int removeVal = 0; removeVal < valuesToRemove.size(); removeVal++) {

											if (currentCell.getPossibleValues()
													.contains(valuesToRemove.get(removeVal))) {
												if (!valueRemoved) {
													result.append(currentCell + " removing ");
												}
												if (removeVal == valuesToRemove.size() - 1) {
													result.append(valuesToRemove.get(removeVal));
													currentCell.removePossibleValues(valuesToRemove.get(removeVal));
													valuesChanged.add(valuesToRemove.get(removeVal));
													result.append("\n");
													continue;
												}

												result.append(valuesToRemove.get(removeVal) + "/");
												currentCell.removePossibleValues(valuesToRemove.get(removeVal));
												valueRemoved = true;
												valuesChanged.add(valuesToRemove.get(removeVal));
											}

										}
										cellsChanged.put(currentCell,
												new CellAdjustment(valuesChanged, new HashSet<>()));
									}
								}

								for (Cell currentCell : cellsChecking) {
									cellsChanged.put(currentCell,
											new CellAdjustment(new HashSet<>(), firstPossibleVals));

								}
								return new Logic(DIFFICULTY_MEDIUM, result.toString(), cellsChanged);
							}

						} else {
							Set<Cell> checkIfRemovingIsNeeded = new HashSet<>(rowElements);
							checkIfRemovingIsNeeded.removeAll(cellsChecking);

							if (isThereAnythingToRemoveFromCells(firstPossibleVals, checkIfRemovingIsNeeded)) {
								if (!descriptionTextShown) {
									result.append(rowElements.get(i) + " and " + rowElements.get(k)
											+ " belong to the same row therefore the two numbers can be removed from all other cells in the row\nROW:\n");
									descriptionTextShown = true;
								} // Remove from row

								for (Cell rowCell : checkIfRemovingIsNeeded) {
									if (!rowCell.isCommitted() && !rowCell.equals(rowElements.get(i))
											&& !rowCell.equals(rowElements.get(k))) {

										Set<Integer> valuesChanged = new HashSet<>();

										List<Integer> valuesToRemove = new ArrayList<>(firstPossibleVals);
										valuesToRemove.retainAll(rowCell.getPossibleValues());

										if (valuesToRemove.isEmpty())
											continue;

										boolean valueRemoved = false;

										for (int removeVal = 0; removeVal < valuesToRemove.size(); removeVal++) {

											if (rowCell.getPossibleValues().contains(valuesToRemove.get(removeVal))) {
												if (!valueRemoved) {
													result.append(rowCell + " removing ");
												}
												if (removeVal == valuesToRemove.size() - 1) {
													result.append(valuesToRemove.get(removeVal));
													rowCell.removePossibleValues(valuesToRemove.get(removeVal));
													valuesChanged.add(valuesToRemove.get(removeVal));
													result.append("\n");
													continue;
												}

												result.append(valuesToRemove.get(removeVal) + "/");
												rowCell.removePossibleValues(valuesToRemove.get(removeVal));
												valueRemoved = true;
												valuesChanged.add(valuesToRemove.get(removeVal));
											}
										}
										cellsChanged.put(rowCell, new CellAdjustment(valuesChanged, new HashSet<>()));

									}
								}

								for (Cell currentCell : cellsChecking) {
									cellsChanged.put(currentCell,
											new CellAdjustment(new HashSet<>(), firstPossibleVals));

								}
								return new Logic(DIFFICULTY_MEDIUM, result.toString(), cellsChanged);
							}
						}

					}
				}
			}

		}

		return null;

	}

	public Logic nakedTriples(KillerSudokuGrid grid) {
		Logic result = nakedTriplesRow(grid);
		if (result != null)
			return result;

		result = nakedTriplesCol(grid);
		if (result != null)
			return result;

		result = nakedTriplesNonet(grid);
		if (result != null)
			return result;

		return null;
	}

	private Logic nakedTriplesCol(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textDisplayed = false;
		StringBuilder result = new StringBuilder();

		for (int col = 1; col <= SIZE; col++) {
			List<Cell> colCells = grid.getCols(col);

			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {

						Set<Integer> threeNumbers = new HashSet<>(Arrays.asList(firstVal, secondVal, thirdVal));

						for (int firstComp = 0; firstComp < colCells.size(); firstComp++) {
							Cell firstCell = colCells.get(firstComp);
							for (int secondComp = firstComp + 1; secondComp < colCells.size(); secondComp++) {
								Cell secondCell = colCells.get(secondComp);
								for (int thirdComp = secondComp + 1; thirdComp < colCells.size(); thirdComp++) {
									Cell thirdCell = colCells.get(thirdComp);

									if (firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted())
										continue;

									Set<Cell> cellsWorkingWith = new HashSet<>();

									cellsWorkingWith.add(firstCell);
									cellsWorkingWith.add(secondCell);
									cellsWorkingWith.add(thirdCell);

									if(firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted())
										continue;
									
									if (areCellsSameNonet(cellsWorkingWith)) {

										Set<Cell> combinedCells = new HashSet<>(colCells);
										combinedCells
												.addAll(grid.getNoNetCells(firstCell.getCellLocation().getNoNet()));
										List<Cell> combinedCellList = new ArrayList<>(combinedCells);
										if (!uniqueToRegion(cellsWorkingWith, combinedCellList, threeNumbers)) {

											if (cellsOnlyContainNumbers(cellsWorkingWith, threeNumbers)) {

												Set<Cell> cellsToCheck = new HashSet<>();
												cellsToCheck.addAll(colCells);
												cellsToCheck.addAll(
														grid.getNoNetCells(firstCell.getCellLocation().getNoNet()));
												cellsToCheck.removeAll(cellsWorkingWith);

												if (isThereAnythingToRemoveFromCells(threeNumbers, cellsToCheck)) {

													for (Cell currentCell : cellsToCheck) {

														List<Integer> valuesToRemove = new ArrayList<>(threeNumbers);
														valuesToRemove.retainAll(currentCell.getPossibleValues());

														if (valuesToRemove.isEmpty())
															continue;

														if (!currentCell.isCommitted()) {

															Set<Integer> valuesChanged = new HashSet<>();

															if (!textDisplayed) {
																result.append(colCells.get(firstComp) + ", "
																		+ colCells.get(secondComp) + " and "
																		+ colCells.get(thirdComp)
																		+ " contain a naked triple unique within the col "
																		+ col
																		+ " and nonet. The values of the candidates are "
																		+ threeNumbers
																		+ " therefore the candidates must be removed from all other cells in the col\nCOLUMN/NONET:\n");
																textDisplayed = true;
															}

															result.append(currentCell + " removing ");
															for (int value = 0; value < valuesToRemove
																	.size(); value++) {
																if (value == valuesToRemove.size() - 1) {
																	result.append(valuesToRemove.get(value));
																	valuesChanged.add(valuesToRemove.get(value));
																	currentCell.removePossibleValues(
																			valuesToRemove.get(value));
																	result.append("\n");
																	continue;
																}
																result.append(valuesToRemove.get(value) + "/");
																currentCell.removePossibleValues(
																		valuesToRemove.get(value));
																valuesChanged.add(valuesToRemove.get(value));

															}
															cellsChanged.put(currentCell,
																	new CellAdjustment(valuesChanged, new HashSet<>()));
														}
													}
													if (cellsChanged.isEmpty())
														continue;

													for (Cell currentCell : cellsWorkingWith) {
														Set<Integer> candidates = new HashSet<>(threeNumbers);
														candidates.retainAll(currentCell.getPossibleValues());

														cellsChanged.put(currentCell,
																new CellAdjustment(new HashSet<>(), candidates));
													}

													return new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
															cellsChanged);
												}

											}
										}
									} else {
										Set<Cell> combinedCells = new HashSet<>(colCells);
										List<Cell> combinedCellList = new ArrayList<>(combinedCells);
										if (!uniqueToRegion(cellsWorkingWith, combinedCellList, threeNumbers)) {

											if (cellsOnlyContainNumbers(cellsWorkingWith, threeNumbers)) {

												Set<Cell> cellsToCheck = new HashSet<>();
												cellsToCheck.addAll(colCells);
												cellsToCheck.removeAll(cellsWorkingWith);

												if (isThereAnythingToRemoveFromCells(threeNumbers, cellsToCheck)) {

													for (Cell currentCell : cellsToCheck) {

														List<Integer> valuesToRemove = new ArrayList<>(threeNumbers);
														valuesToRemove.retainAll(currentCell.getPossibleValues());

														if (valuesToRemove.isEmpty())
															continue;

														if (!currentCell.isCommitted()) {

															Set<Integer> valuesChanged = new HashSet<>();

															if (!textDisplayed) {
																result.append(colCells.get(firstComp) + ", "
																		+ colCells.get(secondComp) + " and "
																		+ colCells.get(thirdComp)
																		+ " contain a naked triple unique within the col "
																		+ col + " . The values of the candidates are "
																		+ threeNumbers
																		+ " therefore the candidates must be removed from all other cells in the row\nCOLUMN:\n");
																textDisplayed = true;
															}

															result.append(currentCell + " removing ");
															for (int value = 0; value < valuesToRemove
																	.size(); value++) {
																if (value == valuesToRemove.size() - 1) {
																	result.append(valuesToRemove.get(value));
																	valuesChanged.add(valuesToRemove.get(value));
																	currentCell.removePossibleValues(
																			valuesToRemove.get(value));
																	result.append("\n");
																	continue;
																}
																result.append(valuesToRemove.get(value) + "/");
																currentCell.removePossibleValues(
																		valuesToRemove.get(value));
																valuesChanged.add(valuesToRemove.get(value));

															}
															cellsChanged.put(currentCell,
																	new CellAdjustment(valuesChanged, new HashSet<>()));
														}
													}
													if (cellsChanged.isEmpty())
														continue;

													for (Cell currentCell : cellsWorkingWith) {
														Set<Integer> candidates = new HashSet<>(threeNumbers);
														candidates.retainAll(currentCell.getPossibleValues());

														cellsChanged.put(currentCell,
																new CellAdjustment(new HashSet<>(), candidates));
													}

													return new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
															cellsChanged);
												}
											}
										}
									}
								}
							}
						}

					}
				}
			}
		}
		return null;

	}

	private Logic nakedTriplesNonet(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textDisplayed = false;
		StringBuilder result = new StringBuilder();

		for (int nonet = 1; nonet <= SIZE; nonet++) {
			List<Cell> nonetCells = grid.getNoNetCells(nonet);

			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {

						Set<Integer> threeNumbers = new HashSet<>(Arrays.asList(firstVal, secondVal, thirdVal));

						for (int firstComp = 0; firstComp < nonetCells.size(); firstComp++) {
							Cell firstCell = nonetCells.get(firstComp);
							for (int secondComp = firstComp + 1; secondComp < nonetCells.size(); secondComp++) {
								Cell secondCell = nonetCells.get(secondComp);
								for (int thirdComp = secondComp + 1; thirdComp < nonetCells.size(); thirdComp++) {
									Cell thirdCell = nonetCells.get(thirdComp);

									if (firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted())
										continue;

									Set<Cell> cellsWorkingWith = new HashSet<>();

									cellsWorkingWith.add(firstCell);
									cellsWorkingWith.add(secondCell);
									cellsWorkingWith.add(thirdCell);

									if(firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted())
										continue;
									if (areCellsSameNonet(cellsWorkingWith)) {

										Set<Cell> combinedCells = new HashSet<>(nonetCells);
										combinedCells
												.addAll(grid.getNoNetCells(firstCell.getCellLocation().getNoNet()));
										List<Cell> combinedCellList = new ArrayList<>(combinedCells);
										if (!uniqueToRegion(cellsWorkingWith, combinedCellList, threeNumbers)) {

											if (cellsOnlyContainNumbers(cellsWorkingWith, threeNumbers)) {

												Set<Cell> cellsToCheck = new HashSet<>();
												cellsToCheck.addAll(nonetCells);
												cellsToCheck.addAll(
														grid.getNoNetCells(firstCell.getCellLocation().getNoNet()));
												cellsToCheck.removeAll(cellsWorkingWith);

												if (isThereAnythingToRemoveFromCells(threeNumbers, cellsToCheck)) {

													for (Cell currentCell : cellsToCheck) {

														List<Integer> valuesToRemove = new ArrayList<>(threeNumbers);
														valuesToRemove.retainAll(currentCell.getPossibleValues());

														if (valuesToRemove.isEmpty())
															continue;

														if (!currentCell.isCommitted()) {

															Set<Integer> valuesChanged = new HashSet<>();

															if (!textDisplayed) {
																result.append(nonetCells.get(firstComp) + ", "
																		+ nonetCells.get(secondComp) + " and "
																		+ nonetCells.get(thirdComp)
																		+ " contain a naked triple unique within the nonet "
																		+ nonet
																		+ ". The values of the candidates are "
																		+ threeNumbers
																		+ " therefore the candidates must be removed from all other cells in the nonet\nNONET:\n");
																textDisplayed = true;
															}

															result.append(currentCell + " removing ");
															for (int value = 0; value < valuesToRemove
																	.size(); value++) {
																if (value == valuesToRemove.size() - 1) {
																	result.append(valuesToRemove.get(value));
																	valuesChanged.add(valuesToRemove.get(value));
																	currentCell.removePossibleValues(
																			valuesToRemove.get(value));
																	result.append("\n");
																	continue;
																}
																result.append(valuesToRemove.get(value) + "/");
																currentCell.removePossibleValues(
																		valuesToRemove.get(value));
																valuesChanged.add(valuesToRemove.get(value));

															}
															cellsChanged.put(currentCell,
																	new CellAdjustment(valuesChanged, new HashSet<>()));
														}
													}
													if (cellsChanged.isEmpty())
														continue;

													for (Cell currentCell : cellsWorkingWith) {
														Set<Integer> candidates = new HashSet<>(threeNumbers);
														candidates.retainAll(currentCell.getPossibleValues());

														cellsChanged.put(currentCell,
																new CellAdjustment(new HashSet<>(), candidates));
													}

													return new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
															cellsChanged);
												}

											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private Logic nakedTriplesRow(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		boolean textDisplayed = false;
		StringBuilder result = new StringBuilder();

		for (int row = 1; row <= SIZE; row++) {
			List<Cell> rowCells = grid.getRow(row);

			for (int firstVal = 1; firstVal <= SIZE; firstVal++) {
				for (int secondVal = firstVal + 1; secondVal <= SIZE; secondVal++) {
					for (int thirdVal = secondVal + 1; thirdVal <= SIZE; thirdVal++) {

						Set<Integer> threeNumbers = new HashSet<>(Arrays.asList(firstVal, secondVal, thirdVal));

						for (int firstComp = 0; firstComp < rowCells.size(); firstComp++) {
							Cell firstCell = rowCells.get(firstComp);
							for (int secondComp = firstComp + 1; secondComp < rowCells.size(); secondComp++) {
								Cell secondCell = rowCells.get(secondComp);
								for (int thirdComp = secondComp + 1; thirdComp < rowCells.size(); thirdComp++) {
									Cell thirdCell = rowCells.get(thirdComp);

									if (firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted())
										continue;

									Set<Cell> cellsWorkingWith = new HashSet<>();

									cellsWorkingWith.add(firstCell);
									cellsWorkingWith.add(secondCell);
									cellsWorkingWith.add(thirdCell);

									if(firstCell.isCommitted() || secondCell.isCommitted() || thirdCell.isCommitted())
										continue;
									
									if (areCellsSameNonet(cellsWorkingWith)) {

										Set<Cell> combinedCells = new HashSet<>(rowCells);
										combinedCells
												.addAll(grid.getNoNetCells(firstCell.getCellLocation().getNoNet()));
										List<Cell> combinedCellList = new ArrayList<>(combinedCells);
										if (!uniqueToRegion(cellsWorkingWith, combinedCellList, threeNumbers)) {

											if (cellsOnlyContainNumbers(cellsWorkingWith, threeNumbers)) {

												Set<Cell> cellsToCheck = new HashSet<>();
												cellsToCheck.addAll(rowCells);
												cellsToCheck.addAll(
														grid.getNoNetCells(firstCell.getCellLocation().getNoNet()));
												cellsToCheck.removeAll(cellsWorkingWith);

												if (isThereAnythingToRemoveFromCells(threeNumbers, cellsToCheck)) {

													for (Cell currentCell : cellsToCheck) {

														List<Integer> valuesToRemove = new ArrayList<>(threeNumbers);
														valuesToRemove.retainAll(currentCell.getPossibleValues());

														if (valuesToRemove.isEmpty())
															continue;

														if (!currentCell.isCommitted()) {

															Set<Integer> valuesChanged = new HashSet<>();

															if (!textDisplayed) {
																result.append(rowCells.get(firstComp) + ", "
																		+ rowCells.get(secondComp) + " and "
																		+ rowCells.get(thirdComp)
																		+ " contain a naked triple unique within the row "
																		+ row
																		+ " and nonet.The values of the candidates are "
																		+ threeNumbers
																		+ " therefore the candidates must be removed from all other cells in the row\nROW/NONET:\n");
																textDisplayed = true;
															}

															result.append(currentCell + " removing ");
															for (int value = 0; value < valuesToRemove
																	.size(); value++) {
																if (value == valuesToRemove.size() - 1) {
																	result.append(valuesToRemove.get(value));
																	valuesChanged.add(valuesToRemove.get(value));
																	currentCell.removePossibleValues(
																			valuesToRemove.get(value));
																	result.append("\n");
																	continue;
																}
																result.append(valuesToRemove.get(value) + "/");
																currentCell.removePossibleValues(
																		valuesToRemove.get(value));
																valuesChanged.add(valuesToRemove.get(value));

															}
															cellsChanged.put(currentCell,
																	new CellAdjustment(valuesChanged, new HashSet<>()));
														}
													}
													if (cellsChanged.isEmpty())
														continue;

													for (Cell currentCell : cellsWorkingWith) {
														Set<Integer> candidates = new HashSet<>(threeNumbers);
														candidates.retainAll(currentCell.getPossibleValues());

														cellsChanged.put(currentCell,
																new CellAdjustment(new HashSet<>(), candidates));
													}

													return new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
															cellsChanged);
												}

											}
										}
									} else {
										Set<Cell> combinedCells = new HashSet<>(rowCells);
										List<Cell> combinedCellList = new ArrayList<>(combinedCells);
										if (!uniqueToRegion(cellsWorkingWith, combinedCellList, threeNumbers)) {

											if (cellsOnlyContainNumbers(cellsWorkingWith, threeNumbers)) {

												Set<Cell> cellsToCheck = new HashSet<>();
												cellsToCheck.addAll(rowCells);
												cellsToCheck.removeAll(cellsWorkingWith);

												if (isThereAnythingToRemoveFromCells(threeNumbers, cellsToCheck)) {

													for (Cell currentCell : cellsToCheck) {

														List<Integer> valuesToRemove = new ArrayList<>(threeNumbers);
														valuesToRemove.retainAll(currentCell.getPossibleValues());

														if (valuesToRemove.isEmpty())
															continue;

														if (!currentCell.isCommitted()) {

															Set<Integer> valuesChanged = new HashSet<>();

															if (!textDisplayed) {
																result.append(rowCells.get(firstComp) + ", "
																		+ rowCells.get(secondComp) + " and "
																		+ rowCells.get(thirdComp)
																		+ " contain a naked triple unique within the row "
																		+ row + " . The values of the candidates are "
																		+ threeNumbers
																		+ " therefore the candidates must be removed from all other cells in the row\nROW:\n");
																textDisplayed = true;
															}

															result.append(currentCell + " removing ");
															for (int value = 0; value < valuesToRemove
																	.size(); value++) {
																if (value == valuesToRemove.size() - 1) {
																	result.append(valuesToRemove.get(value));
																	valuesChanged.add(valuesToRemove.get(value));
																	currentCell.removePossibleValues(
																			valuesToRemove.get(value));
																	result.append("\n");
																	continue;
																}
																result.append(valuesToRemove.get(value) + "/");
																currentCell.removePossibleValues(
																		valuesToRemove.get(value));
																valuesChanged.add(valuesToRemove.get(value));

															}
															cellsChanged.put(currentCell,
																	new CellAdjustment(valuesChanged, new HashSet<>()));
														}
													}
													if (cellsChanged.isEmpty())
														continue;

													for (Cell currentCell : cellsWorkingWith) {
														Set<Integer> candidates = new HashSet<>(threeNumbers);
														candidates.retainAll(currentCell.getPossibleValues());

														cellsChanged.put(currentCell,
																new CellAdjustment(new HashSet<>(), candidates));
													}

													return new Logic(DIFFICULTY_EXPERIENCED, result.toString(),
															cellsChanged);
												}
											}
										}
									}
								}
							}
						}

					}
				}
			}
		}
		return null;
	}

	private boolean isThereAnythingToRemoveFromCells(Set<Integer> jointValuesSubset,
			Set<Cell> checkIfRemovingIsNeeded) {
		for (Cell currentCell : checkIfRemovingIsNeeded) {
			for (int value : jointValuesSubset) {
				if (currentCell.getPossibleValues().contains(value))
					return true;
			}
		}
		return false;
	}


	private boolean areCellsSameNonet(Set<Cell> cells) {
		int nonet = -1;
		boolean nonetSet = false;
		for (Cell currentCell : cells) {
			if (!nonetSet) {
				nonet = currentCell.getCellLocation().getNoNet();
				nonetSet = true;
				continue;
			}
			if (nonet != currentCell.getCellLocation().getNoNet()) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Removes possible values from a nonet after a value has been committed.
	 * 
	 * @param cell
	 *            cell that has been committed
	 * @return
	 * 
	 */
	private Logic removeSolvedNonet(Cell cell) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		Location cellLocation = cell.getCellLocation();
		int cellValue = cell.getValueCommitted();
		int cellNonet = cellLocation.getNoNet();
		int cellRow = cellLocation.getRow();
		int cellCol = cellLocation.getCol();

		List<Cell> cellInNonet = grid.getNoNetCells(cellNonet);

		StringBuilder buildReasonString = new StringBuilder("NONET CELLS\n");

		Set<Cell> cellsWorkingWith = new HashSet<>(cellInNonet);
		cellsWorkingWith.remove(cell);

		Set<Integer> valueToRemove = new HashSet<>(Arrays.asList(cell.getValueCommitted()));

		if (isThereAnythingToRemoveFromCells(valueToRemove, cellsWorkingWith)) {

			for (Cell currentNonetCell : cellsWorkingWith) {
				Set<Integer> valuesChanged = new HashSet<>();

				if (currentNonetCell.getPossibleValues().contains(cell.getValueCommitted())
						&& !currentNonetCell.isCommitted() && !currentNonetCell.equals(cell)) {

					buildReasonString.append(currentNonetCell + " cannot contain " + cellValue + "\n");
					currentNonetCell.removePossibleValues(cellValue);
					valuesChanged.add(cellValue);
				}
				if (!valuesChanged.isEmpty())
					cellsChanged.put(currentNonetCell, new CellAdjustment(valuesChanged, new HashSet<>()));
			}

			if (cellsChanged.isEmpty()) {
				return null;
			}

			cellsChanged.put(cell,
					new CellAdjustment(new HashSet<>(), new HashSet<>(Arrays.asList(cell.getValueCommitted()))));

			return new Logic(DIFFICULTY_EASY, buildReasonString.toString(), cellsChanged);
		}

		return null;
	}

	/**
	 * Remove possible values from a cage after a cell has been committed.
	 * 
	 * @param cell
	 *            cell that has been committed.
	 * @return
	 */
	private Logic removeSolvedCage(Cell cell) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		int cellValue = cell.getValueCommitted();
		Location cellLoc = cell.getCellLocation();
		int cellRow = cellLoc.getRow();
		int cellCol = cellLoc.getCol();

		Cage currentCage = grid.getCage(cell);

		Set<Cell> cellsInCage = new HashSet<>();
		for (Location cellCageLoc : currentCage.getLocation()) {
			cellsInCage.add(grid.getCell(cellCageLoc));
		}

		StringBuilder buildReasonString = new StringBuilder("CAGE CELLS\n");

		Set<Cell> cellsWorkingWith = new HashSet<>(cellsInCage);
		cellsWorkingWith.remove(cell);

		Set<Integer> valueToRemove = new HashSet<>(Arrays.asList(cell.getValueCommitted()));

		if (isThereAnythingToRemoveFromCells(valueToRemove, cellsWorkingWith)) {

			for (Cell currentCageCell : grid.getCellsInCage(currentCage)) {
				Set<Integer> valuesChanged = new HashSet<>();

				if (currentCageCell.getPossibleValues().contains(cell.getValueCommitted())
						&& !currentCageCell.isCommitted() && !currentCageCell.equals(cell)) {
					buildReasonString.append(currentCageCell + " cannot contain " + cellValue + "\n");
					currentCageCell.removePossibleValues(cellValue);
					valuesChanged.add(cellValue);
				}
				if (!valuesChanged.isEmpty())
					cellsChanged.put(currentCageCell, new CellAdjustment(valuesChanged, new HashSet<>()));
			}

			if (cellsChanged.isEmpty()) {
				return null;
			}

			cellsChanged.put(cell,
					new CellAdjustment(new HashSet<>(), new HashSet<>(Arrays.asList(cell.getValueCommitted()))));

			return new Logic(DIFFICULTY_EASY, buildReasonString.toString(), cellsChanged);

		}
		return null;
	}

	/**
	 * Remove possible values for a column after a cell has been committed.
	 * 
	 * @param cell
	 *            that has been committed.
	 */
	private Logic removeSolvedCol(Cell cell) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		Location cellLocation = cell.getCellLocation();
		int cellValue = cell.getValueCommitted();
		int cellCol = cellLocation.getCol();
		int cellRow = cellLocation.getRow();

		List<Cell> cellsInCol = grid.getCols(cellCol);

		StringBuilder buildReasonString = new StringBuilder("COLUMN CELLS\n");

		Set<Cell> cellsWorkingWith = new HashSet<>(cellsInCol);
		cellsWorkingWith.remove(cell);

		Set<Integer> valueToRemove = new HashSet<>(Arrays.asList(cell.getValueCommitted()));

		if (isThereAnythingToRemoveFromCells(valueToRemove, cellsWorkingWith)) {

			for (Cell currentColCell : cellsInCol) { // Run through row cells
				Set<Integer> valuesChanged = new HashSet<>();

				if (currentColCell.getPossibleValues().contains(cell.getValueCommitted())
						&& !currentColCell.isCommitted() && !currentColCell.equals(cell)) {

					buildReasonString.append(currentColCell + " cannot contain " + cellValue + "\n");
					currentColCell.removePossibleValues(cellValue);
					valuesChanged.add(cellValue);
				}

				if (!valuesChanged.isEmpty())
					cellsChanged.put(currentColCell, new CellAdjustment(valuesChanged, new HashSet<>()));

			}

			if (cellsChanged.isEmpty()) {
				return null;
			}

			cellsChanged.put(cell,
					new CellAdjustment(new HashSet<>(), new HashSet<>(Arrays.asList(cell.getValueCommitted()))));

			return new Logic(DIFFICULTY_EASY, buildReasonString.toString(), cellsChanged);

		}
		return null;
	}

	public Logic killerCageCombination(KillerSudokuGrid grid) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		StringBuilder result = new StringBuilder();
		for (Cage currentCage : grid.getCages()) {
			if (isUniqueSum(currentCage) && !currentCage.isUniqueValuesAssigned()) {
				List<SumCombination> combination = SumToN.SumUpTo(currentCage.getRemainingValue(),
						currentCage.getUnsolvedLocations().size(), getAllCagePossibleValues(currentCage));

				if (combination.size() == 1 && !currentCage.isCommitted() && !currentCage.isUniqueValuesAssigned()) {

					boolean anythingToRemove = false;
					Set<Cell> cellsWorkingWith = new HashSet<>();
					for (Location currentCageCell : currentCage.getUnsolvedLocations()) {
						Cell currentCell = grid.getCell(currentCageCell);
						cellsWorkingWith.add(currentCell);

						Set<Integer> cellPossibleValues = new HashSet<>(currentCell.getPossibleValues());
						cellPossibleValues.removeAll(combination.get(0).getValuesThatSum());
						if (!cellPossibleValues.isEmpty())
							anythingToRemove = true;

					}

					if (anythingToRemove) {

						for (Cell currentCell : grid.getCellsInCage(currentCage)) {
							Set<Integer> cells = new HashSet<>(currentCell.getPossibleValues());
							Set<Integer> cellsRemoved = new HashSet<>(currentCell.getPossibleValues());

							cells.retainAll(combination.get(0).getValuesThatSum());
							cellsRemoved.removeAll(combination.get(0).getValuesThatSum());

							cellsChanged.put(currentCell, new CellAdjustment(cellsRemoved, cells));
						}

						boolean displayedText = false;
						for (Location locOfCell : currentCage.getUnsolvedLocations()) {

							Cell currentCell = grid.getCell(locOfCell);

							Set<Cell> currentCellSet = new HashSet<>(Arrays.asList(currentCell));

							if (isThereAnythingToRemoveFromCells(getCellsToRemove(currentCell.getPossibleValues(),
									combination.get(0).getValuesThatSum()), currentCellSet)) {

								if (!displayedText) {
									result.append("\n Cage containing " + currentCage.unsolvedToString()
											+ "with sum of " + currentCage.getRemainingValue() + " has unique sum "
											+ combination.get(0) + "\n\n");
									displayedText = true;
								}

								Set<Integer> valuesChanged = new HashSet<>();

								Set<Integer> cellsToRemove = getCellsToRemove(currentCell.getPossibleValues(),
										combination.get(0).getValuesThatSum());

								result.append(currentCell + " removing ");
								List<Integer> cellsToRemoveList = new ArrayList<>(cellsToRemove);

								for (int i = 0; i < cellsToRemoveList.size(); i++) {
									if (i == cellsToRemoveList.size() - 1) {
										result.append(cellsToRemoveList.get(i));
										currentCell.removePossibleValues(cellsToRemoveList.get(i));
										valuesChanged.add(cellsToRemoveList.get(i));
										continue;
									}

									result.append(cellsToRemoveList.get(i) + "/");
									currentCell.removePossibleValues(cellsToRemoveList.get(i));
									valuesChanged.add(cellsToRemoveList.get(i));

								}
								result.append("\n");
								currentCage.setUniqueValuesAssigned();

							}
						}

					}
				}

			}
		}
		if (cellsChanged.isEmpty())
			return null;

		return new Logic(DIFFICULTY_MEDIUM, result.toString(), cellsChanged);
	}

	/**
	 * Removes possible values from a row after a cell has been committed.
	 * 
	 * @param cell
	 *            the cell that has been committed.
	 * @return
	 */

	private Logic removeSolvedRow(Cell cell) {
		Map<Cell, CellAdjustment> cellsChanged = new HashMap<>();

		Location cellLocation = cell.getCellLocation();
		int cellValue = cell.getValueCommitted();
		int cellRow = cellLocation.getRow();
		int cellCol = cellLocation.getCol();

		List<Cell> cellsInRow = grid.getRow(cellRow);

		StringBuilder buildReasonString = new StringBuilder("ROW CELLS\n");

		Set<Cell> cellsWorkingWith = new HashSet<>(cellsInRow);
		cellsWorkingWith.remove(cell);

		Set<Integer> valueToRemove = new HashSet<>(Arrays.asList(cell.getValueCommitted()));

		if (isThereAnythingToRemoveFromCells(valueToRemove, cellsWorkingWith)) {

			for (Cell currentRowCell : cellsInRow) { // Run through row cells
				Set<Integer> valuesChanged = new HashSet<>();
				if (currentRowCell.getPossibleValues().contains(cell.getValueCommitted())
						&& !currentRowCell.isCommitted() && !currentRowCell.equals(cell)) {
					buildReasonString.append(currentRowCell + " cannot contain " + cellValue + "\n");
					currentRowCell.removePossibleValues(cellValue);
					valuesChanged.add(cellValue);
				}
				if (!valuesChanged.isEmpty())
					cellsChanged.put(currentRowCell, new CellAdjustment(valuesChanged, new HashSet<>()));
			}
			if (cellsChanged.isEmpty()) {
				return null;
			}

			cellsChanged.put(cell,
					new CellAdjustment(new HashSet<>(), new HashSet<>(Arrays.asList(cell.getValueCommitted()))));

			return new Logic(DIFFICULTY_EASY, buildReasonString.toString(), cellsChanged);
		}
		return null;
	}

	private Set<Integer> getCellsToRemove(Set<Integer> currentValues, Set<Integer> valuesToRemove) {
		Set<Integer> result = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

		result.retainAll(currentValues);
		result.removeAll(valuesToRemove);

		return result;
	}

	private boolean isUniqueSum(Cage checkCage) {
		Set<Integer> allPossibleValuesOfCage = getAllCagePossibleValues(checkCage);

		List<SumCombination> combinations = SumToN.SumUpTo(checkCage.getTotalValue(), checkCage.getLocation().size(),
				allPossibleValuesOfCage);

		if (combinations.size() == 1) {
			return true;
		}

		return false;
	}

	private Set<Integer> getAllCagePossibleValues(Cage checkCage) {
		Set<Integer> allPossibleCageValues = new HashSet<>();

		for (Location currentLocation : checkCage.getUnsolvedLocations()) {
			Cell currentCell = grid.getCell(currentLocation);
			allPossibleCageValues.addAll(currentCell.getPossibleValues());
		}
		return allPossibleCageValues;
	}

	public Logic hiddenSingles(KillerSudokuGrid grid) {

		for (Cage cage : grid.getCages()) {
			if (cage.getUnsolvedLocations().size() == 1) {
				for (Location unsolvedLoc : cage.getUnsolvedLocations()) {
					Cell unsolved = grid.getCell(unsolvedLoc);
					String result = unsolved + " is the last remaining cell in cage, setting "
							+ cage.getRemainingValue();
					solveCell(unsolved, cage.getRemainingValue());

					Logic logic = new Logic(DIFFICULTY_EASY, result, unsolved);
					return logic;
				}
			}
		}

		for (int nets = 1; nets <= SIZE; nets++) { // run through all nets
			/* Get list of cells in nonet */
			List<Cell> noNetCells = grid.getNoNetCells(nets);

			for (int cellIdx = 0; cellIdx < noNetCells.size(); cellIdx++) {
				List<Integer> valuesOfCell = new ArrayList<>(noNetCells.get(cellIdx).getPossibleValues());

				Cell currentCell = noNetCells.get(cellIdx);
				for (int numberIdx = 0; numberIdx < valuesOfCell.size(); numberIdx++) {
					int currentNumber = valuesOfCell.get(numberIdx);
					if (uniqueInRegion(cellIdx, noNetCells, currentNumber) && !currentCell.isCommitted()) {

						solveCell(currentCell, currentNumber);
						Location cellLocation = currentCell.getCellLocation();

						String reasonMsg = currentCell + " set to " + currentNumber + " unique within nonet";

						Logic reason = new Logic(DIFFICULTY_MEDIUM, reasonMsg, currentCell);
						return reason;
					}
				}
			}

		}

		for (int rows = 1; rows <= SIZE; rows++) { // run through all rows
			/* Get list of cells in row */
			List<Cell> rowCells = grid.getRow(rows);

			for (int cellIdx = 0; cellIdx < rowCells.size(); cellIdx++) {
				List<Integer> valuesOfCell = new ArrayList<>(rowCells.get(cellIdx).getPossibleValues());

				Cell currentCell = rowCells.get(cellIdx);
				for (int numberIdx = 0; numberIdx < valuesOfCell.size(); numberIdx++) {
					int currentNumber = valuesOfCell.get(numberIdx);
					if (uniqueInRegion(cellIdx, rowCells, currentNumber) && !currentCell.isCommitted()) {

						solveCell(currentCell, currentNumber);
						Location cellLocation = currentCell.getCellLocation();

						String reasonMsg = currentCell + " set to " + currentNumber + " unique within row";

						Logic reason = new Logic(DIFFICULTY_MEDIUM, reasonMsg, currentCell);
						return reason;
					}

				}

			}
		}

		for (int cols = 1; cols <= SIZE; cols++) { // run through all cols
			/* Get list of cells in cols */
			List<Cell> colCells = grid.getCols(cols);

			for (int cellIdx = 0; cellIdx < colCells.size(); cellIdx++) {
				List<Integer> valuesOfCell = new ArrayList<>(colCells.get(cellIdx).getPossibleValues());

				Cell currentCell = colCells.get(cellIdx);
				for (int numberIdx = 0; numberIdx < valuesOfCell.size(); numberIdx++) {
					int currentNumber = valuesOfCell.get(numberIdx);

					if (uniqueInRegion(cellIdx, colCells, currentNumber) && !currentCell.isCommitted()) {
						solveCell(currentCell, currentNumber);
						Location cellLocation = currentCell.getCellLocation();

						String reasonMsg = currentCell + " set to " + currentNumber + " unique within column";

						Logic reason = new Logic(DIFFICULTY_MEDIUM, reasonMsg, currentCell);
						return reason;
					}
				}
			}

		}

		return null;
	}

	private boolean uniqueInRegion(int idx, List<Cell> cells, int value) {

		for (int x = 0; x < cells.size(); x++) {
			if (idx != x) { // Don't view current cell
				if (cells.get(x).getPossibleValues().contains(value)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Method when given a cell and a value will then set the cell value to be
	 * that which was given.
	 * 
	 * @param cell
	 *            the cell to set the value within
	 * @param value
	 *            the value to commit.
	 */
	private void solveCell(Cell cell, int value) {
		Cage cage = grid.getCage(cell);
		cell.setValueCommitted(value);
		cage.decreaseRemainingSum(value);
		cage.setSolvedCells(cell.getCellLocation());

		if (cage.cageIsFilled()) // If all values committed in cage
			cage.setCommitted();

	}

	public Logic nakedSingles() {
		for (Cell cell : getSingleCellsSet()) {
			return solveSingleCell(cell);
		}

		return null;
	}

	private Logic solveSingleCell(Cell cell) {
		if (cell.isOnlyOnePossibility()) {
			int commitVal = cell.getOnlyPossibility();
			solveCell(cell, commitVal);
			String helpText = cell + " only has one possible value, setting " + commitVal;
			Logic logic = new Logic(DIFFICULTY_EASY, helpText, cell);

			return logic;
		}
		return null;
	}

	private Set<Cell> getSingleCellsSet() {
		Set<Cell> singleCells = new HashSet<Cell>();
		Cell[][] cells = grid.getSudokuCells();
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (cells[row][col].isOnlyOnePossibility() && !cells[row][col].isCommitted()) {
					singleCells.add(cells[row][col]);
				}
			}
		}
		return singleCells;
	}

}
