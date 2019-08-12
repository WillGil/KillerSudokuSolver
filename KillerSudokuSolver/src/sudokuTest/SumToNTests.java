package sudokuTest;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import sudokuModel.SumToN;

public class SumToNTests {

	@Test
	public void testAllSubsetsOfSet() {
		Set<Integer> testValues = new HashSet<>(Arrays.asList(1, 2));

		Set<Set<Integer>> sets = SumToN.getSubsets(testValues);

		Set<Integer> valueChecking = new HashSet<>(Arrays.asList(1));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(2));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 2));
		assertTrue(sets.contains(valueChecking));

		// More complex example
		testValues = new HashSet<>(Arrays.asList(1, 2, 3));

		sets = SumToN.getSubsets(testValues);

		valueChecking = new HashSet<>(Arrays.asList(1));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(2));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 2));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(3));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 3));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(2, 3));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 2, 3));
		assertTrue(sets.contains(valueChecking));

		// Even more complex
		testValues = new HashSet<>(Arrays.asList(1, 2, 3, 4));

		sets = SumToN.getSubsets(testValues);

		valueChecking = new HashSet<>(Arrays.asList(1));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(2));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 2));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(3));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 3));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(4));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 4));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(2, 3));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 2, 3));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(2, 4));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 2, 4));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(3, 4));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 3, 4));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(2, 3, 4));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 2, 3, 4));
		assertTrue(sets.contains(valueChecking));
	}

	@Test
	public void testSubsetsOfSize() {
		Set<Integer> testValues = new HashSet<>(Arrays.asList(1, 2));

		Set<Set<Integer>> sets = SumToN.getSubsetsOfSize(1, testValues);

		Set<Integer> valueChecking = new HashSet<>(Arrays.asList(1));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(2));
		assertTrue(sets.contains(valueChecking));

		// More complex example
		testValues = new HashSet<>(Arrays.asList(1, 2, 4, 5));

		sets = SumToN.getSubsetsOfSize(3, testValues);

		valueChecking = new HashSet<>(Arrays.asList(1, 2, 4));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 2, 5));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 4, 5));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(2, 4, 5));
		assertTrue(sets.contains(valueChecking));

		// More advanced
		// More complex example
		testValues = new HashSet<>(Arrays.asList(1, 2, 4, 5, 8));

		sets = SumToN.getSubsetsOfSize(2, testValues);
		
		valueChecking = new HashSet<>(Arrays.asList(1, 2));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 4));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 5));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(2, 4));
		assertTrue(sets.contains(valueChecking));
		
		valueChecking = new HashSet<>(Arrays.asList(2, 5));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(1, 8));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(4, 5));
		assertTrue(sets.contains(valueChecking));
		
		valueChecking = new HashSet<>(Arrays.asList(2, 8));
		assertTrue(sets.contains(valueChecking));

		valueChecking = new HashSet<>(Arrays.asList(4, 8));
		assertTrue(sets.contains(valueChecking));
		
		valueChecking = new HashSet<>(Arrays.asList(5, 8));
		assertTrue(sets.contains(valueChecking));
	}
}
