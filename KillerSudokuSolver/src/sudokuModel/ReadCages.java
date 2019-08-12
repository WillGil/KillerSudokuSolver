package sudokuModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * A class which is sued to read in the cages from a text file.
 * 
 * @author William Gilgunn
 * @Version 2.0
 */
public class ReadCages {
	private static final int SIZE = 9;
	private static String filePath;

	/**
	 * Method that takes a file name parameter and creates a list of cages to be
	 * used on the board
	 * 
	 * @param fileName
	 *            the file the cages are stored in
	 * @return a list of cages on the board.
	 */
	public static Set<Cage> readCagesFromString(String fileName) {
		int cellTotal = 0;
		int cageTotal = 0;
		
		List<Cage> cagesList = new ArrayList<>();
		/* Try with resources block */
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line;
			/* While the file has lines remaining within it */
			while ((line = reader.readLine()) != null) {
				String[] split = line.split(" "); // Split based on spaces
				int elements = Integer.parseInt(split[0]);
				int sumValue = Integer.parseInt(split[1]);

				int cageCells = elements;
				Set<Location> cellsLocation = new HashSet<Location>();
				/* Run for the number of cells in cage */
				while ((line = reader.readLine()) != null) {

					split = line.split(" ");
					int verticalCoord = Integer.parseInt(split[0]);
					int horizontalCoord = Integer.parseInt(split[1]);

					cellsLocation.add(Location.getInstance(verticalCoord, horizontalCoord));

					cageCells--;
					/*
					 * When all cells in cage have been added break out of inner
					 * for loop
					 */
					cellTotal++;
					if (cageCells == 0) {
						cagesList.add(new Cage(sumValue, cellsLocation));
						cageTotal += sumValue;
						break;
					}
				}
			}
		} catch (IOException io) {
			System.err.format("Exception occurred trying to read '%s'.", fileName);
			io.printStackTrace();

		}
		/* Ensure valid board */
		if (cageTotal != 405 || cellTotal != 81)
			return null;

		Set<Cage> cageSet = new HashSet<Cage>(cagesList);
		return cageSet;

	}

	public static Set<Cage> readCagesFromFile(File cageFile) {
		int cellTotal = 0;
		int cageTotal = 0;
		filePath = cageFile.getAbsolutePath();
		Set<Cage> cageList = new HashSet<>();
		try (Scanner fileScanner = new Scanner(cageFile)) {

			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine();
				if (line.equals("----------")) // Used to existing boards.
					break;

				String[] split = line.split(" "); // Split based on spaces
				int elements = Integer.parseInt(split[0]);
				int sumValue = Integer.parseInt(split[1]);

				int cageCells = elements;

				Set<Location> cellsLocation = new HashSet<Location>();

				while (fileScanner.hasNextLine()) {
					String lineLocation = fileScanner.nextLine();
					split = lineLocation.split(" ");

					int verticalCoord = Integer.parseInt(split[0]);
					int horizontalCoord = Integer.parseInt(split[1]);

					cellsLocation.add(Location.getInstance(verticalCoord, horizontalCoord));
					cellTotal++;
					cageCells--;
					if (cageCells == 0) {
						System.out.println("Cage hint:" + sumValue + " " + cellsLocation);
						cageList.add(new Cage(sumValue, cellsLocation));
						cageTotal += sumValue;
						System.out.println(sumValue);
						break;
					}
				}

			}
		} catch (IOException io) {
			System.err.format("Exception occurred trying to read '%s'.", cageFile.toPath());
			io.printStackTrace();
		}

		System.out.println(cageTotal);
		System.out.println(cellTotal);

		if (cageTotal != 405 || cellTotal != 81)
			return null;

		Set<Cage> cageSet = new HashSet<Cage>(cageList);
		return cageSet;
	}

	public static String getFilePath() {
		return filePath;
	}

	public static Pair<Set<Cage>, Cell[][]> readCellsIn(File File) {
		int cellTotal = 0;
		int cageTotal = 0;

		SudokuGrid grid = null;
		Cell[][] newCells = new Cell[SIZE][SIZE];
		filePath = File.getAbsolutePath();
		Set<Cage> cageList = new HashSet<>();
		try (Scanner fileScanner = new Scanner(File)) {

			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine();
				if (line.equals("----------"))
					break;

				String[] split = line.split(" "); // Split based on spaces
				int elements = Integer.parseInt(split[0]);
				int sumValue = Integer.parseInt(split[1]);

				int cageCells = elements;

				Set<Location> cellsLocation = new HashSet<Location>();

				while (fileScanner.hasNextLine()) {
					String lineLocation = fileScanner.nextLine();
					split = lineLocation.split(" ");

					int verticalCoord = Integer.parseInt(split[0]);
					int horizontalCoord = Integer.parseInt(split[1]);

					cellsLocation.add(Location.getInstance(verticalCoord, horizontalCoord));
					cellTotal++;
					cageCells--;
					if (cageCells == 0) {
						cageList.add(new Cage(sumValue, cellsLocation));
						cageTotal += sumValue;
						break;
					}
				}
			}

			/*
			 * Cell changes
			 */

			if (fileScanner.hasNextLine()) {

				while (fileScanner.hasNextLine()) {

					String line = fileScanner.nextLine();

					String[] split = line.split(" "); // Split based on spaces
					int row = Integer.parseInt(split[0]);
					int col = Integer.parseInt(split[1]);

					Cell currentCell = new Cell(Location.getInstance(row, col));

					newCells[row - 1][col - 1] = currentCell;

					String committed = split[2];

					if (committed.equals("y")) {
						line = fileScanner.nextLine();
						int value = Integer.parseInt(line);
						currentCell.setValueCommitted(value);
					} else {
						if (committed.equals("n")) {
							line = fileScanner.nextLine();
							line = line.replaceAll("\\[", "").replaceAll("\\]", "");

							String[] posVal = line.split(", ");
							Set<Integer> valuesInSet = new TreeSet<>();

							for (String valuesCell : posVal) {
								int valuesInt = Integer.parseInt(valuesCell);
								valuesInSet.add(valuesInt);
							}

							Set<Integer> valuesNotInSet = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
							valuesNotInSet.removeAll(valuesInSet);
							for (int removeVal : valuesNotInSet) {
								currentCell.removePossibleValues(removeVal);
							}
						}
					}

				}
			}

		} catch (FileNotFoundException e) {
			System.err.format("Exception occurred trying to read '%s'.", File.toPath());
			e.printStackTrace();
		}

		if (cageTotal != 405 || cellTotal != 81)
			return null;

		Set<Cage> cageSet = new HashSet<Cage>(cageList);

		return new Pair<Set<Cage>, Cell[][]>(cageSet, newCells);
	}

}
