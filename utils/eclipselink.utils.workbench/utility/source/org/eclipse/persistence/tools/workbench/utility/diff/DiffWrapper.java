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

import java.io.StringWriter;

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * Wrap another diff.
 */
public abstract class DiffWrapper implements Diff {
	protected final Diff diff;
	protected final Differentiator differentiator;


	public DiffWrapper(Diff diff, Differentiator differentiator) {
		super();
		this.diff = diff;
		this.differentiator = differentiator;
	}

	/**
	 * @see Diff#getObject1()
	 */
	public Object getObject1() {
		return this.diff.getObject1();
	}

	/**
	 * @see Diff#getObject2()
	 */
	public Object getObject2() {
		return this.diff.getObject2();
	}

	/**
	 * @see Diff#identical()
	 */
	public boolean identical() {
		return this.diff.identical();
	}

	/**
	 * @see Diff#different()
	 */
	public boolean different() {
		return this.diff.different();
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
		if (this.diff.identical()) {
			return "";
		}
		StringWriter sw = new StringWriter();
		IndentingPrintWriter pw = new IndentingPrintWriter(sw);
		this.diff.appendDescription(pw);
		return sw.toString();
	}

	/**
	 * @see Diff#appendDescription(org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter)
	 */
	public void appendDescription(IndentingPrintWriter pw) {
		this.diff.appendDescription(pw);
	}

	public Diff getDiff() {
		return this.diff;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return (this.identical()) ? NO_DIFFERENCE_DESCRIPTION : this.getDescription();
	}

}
