/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.diff;

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;

/**
 * This diff records the differences between two objects compared reflectively.
 */
public class ReflectiveDiff extends CompositeDiff {
	private final Class javaClass;

	public ReflectiveDiff(Class javaClass, Object object1, Object object2, ReflectiveFieldDiff[] diffs, Differentiator differentiator) {
		super(object1, object2, diffs, differentiator);
		this.javaClass = javaClass;
	}

	/**
	 * @see Diff#appendDescription(org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter)
	 */
	public void appendDescription(IndentingPrintWriter pw) {
		if (this.different()) {
			pw.print("Reflective comparison (");
			pw.print(this.javaClass.getName());
			pw.print(") - ");
			this.appendHeader(pw);
		}
		super.appendDescription(pw);
	}

}
