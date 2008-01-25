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

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;


/**
 * Implementation of PropertyValueModel that simply holds on to an
 * object and uses it as the value.
 */
public class SimplePropertyValueModel
	extends AbstractModel
	implements PropertyValueModel
{
	/** The value. */
	protected Object value;


	/**
	 * Construct a PropertyValueModel for the specified value.
	 */
	public SimplePropertyValueModel(Object value) {
		super();
		this.value = value;
	}

	/**
	 * Construct a PropertyValueModel with a starting value of null.
	 */
	public SimplePropertyValueModel() {
		this(null);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#buildDefaultChangeSupport()
	 */
	protected ChangeSupport buildDefaultChangeSupport() {
		return new ValueModelChangeSupport(this);
	}


	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * @see PropertyValueModel#setValue(Object)
	 */
	public void setValue(Object value) {
		Object old = this.value;
		this.value = value;
		this.firePropertyChanged(VALUE, old, value);
	}

	public void toString(StringBuffer sb) {
		sb.append(this.value);
	}

}
