/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.string;

/**
 * This simple interface defines a protocol for comparing two strings.
 */
public interface PartialStringComparator {

	/**
	 * Return a value between 0.0 and 1.0, inclusive, indicating the
	 * similarity of the two specified strings, where the greater the
	 * value the better the match.
	 */
	double compare(String s1, String s2);


	/**
	 * Probably the most useful of the partial string comparators.
	 */
//	PartialStringComparator OLD = new OldPartialStringComparator(OldPartialStringComparator.instance());
 
	PartialStringComparator EMPTY_COMPARATOR = new EmptyStringComparator();
}
