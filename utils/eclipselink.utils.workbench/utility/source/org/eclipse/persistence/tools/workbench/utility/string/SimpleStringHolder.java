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
package org.eclipse.persistence.tools.workbench.utility.string;

/**
 * Straightforward implementation of the StringHolder interface.
 */
public class SimpleStringHolder extends AbstractStringHolder {
	protected final String string;

	public SimpleStringHolder(String string) {
		super();
		this.string = this.buildString(string);
	}

	/**
	 * Allow subclasses to manipulate the string before it is stored
	 * (e.g. convert it to lowercase).
	 */
	protected String buildString(String s) {
		return s;
	}

	/**
	 * @see StringHolder#getString()
	 */
	public String getString() {
		return this.string;
	}

}
