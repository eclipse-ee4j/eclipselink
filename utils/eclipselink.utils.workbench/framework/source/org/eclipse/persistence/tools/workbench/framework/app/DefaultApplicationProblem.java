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
package org.eclipse.persistence.tools.workbench.framework.app;

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;

/**
 * Straightforward implementation of the ApplicationNode interface.
 */
public class DefaultApplicationProblem implements ApplicationProblem {
	private final ApplicationNode source;
	private final String messageCode;
	private final String message;

	public DefaultApplicationProblem(ApplicationNode source, String messageCode, String message) {
		super();
		this.source = source;
		this.messageCode = messageCode;
		this.message = message;
	}

	/**
	 * @see ApplicationProblem#getSource()
	 */
	public ApplicationNode getSource() {
		return this.source;
	}

	/**
	 * @see ApplicationProblem#getMessageCode()
	 */
	public String getMessageCode() {
		return this.messageCode;
	}

	/**
	 * @see ApplicationProblem#getMessage()
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.app.ApplicationProblem#printOn(org.eclipse.persistence.tools.workbench.utility.IndentingPrintWriter)
	 */
	public void printOn(IndentingPrintWriter writer) {
		writer.print(this.messageCode);
		writer.print(" - ");
		writer.print(this.message);
	}

}
