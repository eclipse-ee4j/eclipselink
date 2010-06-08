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
package org.eclipse.persistence.tools.workbench.test.models.phone;

/**
 * Insert the type's description here.
 * Creation date: (9/22/00 10:54:53 AM)
 * @author: Christopher Garrett
 */
public class State {
	private String name;
	private String abbreviation;
/**
 * State constructor comment.
 */
public State() {
	super();
}
/**
 * 
 * @return java.lang.String
 */
public java.lang.String getAbbreviation() {
	return this.abbreviation;
}
/**
 * 
 * @return java.lang.String
 */
public java.lang.String getName() {
	return this.name;
}
/**
 * 
 * @param newAbbreviation java.lang.String
 */
public void setAbbreviation(java.lang.String newAbbreviation) {
	this.abbreviation = newAbbreviation;
}
/**
 * 
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	this.name = newName;
}
}
