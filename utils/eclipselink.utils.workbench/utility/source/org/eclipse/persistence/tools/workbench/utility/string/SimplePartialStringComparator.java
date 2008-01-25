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

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * This implementation of PartialStringComparator will simply compare
 * the specified strings, returning 1 if they are equal, 0 if they are not.
 */
public final class SimplePartialStringComparator
	implements PartialStringComparator
{

	// singleton
	private static SimplePartialStringComparator INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized PartialStringComparator instance() {
		if (INSTANCE == null) {
			INSTANCE = new SimplePartialStringComparator();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private SimplePartialStringComparator() {
		super();
	}

	/**
	 * @see PartialStringComparator#compare(String, String)
	 */
	public double compare(String s1, String s2) {
		return s1.equals(s2) ? 1 : 0;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}

}
