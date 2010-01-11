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

import java.util.Comparator;

/**
 * This simple interface defines a protocol for matching a string with another
 * string from a set of string holders.
 */
public interface PartialStringMatcher {

	/**
	 * Return the string holder score from the specified set of string holders
	 * that most closely matches the specified string.
	 */
	StringHolderScore match(String string, StringHolder[] stringHolders);


	/**
	 * A string holder score pairs a string holder with the score assigned it
	 * by the partial string comparator.
	 */
	public interface StringHolderScore extends Comparable {

		/**
		 * Return the string holder taken from the set of string holders.
		 */
		StringHolder getStringHolder();

		/**
		 * Return the score assigned the string holder by the matcher's partial
		 * string comparator.
		 */
		double getScore();

		/**
		 * Provide an implementation of Comparator that can be easily used
		 * by StringHolderPair implementations to implement Comparable.
		 */
		Comparator DEFAULT_COMPARATOR =
			new Comparator() {
				public int compare(Object o1, Object o2) {
					double score1 = ((StringHolderScore) o1).getScore();
					double score2 = ((StringHolderScore) o2).getScore();
					return (score1 < score2) ? -1 : ((score1 == score2) ? 0 : 1);
				}
			};

	}

	/**
	 * Straightforward implementation of the StringHolderScore interface.
	 */
	public class SimpleStringHolderScore implements StringHolderScore {
		private final StringHolder stringHolder;
		private final double score;
	
		public SimpleStringHolderScore(StringHolder stringHolder, double score) {
			super();
			this.stringHolder = stringHolder;
			this.score = score;
		}

		public StringHolder getStringHolder() {
			return this.stringHolder;
		}

		public double getScore() {
			return this.score;
		}

		public int compareTo(Object o) {
			return DEFAULT_COMPARATOR.compare(this, o);
		}

		public String toString() {
			return StringTools.buildToStringFor(this, "\"" + this.stringHolder.getString() + "\" : " + this.score);
		}

	}

}
