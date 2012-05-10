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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

/**
 * Thrown when there are problems loading an ExternalClass.
 */
public class ExternalClassNotFoundException extends Exception {

	/**
	 * Constructs an ExternalClassNotFoundException with no detail message.
	 */
	public ExternalClassNotFoundException() {
		super();
	}
	
	/**
	 * Constructs an ExternalClassNotFoundException with the
	 * specified detail message.
	 * 
	 * @param message
	 */
	public ExternalClassNotFoundException(String message) {
		super(message);
	}
	
	/**
	 * Constructs an ExternalClassNotFoundException with the
	 * specified detail message and optional exception that was raised
	 * while loading the external class.
	 * 
	 * @param message
	 * @param cause
	 */
	public ExternalClassNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs an ExternalClassNotFoundException with the
	 * optional exception that was raised while loading the external class.
	 * 
	 * @param cause
	 */
	public ExternalClassNotFoundException(Throwable cause) {
		super(cause);
	}

}
