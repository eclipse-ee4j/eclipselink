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
 * Straightforward implementation of the PartialStringMatcher interface.
 */
public class SimplePartialStringMatcher
	implements PartialStringMatcher
{
	private final PartialStringComparator partialStringComparator;

	public SimplePartialStringMatcher(PartialStringComparator partialStringComparator) {
		super();
		this.partialStringComparator = partialStringComparator;
	}

	/**
	 * @see PartialStringMatcher#match(String, StringHolder[])
	 */
	public StringHolderScore match(String string, StringHolder[] stringHolders) {
		double bestScore = -1.0;
		StringHolder best = null;
		int len = stringHolders.length;
		for (int i = 0; i < len; i++) {
			StringHolder stringHolder = stringHolders[i];
			double score = this.partialStringComparator.compare(string, stringHolder.getString());
			if (score > bestScore) {
				bestScore = score;
				best = stringHolder;
			}
		}
		return new SimpleStringHolderScore(best, bestScore);
	}

}
