/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework;

/**
 * This exception is thrown when a {@link Plugin}
 * has problems opening a file.
 */
public class OpenException
	extends Exception
{

	/**
	 * Construct an exception with the specified root cause.
	 */
	public OpenException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct an exception with the specified root cause and message.
	 */
	public OpenException(String message, Throwable cause) {
		super(message, cause);
	}

}
