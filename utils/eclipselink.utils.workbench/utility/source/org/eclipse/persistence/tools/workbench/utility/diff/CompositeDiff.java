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

import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * Allow a collection of diffs to be treated as a single diff.
 */
public class CompositeDiff implements Diff {
	/** the compared objects */
	private final Object object1;
	private final Object object2;
	
	/** the component diffs */
	private final Diff[] diffs;

	/** the differentiator the performed the comparison */
	private final Differentiator differentiator;


	public CompositeDiff(Object object1, Object object2, Diff[] diffs, Differentiator differentiator) {
		super();
		this.object1 = object1;
		this.object2 = object2;
		this.diffs = diffs;
		this.differentiator = differentiator;
	}

	/**
	 * @see Diff#getObject1()
	 */
	public Object getObject1() {
		return this.object1;
	}

	/**
	 * @see Diff#getObject2()
	 */
	public Object getObject2() {
		return this.object2;
	}

	/**
	 * @see Diff#identical()
	 */
	public boolean identical() {
		return ! this.different();
	}

	/**
	 * @see Diff#different()
	 */
	public boolean different() {
		// if any component diff is different, the entire diff is different
		for (int i = this.diffs.length; i-- > 0; ) {
			if (this.diffs[i].different()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see Diff#getDifferentiator()
	 */
	public Differentiator getDifferentiator() {
		return this.differentiator;
	}

	/**
	 * @see Diff#getDescription()
	 */
	public String getDescription() {
		if (this.identical()) {
			return "";
		}
		Writer sw = new StringWriter();
		IndentingPrintWriter pw = new IndentingPrintWriter(sw, "    ");
		this.appendHeader(pw);
		pw.indent();
			this.appendDescription(pw);
		pw.undent();
		return sw.toString();
	}

	protected void appendHeader(IndentingPrintWriter pw) {
		pw.print("Objects are different:");
		pw.println();
		pw.print("object 1: ");
		pw.print(this.object1);
		pw.println();
		pw.print("object 2: ");
		pw.print(this.object2);
		pw.println();
	}

	/**
	 * @see Diff#appendDescription(org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter)
	 */
	public void appendDescription(IndentingPrintWriter pw) {
		// preserve the order of the component diffs
		int len = this.diffs.length;
		for (int i = 0; i < len; i++) {
			this.diffs[i].appendDescription(pw);
		}
	}

	/**
	 * return the composite's component diffs
	 */
	public Diff[] getDiffs() {
		return this.diffs;
	}

	/**
	 * return the specified component diff
	 */
	public Diff getDiff(int index) {
		return this.diffs[index];
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return (this.identical()) ? NO_DIFFERENCE_DESCRIPTION : this.getDescription();
	}

}
