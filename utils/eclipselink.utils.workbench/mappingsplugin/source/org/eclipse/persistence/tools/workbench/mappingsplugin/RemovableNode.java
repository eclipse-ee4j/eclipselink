/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin;

/**
 * Define an interface that can be used to identify and remove
 * objects from the project tree.
 */
public interface RemovableNode {

	/**
	 * Remove the object from the project tree.
	 */
	void remove();

	/**
	 * Return the object's name, so its removal can be confirmed.
	 */
	String getName();

}
