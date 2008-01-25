/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.resources;


/**
 * This is the exception that will be thrown if an icon repository
 * cannot find the icon for the specified key.
 */
public class MissingIconException extends MissingResourceException {

	public MissingIconException(String message, String key) {
		super(message, key);
	}

}
