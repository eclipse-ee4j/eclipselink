/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility;

/**
 * Straightforward definition of an object pairing.
 * The key is immutable.
 */
public interface Association {

	/**
	 * Return the association's key.
	 */
	Object getKey();

	/**
	 * Return the association's value.
	 */
	Object getValue();

	/**
	 * Set the association's value.
	 * Return the previous value.
	 */
	Object setValue(Object value);

	/**
	 * Return true if both the associations' keys and values
	 * are equal.
	 */
	boolean equals(Object o);

	/**
	 * Return a hash code based on the association's
	 * key and value.
	 */
	int hashCode();

}
