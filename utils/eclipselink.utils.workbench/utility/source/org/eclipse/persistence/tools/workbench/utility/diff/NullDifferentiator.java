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

/**
 * The two objects are always "identical".
 * 
 * All of the behavior for this class is parameterized,
 * allowing us to use a singleton.
 */
public class NullDifferentiator implements Differentiator {

	// singleton
	private static NullDifferentiator INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized Differentiator instance() {
		if (INSTANCE == null) {
			INSTANCE = new NullDifferentiator();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private NullDifferentiator() {
		super();
	}

	/**
	 * @see Differentiator#diff(Object, Object)
	 */
	public Diff diff(Object object1, Object object2) {
		return new NullDiff(object1, object2, this);
	}

	/**
	 * @see Differentiator#keyDiff(Object, Object)
	 */
	public Diff keyDiff(Object object1, Object object2) {
		return this.diff(object1, object2);
	}

	/**
	 * @see Differentiator#comparesValueObjects()
	 */
	public boolean comparesValueObjects() {
		return false;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "NullDifferentiator";
	}

}
