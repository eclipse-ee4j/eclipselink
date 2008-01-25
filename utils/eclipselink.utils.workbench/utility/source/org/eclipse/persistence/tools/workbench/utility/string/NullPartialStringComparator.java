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
 * This implementation of PartialStringComparator will always return zero when
 * comparing two strings.
 */
public final class NullPartialStringComparator
	implements PartialStringComparator
{

	// singleton
	private static NullPartialStringComparator INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized PartialStringComparator instance() {
		if (INSTANCE == null) {
			INSTANCE = new NullPartialStringComparator();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private NullPartialStringComparator() {
		super();
	}

	/**
	 * @see PartialStringComparator#compare(String, String)
	 */
	public double compare(String s1, String s2) {
		return 0;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}

}
