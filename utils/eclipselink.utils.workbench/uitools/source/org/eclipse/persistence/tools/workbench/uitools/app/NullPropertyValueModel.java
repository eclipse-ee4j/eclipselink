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

/**
 * A read-only property value model for when you
 * don't need to support a value.
 */
public final class NullPropertyValueModel
	extends AbstractReadOnlyPropertyValueModel
{

	private static final long serialVersionUID = 1L;

	// singleton
	private static NullPropertyValueModel INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized PropertyValueModel instance() {
		if (INSTANCE == null) {
			INSTANCE = new NullPropertyValueModel();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private NullPropertyValueModel() {
		super();
	}
	

	// ********** PropertyValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		return null;
	}


	// ********** Object overrides **********

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "NullPropertyValueModel";
	}

	/**
	 * Serializable singleton support
	 */
	private Object readResolve() {
		return instance();
	}

}
