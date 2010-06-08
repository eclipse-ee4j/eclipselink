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
 * This is the exception that will be thrown if a repository
 * cannot find the resource for the specified key.
 */
public class MissingResourceException extends RuntimeException {
	private String key;

	public MissingResourceException(String message, String key) {
		super(message);
		this.key = key;
	}

	/**
	 * Return the key for which the repository had no resource.
	 */
	public String getKey() {
		return this.key;
	}

}
