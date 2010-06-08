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
	PartialStringComparator DEFAULT_COMPARATOR = PFPartialStringComparator.instance();
 
}
