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

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This diff records the differences between two ordered containers,
 * where matching elements must occupy the same positions in their
 * respective containers.
 */
public class OrderedContainerDiff extends CompositeDiff {
	private final Class javaClass;


	public OrderedContainerDiff(Class javaClass, Object object1, Object object2, Diff[] diffs, Differentiator differentiator) {
		super(object1, object2, diffs, differentiator);
		this.javaClass = javaClass;
	}

	/**
	 * @see Diff#appendDescription(org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter)
	 */
	public void appendDescription(IndentingPrintWriter pw) {
		if (this.different()) {
			pw.print("The ");
			pw.print(ClassTools.shortNameFor(this.javaClass));
			pw.print("s are different:");
			pw.println();
		}
		super.appendDescription(pw);
	}

}
