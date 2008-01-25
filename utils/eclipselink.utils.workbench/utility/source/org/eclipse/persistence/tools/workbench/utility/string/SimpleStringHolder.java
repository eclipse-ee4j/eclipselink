/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
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
