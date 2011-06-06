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
 * Pluggable adapter that allows the same code to be used for both "normal"
 * and "key" diffs.
 */
public interface DiffableAdapter {

	/**
	 * Adapt the specified diffable to "diff" the specified object.
	 */
	Diff diff(Diffable object1, Object object2);


	DiffableAdapter NORMAL = new DiffableAdapter() {
		public Diff diff(Diffable object1, Object object2) {
			return object1.diff(object2);
		}
	};

	DiffableAdapter KEY = new DiffableAdapter() {
		public Diff diff(Diffable object1, Object object2) {
			return object1.keyDiff(object2);
		}
	};

}
