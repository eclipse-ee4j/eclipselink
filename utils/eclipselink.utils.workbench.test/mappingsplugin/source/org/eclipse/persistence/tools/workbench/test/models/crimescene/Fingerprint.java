/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

public class Fingerprint extends PieceOfEvidence {
	private String type;
	private java.lang.String image;
/**
 * Fingerprint constructor comment.
 */
public Fingerprint() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 3:06:17 PM)
 * @return java.lang.String
 */
public java.lang.String getImage() {
	return image;
}
/**
 * Get the type of this fingerprint (e.g., whorl)
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 3:06:17 PM)
 * @param newImage java.lang.String
 */
public void setImage(java.lang.String newImage) {
	image = newImage;
}
/**
 * Set the type of this fingerprint (e.g., whorl)
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setType(String newValue) {
	this.type = newValue;
}
/**
 * Return the list of fingerprint types
 * Source: http://www.chickasaw.com/~waedens/fpc/ncic.htm
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public static Vector types() {
	Vector types = new Vector();
	types.addElement("Loop, Ulnar");
	types.addElement("Loop, Radial");
	types.addElement("Arch, Plain");
	types.addElement("Arch, Tented");
	types.addElement("Whorl, Plain");
	types.addElement("Whorl, Central Pocket");
	types.addElement("Whorl, Double");
	types.addElement("Whorl, Accidental");
	types.addElement("Amputation");
	return types;
}
}
