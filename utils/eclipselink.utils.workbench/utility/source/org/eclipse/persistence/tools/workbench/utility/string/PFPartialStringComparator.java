package org.eclipse.persistence.tools.workbench.utility.string;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * This implementation of the PartialStringComparator interface uses the
 * the weighted sum of percentage of matched characters from each string.
 * 
 * @author lddavis
 *
 */
public class PFPartialStringComparator implements PartialStringComparator {

	// singleton
	private static PFPartialStringComparator INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized PartialStringComparator instance() {
		if (INSTANCE == null) {
			INSTANCE = new PFPartialStringComparator();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private PFPartialStringComparator() {
		super();
	}

	public double compare(String s1, String s2) {
		return StringTools.calculateHighestMatchWeight(s1, s2);
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}

}
