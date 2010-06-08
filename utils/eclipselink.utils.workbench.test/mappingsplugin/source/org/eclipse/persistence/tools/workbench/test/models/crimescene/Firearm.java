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

import java.util.Vector;

public class Firearm extends Weapon {
	private String caliber;
	private String type;
	private byte[] byteArray;

/**
 * Firearm constructor comment.
 */
public Firearm() {
	super();
}
/**
 * Return this weapon's caliber
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getCaliber() {
	return caliber;
}
/**
 * Return the type of this weapon
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getType() {
	return type;
}
/**
 * Set the caliber of this weapon
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setCaliber(String newValue) {
	this.caliber = newValue;
}
/**
 * Set the type of this weapon
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setType(String newValue) {
	this.type = newValue;
}

public byte[] getByteArray() {
	return this.byteArray;
}

public void setByteArray(byte[] byteArray) {
	this.byteArray = byteArray;
}

	public static Vector types() {
		Vector v = new Vector();
		v.addElement("Pistol");
		v.addElement("Rifle");
		v.addElement("Shotgun");
		v.addElement("Other");
		return v;
	}
}
