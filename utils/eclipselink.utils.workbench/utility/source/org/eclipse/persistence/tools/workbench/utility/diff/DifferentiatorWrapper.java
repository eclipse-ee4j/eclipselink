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

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * Wrap another differentiator.
 */
public class DifferentiatorWrapper implements Differentiator {
	protected final Differentiator differentiator;

	/**
	 * Wrap the specified a differentiator.
	 */
	public DifferentiatorWrapper(Differentiator differentiator) {
		super();
		this.differentiator = differentiator;
	}

	/**
	 * @see Differentiator#diff(Object, Object)
	 */
	public Diff diff(Object object1, Object object2) {
		return this.differentiator.diff(object1, object2);
	}

	/**
	 * @see Differentiator#keyDiff(Object, Object)
	 */
	public Diff keyDiff(Object object1, Object object2) {
		return this.differentiator.keyDiff(object1, object2);
	}

	/**
	 * @see Differentiator#comparesValueObjects()
	 */
	public boolean comparesValueObjects() {
		return this.differentiator.comparesValueObjects();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.differentiator);
	}

}
