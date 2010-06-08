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
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

/**
 * A <code>ComboBoxSelection</code> is used to wrap items in a combo box. When
 * this wrapper is selected, the appropriate aspect adapter simply perform this
 * command:
 * <pre>
 * protected void setValueOnSubject(Object value)
 * {
 * 	((ComboBoxSelection) value).setPropertyOn(subject);
 * }
 * </pre>
 * This is useful when each selected item performs a different task.
 *
 * @version 10.1.3
 * @author Trân Lê
 */
public abstract class ComboBoxSelection implements Comparable {

	/**
	 * The localized string used to decorate this <code>ComboBoxSelection</code>.
	 */
	private String displayString;

	/**
	 * Creates a new <code>ComboBoxSelection</code>.
	 *
	 * @param displayString The localized string used to decorate this
	 * <code>ComboBoxSelection</code>
	 */
	public ComboBoxSelection( String displayString) {
		super();
		this.displayString = displayString;
	}

	public int compareTo( Object object) {
		ComboBoxSelection selection = ( ComboBoxSelection)object;
		return displayString().compareTo( selection.displayString());
	}

	/**
	 * Returns the localized string used to decorate this <code>ComboBoxSelection</code>.
	 *
	 * @return The string to be used by the combo box's renderer
	 */
	public String displayString() {
		return displayString;
	}

	/**
	 * Sets the property on the given subject.
	 *
	 * @param suject The subject to be updated because this wrapper has been
	 * selected
	 */
	public abstract void setPropertyOn( Object suject);

	/**
	 * Returns a string representation of this <code>ComboBoxSelection</code>.
	 *
	 * @return #displayString()
	 */
	public String toString() {
		return displayString();
	}
}
