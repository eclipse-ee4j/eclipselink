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
 * The two objects are "identical" if either
 *     they are both null
 * or
 *     they are "equal" to each other, as determined by object 1
 * 
 * All of the behavior for this class is parameterized,
 * allowing us to use a singleton.
 */
public class EqualityDifferentiator implements Differentiator {

	// singleton
	private static EqualityDifferentiator INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized Differentiator instance() {
		if (INSTANCE == null) {
			INSTANCE = new EqualityDifferentiator();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private EqualityDifferentiator() {
		super();
	}
	
	/**
	 * @see Differentiator#diff(Object, Object)
	 */
	public Diff diff(Object object1, Object object2) {
		if (this.objectsAreIdentical(object1, object2)) {
			return new NullDiff(object1, object2, this);
		}
		return new SimpleDiff(object1, object2, this.descriptionTitle(), this);
	}

	private boolean objectsAreIdentical(Object object1, Object object2) {
		if (object1 == object2) {
			return true;
		}
		if ((object1 == null) || (object2 == null)) {
			return false;
		}
		return object1.equals(object2);
	}

	private String descriptionTitle() {
		return "Objects are not equal";
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
		return true;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "EqualityDifferentiator";
	}
	
}
