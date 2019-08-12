package sudokuModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SumToN {

	public static List<SumCombination> SumUpTo(int target, int length, Set<Integer> possibleValues) {
		List<SumCombination> combinations = new ArrayList<>();

		List<Integer> possibleValuesList = new ArrayList<>(possibleValues);

		List<Integer> partial = new ArrayList<>();

		SumUpToRecursive(possibleValuesList, target, partial, combinations);

		for (Iterator<SumCombination> itr = combinations.iterator(); itr.hasNext();) {
			SumCombination combination = itr.next();
			if (!(combination.getValuesThatSum().size() == length)) {
				itr.remove();
			}
		}

		return combinations;
	}

	private static void SumUpToRecursive(List<Integer> possibleValues, int target, List<Integer> partial,
			List<SumCombination> combinations) {

		int sum = 0;

		for (int partialVal : partial)
			sum += partialVal;

		if (sum == target)
			combinations.add(new SumCombination(target, new TreeSet<Integer>(partial)));

		if (sum >= target)
			return;

		for (int i = 0; i < possibleValues.size(); i++) {
			List<Integer> remaining = new ArrayList<Integer>();

			int currentNumber = possibleValues.get(i);
			for (int j = i + 1; j < possibleValues.size(); j++)
				remaining.add(possibleValues.get(j));

			List<Integer> partialList = new ArrayList<Integer>(partial);

			partialList.add(currentNumber);
			SumUpToRecursive(remaining, target, partialList, combinations);

		}

	}

	/**
	 * Powerset
	 *
	 * @param setGiven
	 * @return
	 */

	public static Set<Set<Integer>> getSubsetsOfSize(int size, Set<Integer> setGiven) {
		Set<Set<Integer>> combos = getSubsets(setGiven);
		for (Iterator<Set<Integer>> itr = combos.iterator(); itr.hasNext();) {
			Set<Integer> combination = itr.next();
			if (combination.size() != size)
				itr.remove();
		}
		return combos;
	}

	public static Set<Set<Integer>> getSubsets(Set<Integer> setGiven) {
		Set<Set<Integer>> combinations = allSubsetsOfSet(setGiven);

		for (Iterator<Set<Integer>> itr = combinations.iterator(); itr.hasNext();) {
			Set<Integer> combination = itr.next();
			if (combination.isEmpty())
				itr.remove();
		}
		return combinations;

	}

	private static Set<Set<Integer>> allSubsetsOfSet(Set<Integer> setGiven) {
		Set<Set<Integer>> sets = new HashSet<Set<Integer>>();

		if (setGiven.isEmpty()) {
			sets.add(new HashSet<Integer>());
			return sets;
		}

		List<Integer> list = new ArrayList<Integer>(setGiven);
		Integer head = list.get(0);
		Set<Integer> rest = new HashSet<Integer>(list.subList(1, list.size()));

		for (Set<Integer> set : allSubsetsOfSet(rest)) {
			Set<Integer> newSet = new HashSet<Integer>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}

		return sets;

	}

}
