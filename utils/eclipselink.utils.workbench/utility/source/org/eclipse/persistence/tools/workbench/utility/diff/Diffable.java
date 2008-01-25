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
 * An interface implemented by objects that can be "diffed"
 * directly, as opposed to using a differentiator.
 */
public interface Diffable {

	/**
	 * Return the "diff" between this object and the specified object.
	 */
	Diff diff(Object o);

	/**
	 * Return the "key diff" between this object and the specified object.
	 */
	Diff keyDiff(Object o);

}
