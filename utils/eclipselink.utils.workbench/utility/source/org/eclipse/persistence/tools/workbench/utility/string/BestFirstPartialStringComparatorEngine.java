/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.string;

/**
 * This partial string comparator engine performs a "best-first" matching of
 * the strings in the two sets of string holders. Each string in the first set is
 * compared with all the strings in the second set. The best match in the
 * second set is removed from the set and used to create a pair with the
 * current element of the first set.
 * This results in (n^2 + n)/2 comparisons, as opposed to the n^2 comparisons
 * used by the ExhaustivePartialStringComparatorEngine.
 */
public class BestFirstPartialStringComparatorEngine
	implements PartialStringComparatorEngine
{
	private final PartialStringComparator partialStringComparator;

	public BestFirstPartialStringComparatorEngine(PartialStringComparator partialStringComparator) {
		super();
		this.partialStringComparator = partialStringComparator;
	}

	public StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
		int len1 = stringHolders1.length;
		int len2 = stringHolders2.length;
		// create a copy of the second array that will be cleared as we
		// match the elements up with elements from the first array
		StringHolder[] stringHolders2Copy = new StringHolder[len2];
		System.arraycopy(stringHolders2, 0, stringHolders2Copy, 0, len2);
		int minLen = Math.min(len1, len2);
		int maxLen = Math.max(len1, len2);

		StringHolderPair[] pairs = new StringHolderPair[maxLen];
		for (int i = 0; i < minLen; i++) {
			StringHolder stringHolder1 = stringHolders1[i];
			String string1 = stringHolder1.getString();
			double bestScore = -1.0;
			StringHolder best = null;
			int bestIndex = -1;
			for (int j = 0; j < len2; j++) {
				StringHolder stringHolder2 = stringHolders2Copy[j];
				if (stringHolder2 != null) {
					double score = this.partialStringComparator.compare(string1, stringHolder2.getString());
					if (score > bestScore) {
						bestScore = score;
						best = stringHolder2;
						bestIndex = j;
					}
				}
			}
			pairs[i] = new SimpleStringHolderPair(stringHolder1, best, bestScore);
			stringHolders2Copy[bestIndex] = null;
		}

		if (maxLen > len1) {
			for (int i = minLen; i < maxLen; i++) {
				for (int j = 0; j < len2; j++) {
					StringHolder stringHolder2 = stringHolders2Copy[j];
					if (stringHolder2 != null) {
						pairs[i] = new SimpleStringHolderPair(null, stringHolder2, 0.0);
						stringHolders2Copy[j] = null;
					}
				}
			}
		} else if (maxLen > len2) {
			for (int i = minLen; i < maxLen; i++) {
				pairs[i] = new SimpleStringHolderPair(stringHolders1[i], null, 0.0);
			}
		}

		return pairs;
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.partialStringComparator);
	}

}
