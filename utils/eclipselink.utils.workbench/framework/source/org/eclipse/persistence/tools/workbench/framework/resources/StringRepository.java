/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 * This interface provides a convenient protocol for clients
 * to internationalize their messages.
 */
public interface StringRepository {

	/**
	 * Return whether this repository includes the specified key.
	 * Call this before calling the #getString() methods to avoid
	 * excessive MissingStringExceptions being created.
	 */
	boolean hasString(String key);

	/**
	 * Return the localized string for the specified key.
	 */
	String getString(String key);

	/**
	 * Return the localized string for the specified key;
	 * formatting it with the specified argument.
	 */
	String getString(String key, Object argument);

	/**
	 * Return the localized string for the specified key;
	 * formatting it with the specified arguments.
	 */
	String getString(String key, Object argument1, Object argument2);

	/**
	 * Return the localized string for the specified key;
	 * formatting it with the specified arguments.
	 */
	String getString(String key, Object argument1, Object argument2, Object argument3);

	/**
	 * Return the localized string for the specified key;
	 * formatting it with the specified arguments.
	 */
	String getString(String key, Object[] arguments);


	String EMPTY_STRING = "";

	/**
	 * This instance will throw an exception for any non-null key
	 * and return empty string for any null key.
	 */
	StringRepository NULL_INSTANCE =
		new StringRepository() {
			public boolean hasString(String key) {
				return key == null;
			}
			public String getString(String key) {
				return this.getString(key, new Object[0]);
			}
			public String getString(String key, Object argument) {
				return this.getString(key, new Object[] {argument});
			}
			public String getString(String key, Object argument1, Object argument2) {
				return this.getString(key, new Object[] {argument1, argument2});
			}
			public String getString(String key, Object argument1, Object argument2, Object argument3) {
				return this.getString(key, new Object[] {argument1, argument2, argument3});
			}
			public String getString(String key, Object[] arguments) {
				if (key == null) {
					return EMPTY_STRING;
				}
				throw new MissingStringException("Missing string: " + key, key);
			}
			public String toString() {
				return "NullStringRepository";
			}
		};

}
