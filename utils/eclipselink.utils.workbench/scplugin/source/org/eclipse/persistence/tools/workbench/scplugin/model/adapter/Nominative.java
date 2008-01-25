/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

/**
 * Names are often used to uniquely identify a model object within a set of
 * objects of the same type. This interface allows those objects to be handled
 * by client code that is only interested in that uniquely-identifying name.
 * 
 * @version 10.1.3
 * @author Pascal Filion
 */
public interface Nominative
{
	/**
	 * Notifies a change in this nominative's name.
	 */
	public static final String NAME_PROPERTY = "name";

	/**
	 * Returns the object's name.
	 *
	 * @return A non-<code>null</code> string representing the name of the object
	 */
	String getName();
}