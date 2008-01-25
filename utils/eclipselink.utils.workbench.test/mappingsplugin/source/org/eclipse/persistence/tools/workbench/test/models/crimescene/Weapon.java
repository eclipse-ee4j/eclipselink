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

public class Weapon extends PieceOfEvidence {
	private boolean usedInCrime;
/**
 * Weapon constructor comment.
 */
public Weapon() {
	super();
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return boolean
 */
public boolean isUsedInCrime() {
	return this.usedInCrime;
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue boolean
 */
public void setUsedInCrime(boolean newValue) {
	this.usedInCrime = newValue;
}
}
