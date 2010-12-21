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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

/**
 * This defines a common interface for all the types (internal, external, and user-defined)
 * so they can be consolidated whenever necessary (e.g. in the UI choosers).
 */
public interface ClassDescription {
	
	/**
	 * Returns the name of the entity (class, interface,
	 * array class, primitive type, or void) represented by
	 * this ClassDescription object, as a String.
	 * 
	 * @see java.lang.Class#getName()
	 */
	String getName();
	
	/**
	 * Returns any additional information about the entity
	 * (class, interface, array class, primitive type, or void)
	 * represented by this ClassDescription object, as a String.
	 * This information can be used to differentiate among
	 * ClassDescription objects that might have the same name.
	 * It can also be used for debugging user-developed
	 * external class repositories while using the TopLink
	 * Mapping Workbench.
	 */
	String getAdditionalInfo();

}
