/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.uitools;

/**
 * This exception can be used to interrupt a process
 * (such as in a UI dialog process).
 */
public class CancelException extends RuntimeException {

	public CancelException() {
		super();
	}

}
