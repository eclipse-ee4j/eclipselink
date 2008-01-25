/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.crimescene;

public class Victim extends Person {
	private String statement;
/**
 * Victim constructor comment.
 */
public Victim() {
	super();
}
/**
 * Return this victim's statement
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getStatement() {
	return this.statement;
}
/**
 * Set this victim's statement
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setStatement(String newValue) {
	this.statement = newValue;
}
}
