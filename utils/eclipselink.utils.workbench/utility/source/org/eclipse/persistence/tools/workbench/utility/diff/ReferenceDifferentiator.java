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

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * Redirect calls to #diff(Object, Object) to #keyDiff(Object, Object).
 * Disallow calls to #keyDiff(Object, Object).
 */
public class ReferenceDifferentiator implements Differentiator {
	private final Differentiator differentiator;

	public ReferenceDifferentiator(Differentiator differentiator) {
		super();
		this.differentiator = differentiator;
	}

	/**
	 * @see Differentiator#diff(Object, Object)
	 */
	public Diff diff(Object object1, Object object2) {
		return this.differentiator.keyDiff(object1, object2);
	}

	/**
	 * @see Differentiator#keyDiff(Object, Object)
	 */
	public Diff keyDiff(Object object1, Object object2) {
		throw new UnsupportedOperationException();
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
		return StringTools.buildToStringFor(this, this.differentiator);
	}

}
