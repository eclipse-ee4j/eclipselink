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
package org.eclipse.persistence.tools.workbench.platformsmodel;

/**
 * A CorruptFileException is thrown whenever a "corrupt" XML file
 * is read in.
 */
public class CorruptXMLException extends Exception {

	/**
	 * Construct an exception with no message.
	 */
	public CorruptXMLException() {
		super();
	}

	/**
	 * Construct an exception with the specified message.
	 */
	public CorruptXMLException(String message) {
		super(message);
	}

	/**
	 * Construct an exception chained to the specified cause.
	 */
	public CorruptXMLException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct an exception with the specified message and
	 * chained to the specified cause.
	 */
	public CorruptXMLException(String message, Throwable cause) {
		super(message, cause);
	}

}
