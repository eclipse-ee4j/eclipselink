/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.uitools;

import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;

/**
 * Straightforward adaptation of Display to StringConverter.
 */
public class DisplayableStringConverter implements StringConverter {

	// singleton
	private static DisplayableStringConverter INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized StringConverter instance() {
		if (INSTANCE == null) {
			INSTANCE = new DisplayableStringConverter();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private DisplayableStringConverter() {
		super();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.StringConverter#convert(java.lang.Object)
	 */
	public String convertToString(Object o) {
		return (o == null) ? null : ((Displayable) o).displayString();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "DisplayableStringConverter";
	}

}
