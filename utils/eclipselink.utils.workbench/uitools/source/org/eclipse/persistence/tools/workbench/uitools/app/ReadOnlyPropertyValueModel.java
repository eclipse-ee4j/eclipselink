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
 * Implementation of ValueModel that can be used for
 * returning a static value, but still allows listeners to be added.
 * Listeners will NEVER be notified of any changes, because there should be none.
 */
public class ReadOnlyPropertyValueModel
	extends AbstractReadOnlyPropertyValueModel
{
	/** The value. */
	protected Object value;

	private static final long serialVersionUID = 1L;


	/**
	 * Construct a read-only PropertyValueModel for the specified value.
	 */
	public ReadOnlyPropertyValueModel(Object value) {
		super();
		this.value = value;
	}


	// ********** ValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		return this.value;
	}

}
