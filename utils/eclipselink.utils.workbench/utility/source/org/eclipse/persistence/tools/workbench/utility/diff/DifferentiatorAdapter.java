/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.diff;

/**
 * Pluggable adapter that allows the same code to be used for both "normal"
 * and "key" diffs.
 */
public interface DifferentiatorAdapter {

	/**
	 * Adapt the specified differentiator to "diff" the specified objects.
	 */
	Diff diff(Differentiator differentiator, Object object1, Object object2);


	DifferentiatorAdapter NORMAL = new DifferentiatorAdapter() {
		public Diff diff(Differentiator differentiator, Object object1, Object object2) {
			return differentiator.diff(object1, object2);
		}
	};

	DifferentiatorAdapter KEY = new DifferentiatorAdapter() {
		public Diff diff(Differentiator differentiator, Object object1, Object object2) {
			return differentiator.keyDiff(object1, object2);
		}
	};

}
