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
package org.eclipse.persistence.tools.workbench.utility.diff;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This diff records the differences between two unordered collections,
 * noting, in addition to which elements are different, which elements
 * were removed and added.
 */
public class ContainerDiff extends CompositeDiff {
	private final Class javaClass;
	private final Object[] removedElements;
	private final Object[] addedElements;


	public ContainerDiff(Class javaClass, Object object1, Object object2, Diff[] diffs, Object[] removedElements, Object[] addedElements, Differentiator differentiator) {
		super(object1, object2, diffs, differentiator);
		this.javaClass = javaClass;
		this.removedElements = removedElements;
		this.addedElements = addedElements;
	}

	/**
	 * @see Diff#different()
	 */
	public boolean different() {
		if (this.removedElements.length != 0 ) {
			return true;
		}
		if (this.addedElements.length != 0 ) {
			return true;
		}
		return super.different();
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

			this.appendDiffs(pw);
			this.appendRemovedElements(pw);
			this.appendAddedElements(pw);
		}
	}

	private void appendDiffs(IndentingPrintWriter pw) {
		Diff[] diffs = this.getDiffs();
		boolean headerPrinted = false;
		// preserve the order of the component diffs
		int len = diffs.length;
		for (int i = 0; i < len; i++) {
			Diff diff = diffs[i];
			if (diff.different()) {
				if ( ! headerPrinted) {
					pw.println("The following pairs of elements have matching \"keys\" but are different:");
					pw.indent();
					headerPrinted = true;
				}
				diff.appendDescription(pw);
			}
		}
		if (headerPrinted) {
			pw.undent();
		}
	}

	private void appendRemovedElements(IndentingPrintWriter pw) {
		int len = this.removedElements.length;
		if (len == 0) {
			return;
		}
		pw.println("The following elements were removed (they existed in container 1, but not in container 2):");
		pw.indent();
			for (int i = 0; i < len; i++) {
				pw.println(this.removedElements[i]);
			}
		pw.undent();
	}

	private void appendAddedElements(IndentingPrintWriter pw) {
		int len = this.addedElements.length;
		if (len == 0) {
			return;
		}
		pw.println("The following elements were added (they existed in container 2, but not in container 1):");
		pw.indent();
			for (int i = 0; i < len; i++) {
				pw.println(this.addedElements[i]);
			}
		pw.undent();
	}

	/**
	 * return the elements found in container 2 but not container 1
	 */
	public Object[] getAddedElements() {
		return this.addedElements;
	}

	/**
	 * return the elements found in container 1 but not container 2
	 */
	public Object[] getRemovedElements() {
		return this.removedElements;
	}

}
