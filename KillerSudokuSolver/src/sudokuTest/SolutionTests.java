package sudokuTest;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import sudokuModel.Cage;
import sudokuModel.Cell;
import sudokuModel.KillerSudokuGrid;
import sudokuModel.Location;
import sudokuModel.ReadCages;
import sudokuModel.Solver;

public class SolutionTests {

	protected Solver solver;
	protected KillerSudokuGrid grid;

	/**
	 * Before each test the grid needs to be constructed.
	 */
	@Before
	public void init() {
		Set<Cage> cages = ReadCages.readCagesFromString("Example1.txt");
		grid = new KillerSudokuGrid(cages);
		solver = new Solver(grid);
	}

	@Test
	public void testSingleCellCages() {
		Set<Cage> cagesOnBoard = ReadCages.readCagesFromString("testing.txt");
		grid = new KillerSudokuGrid(cagesOnBoard);
		solver.changeGridForSolving(grid);

		
		assertTrue(solver.singleCagesPresent(grid));
		
		solver.checkSingleCages(grid);
		solver.checkSingleCages(grid);

		assertEquals(grid.getCell(Location.getInstance(6, 3)).getValueCommitted(), 7);
		assertEquals(grid.getCell(Location.getInstance(4, 7)).getValueCommitted(), 4);

	}

	@Test
	public void testNakedSingles() {

		grid.getCell(Location.getInstance(1, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2)));

		solver.nakedSingles();

		assertEquals(grid.getCell(Location.getInstance(1, 2)).getValueCommitted(), 2);
	}

	@Test
	public void testHiddenSinglesNonet() {
		// Cells needed to be preset
		grid.getCell(Location.getInstance(9, 1)).setValueCommitted(9);
		grid.getCell(Location.getInstance(9, 2)).setValueCommitted(1);
		grid.getCell(Location.getInstance(8, 2)).setValueCommitted(6);
		grid.getCell(Location.getInstance(4, 3)).setValueCommitted(8);
		grid.getCell(Location.getInstance(7, 5)).setValueCommitted(8);
		int count = 5;
		while (count != 0) {
			solver.ruleOfOne(grid);
			count--;
		}

		// Hidden single should be in R8 C1
		solver.hiddenSingles(grid);
		Cell cellChanged = grid.getCell(Location.getInstance(8, 1));
		int valueShouldBe = 8;
		assertEquals(cellChanged.getValueCommitted(), valueShouldBe);
	}

	@Test
	public void testHiddenSinglesRow() {
		// Cells needed to be preset
		grid.getCell(Location.getInstance(1, 1)).setValueCommitted(2);
		grid.getCell(Location.getInstance(1, 5)).setValueCommitted(7);
		grid.getCell(Location.getInstance(1, 8)).setValueCommitted(3);
		grid.getCell(Location.getInstance(1, 9)).setValueCommitted(8);
		grid.getCell(Location.getInstance(7, 3)).setValueCommitted(4);
		grid.getCell(Location.getInstance(3, 5)).setValueCommitted(4);
		grid.getCell(Location.getInstance(6, 7)).setValueCommitted(4);

		int count = 7;
		while (count != 0) {
			solver.ruleOfOne(grid);
			count--;
		}
		// Hidden single should be in R1 C2
		solver.hiddenSingles(grid);
		Cell cellChanged = grid.getCell(Location.getInstance(1, 2));
		int valueShouldBe = 4;
		assertEquals(cellChanged.getValueCommitted(), valueShouldBe);

	}

	@Test
	public void testHiddenSinglePinned() {

		grid.getCell(Location.getInstance(7, 3)).setValueCommitted(4);
		grid.getCell(Location.getInstance(8, 4)).setValueCommitted(4);
		grid.getCell(Location.getInstance(6, 7)).setValueCommitted(4);
		grid.getCell(Location.getInstance(3, 5)).setValueCommitted(4);
		grid.getCell(Location.getInstance(7, 3)).setValueCommitted(4);
		grid.getCell(Location.getInstance(9, 1)).setValueCommitted(9);
		grid.getCell(Location.getInstance(9, 2)).setValueCommitted(1);
		grid.getCell(Location.getInstance(8, 5)).setValueCommitted(6);
		grid.getCell(Location.getInstance(1, 8)).setValueCommitted(3);
		grid.getCell(Location.getInstance(2, 8)).setValueCommitted(7);
		grid.getCell(Location.getInstance(7, 3)).setValueCommitted(4);

		int count = 11;
		while (count != 0) {
			solver.ruleOfOne(grid);
			count--;
		}

		// Hidden single should be in R9 C8
		solver.hiddenSingles(grid);
		Cell cellChanged = grid.getCell(Location.getInstance(9, 8));
		int valueShouldBe = 4;
		assertEquals(cellChanged.getValueCommitted(), valueShouldBe);
	}

	@Test
	public void testNakedPairsRow() {
		grid.getCell(Location.getInstance(1, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 6)));
		grid.getCell(Location.getInstance(1, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 6)));
		grid.getCell(Location.getInstance(3, 9)).setPossibleValues(new HashSet<Integer>(Arrays.asList(7, 6)));
		grid.getCell(Location.getInstance(3, 6)).setPossibleValues(new HashSet<Integer>(Arrays.asList(7, 6)));

		int count = 2;

		while (count != 0) {
			solver.nakedPairs(grid);
			count--;
		}
		// Should be removed from row and nonet
		List<Cell> cellsInFirstRow = grid.getRow(1);
		List<Cell> cellsInFirstNonet = grid.getNoNetCells(1);
		List<Cell> cellsToCheckFirstRow = new ArrayList<>(cellsInFirstRow);
		cellsToCheckFirstRow.remove(grid.getCell(Location.getInstance(1, 2)));
		cellsToCheckFirstRow.remove(grid.getCell(Location.getInstance(1, 3)));
		cellsToCheckFirstRow.addAll(cellsInFirstNonet);
		cellsToCheckFirstRow.remove(grid.getCell(Location.getInstance(1, 2)));
		cellsToCheckFirstRow.remove(grid.getCell(Location.getInstance(1, 3)));
		Set<Integer> valuesRemovedFirst = new HashSet<>(Arrays.asList(1, 6));
		assertTrue(!cellsContainPossibleValues(cellsToCheckFirstRow, valuesRemovedFirst));

		// Should be removed from row.
		List<Cell> cellsInThirdRow = grid.getRow(3);
		List<Cell> cellsToCheckInSecondRow = new ArrayList<>(cellsInThirdRow);
		cellsToCheckInSecondRow.remove(grid.getCell(Location.getInstance(3, 9)));
		cellsToCheckInSecondRow.remove(grid.getCell(Location.getInstance(3, 6)));
		Set<Integer> valuesRemovedSecond = new HashSet<>(Arrays.asList(7, 6));
		assertTrue(!cellsContainPossibleValues(cellsToCheckInSecondRow, valuesRemovedSecond));
	}

	@Test
	public void testNakedPairsNonet() {
		grid.getCell(Location.getInstance(9, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 7)));
		grid.getCell(Location.getInstance(8, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 7)));

		// Run strategy
		solver.nakedPairs(grid);

		List<Cell> cells = grid.getNoNetCells(7);
		List<Cell> cellsToCheck = new ArrayList<>(cells);
		cellsToCheck.remove(grid.getCell(Location.getInstance(9, 1)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(8, 2)));
		Set<Integer> valuesRemoved = new HashSet<>(Arrays.asList(4, 7));
		assertTrue(!cellsContainPossibleValues(cellsToCheck, valuesRemoved));
	}

	@Test
	public void testNakedPairsCols() {
		grid.getCell(Location.getInstance(1, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 9)));
		grid.getCell(Location.getInstance(8, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 9)));

		grid.getCell(Location.getInstance(7, 7)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 9)));
		grid.getCell(Location.getInstance(9, 7)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 9)));

		// Run strategy
		solver.nakedPairs(grid);
		solver.nakedPairs(grid);

		List<Cell> cellsInFirstCol = grid.getCols(7);
		List<Cell> cellsInFirstNonet = grid.getNoNetCells(9);
		List<Cell> cellsToCheckFirstCol = new ArrayList<>(cellsInFirstCol);
		cellsToCheckFirstCol.remove(grid.getCell(Location.getInstance(7, 7)));
		cellsToCheckFirstCol.remove(grid.getCell(Location.getInstance(9, 7)));
		cellsToCheckFirstCol.addAll(cellsInFirstNonet);
		cellsToCheckFirstCol.remove(grid.getCell(Location.getInstance(7, 7)));
		cellsToCheckFirstCol.remove(grid.getCell(Location.getInstance(9, 7)));

		Set<Integer> valuesRemovedFirst = new HashSet<>(Arrays.asList(4, 9));
		assertTrue(!cellsContainPossibleValues(cellsToCheckFirstCol, valuesRemovedFirst));

		List<Cell> cells = grid.getCols(3);
		List<Cell> cellsToCheck = new ArrayList<>(cells);
		cellsToCheck.remove(grid.getCell(Location.getInstance(1, 3)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(8, 3)));
		Set<Integer> valuesRemoved = new HashSet<>(Arrays.asList(4, 9));
		assertTrue(!cellsContainPossibleValues(cellsToCheck, valuesRemoved));
	}

	@Test
	public void testNakedTriplesRow() {

		grid.getCell(Location.getInstance(5, 4)).setPossibleValues(new HashSet<Integer>(Arrays.asList(5, 8, 9)));
		grid.getCell(Location.getInstance(5, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(5, 8)));
		grid.getCell(Location.getInstance(5, 6)).setPossibleValues(new HashSet<Integer>(Arrays.asList(5, 9)));

		// Run strategy
		solver.nakedTriples(grid);

		List<Cell> cells = grid.getRow(5);
		List<Cell> cellsInNonet = grid.getNoNetCells(5);
		List<Cell> cellsToCheck = new ArrayList<>(cells);
		cellsToCheck.remove(grid.getCell(Location.getInstance(5, 4)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(5, 5)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(5, 6)));
		cells.addAll(cellsInNonet);
		cellsToCheck.remove(grid.getCell(Location.getInstance(5, 4)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(5, 5)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(5, 6)));

		Set<Integer> valuesRemoved = new HashSet<>(Arrays.asList(5, 8, 9));
		assertTrue(!cellsContainPossibleValues(cellsToCheck, valuesRemoved));
	}

	@Test
	public void testNakedTriplesCols() {
		grid.getCell(Location.getInstance(4, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 8)));
		grid.getCell(Location.getInstance(5, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 5)));
		grid.getCell(Location.getInstance(6, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 5, 8)));
		grid.getCell(Location.getInstance(4, 9)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 3, 8)));
		grid.getCell(Location.getInstance(5, 9)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 3)));
		grid.getCell(Location.getInstance(6, 9)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 3, 8)));

		// Run strategy
		solver.nakedTriples(grid);
		solver.nakedTriples(grid);

		List<Cell> cells = grid.getCols(1);
		List<Cell> cellsInNonet = grid.getNoNetCells(4);
		Set<Cell> cellsToCheck = new HashSet<>(cells);
		cellsToCheck.addAll(cellsInNonet);
		cellsToCheck.remove(grid.getCell(Location.getInstance(4, 1)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(5, 1)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(6, 1)));

		Set<Integer> valuesRemoved = new HashSet<>(Arrays.asList(1, 5, 8));
		assertTrue(!cellsContainPossibleValues(new ArrayList<Cell>(cellsToCheck), valuesRemoved));

		cells = grid.getCols(9);
		cellsInNonet = grid.getNoNetCells(6);
		cellsToCheck = new HashSet<>(cells);
		cellsToCheck.addAll(cellsInNonet);
		cellsToCheck.remove(grid.getCell(Location.getInstance(4, 9)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(5, 9)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(6, 9)));

		valuesRemoved = new HashSet<>(Arrays.asList(2, 3, 8));
		assertTrue(!cellsContainPossibleValues(new ArrayList<Cell>(cellsToCheck), valuesRemoved));
	}

	@Test
	public void testNakedTriplesNonets() {
		grid.getCell(Location.getInstance(4, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 8)));
		grid.getCell(Location.getInstance(5, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 5)));
		grid.getCell(Location.getInstance(6, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 5, 8)));

		// Run strategy
		solver.nakedTriples(grid);

		List<Cell> cells = grid.getNoNetCells(4);
		Set<Cell> cellsToCheck = new HashSet<>(cells);
		cellsToCheck.remove(grid.getCell(Location.getInstance(4, 2)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(5, 1)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(6, 1)));

		Set<Integer> valuesRemoved = new HashSet<>(Arrays.asList(1, 5, 8));
		assertTrue(!cellsContainPossibleValues(new ArrayList<Cell>(cellsToCheck), valuesRemoved));

	}

	@Test
	public void testNakedQuadsRow() {
		grid.getCell(Location.getInstance(1, 1)).setValueCommitted(1);
		grid.getCell(Location.getInstance(1, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 5, 6)));
		grid.getCell(Location.getInstance(1, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 9)));
		grid.getCell(Location.getInstance(1, 4)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 6)));
		grid.getCell(Location.getInstance(1, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 6, 7)));
		grid.getCell(Location.getInstance(1, 6)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 7)));
		grid.getCell(Location.getInstance(1, 7)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4, 8, 9)));
		grid.getCell(Location.getInstance(1, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4)));
		grid.getCell(Location.getInstance(1, 9)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 8, 9)));

		solver.nakedQuads(grid);

		List<Cell> cells = grid.getRow(1);
		Set<Cell> cellsToCheck = new HashSet<>(cells);

		cellsToCheck.remove(grid.getCell(Location.getInstance(1, 3)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(1, 7)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(1, 8)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(1, 9)));

		Set<Integer> valuesRemoved = new HashSet<>(Arrays.asList(2, 4, 8, 9));
		assertTrue(!cellsContainPossibleValues(new ArrayList<Cell>(cellsToCheck), valuesRemoved));

	}

	@Test
	public void testNakedQuadsCols() {

		grid.getCell(Location.getInstance(1, 1)).setValueCommitted(1);
		grid.getCell(Location.getInstance(2, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 5, 6)));
		grid.getCell(Location.getInstance(3, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 9)));
		grid.getCell(Location.getInstance(4, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 6)));
		grid.getCell(Location.getInstance(5, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 6, 7)));
		grid.getCell(Location.getInstance(6, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 7)));
		grid.getCell(Location.getInstance(7, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4, 8, 9)));
		grid.getCell(Location.getInstance(8, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4)));
		grid.getCell(Location.getInstance(9, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 8, 9)));

		solver.nakedQuads(grid);

		List<Cell> cells = grid.getCols(1);
		Set<Cell> cellsToCheck = new HashSet<>(cells);

		cellsToCheck.remove(grid.getCell(Location.getInstance(3, 1)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(7, 1)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(8, 1)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(9, 1)));

		Set<Integer> valuesRemoved = new HashSet<>(Arrays.asList(2, 4, 8, 9));
		assertTrue(!cellsContainPossibleValues(new ArrayList<Cell>(cellsToCheck), valuesRemoved));

	}

	@Test
	public void testNakedQuadsNonets() {
		grid.getCell(Location.getInstance(1, 1)).setValueCommitted(1);
		grid.getCell(Location.getInstance(2, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 5, 6)));
		grid.getCell(Location.getInstance(3, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 9)));
		grid.getCell(Location.getInstance(1, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 6)));
		grid.getCell(Location.getInstance(2, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 6, 7)));
		grid.getCell(Location.getInstance(3, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 7)));
		grid.getCell(Location.getInstance(1, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4, 8, 9)));
		grid.getCell(Location.getInstance(2, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4)));
		grid.getCell(Location.getInstance(3, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 8, 9)));

		solver.nakedQuads(grid);

		List<Cell> cells = grid.getNoNetCells(1);
		Set<Cell> cellsToCheck = new HashSet<>(cells);

		cellsToCheck.remove(grid.getCell(Location.getInstance(3, 1)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(1, 3)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(2, 3)));
		cellsToCheck.remove(grid.getCell(Location.getInstance(3, 3)));

		Set<Integer> valuesRemoved = new HashSet<>(Arrays.asList(2, 4, 8, 9));
		assertTrue(!cellsContainPossibleValues(new ArrayList<Cell>(cellsToCheck), valuesRemoved));
	}

	@Test
	public void testHiddenPairsCols() {

		grid.getCell(Location.getInstance(1, 5)).setValueCommitted(1);
		grid.getCell(Location.getInstance(2, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 3, 4, 5)));
		grid.getCell(Location.getInstance(3, 5)).setValueCommitted(6);
		grid.getCell(Location.getInstance(4, 5)).setValueCommitted(9);
		grid.getCell(Location.getInstance(5, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 7)));
		grid.getCell(Location.getInstance(6, 5)).setValueCommitted(8);
		grid.getCell(Location.getInstance(7, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 5, 4)));
		grid.getCell(Location.getInstance(8, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 7)));
		grid.getCell(Location.getInstance(9, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 7)));

		// Run strategy
		solver.hiddenPairs(grid);

		Set<Integer> values = new HashSet<>(Arrays.asList(2, 5));
		assertEquals(grid.getCell(Location.getInstance(2, 5)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(7, 5)).getPossibleValues(), values);
	}

	@Test
	public void testHiddenPairsRows() {
		grid.getCell(Location.getInstance(5, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(6, 9)));
		grid.getCell(Location.getInstance(5, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 7, 9)));
		grid.getCell(Location.getInstance(5, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4)));
		grid.getCell(Location.getInstance(5, 4)).setValueCommitted(8);
		grid.getCell(Location.getInstance(5, 5)).setValueCommitted(5);
		grid.getCell(Location.getInstance(5, 6)).setValueCommitted(1);
		grid.getCell(Location.getInstance(5, 7)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 7, 6, 9)));
		grid.getCell(Location.getInstance(5, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 6, 9)));
		grid.getCell(Location.getInstance(5, 9)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 6, 9)));

		// Run strategy
		solver.hiddenPairs(grid);

		Set<Integer> values = new HashSet<>(Arrays.asList(3, 7));
		assertEquals(grid.getCell(Location.getInstance(5, 2)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(5, 7)).getPossibleValues(), values);

	}

	@Test
	public void testHiddenPairNonets() {
		grid.getCell(Location.getInstance(7, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 6, 8)));
		grid.getCell(Location.getInstance(7, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 3, 5, 6)));
		grid.getCell(Location.getInstance(7, 3))
				.setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 2, 3, 5, 6, 9)));
		grid.getCell(Location.getInstance(8, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 5)));
		grid.getCell(Location.getInstance(8, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 3, 4, 5)));
		grid.getCell(Location.getInstance(8, 3))
				.setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 9)));
		grid.getCell(Location.getInstance(9, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 8)));
		grid.getCell(Location.getInstance(9, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4)));
		grid.getCell(Location.getInstance(9, 3)).setValueCommitted(7);

		// Run strategy
		solver.hiddenPairs(grid);

		Set<Integer> values = new HashSet<>(Arrays.asList(1, 9));
		assertEquals(grid.getCell(Location.getInstance(7, 3)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(8, 3)).getPossibleValues(), values);

	}

	@Test
	public void testHiddenTriplesCol() {
		grid.getCell(Location.getInstance(1, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 2, 3, 9)));
		grid.getCell(Location.getInstance(2, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 3, 4, 9)));
		grid.getCell(Location.getInstance(3, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 9)));
		grid.getCell(Location.getInstance(4, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 5, 6, 7)));
		grid.getCell(Location.getInstance(5, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 4, 5, 6, 8)));
		grid.getCell(Location.getInstance(6, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4, 5, 9)));
		grid.getCell(Location.getInstance(7, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 9)));
		grid.getCell(Location.getInstance(8, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 2, 5)));
		grid.getCell(Location.getInstance(9, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 7, 8)));

		// Run strategy
		solver.hiddenTriples(grid);

		Set<Integer> values = new HashSet<>(Arrays.asList(6, 7, 8));
		Set<Integer> cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(4, 8)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(4, 8)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(5, 8)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(5, 8)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(9, 8)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(9, 8)).getPossibleValues(), cellValues);

	}

	@Test
	public void testHiddenTriplesRow() {
		grid.getCell(Location.getInstance(1, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 2, 3, 9)));
		grid.getCell(Location.getInstance(1, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 3, 4, 9)));
		grid.getCell(Location.getInstance(1, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 9)));
		grid.getCell(Location.getInstance(1, 4)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 5, 6, 7)));
		grid.getCell(Location.getInstance(1, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 4, 5, 6, 8)));
		grid.getCell(Location.getInstance(1, 6)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4, 5, 9)));
		grid.getCell(Location.getInstance(1, 7)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 9)));
		grid.getCell(Location.getInstance(1, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 2, 5)));
		grid.getCell(Location.getInstance(1, 9)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 7, 8)));

		// Run strategy
		solver.hiddenTriples(grid);

		Set<Integer> values = new HashSet<>(Arrays.asList(6, 7, 8));
		Set<Integer> cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(1, 4)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(1, 4)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(1, 5)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(1, 5)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(1, 9)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(1, 9)).getPossibleValues(), cellValues);

	}

	@Test
	public void testhiddenTriplesNonet() {

		grid.getCell(Location.getInstance(1, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 2, 3, 9)));
		grid.getCell(Location.getInstance(1, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 3, 4, 9)));
		grid.getCell(Location.getInstance(1, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 9)));
		grid.getCell(Location.getInstance(2, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 5, 6, 7)));
		grid.getCell(Location.getInstance(2, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 4, 5, 6, 8)));
		grid.getCell(Location.getInstance(2, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4, 5, 9)));
		grid.getCell(Location.getInstance(3, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 9)));
		grid.getCell(Location.getInstance(3, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 2, 5)));
		grid.getCell(Location.getInstance(3, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 7, 8)));

		// Run strategy
		solver.hiddenTriples(grid);

		Set<Integer> values = new HashSet<>(Arrays.asList(6, 7, 8));
		Set<Integer> cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(2, 1)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(2, 1)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(2, 2)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(2, 2)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(3, 3)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(3, 3)).getPossibleValues(), cellValues);

	}

	@Test
	public void testHiddenQuadsRow() {
		grid.getCell(Location.getInstance(1, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 9)));
		grid.getCell(Location.getInstance(1, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 8)));
		grid.getCell(Location.getInstance(1, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 6, 8)));
		grid.getCell(Location.getInstance(1, 4)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 9)));
		grid.getCell(Location.getInstance(1, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 7)));
		grid.getCell(Location.getInstance(1, 6)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 7)));
		grid.getCell(Location.getInstance(1, 7)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 6, 7)));
		grid.getCell(Location.getInstance(1, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 5, 6, 7)));
		grid.getCell(Location.getInstance(1, 9)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 6)));

		solver.hiddenQuads(grid);

		Set<Integer> values = new HashSet<>(Arrays.asList(3, 4, 5, 7));
		Set<Integer> cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(1, 5)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(1, 5)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(1, 6)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(1, 6)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(1, 7)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(1, 7)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(1, 8)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(1, 8)).getPossibleValues(), cellValues);
	}

	@Test
	public void testHiddenQuadsCol() {
		grid.getCell(Location.getInstance(1, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 9)));
		grid.getCell(Location.getInstance(2, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 8)));
		grid.getCell(Location.getInstance(3, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 6, 8)));
		grid.getCell(Location.getInstance(4, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 9)));
		grid.getCell(Location.getInstance(5, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 7)));
		grid.getCell(Location.getInstance(6, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 7)));
		grid.getCell(Location.getInstance(7, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 6, 7)));
		grid.getCell(Location.getInstance(8, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 5, 6, 7)));
		grid.getCell(Location.getInstance(9, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 6)));

		solver.hiddenQuads(grid);

		Set<Integer> values = new HashSet<>(Arrays.asList(3, 4, 5, 7));
		Set<Integer> cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(5, 1)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(5, 1)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(6, 1)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(6, 1)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(7, 1)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(7, 1)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(8, 1)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(8, 1)).getPossibleValues(), cellValues);

	}

	@Test
	public void testHiddenQuadsNonet() {

		grid.getCell(Location.getInstance(1, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 9)));
		grid.getCell(Location.getInstance(2, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 8)));
		grid.getCell(Location.getInstance(3, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 6, 8)));
		grid.getCell(Location.getInstance(1, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 9)));
		grid.getCell(Location.getInstance(2, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 7)));
		grid.getCell(Location.getInstance(3, 2)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 7)));
		grid.getCell(Location.getInstance(1, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 5, 6, 7)));
		grid.getCell(Location.getInstance(2, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 5, 6, 7)));
		grid.getCell(Location.getInstance(3, 3)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 6)));

		solver.hiddenQuads(grid);

		Set<Integer> values = new HashSet<>(Arrays.asList(3, 4, 5, 7));
		Set<Integer> cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(2, 2)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(2, 2)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(3, 2)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(3, 2)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(1, 3)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(1, 3)).getPossibleValues(), cellValues);

		cellValues = new HashSet<>(values);
		cellValues.retainAll(grid.getCell(Location.getInstance(2, 3)).getPossibleValues());
		assertEquals(grid.getCell(Location.getInstance(2, 3)).getPossibleValues(), cellValues);

	}

	@Test
	public void testPointingPairsAndTriples() {
		grid.getCell(Location.getInstance(1, 7)).setValueCommitted(6);
		grid.getCell(Location.getInstance(1, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 8)));
		grid.getCell(Location.getInstance(1, 9)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4, 8)));
		grid.getCell(Location.getInstance(2, 7)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 3, 9)));
		grid.getCell(Location.getInstance(2, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 4, 9)));
		grid.getCell(Location.getInstance(2, 9)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 9)));
		grid.getCell(Location.getInstance(3, 7)).setValueCommitted(5);
		grid.getCell(Location.getInstance(3, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 4, 8)));
		grid.getCell(Location.getInstance(3, 9)).setPossibleValues(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 9)));

		solver.pointingPairsAndTriples(grid);

		List<Cell> getCol = grid.getCols(9);
		getCol.remove(grid.getCell(Location.getInstance(1, 9)));
		getCol.remove(grid.getCell(Location.getInstance(2, 9)));
		getCol.remove(grid.getCell(Location.getInstance(3, 9)));

		Set<Integer> valueRemoved = new HashSet<>(Arrays.asList(2));

		assertTrue(!cellsContainPossibleValues(getCol, valueRemoved));
	}

	@Test
	public void testBoxLineReductionRow() {

		grid.getCell(Location.getInstance(1, 4)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4, 5)));
		grid.getCell(Location.getInstance(1, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 4, 5, 9)));
		grid.getCell(Location.getInstance(1, 6)).setValueCommitted(7);
		grid.getCell(Location.getInstance(2, 4)).setValueCommitted(8);
		grid.getCell(Location.getInstance(2, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 3, 4, 5, 6)));
		grid.getCell(Location.getInstance(2, 6)).setPossibleValues(new HashSet<Integer>(Arrays.asList(3, 4, 5, 6)));
		grid.getCell(Location.getInstance(3, 4)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 3, 4, 5)));
		grid.getCell(Location.getInstance(3, 5)).setPossibleValues(new HashSet<Integer>(Arrays.asList(2, 3, 4, 5, 9)));
		grid.getCell(Location.getInstance(3, 6)).setValueCommitted(1);
		grid.getCell(Location.getInstance(1, 1)).setPossibleValues(new HashSet<Integer>(Arrays.asList(4, 5)));
		grid.getCell(Location.getInstance(1, 2)).setValueCommitted(1);
		grid.getCell(Location.getInstance(1, 3)).setValueCommitted(6);
		grid.getCell(Location.getInstance(1, 7)).setValueCommitted(8);

		solver.boxLineReduction(grid);

		List<Cell> nonetCells = grid.getNoNetCells(3);
		nonetCells.remove(grid.getCell(Location.getInstance(1, 8)));
		nonetCells.remove(grid.getCell(Location.getInstance(1, 9)));

		Set<Integer> valueRemoved = new HashSet<>(Arrays.asList(3));

		assertTrue(!cellsContainPossibleValues(nonetCells, valueRemoved));

	}

	@Test
	public void testBoxLineReductionCol() {
		// Nonet
		grid.getCell(Location.getInstance(1, 7)).setValueCommitted(8);
		grid.getCell(Location.getInstance(1, 9)).setValueCommitted(3);
		grid.getCell(Location.getInstance(3, 8)).setValueCommitted(6);
		grid.getCell(Location.getInstance(5, 8)).setValueCommitted(8);
		grid.getCell(Location.getInstance(6, 8)).setValueCommitted(5);
		grid.getCell(Location.getInstance(7, 8)).setValueCommitted(2);
		grid.getCell(Location.getInstance(8, 8)).setValueCommitted(3);
		grid.getCell(Location.getInstance(9, 8)).setValueCommitted(1);
		grid.getCell(Location.getInstance(3, 8)).setValueCommitted(1);
		grid.getCell(Location.getInstance(4, 8)).setPossibleValues(new HashSet<Integer>(Arrays.asList(7, 9)));

		solver.boxLineReduction(grid);

		List<Cell> nonetCells = grid.getNoNetCells(3);
		nonetCells.remove(grid.getCell(Location.getInstance(1, 8)));
		nonetCells.remove(grid.getCell(Location.getInstance(2, 8)));

		Set<Integer> valueRemoved = new HashSet<>(Arrays.asList(4));

		assertTrue(!cellsContainPossibleValues(nonetCells, valueRemoved));

	}

	@Test
	public void testKillerCombosEasy() {

		// Run strategy
		solver.killerCageCombination(grid);

		// Check all combiantions set

		/* Cage starting on R1 C1 */
		Set<Integer> values = new HashSet<>(Arrays.asList(1, 2));
		assertEquals(grid.getCell(Location.getInstance(1, 1)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(1, 2)).getPossibleValues(), values);

		/* Cage starting on R2 C3 */
		values = new HashSet<>(Arrays.asList(8, 9));
		assertEquals(grid.getCell(Location.getInstance(2, 3)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(2, 4)).getPossibleValues(), values);

		/* Cage starting on R1 C7 */
		values = new HashSet<>(Arrays.asList(1, 3));
		assertEquals(grid.getCell(Location.getInstance(1, 7)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(2, 7)).getPossibleValues(), values);

		/* Cage starting on R1 C8 */
		values = new HashSet<>(Arrays.asList(7, 9));
		assertEquals(grid.getCell(Location.getInstance(1, 8)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(2, 8)).getPossibleValues(), values);

		/* Cage starting on R6 C3 */
		values = new HashSet<>(Arrays.asList(1, 2, 3));
		assertEquals(grid.getCell(Location.getInstance(6, 3)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(7, 3)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(7, 2)).getPossibleValues(), values);

		/* Cage starting on R8 C3 */
		values = new HashSet<>(Arrays.asList(7, 9));
		assertEquals(grid.getCell(Location.getInstance(8, 3)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(9, 3)).getPossibleValues(), values);

		/* Cage starting on R7 C5 */
		values = new HashSet<>(Arrays.asList(1, 2, 3, 4));
		assertEquals(grid.getCell(Location.getInstance(7, 5)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(8, 5)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(8, 4)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(9, 4)).getPossibleValues(), values);

		/* Cage starting on R9 C8 */
		values = new HashSet<>(Arrays.asList(8, 9));
		assertEquals(grid.getCell(Location.getInstance(9, 8)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(9, 9)).getPossibleValues(), values);

	}

	@Test
	public void testKillerCagesHarder() {
		// Test on default grid
		solver.killerCageSumsHarder(grid);

		Set<Integer> values = new HashSet<>(Arrays.asList(6, 7, 8, 9));
		assertEquals(grid.getCell(Location.getInstance(8, 6)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(8, 7)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(7, 9));
		assertEquals(grid.getCell(Location.getInstance(1, 8)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(2, 8)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(8, 9));
		assertEquals(grid.getCell(Location.getInstance(9, 8)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(9, 9)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 5, 3, 4));
		assertEquals(grid.getCell(Location.getInstance(4, 6)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(5, 6)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(3, 6)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6));
		assertEquals(grid.getCell(Location.getInstance(4, 4)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(3, 3)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(3, 4)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6));
		assertEquals(grid.getCell(Location.getInstance(4, 4)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(3, 3)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(3, 4)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(3, 4, 5, 6, 7, 8, 9));
		assertEquals(grid.getCell(Location.getInstance(7, 6)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(7, 7)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(6, 6)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(3, 4, 5, 6, 7, 8, 9));
		assertEquals(grid.getCell(Location.getInstance(7, 1)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(8, 1)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(6, 1)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(9, 1)).getPossibleValues(), values);

		/* Tested a bunch of potntials */
	}

	@Test
	public void testInniesAndOuties() {
		Set<Cage> cagesOnBoard = ReadCages.readCagesFromString("testing.txt");
		grid = new KillerSudokuGrid(cagesOnBoard);
		solver.changeGridForSolving(grid);

		int count = 8;
		while (count != 0) {
			solver.solveInniesAndOuties(grid);
			count--;
		}

		assertEquals(grid.getCell(Location.getInstance(1, 8)).getValueCommitted(), 6);
		assertEquals(grid.getCell(Location.getInstance(3, 6)).getValueCommitted(), 1);
		assertEquals(grid.getCell(Location.getInstance(3, 7)).getValueCommitted(), 3);
		assertEquals(grid.getCell(Location.getInstance(4, 4)).getValueCommitted(), 5);

		assertEquals(grid.getCell(Location.getInstance(7, 3)).getValueCommitted(), 5);
		assertEquals(grid.getCell(Location.getInstance(7, 4)).getValueCommitted(), 7);
		assertEquals(grid.getCell(Location.getInstance(9, 2)).getValueCommitted(), 9);

		// Using committed cells
		assertEquals(grid.getCell(Location.getInstance(6, 6)).getValueCommitted(), 4);
	}

	@Test
	public void testInniesAndOutiesTwo() {
		Set<Cage> cagesOnBoard = ReadCages.readCagesFromString("testing.txt");
		grid = new KillerSudokuGrid(cagesOnBoard);
		solver.changeGridForSolving(grid);

		int count = 8;
		while (count != 0) {
			solver.solveInniesAndOutiesTwoCells(grid);
			count--;
		}

		// Check cells in scenario
		Set<Integer> values = new HashSet<>(Arrays.asList(1, 2, 4, 5));
		assertEquals(grid.getCell(Location.getInstance(2, 1)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(2, 2)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(6, 7, 8, 9));
		assertEquals(grid.getCell(Location.getInstance(8, 9)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(8, 8)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(7, 9));
		assertEquals(grid.getCell(Location.getInstance(1, 7)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(2, 7)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
		assertEquals(grid.getCell(Location.getInstance(8, 3)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(9, 3)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
		assertEquals(grid.getCell(Location.getInstance(8, 3)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(9, 3)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(6, 7, 8, 9));
		assertEquals(grid.getCell(Location.getInstance(1, 8)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(9, 2)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
		assertEquals(grid.getCell(Location.getInstance(4, 4)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(6, 6)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
		assertEquals(grid.getCell(Location.getInstance(1, 3)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(2, 3)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6));
		assertEquals(grid.getCell(Location.getInstance(8, 7)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(9, 7)).getPossibleValues(), values);

		cagesOnBoard = ReadCages.readCagesFromString("testing2.txt");
		grid = new KillerSudokuGrid(cagesOnBoard);
		solver.changeGridForSolving(grid);

		count = 6;
		while (count != 0) {
			solver.solveInniesAndOutiesTwoCells(grid);
			count--;
		}

		values = new HashSet<>(Arrays.asList(1, 2, 3, 5, 6, 7));
		assertEquals(grid.getCell(Location.getInstance(4, 5)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(4, 9)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6));
		assertEquals(grid.getCell(Location.getInstance(8, 3)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(8, 9)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6));
		assertEquals(grid.getCell(Location.getInstance(1, 4)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(1, 5)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4, 6, 7, 8, 9));
		assertEquals(grid.getCell(Location.getInstance(5, 7)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(6, 7)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4));
		assertEquals(grid.getCell(Location.getInstance(6, 1)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(6, 2)).getPossibleValues(), values);

		values = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
		assertEquals(grid.getCell(Location.getInstance(8, 6)).getPossibleValues(), values);
		assertEquals(grid.getCell(Location.getInstance(9, 6)).getPossibleValues(), values);

	}

	/* AUXILIARY FUNCTIONSgrid */
	public boolean cellsContainPossibleValues(List<Cell> cells, Set<Integer> vals) {
		for (Cell currentCell : cells) {
			for (int value : vals) {
				if (currentCell.getPossibleValues().contains(value)) {
					return true;
				}
			}
		}
		return false;
	}

}
