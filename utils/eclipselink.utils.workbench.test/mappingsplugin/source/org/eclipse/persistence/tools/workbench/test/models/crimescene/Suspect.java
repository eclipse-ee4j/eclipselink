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
package org.eclipse.persistence.tools.workbench.test.models.crimescene;

import org.eclipse.persistence.sessions.Record;

public class Suspect extends Person {
	private String alias = "";
	private float height;
/**
 * Create a new Suspect object.
 */
public Suspect() {
	super();
}
/**
*
* Calculate and set the value of height based on the
* feet and inches values in the Database row.
*
**/
public float calculateHeight(Record row) {
	Integer feet = (Integer) row.get("HEIGHT_FEET");
	Integer inches = (Integer) row.get("HEIGHT_INCHES");
	int totalInches = feet.intValue() * 12 + inches.intValue();
	return totalInches * (float).0254;
}
/**
 * Return this suspect's alias
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getAlias() {
	return this.alias;
}
/**
*
* Return the suspect's height in meters.
*
**/
public float getHeight() {
	return this.height;
}
/**
*
* Convert the height (in meters) into feet,
* ignoring any remainder.
*
**/
public int heightInFeet() {
	return (int)((getHeight() / (float).0254) / 12);
}
/**
*
* Answer the remaining number of inches after
* converting the height (in meters) to feet.
*
**/
public int inchesRemainder() {
	return (int)(getHeight() / (float).0254) % 12;
}
/**
 * Set this suspect's alias
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setAlias(String newValue) {
	this.alias = newValue;
}
/**
 * 
 * @param newHeight float
 */
public void setHeight(float newHeight) {
	this.height = newHeight;
}
/**
 * Return the full name of this suspect, with his/her alias,
 * e.g., Christopher "The Cleaner" Garrett
 * @return a string representation of the receiver
 */
@Override
public String toString() {
	return getFirstName() + " \"" + getAlias() + "\" " + getLastName();
}
}
