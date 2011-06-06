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
 * This differentiator delegates to the actual objects being compared.
 * The two objects are "identical" if either
 *     they are both null
 * or
 *     object 1 says they are
 * 
 * All of the behavior for this class is paremeterized,
 * allowing us to use a singleton.
 */
public class DiffableDifferentiator implements Differentiator {

	// singleton
	private static DiffableDifferentiator INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized Differentiator instance() {
		if (INSTANCE == null) {
			INSTANCE = new DiffableDifferentiator();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private DiffableDifferentiator() {
		super();
	}

	/**
	 * @see Differentiator#diff(Object, Object)
	 */
	public Diff diff(Object object1, Object object2) {
		return this.diff(object1, object2, DiffableAdapter.NORMAL);
	}

	/**
	 * @see Differentiator#keyDiff(Object, Object)
	 */
	public Diff keyDiff(Object object1, Object object2) {
		return this.diff(object1, object2, DiffableAdapter.KEY);
	}

	private String fatalDescriptionTitle() {
		return "The two objects cannot be compared";
	}

	private Diff diff(Object object1, Object object2, DiffableAdapter adapter) {
		if (object1 == object2) {
			return new NullDiff(object1, object2, this);
		}
		if ((object1 == null) || (object2 == null)) {
			return new SimpleDiff(object1, object2, this.fatalDescriptionTitle(), this);
		}
		return adapter.diff((Diffable) object1, object2);
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
		return "DiffableDifferentiator";
	}
	
}
