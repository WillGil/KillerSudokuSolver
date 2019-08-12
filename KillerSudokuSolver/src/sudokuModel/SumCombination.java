package sudokuModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SumCombination {

	private int totalValue;
	private Set<Integer> valuesThatSum;

	public SumCombination(int totalValue, Set<Integer> valuesThatSum) {
		super();
		this.totalValue = totalValue;
		this.valuesThatSum = valuesThatSum;
	}

	public int getTotalValue() {
		return totalValue;
	}

	public Set<Integer> getValuesThatSum() {
		return valuesThatSum;
	}

	public boolean containsValue(int checkValue) {
		if (valuesThatSum.contains(checkValue))
			return true;
		return false;
	}

	public boolean containsOneValue() {
		if (valuesThatSum.size() == 1)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + totalValue;
		result = prime * result + ((valuesThatSum == null) ? 0 : valuesThatSum.hashCode());
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
		SumCombination other = (SumCombination) obj;
		if (totalValue != other.totalValue)
			return false;
		if (valuesThatSum == null) {
			if (other.valuesThatSum != null)
				return false;
		} else if (!valuesThatSum.equals(other.valuesThatSum))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		List<Integer> values = new ArrayList<Integer>(valuesThatSum);

		for (int x = 0; x < values.size(); x++) {
			if (x == values.size() - 1){
				sb.append(values.get(x));
				continue;
			}

			sb.append(values.get(x) + "/");
		}
		return sb.toString();
	}

}
