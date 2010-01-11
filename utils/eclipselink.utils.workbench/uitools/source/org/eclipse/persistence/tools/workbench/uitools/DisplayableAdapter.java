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
package org.eclipse.persistence.tools.workbench.uitools;

import javax.swing.Icon;

/**
 * Indirectly adapt an object to the Displayable interface.
 */
public interface DisplayableAdapter {

	/**
	 * Return the specified object's display string.
	 * @see Displayable#displayString()
	 */
	String displayString(Object object);

	/**
	 * Return the specified object's icon.
	 * @see Displayable#icon()
	 */
	Icon icon(Object object);


	/**
	 * Simple implementation of the interface that uses the object's
	 * #toString() for its display string and returns null for the icon.
	 */
	DisplayableAdapter DEFAULT_INSTANCE = 
		new DisplayableAdapter() {

			public String displayString(Object object) {
				return String.valueOf(object);
			}

			public Icon icon(Object object) {
				return null;
			}

		};

}
