package sudokuModel;

import java.util.HashSet;
import java.util.Set;

/**
 * A class that represents a cage on the killer sudoku board.
 * 
 * @author William Gilgunn
 * @version 2.0
 *
 */
public class Cage {
	private Set<Location> locations, solvedLocation, unsolvedLocation;
	private boolean committed, uniqueValuesAssigned;
	private int totalValue, remainingValue;
	public static int numberOfCages = 0;

	public Cage(int totalValue) {
		numberOfCages++;
		this.totalValue = totalValue;
		remainingValue = totalValue;
		locations = new HashSet<Location>();
		solvedLocation = new HashSet<>();
		unsolvedLocation = new HashSet<>();
		committed = false;
		uniqueValuesAssigned = false;
	}

	/**
	 * Constructor that takes a int total and a list of cells and constructs a
	 * cage object
	 * 
	 * @param totalValue
	 * @param cageLocation
	 */
	public Cage(int totalValue, Set<Location> cageLocation) {
		numberOfCages++;
		this.totalValue = totalValue;
		remainingValue = totalValue;
		locations = cageLocation;
		solvedLocation = new HashSet<>(); // No cells solved
		unsolvedLocation = new HashSet<>(locations); // All cells unsolved
		committed = false;
		uniqueValuesAssigned = false;
	}

	public static void resetCages() {
		numberOfCages = 0;
	}

	/**
	 * Returns the cells of the cage.
	 * 
	 * @return the cells of the cage.
	 */
	public Set<Location> getLocation() {
		return locations;
	}

	/**
	 * Returns the solved cells of the cage.
	 * 
	 * @return the solved cells of the cage.
	 */
	public Set<Location> getSolvedLocations() {
		return solvedLocation;
	}

	/**
	 * Returns the unsolved cells of the cage.
	 * 
	 * @return the unsolved cells of the cage
	 */
	public Set<Location> getUnsolvedLocations() {
		return unsolvedLocation;
	}

	/**
	 * Returns if the cage has been committed.
	 * 
	 * @return if the cage has been committed.
	 */
	public boolean isCommitted() {
		return committed;
	}

	/**
	 * if the cage is all committed then set committed .
	 */
	public void setCommitted() {
		if (locations.equals(solvedLocation))
			committed = true;
	}

	/**
	 * Returns the total value of the cage
	 * 
	 * @return the total value of the cage
	 */
	public int getTotalValue() {
		return totalValue;
	}

	/**
	 * Gets the remaining value needed to commit the cage.
	 * 
	 * @return remaining value needed to commit the cage.
	 */
	public int getRemainingValue() {
		return remainingValue;
	}

	/**
	 * Updates the set of solved cells by checking if the cells are committed.
	 * 
	 * @param solvedCell
	 *            the cell..
	 */
	public void setSolvedCells(Location location) {
		unsolvedLocation.remove(location);
		solvedLocation.add(location);
	}

	/**
	 * Add a cell to the cage
	 * 
	 * @param cell
	 *            cell to be added to the cage.
	 */
	public void addCell(Location location) {
		unsolvedLocation.add(location);
		locations.add(location);
	}

	/**
	 * Method to remove a cell from a cage.
	 * 
	 * @param cell
	 *            remove a cell from a cage
	 */
	public void removeLocation(Location location) {
		locations.remove(location);
		if (unsolvedLocation.contains(location))
			unsolvedLocation.remove(location);

		if (solvedLocation.contains(location))
			solvedLocation.remove(location);

	}

	public boolean cageIsFilled() {
		return solvedLocation.size() == locations.size();
	}

	/**
	 * Work out the new value the cage needs to be committed
	 * 
	 */
	public void decreaseRemainingSum(int value) {
		remainingValue -= value;
	}

	/**
	 * The size of the cage
	 * 
	 * @return the size of the cage
	 */
	public int getSize() {
		return locations.size();
	}

	public boolean isUniqueValuesAssigned() {
		return uniqueValuesAssigned;
	}

	public void setUniqueValuesAssigned() {
		if (!uniqueValuesAssigned) {
			uniqueValuesAssigned = true;
		}
	}

	/**
	 * Overrding toString so Cage can be printed.
	 */
	@Override
	public String toString() {
		return "Total :" + totalValue + " -> " + locations;
	}
	
	public String unsolvedToString(){
		StringBuilder result = new StringBuilder();
		for(Location loc: unsolvedLocation){
			result.append(loc+ " ");
			
		}
		return result.toString();
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locations == null) ? 0 : locations.hashCode());
		result = prime * result + totalValue;
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
		Cage other = (Cage) obj;
		if (locations == null) {
			if (other.locations != null)
				return false;
		} else if (!locations.equals(other.locations))
			return false;
		if (totalValue != other.totalValue)
			return false;
		return true;
	}

}
