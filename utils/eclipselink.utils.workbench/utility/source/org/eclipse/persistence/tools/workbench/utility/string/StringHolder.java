/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.string;

/**
 * This interface allows tools to handle strings indirectly so the strings
 * can remain associated with another object.
 */
public interface StringHolder {

	/**
	 * Return the string.
	 */
	String getString();

}
