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

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;

/**
 * A diff for when there is no difference between the two objects.
 */
public class NullDiff implements Diff {
	private final Object object1;
	private final Object object2;
	private final Differentiator differentiator;


	public NullDiff(Object object1, Object object2, Differentiator differentiator) {
		super();
		this.object1 = object1;
		this.object2 = object2;
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
		return true;
	}

	/**
	 * @see Diff#different()
	 */
	public boolean different() {
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
		return "";
	}

	/**
	 * @see Diff#appendDescription(org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter)
	 */
	public void appendDescription(IndentingPrintWriter pw) {
		// nothing to describe
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return NO_DIFFERENCE_DESCRIPTION;
	}

}
