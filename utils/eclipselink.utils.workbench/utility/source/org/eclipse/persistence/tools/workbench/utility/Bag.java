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
 * A collection that allows duplicate elements.
 * <p>
 * The <code>Bag</code> interface places additional stipulations,
 * beyond those inherited from the <code>java.util.Collection</code> interface,
 * on the contracts of the <code>equals</code> and <code>hashCode</code> methods.
 * 
 * @version 1.00 Jan-2002
 * @see java.util.Collection
 * @see HashBag
 */

public interface Bag extends java.util.Collection {
	
	/**
	 * Compares the specified object with this bag for equality. Returns
	 * <code>true</code> if the specified object is also a bag, the two bags
	 * have the same size, and every member of the specified bag is
	 * contained in this bag with the same number of occurrences (or equivalently,
	 * every member of this bag is contained in the specified bag with the same
	 * number of occurrences). This definition ensures that the
	 * equals method works properly across different implementations of the
	 * bag interface.
	 */
	boolean equals(Object o);
	
	/**
	 * Returns the hash code value for this bag. The hash code of a bag is
	 * defined to be the sum of the hash codes of the elements in the bag,
	 * where the hashcode of a <code>null</code> element is defined to be zero.
	 * This ensures that <code>b1.equals(b2)</code> implies that
	 * <code>b1.hashCode() == b2.hashCode()</code> for any two bags
	 * <code>b1</code> and <code>b2</code>, as required by the general
	 * contract of the <code>Object.hashCode</code> method.
	 */
	int hashCode();
	
}
