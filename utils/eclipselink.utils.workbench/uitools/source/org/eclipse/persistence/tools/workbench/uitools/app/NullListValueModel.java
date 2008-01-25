/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.uitools.app;

import org.eclipse.persistence.tools.workbench.utility.iterators.NullListIterator;

/**
 * A read-only list value model for when you don't
 * need to support a list.
 */
public final class NullListValueModel
	extends AbstractReadOnlyListValueModel
{

	private static final long serialVersionUID = 1L;

	// singleton
	private static NullListValueModel INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized ListValueModel instance() {
		if (INSTANCE == null) {
			INSTANCE = new NullListValueModel();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private NullListValueModel() {
		super();
	}


	// ********** ListValueModel implementation **********

	/**
	 * @see ListValueModel#size()
	 */
	public int size() {
		return 0;
	}

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		return NullListIterator.instance();
	}


	// ********** Object overrides **********

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "NullListValueModel";
	}

	/**
	 * Serializable singleton support
	 */
	private Object readResolve() {
		return instance();
	}

}
