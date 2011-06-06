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
package org.eclipse.persistence.tools.workbench.utility.string;

/**
 * Used by various "pluggable" classes to transform objects
 * into strings.
 */
public interface StringConverter {

	/**
	 * Convert the specified object into a string.
	 * The semantics of "convert" is determined by the
	 * contract between the client and the server.
	 */
	String convertToString(Object o);


	StringConverter DEFAULT_INSTANCE =
		new StringConverter() {
			// simply return the object's #toString() result
			public String convertToString(Object o) {
				return (o == null) ? null : o.toString();
			}
			public String toString() {
				return "DefaultStringConverter";
			}
		};

}
