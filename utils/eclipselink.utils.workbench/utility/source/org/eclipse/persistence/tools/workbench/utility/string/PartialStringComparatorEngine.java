/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Comparator;

/**
 * This interface defines a protocol for matching up strings from
 * two sets of string holders.
 */
public interface PartialStringComparatorEngine {

	/**
	 * Return a set of string holder pairs that reflect the best-matched pairs of
	 * strings from the specified sets of string holders. If either of the sets is
	 * larger than the other, the strings that are the worst-matched will be 
	 * paired with nulls.
	 */
	StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2);


	/**
	 * A string holder pair holds a pair of string holders that have been
	 * matched up by a partial string comparator engine. It also holds
	 * the score assigned to the pair by the engine.
	 */
	public interface StringHolderPair extends Comparable {

		/**
		 * Return the string holder taken from the first set of string holders.
		 */
		StringHolder getStringHolder1();

		/**
		 * Return the string holder taken from the second set of string holders.
		 */
		StringHolder getStringHolder2();

		/**
		 * Return the score that indicates the quality of the match
		 * of the pair's strings. The score is between 0.0 and 1.0,
		 * inclusive, where a higher score indicates a better match.
		 */
		double getScore();

		/**
		 * Provide an implementation of Comparator that can be easily used
		 * by StringHolderPair implementations to implement Comparable.
		 */
		Comparator DEFAULT_COMPARATOR =
			new Comparator() {
				public int compare(Object o1, Object o2) {
					double score1 = ((StringHolderPair) o1).getScore();
					double score2 = ((StringHolderPair) o2).getScore();
					return (score1 < score2) ? -1 : ((score1 == score2) ? 0 : 1);
				}
			};

	}

	/**
	 * Straightforward implementation of the StringHolderPair interface.
	 */
	public class SimpleStringHolderPair implements StringHolderPair {
		private final StringHolder stringHolder1;
		private final StringHolder stringHolder2;
		private final double score;

		public SimpleStringHolderPair(StringHolder stringHolder1, StringHolder stringHolder2, double score) {
			super();
			this.stringHolder1 = stringHolder1;
			this.stringHolder2 = stringHolder2;
			this.score = score;
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

		public int compareTo(Object o) {
			return DEFAULT_COMPARATOR.compare(this, o);
		}

		public String toString() {
			StringBuffer sb = new StringBuffer(100);
			StringTools.buildSimpleToStringOn(this, sb);
			sb.append(" (");
			this.appendStringHolder(this.stringHolder1, sb);
			sb.append(" vs. ");
			this.appendStringHolder(this.stringHolder2, sb);
			sb.append(" => ");
			sb.append(this.score);
			sb.append(')');
			return sb.toString();
		}

		private void appendStringHolder(StringHolder stringHolder, StringBuffer sb) {
			if (stringHolder == null) {
				sb.append("<null>");
			} else {
				sb.append('"');
				sb.append(stringHolder.getString());
				sb.append('"');
			}
		}

	}

}
