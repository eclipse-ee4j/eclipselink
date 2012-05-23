/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
 * The two objects are "identical" only if they are both the
 * same object.
 * 
 * All of the behavior for this class is parameterized,
 * allowing us to use a singleton.
 */
public class IdentityDifferentiator implements Differentiator {

	// singleton
	private static IdentityDifferentiator INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized Differentiator instance() {
		if (INSTANCE == null) {
			INSTANCE = new IdentityDifferentiator();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private IdentityDifferentiator() {
		super();
	}
	
	/**
	 * @see Differentiator#diff(Object, Object)
	 */
	public Diff diff(Object object1, Object object2) {
		if (object1 == object2) {
			return new NullDiff(object1, object2, this);
		}
		return new SimpleDiff(object1, object2, this.descriptionTitle(), this);
	}

	private String descriptionTitle() {
		return "Objects are not the exact same object";
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
		return "IdentityDifferentiator";
	}
	
}
