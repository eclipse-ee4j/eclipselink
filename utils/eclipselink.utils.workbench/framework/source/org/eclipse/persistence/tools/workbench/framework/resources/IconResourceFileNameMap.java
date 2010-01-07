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
package org.eclipse.persistence.tools.workbench.framework.resources;

/**
 * Interface used by DefaultIconRepository to map icon keys
 * to their associated resource files.
 */
public interface IconResourceFileNameMap {

	/**
	 * Return whether there is a resource file name associated with the 
	 * specified key.  This is to avoid throwing tons of MissingIconExceptions.
	 * Call this before calling #getResourceFileName(String)
	 */
	boolean  hasResourceFileName(String key);

	/**
	 * Return the name of the resource file containing the icon
	 * associated with the specified key. Throw a MissingIconException
	 * if there is not resource file name associated with the specified key.
	 */
	String getResourceFileName(String key);


	// ********** null implementation **********

	/**
	 * This instance will throw an exception for any key.
	 */
	IconResourceFileNameMap NULL_INSTANCE =
		new IconResourceFileNameMap() {
            public boolean hasResourceFileName(String key) {
                return false;
            }
			public String getResourceFileName(String key) {
				throw new MissingIconException("Missing icon file resource name: " + key, key);
			}
			public String toString() {
				return "NullIconResourceFileNameMap";
			}
		};

}
