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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

// JDK
import java.util.Iterator;

/**
 * This <code>Property</code> provides the common API for managing properties,
 * which is a collection of names and values.
 *
 * @see LoginAdapter
 * @see JNDINamingService
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public interface Property
{
	/**
	 * Identifies a change in the <code>Property</code>'s property collection.
	 */
	public final static String PROPERTY_COLLECTION = "property";

	/**
	 * Adds a new {@link PropertyAdapter} for the given name and value.
	 *
	 * @param name The key of the properties
	 * @param value Its value
	 * @return The newly created {@link PropertyAdapter}
	 */
	public PropertyAdapter addProperty(String name, String value);

	/**
	 * Returns an iterator on this collection of properties.
	 *
	 * @return An iterator over the properties
	 */
	public Iterator properties();

	/**
	 * Returns the count of {@link PropertyAdapter}s contained by the implementer.
	 *
	 * @return The count of {@link PropertyAdapter}s
	 */
	public int propertySize();

	/**
	 * Removes the given {@link PropertyAdapter} from who the implementer.
	 *
	 * @param property The property to be removed
	 */
	public void removeProperty(PropertyAdapter property);
}
