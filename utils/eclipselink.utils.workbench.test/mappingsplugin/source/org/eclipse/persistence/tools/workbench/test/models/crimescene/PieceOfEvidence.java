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

/**
 * A class representing pieces of evidence one might find at a crime scene
 **/
 
public class PieceOfEvidence {
	private String name;
	private String description;
	private long id;
	private CrimeScene crimeScene;
/**
 * Create a new PieceOfEvidence object
 */
public PieceOfEvidence() {
	setName("");
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return TOPLink.Course.CrimeScene.model.CrimeScene
 */
public CrimeScene getCrimeScene() {
	return crimeScene;
}
/**
 * Return a description of this piece of evidence
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getDescription() {
	return description;
}
/**
 * Return the ID of this object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return long
 */
public long getId() {
	return id;
}
/**
 * Return the name of this object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getName() {
	return name;
}
/**
 * @author Christopher Garrett
 * @param newValue TOPLink.Course.CrimeScene.model.CrimeScene
 */
public void setCrimeScene(CrimeScene newValue) {
	crimeScene = newValue;
}
/**
 * Set the description of this object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setDescription(String newValue) {
	this.description = newValue;
}
/**
 * Set the id for this object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue long
 */
public void setId(long newValue) {
	this.id = newValue;
}
/**
 * Set the name of this object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setName(String newValue) {
	this.name = newValue;
}
/**
 * Returns the name of this object
 * @return the name of this object
 */
@Override
public String toString() {
	return getName();	
}
}
