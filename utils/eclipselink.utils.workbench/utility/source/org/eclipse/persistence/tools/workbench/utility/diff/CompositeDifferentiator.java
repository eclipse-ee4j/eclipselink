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

import java.util.Collection;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Delegate behavior to the component differentiators,
 * and gather up the results in a composite diff.
 */
public class CompositeDifferentiator implements Differentiator {
	/** the component differentiators */
	private final Differentiator[] differentiators;

	/** calculated from the component differentiators */
	private final boolean comparesValueObjects;


	public CompositeDifferentiator(Collection differentiators) {
		this((Differentiator[]) differentiators.toArray(new Differentiator[differentiators.size()]));
	}

	public CompositeDifferentiator(Differentiator[] differentiators) {
		super();
		this.differentiators = differentiators;
		this.comparesValueObjects = this.buildcomparesValueObjects();
	}

	private boolean buildcomparesValueObjects() {
		int len = this.differentiators.length;
		if (len == 0) {
			return false;		// the default is 'false'
		}
		// make sure all the components match
		boolean result = this.differentiators[len - 1].comparesValueObjects();
		for (int i =len - 1; i-- > 0; ) {
			if (this.differentiators[i].comparesValueObjects() != result) {
				throw new IllegalStateException("all the component differentiators must match on Differentiator.comparesValueObjects()");
			}
		}
		return result;
	}

	/**
	 * @see Differentiator#diff(Object, Object)
	 */
	public Diff diff(Object object1, Object object2) {
		return this.diff(object1, object2, DifferentiatorAdapter.NORMAL);
	}

	/**
	 * @see Differentiator#keyDiff(Object, Object)
	 */
	public Diff keyDiff(Object object1, Object object2) {
		return this.diff(object1, object2, DifferentiatorAdapter.KEY);
	}

	private Diff diff(Object object1, Object object2, DifferentiatorAdapter adapter) {
		int len = this.differentiators.length;
		Diff[] diffs = new Diff[len];
		for (int i = 0; i < len; i++) {
			diffs[i] = adapter.diff(this.differentiators[i], object1, object2);
		}
		return new CompositeDiff(object1, object2, diffs, this);
	}

	/**
	 * @see Differentiator#comparesValueObjects()
	 */
	public boolean comparesValueObjects() {
		return this.comparesValueObjects;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this);
	}

}
