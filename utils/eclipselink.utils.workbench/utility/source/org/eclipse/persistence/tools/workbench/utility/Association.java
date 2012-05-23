/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
