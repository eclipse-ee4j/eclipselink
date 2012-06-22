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
package org.eclipse.persistence.tools.workbench.framework.resources;

import javax.swing.Icon;

/**
 * Interface used by UI components to fetch icons based on
 * icon keys. This allows a level of indirection between the UI
 * code and the actual icon. The UI code can be ignorant of
 * the location and construction of the icons.
 */
public interface IconRepository {

	/**
	 * Return true if the repository has an icon for the given key.
	 * Use this before calling getIcon(String) to avoid creating
	 * numerous MissingIconExceptions.
	 */
	boolean hasIcon(String key);
   
	/**
	 * Return the icon for the specified key.
	 * Throw an exception if the icon is not found. Call
	 * hasIcon(String) first to avoid getting an exception when
	 * the icon does not exist.
	 * Return null if the key is null, this allows for
	 * situations where some objects have icons
	 * and some do not; the objects without icons can
	 * simply return null for their icon keys.
	 * Also, null will be returned if a particular repository
	 * cannot, or should not, supply an icon for the key.
	 */
	Icon getIcon(String key);


	// ********** null implementation **********

	/**
	 * This instance will throw an exception for any non-null key
	 * and return null for any null key.
	 */
	IconRepository NULL_INSTANCE =
		new IconRepository() {
			public boolean hasIcon(String key) {
				return key == null;
			}
			public Icon getIcon(String key) {
				if (key == null) {
					return null;
				}
				throw new MissingIconException("Missing icon file resource name: " + key, key);
			}
			public String toString() {
				return "NullIconRepository";
			}
		};

}
