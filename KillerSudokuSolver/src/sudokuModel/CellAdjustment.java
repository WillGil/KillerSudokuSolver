package sudokuModel;

import java.util.HashSet;
import java.util.Set;

public class CellAdjustment {

	private boolean cellCausingChange;
	private Set<Integer> valuesRemoved;
	private Set<Integer> valuesCausing;

	public CellAdjustment(Set<Integer> valuesRemoved, Set<Integer> valuesCausing) {

		super();
		if (valuesCausing.isEmpty())
			cellCausingChange = false;
		else
			cellCausingChange = true;

		Set<Integer> checkCells = new HashSet<>(valuesRemoved);
		checkCells.retainAll(valuesCausing);
		
		
		
		if(!checkCells.isEmpty()){
			throw new IllegalArgumentException("Values Removed and causing values cannot intersect.");
		}
		
		this.valuesRemoved = valuesRemoved;
		this.valuesCausing = valuesCausing;
	}

	public boolean isCellCausingChange(){
		return cellCausingChange;
	}

	public Set<Integer> getValuesRemoved() {
		return valuesRemoved;
	}

	public Set<Integer> getValuesCausing() {
		return valuesCausing;
	}
	

}
