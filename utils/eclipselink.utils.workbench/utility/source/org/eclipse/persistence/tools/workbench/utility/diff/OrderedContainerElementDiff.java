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
package org.eclipse.persistence.tools.workbench.utility.diff;

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;

/**
 * Wrap a list element diff and associate it with an index.
 */
public class OrderedContainerElementDiff extends DiffWrapper {
	private final int index;


	public OrderedContainerElementDiff(int index, Diff diff, Differentiator differentiator) {
		super(diff, differentiator);
		this.index = index;
	}

	/**
	 * @see Diff#appendDescription(org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter)
	 */
	public void appendDescription(IndentingPrintWriter pw) {
		if (this.different()) {
			pw.print("The values at index ");
			pw.print(this.index);
			pw.print(" are different");
			pw.println();
			pw.indent();
				super.appendDescription(pw);
			pw.undent();
		}
	}

	public int getIndex() {
		return this.index;
	}

	public boolean elementWasAdded() {
		return this.getObject1() == OrderedContainerDifferentiator.UNDEFINED_ELEMENT;
	}

	public boolean elementWasRemoved() {
		return this.getObject2() == OrderedContainerDifferentiator.UNDEFINED_ELEMENT;
	}

	public boolean elementWasModified() {
		return this.different() &&
			! this.elementWasAdded() &&
			! this.elementWasRemoved();
	}

}
