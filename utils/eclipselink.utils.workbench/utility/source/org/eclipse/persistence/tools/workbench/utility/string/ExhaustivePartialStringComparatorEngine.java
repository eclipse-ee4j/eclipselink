/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 * This partial string comparator engine performs a brute-force matching of
 * the strings in the two sets of string holders. Every string in the first set is
 * compared with every string in the second set and the returned matches
 * are those with the highest scores. This engine may not provide timely
 * performance with large sets of strings.
 */
public class ExhaustivePartialStringComparatorEngine
	implements PartialStringComparatorEngine
{
	private final PartialStringComparator partialStringComparator;

	public ExhaustivePartialStringComparatorEngine(PartialStringComparator partialStringComparator) {
		super();
		this.partialStringComparator = partialStringComparator;
	}

	/**
	 * @see PartialStringComparatorEngine#match(StringHolder[], StringHolder[])
	 */
	public StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
		InternalStringHolderPair[] stringHolderPairs = this.buildStringHolderPairs(stringHolders1, stringHolders2);
		this.calculateScores(stringHolderPairs, stringHolders2);
		this.setString2(stringHolderPairs, stringHolders2);
		return stringHolderPairs;
	}

	/**
	 * Build enough string holder pairs to hold all the string holders in the specified sets
	 * and initialize them with the string holders from the first set.
	 */
	private InternalStringHolderPair[] buildStringHolderPairs(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
		int len1 = stringHolders1.length;
		int len2 = stringHolders2.length;

		// build up the string pairs
		int len = Math.max(len1, len2);
		InternalStringHolderPair[] stringHolderPairs = new InternalStringHolderPair[len];
		for (int i = 0; i < len1; i++) {
			stringHolderPairs[i] = new SimpleInternalStringHolderPair(stringHolders1[i], len2);
		}
		if (len1 < len2) {
			for (int i = len1; i < len2; i++) {
				stringHolderPairs[i] = new NullInternalStringHolderPair();
			}
		}

		return stringHolderPairs;
	}

	/**
	 * Loop through the partially-populated string holder pairs (at this point they
	 * hold only string holders from the first set of string holders) and calculate the
	 * scores for matching the pair's string from the first set of string holders with
	 * *every* string in the second set.
	 */
	private void calculateScores(InternalStringHolderPair[] stringHolderPairs, StringHolder[] stringHolders2) {
		PartialStringComparator psc = this.partialStringComparator;
		for (int i = stringHolderPairs.length; i-- > 0; ) {
			stringHolderPairs[i].calculateScores(psc, stringHolders2);
		}
	}

	/**
	 * Loop through the still partially-populated string holder pairs and determine
	 * which string holder from the first set of string holders each string holder
	 * from the second set should be assigned to.
	 */
	private void setString2(InternalStringHolderPair[] stringHolderPairs, StringHolder[] stringHolders2) {
		// this copy will be cleared out as we match all the string pairs
		int stringHolderPairsLength = stringHolderPairs.length;
		InternalStringHolderPair[] stringHolderPairsCopy = new InternalStringHolderPair[stringHolderPairsLength];
		System.arraycopy(stringHolderPairs, 0, stringHolderPairsCopy, 0, stringHolderPairsLength);

		// this copy will also be cleared out as we match all the string holder pairs
		int stringHolders2Length = stringHolders2.length;
		StringHolder[] stringHolders2Copy = new StringHolder[stringHolders2Length];
		System.arraycopy(stringHolders2, 0, stringHolders2Copy, 0, stringHolders2Length);

		for (int x = stringHolders2Length; x-- > 0; ) {
			double maxScore = -2;
			int maxIndex = -1;
			for (int i = stringHolderPairsLength; i-- > 0; ) {
				InternalStringHolderPair stringHolderPair = stringHolderPairsCopy[i];
				if (stringHolderPair == null) {
					continue;	// skip to next slot
				}
				double stringHolderPairScore = stringHolderPair.maxScore();
				if (stringHolderPairScore > maxScore) {
					maxScore = stringHolderPairScore;
					maxIndex = i;
				}
			}
			int stringHolder2Index = stringHolderPairsCopy[maxIndex].setStringHolder2(stringHolders2Copy);
			stringHolders2Copy[stringHolder2Index] = null;
			stringHolderPairsCopy[maxIndex] = null;

			// tell the remaining string holder pairs that one of the stringHolder2 entries has been assigned
			for (int i = stringHolderPairsLength; i-- > 0; ) {
				InternalStringHolderPair stringPair = stringHolderPairsCopy[i];
				if (stringPair == null) {
					continue;	// skip to next slot
				}
				stringPair.clearStringHolder2(stringHolder2Index);
			}
		}

		// the remaining string holder pairs do not get a stringHolder2
		for (int x = stringHolderPairsLength; x-- > stringHolders2Length; ) {
			for (int i = stringHolderPairsLength; i-- > 0; ) {
				InternalStringHolderPair stringHolderPair = stringHolderPairsCopy[i];
				if (stringHolderPair == null) {
					continue;	// skip to next slot
				}
				stringHolderPair.setStringHolder2(null);
			}
		}
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.partialStringComparator);
	}


	// ********** member classes **********

	/**
	 * Extend the StringHolderPair interface with some behavior we
	 * can delegate to the pairs themselves.
	 */
	private interface InternalStringHolderPair extends StringHolderPair {

		/**
		 * Use the specified comparator to calculate the scores for
		 * the pair's assigned string holder 1 with every possible
		 * string holder 2.
		 */
		void calculateScores(PartialStringComparator psc, StringHolder[] stringHolders2);

		/**
		 * Return the highest remaining score among those
		 * calculated earlier.
		 */
		double maxScore();

		/**
		 * The engine has determined that the pair can now be assigned
		 * a string holder 2 from the specified set of remaining string holders.
		 */
		int setStringHolder2(StringHolder[] stringHolders2);

		/**
		 * The specified string holder 2 has been assigned to some other pair,
		 * clear it and its score from the pair's state.
		 */
		void clearStringHolder2(int index);

	}


	/**
	 * This string holder pair does not have a string holder 1. It is used when
	 * there are more string holders in the second set of string holders
	 * than in the first.
	 */
	private static class NullInternalStringHolderPair implements InternalStringHolderPair {
		private StringHolder stringHolder2;

		public StringHolder getStringHolder1() {
			return null;
		}

		public StringHolder getStringHolder2() {
			return this.stringHolder2;
		}

		public double getScore() {
			// return the lowest possible score
			return 0.0;
		}

		public void calculateScores(PartialStringComparator psc, StringHolder[] stringHolders2) {
			// do nothing
		}

		public double maxScore() {
			// return something less than the lowest possible score
			return -1;
		}

		public int setStringHolder2(StringHolder[] stringHolders2) {
			// all the remaining strings didn't match with any string;
			// so just take the first one
			for (int i = stringHolders2.length; i-- > 0; ) {
				if (stringHolders2[i] != null) {
					this.stringHolder2 = stringHolders2[i];
					return i;
				}
			}
			throw new IllegalStateException("'stringHolders2' is empty");
		}

		public void clearStringHolder2(int index) {
			// do nothing
		}

		public int compareTo(Object o) {
			return DEFAULT_COMPARATOR.compare(this, o);
		}

		public String toString() {
			StringBuffer sb = new StringBuffer(100);
			StringTools.buildSimpleToStringOn(this, sb);
			sb.append(" (");
			sb.append("<null> vs. ");
			if (this.stringHolder2 == null) {
				sb.append("<null>");
			} else {
				sb.append('"');
				sb.append(this.stringHolder2.getString());
				sb.append('"');
			}
			sb.append(" => 0");
			sb.append(')');
			return sb.toString();
		}

	}


	/**
	 * Hold the pair of string holders and their score. Also hold some transient state
	 * that is used by the engine while pairing up string holders.
	 */
	private static class SimpleInternalStringHolderPair implements InternalStringHolderPair {
		private final StringHolder stringHolder1;
		private StringHolder stringHolder2;
		private double score;
		private double[] scores;		// transient
		private double maxScore;		// transient
		private int maxScoreIndex;		// transient

		SimpleInternalStringHolderPair(StringHolder stringHolder1, int stringHolders2Size) {
			super();
			this.stringHolder1 = stringHolder1;
			this.score = -1;
			this.scores = new double[stringHolders2Size];
			this.maxScore = -1;
			this.maxScoreIndex = -1;
		}

		public StringHolder getStringHolder1() {
			return this.stringHolder1;
		}

		public StringHolder getStringHolder2() {
			return this.stringHolder2;
		}

		public double getScore() {
			return this.score;
		}

		public void calculateScores(PartialStringComparator psc, StringHolder[] stringHolders2) {
			String localString1 = this.stringHolder1.getString();
			double[] localScores = this.scores;
			double localMaxScore = this.maxScore;
			int localMaxScoreIndex = this.maxScoreIndex;

			for (int i = stringHolders2.length; i-- > 0; ) {
				double localScore = psc.compare(localString1, stringHolders2[i].getString());
				localScores[i] = localScore;
				if (localScore > localMaxScore) {
					localMaxScore = localScore;
					localMaxScoreIndex = i;
				}
			}

			this.maxScore = localMaxScore;
			this.maxScoreIndex = localMaxScoreIndex;
		}

		public double maxScore() {
			if (this.maxScore == -1) {
				this.recalculateMaxScoreAndIndex();
			}
			return this.maxScore;
		}

		private void recalculateMaxScoreAndIndex() {
			double[] localScores = this.scores;
			double localMaxScore = this.maxScore;
			int localMaxScoreIndex = this.maxScoreIndex;

			for (int i = localScores.length; i-- > 0; ) {
				double localScore = localScores[i];
				if (localScore > localMaxScore) {
					localMaxScore = localScore;
					localMaxScoreIndex = i;
				}
			}

			this.maxScore = localMaxScore;
			this.maxScoreIndex = localMaxScoreIndex;
		}

		public int setStringHolder2(StringHolder[] stringHolders2) {
			int index = this.maxScoreIndex;
			if (stringHolders2 == null) {
				this.stringHolder2 = null;
				this.score = 0.0;
			} else {
				this.stringHolder2 = stringHolders2[index];
				this.score = this.scores[index];
			}
			this.scores = null;
			this.maxScore = 0;
			this.maxScoreIndex = -1;
			return index;
		}

		public void clearStringHolder2(int index) {
			this.scores[index] = -1;
			this.maxScore = -1;
			this.maxScoreIndex = -1;
		}

		public int compareTo(Object o) {
			return DEFAULT_COMPARATOR.compare(this, o);
		}

		public String toString() {
			return StringTools.buildToStringFor(this, this.additionalInfo());
		}

		private String additionalInfo() {
			StringBuffer sb = new StringBuffer(200);
			sb.append("\"");
			sb.append(this.stringHolder1.getString());
			sb.append("\" vs. ");
			if (this.stringHolder2 == null) {
				sb.append("<null>");
			} else {
				sb.append('"');
				sb.append(this.stringHolder2.getString());
				sb.append('"');
			}
			sb.append(" => ");
			sb.append(this.score);
			return sb.toString();
		}

	}

}
