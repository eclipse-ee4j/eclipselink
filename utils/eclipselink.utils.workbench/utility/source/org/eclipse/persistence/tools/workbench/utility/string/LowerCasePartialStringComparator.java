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
 * This implementation of PartialStringComparator will convert the strings to
 * lower case before using the nested comparator to compare them.
 */
public class LowerCasePartialStringComparator
	implements PartialStringComparator
{
	private final PartialStringComparator partialStringComparator;

	public LowerCasePartialStringComparator(PartialStringComparator partialStringComparator) {
		super();
		this.partialStringComparator = partialStringComparator;
	}

	/**
	 * @see PartialStringComparator#compare(String, String)
	 */
	public double compare(String s1, String s2) {
		return this.partialStringComparator.compare(s1.toLowerCase(), s2.toLowerCase());
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.partialStringComparator);
	}

}
